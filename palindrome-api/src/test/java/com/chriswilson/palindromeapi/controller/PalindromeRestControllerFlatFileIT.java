package com.chriswilson.palindromeapi.controller;

import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Path;

@ActiveProfiles("local")
public class PalindromeRestControllerFlatFileIT extends PalindromeRestControllerIT {
  @TempDir private static Path tempPath;

  @DynamicPropertySource
  public static void testPropsSetup(DynamicPropertyRegistry registry) {
    registry.add("flatfile.storage.path", () -> tempPath.toString());
  }
}
