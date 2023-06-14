package com.chriswilson.palindromeapi.controller;

import com.chriswilson.palindromeapi.api.request.PalindromeRequest;
import com.chriswilson.palindromeapi.api.response.PalindromeResponse;
import com.chriswilson.palindromeapi.service.PalindromeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/palindrome")
public class PalindromeRestController {
  private final PalindromeService palindromeService;

  @PostMapping
  public ResponseEntity<PalindromeResponse> checkPalindrome(
      @RequestBody @Valid PalindromeRequest palindromeRequest) {
    boolean isPalindrome = palindromeService.processPalindromeRequest(palindromeRequest.word());
    return ResponseEntity.ok(new PalindromeResponse(palindromeRequest.word(), isPalindrome));
  }
}
