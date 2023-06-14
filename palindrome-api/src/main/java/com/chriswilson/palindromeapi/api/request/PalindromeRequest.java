package com.chriswilson.palindromeapi.api.request;

import com.chriswilson.palindromeapi.validation.PalindromeRequestValid;
import jakarta.validation.constraints.NotEmpty;

public record PalindromeRequest(
    @NotEmpty(message = "userName must not be blank or null") String userName,
    @PalindromeRequestValid String word) {}
