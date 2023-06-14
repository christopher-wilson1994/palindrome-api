package com.chriswilson.palindromeapi.controller;

import com.chriswilson.palindromeapi.api.request.PalindromeRequest;
import com.chriswilson.palindromeapi.service.PalindromeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PalindromeRestController.class)
public class PalindromeRestControllerMockMvcTest {
  @Autowired private ObjectMapper om;
  @Autowired private MockMvc mockMvc;

  @MockBean private PalindromeService palindromeService;

  @Test
  void checkPalindrome_should_return_palindrome() throws Exception {
    PalindromeRequest request = new PalindromeRequest("User", "Word");
    when(palindromeService.processPalindromeRequest(request.word())).thenReturn(false);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/palindrome")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.word").value(request.word()))
        .andExpect(jsonPath("$.palindrome").value(false));

    verify(palindromeService).processPalindromeRequest(request.word());
  }

  @Test
  void checkPalindrome_should_return_400_for_invalid_word() throws Exception {
    PalindromeRequest request = new PalindromeRequest("User", "Word123");
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/palindrome")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
