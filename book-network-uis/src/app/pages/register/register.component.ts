import { Component } from '@angular/core';
import {RegistrationRequest} from "../../services/models/registration-request";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  registerRequest: RegistrationRequest = {email: '', firstname:'', lastname:'',password:''};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService,
  ) {
  }

  register() {
    // Initialize an empty array for error messages
    this.errorMsg = [];

    // Call the registration service
    this.authService.register({
      body: this.registerRequest
    }).subscribe({
      next: () => {
        // Navigate to the account activation page on successful registration
        this.router.navigate(['activate-account']);
      },
      error: (err) => {
        // Check if validation errors exist and assign them to errorMsg
        if (err.error && err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else {
          // Handle unexpected errors
          this.errorMsg.push('An unexpected error occurred. Please try again later.');
        }
      }
    });
  }

  login() {
    this.router.navigate(['login']);
  }
}
