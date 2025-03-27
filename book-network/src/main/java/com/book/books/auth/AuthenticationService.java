package com.book.books.auth;


import com.book.books.role.RoleRepository;
import com.book.books.security.JwtService;
import com.book.books.user.Token;
import com.book.books.user.TokenRepository;
import com.book.books.user.User;
import com.book.books.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;



@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
   private final PasswordEncoder passwordEncoder;
   private final UserRepository userRepository;
   private final TokenRepository tokenRepository;
   private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${spring.application.mailing.frontend.activation-url}")
    private String activationUrl;


    public void register(RegistrationRequest request) throws MessagingException {
           var userRole = roleRepository.findByName("USER")
//           to do - better exception handling
                   .orElseThrow(()-> new IllegalStateException("Role User was not initialized . "));

           var user  = User.builder()
                   .firstname(request.getFirstname())
                   .lastname(request.getLastname())
                   .email(request.getEmail())
                   .password(passwordEncoder.encode(request.getPassword()))
                   .accountLocked(false)
                   .enabled(false)
                   .roles(List.of(userRole))
                   .build();
           userRepository.save(user);
           sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        System.out.println(newToken);
        // send email

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"

        );



    }

    private String generateAndSaveActivationToken(User user) {
        // generate a token
        String generatedToken = generateActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();

        tokenRepository.save(token);

        return generatedToken;
    }


    // Generate An Activation Code
    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());// (0 - > 9)
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var user = (User) auth.getPrincipal();
            if (user == null) {
                throw new RuntimeException("User not found after authentication");
            }

            var claims = new HashMap<String, Object>();
            claims.put("fullname", user.fullName());

            var jwtToken = jwtService.generateToken(claims, user);
            if (jwtToken == null || jwtToken.isEmpty()) {
                throw new RuntimeException("Failed to generate JWT token");
            }

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            // Handle authentication or token generation errors
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            // Send validation email
            sendValidationEmail(savedToken.getUser());

            // Create and save a new activation token
            Token newToken = createNewToken(savedToken.getUser());
            tokenRepository.save(newToken);

            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email address.");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private Token createNewToken(User user) {
        // Logic to create a new activation token for the user
        Token newToken = new Token();
        newToken.setUser(user);
        newToken.setExpiresAt(LocalDateTime.now().plusHours(24)); // Set expiration time as needed
        // Additional properties can be set here
        return newToken;
    }
}
