package com.chriswilson.palindromeapi.service;

import com.chriswilson.palindromeapi.entity.Palindrome;
import com.chriswilson.palindromeapi.repository.PalindromeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PalindromeServiceTest {

  @Mock PalindromeRepository palindromeRepository;

  @InjectMocks PalindromeService palindromeService;

  static Stream<Arguments> palindromeTestData() {
    return Stream.of(Arguments.of("kayak", true), Arguments.of("World", false));
  }

  @Test
  void processPalindromeRequest_should_return_value_from_storage() {
    String word = "word";
    when(palindromeRepository.findByWord(word))
        .thenReturn(Optional.of(new Palindrome(word, false)));

    boolean result = palindromeService.processPalindromeRequest(word);
    assertThat(result).isFalse();
    verify(palindromeRepository).findByWord(word);
  }

  @ParameterizedTest
  @MethodSource("palindromeTestData")
  void processPalindromeRequest_should_calculate_and_persist(String word, boolean expected) {
    when(palindromeRepository.findByWord(word)).thenReturn(Optional.empty());
    boolean result = palindromeService.processPalindromeRequest(word);
    verify(palindromeRepository, timeout(100))
        .save(argThat(arg -> arg.getWord().equals(word) && arg.isPalindrome() == expected));
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void findAll_should_return_page_data() {
    Pageable pageable = PageRequest.of(0, 1);
    Page<Palindrome> page = Page.empty();
    when(palindromeRepository.findAll(pageable)).thenReturn(page);

    Page<Palindrome> result = palindromeRepository.findAll(pageable);
    assertThat(result).isEqualTo(page);
    verify(palindromeRepository).findAll(pageable);
  }
}
