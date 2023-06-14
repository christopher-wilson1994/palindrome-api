package com.chriswilson.palindromeapi.repository;

import com.chriswilson.palindromeapi.entity.Palindrome;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PalindromeRepository {
  Palindrome save(Palindrome palindrome);

  Page<Palindrome> findAll(Pageable page);

  Optional<Palindrome> findByWord(String word);
}
