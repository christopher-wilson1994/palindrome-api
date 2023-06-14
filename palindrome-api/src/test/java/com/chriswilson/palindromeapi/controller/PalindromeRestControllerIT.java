package com.chriswilson.palindromeapi.controller;

import com.chriswilson.palindromeapi.api.request.PalindromeRequest;
import com.chriswilson.palindromeapi.config.CacheConfig;
import com.chriswilson.palindromeapi.entity.Palindrome;
import com.chriswilson.palindromeapi.repository.PalindromeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class PalindromeRestControllerIT {
  @Autowired PalindromeRepository palindromeRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private CacheManager cacheManager;

  @LocalServerPort private int port;

  static Stream<Arguments> palindromeTestData() {
    return Stream.of(Arguments.of("kayak", true), Arguments.of("world", false));
  }

  @ParameterizedTest
  @MethodSource("palindromeTestData")
  void checkPalindrome_should_calc_and_store(String word, boolean expected) throws Exception {
    given()
        .contentType(ContentType.JSON)
        .body(objectMapper.writeValueAsString(new PalindromeRequest("user", word)))
        .when()
        .post()
        .then()
        .statusCode(200);
    // verify cache
    boolean cacheValue =
        (boolean) cacheManager.getCache(CacheConfig.PALINDROME_CACHE).get(word).get();
    assertThat(cacheValue).isEqualTo(expected);
    // verify db
    Optional<Palindrome> result = palindromeRepository.findByWord(word);
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get())
        .matches(
            dbResult -> dbResult.getWord().equals(word) && dbResult.isPalindrome() == expected);
  }

  private RequestSpecification given() {
    return RestAssured.given().baseUri("http://localhost/api/palindrome").port(port);
  }
}
