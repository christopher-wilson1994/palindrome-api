package com.chriswilson.palindromeapi.service;

import com.chriswilson.palindromeapi.config.CacheConfig;
import com.chriswilson.palindromeapi.entity.Palindrome;
import com.chriswilson.palindromeapi.repository.PalindromeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class PalindromeService {

  private final PalindromeRepository palindromeRepository;

  @Cacheable(cacheNames = CacheConfig.PALINDROME_CACHE)
  public boolean processPalindromeRequest(String word) {
    Optional<Palindrome> optionalPalindrome = palindromeRepository.findByWord(word);
    if (optionalPalindrome.isPresent()) {
      return optionalPalindrome.get().isPalindrome();
    }
    boolean palindrome = isPalindrome(word);
    CompletableFuture.runAsync(() -> palindromeRepository.save(new Palindrome(word, palindrome)));
    return palindrome;
  }

  public Page<Palindrome> findAll(Pageable page) {
    return palindromeRepository.findAll(page);
  }

  private boolean isPalindrome(String str) {

    int leftIndex = 0;
    int rightIndex = str.length() - 1;

    while (leftIndex < rightIndex) {
      if (str.charAt(leftIndex) != str.charAt(rightIndex)) {
        return false;
      }
      leftIndex++;
      rightIndex--;
    }
    return true;
  }
}
