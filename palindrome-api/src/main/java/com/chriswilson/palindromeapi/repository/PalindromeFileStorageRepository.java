package com.chriswilson.palindromeapi.repository;

import com.chriswilson.palindromeapi.entity.Palindrome;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@Profile("local")
@Repository
@Slf4j
public class PalindromeFileStorageRepository implements PalindromeRepository {

  private static final String FILE_NAME = "palindromes.csv";
  private static final String ERROR_MESSAGE_TEMPLATE =
      "Exception occurred while reading {} with exception {}";
  final Path storagePath;
  private final Lock fileLock;

  public PalindromeFileStorageRepository(
      @Value("${flatfile.storage.path:#{systemProperties['user.dir']}}") String storagePathPrefix) {
    fileLock = new ReentrantLock();
    storagePath = Path.of(storagePathPrefix, FILE_NAME);
  }

  @Override
  public synchronized Palindrome save(Palindrome palindrome) {
    if (findByWord(palindrome.getWord()).isPresent()) {
      return palindrome;
    }
    fileLock.lock();
    File csvOutputFile = new File(storagePath.toUri());

    try (PrintWriter pw = new PrintWriter(new FileOutputStream(csvOutputFile, true))) {
      pw.println(palindrome.getWord() + "," + palindrome.isPalindrome());
      return palindrome;
    } catch (FileNotFoundException e) {
      log.error("Failed to find file {} with exception {}", storagePath, e.getMessage());
      return null;
    } finally {
      fileLock.unlock();
    }
  }

  @Override
  public Page<Palindrome> findAll(Pageable pageRequest) {
    if (!storagePath.toFile().exists()) {
      return Page.empty();
    }
    fileLock.lock();
    try (Stream<String> lines = Files.lines(storagePath);
        Stream<String> linesForCount = Files.lines(storagePath)) {
      int toSkip = pageRequest.getPageNumber() * pageRequest.getPageSize();

      // TODO FUTURE WORK - we may want to include all the func spring data paging and sorting
      // provides( params in pageRequest  IE sorting)
      List<Palindrome> elements =
          lines
              .skip(toSkip)
              .limit(pageRequest.getPageSize())
              .map(this::palindromeFromCsvLine)
              .filter(Objects::nonNull)
              .toList();
      return new PageImpl<>(elements, pageRequest, linesForCount.count());

    } catch (IOException e) {
      log.error(ERROR_MESSAGE_TEMPLATE, storagePath, e.getMessage());
      return Page.empty();
    } finally {
      fileLock.unlock();
    }
  }

  @Override
  public Optional<Palindrome> findByWord(String word) {
    if (!storagePath.toFile().exists()) {
      return Optional.empty();
    }
    fileLock.lock();
    try (Stream<String> streamOfLines = Files.lines(storagePath)) {
      return streamOfLines
          .filter(line -> word.equals(line.split(",")[0]))
          .map(this::palindromeFromCsvLine)
          .filter(Objects::nonNull)
          .findFirst();
    } catch (IOException e) {
      log.error(ERROR_MESSAGE_TEMPLATE, storagePath, e.getMessage());
      return Optional.empty();
    } finally {
      fileLock.unlock();
    }
  }

  private Palindrome palindromeFromCsvLine(String line) {
    String[] cols = line.split(",");
    if (cols.length != 2) {
      log.error("Unexpected cols in line {} of file", line, storagePath);
      return null;
    }
    return new Palindrome(cols[0], Boolean.parseBoolean(cols[1]));
  }
}
