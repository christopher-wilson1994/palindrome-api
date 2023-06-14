package com.chriswilson.palindromeapi.repository;

import com.chriswilson.palindromeapi.entity.Palindrome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PalindromeFileStorageRepositoryTest {
  @TempDir Path tempPath;

  @Test
  void save_should_save_to_file() throws IOException {
    PalindromeFileStorageRepository repo = new PalindromeFileStorageRepository(tempPath.toString());
    Palindrome toSave = new Palindrome("word", false);

    Palindrome result = repo.save(toSave);

    assertThat(result).isEqualTo(toSave);
    String content = Files.readString(repo.storagePath);
    assertThat(content).isEqualTo(toSave.getWord() + "," + toSave.isPalindrome() + "\n");
  }

  @Test
  void save_should_not_save_duplicates() throws IOException {
    PalindromeFileStorageRepository repo = new PalindromeFileStorageRepository(tempPath.toString());
    Palindrome toSave = new Palindrome("word", false);

    repo.save(toSave);
    repo.save(toSave);

    String content = Files.readString(repo.storagePath);
    assertThat(content).isEqualTo(toSave.getWord() + "," + toSave.isPalindrome() + "\n");
  }

  @Test
  void findByWord_should_return_empty_when_no_file() {
    PalindromeFileStorageRepository repo = new PalindromeFileStorageRepository("fake/dir/test");
    Optional<Palindrome> result = repo.findByWord("word");
    assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void findByWord_should_return_empty_when_no_match() {
    PalindromeFileStorageRepository repo = new PalindromeFileStorageRepository(tempPath.toString());
    Optional<Palindrome> result = repo.findByWord("word");
    assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void findByWord_should_return_match() {
    PalindromeFileStorageRepository repo = new PalindromeFileStorageRepository(tempPath.toString());
    repo.save(new Palindrome("madam", true));
    Palindrome toMatch = repo.save(new Palindrome("match", false));

    Optional<Palindrome> result = repo.findByWord(toMatch.getWord());
    Palindrome resolvedPalindrome = result.get();

    assertThat(resolvedPalindrome).isEqualTo(toMatch);
  }

  @Test
  void findAll_should_return_pageable_data() {
    PalindromeFileStorageRepository repo = new PalindromeFileStorageRepository(tempPath.toString());
    Palindrome p1 = repo.save(new Palindrome("madam", true));
    Palindrome p2 = repo.save(new Palindrome("kayak", true));
    Palindrome p3 = repo.save(new Palindrome("test", false));

    // verify multi pages
    int smallPageSize = 1;
    Page<Palindrome> page1 = repo.findAll(PageRequest.of(0, smallPageSize));
    assertThat(page1.get().toList()).isEqualTo(List.of(p1));
    Page<Palindrome> page2 = repo.findAll(PageRequest.of(1, smallPageSize));
    assertThat(page2.get().toList()).isEqualTo(List.of(p2));
    Page<Palindrome> page3 = repo.findAll(PageRequest.of(2, smallPageSize));
    assertThat(page3.get().toList()).isEqualTo(List.of(p3));

    // verify no more results
    assertThat(page3.hasNext()).isFalse();
    Page<Palindrome> page4 = repo.findAll(PageRequest.of(3, smallPageSize));
    assertThat(page4.get().toList()).isEqualTo(Collections.emptyList());

    // verify larger page size
    int largerPageSize = 2;
    Page<Palindrome> page1Large = repo.findAll(PageRequest.of(0, largerPageSize));
    assertThat(page1Large.get().toList()).isEqualTo(List.of(p1, p2));

    Page<Palindrome> page2Large = repo.findAll(PageRequest.of(1, largerPageSize));
    assertThat(page2Large.get().toList()).isEqualTo(List.of(p3));

    // verify no more results for larger page size
    assertThat(page2Large.hasNext()).isFalse();
    Page<Palindrome> page3Large = repo.findAll(PageRequest.of(2, largerPageSize));
    assertThat(page3Large.get().toList()).isEqualTo(Collections.emptyList());
  }
}
