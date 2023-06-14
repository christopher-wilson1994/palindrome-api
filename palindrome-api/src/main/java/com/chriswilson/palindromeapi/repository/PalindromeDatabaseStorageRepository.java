package com.chriswilson.palindromeapi.repository;

import com.chriswilson.palindromeapi.entity.Palindrome;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.PagingAndSortingRepository;

@Profile("scalable")
public interface PalindromeDatabaseStorageRepository
    extends PagingAndSortingRepository<Palindrome, String>, PalindromeRepository {}
