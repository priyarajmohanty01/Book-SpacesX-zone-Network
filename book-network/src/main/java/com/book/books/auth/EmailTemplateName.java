package com.book.books.auth;


import lombok.Getter;

@Getter

public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account");

    private final String name;

    private EmailTemplateName(String name) {
        this.name = name;

    }


}
