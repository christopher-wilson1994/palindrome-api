package com.chriswilson.palindromeapi.startup;

import com.chriswilson.palindromeapi.config.CacheConfig;
import com.chriswilson.palindromeapi.entity.Palindrome;
import com.chriswilson.palindromeapi.service.PalindromeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheSync {

  private final PalindromeService palindromeService;
  private final CacheManager cacheManager;

  @EventListener
  public void populateCache(ContextRefreshedEvent event) {
    Cache palindromeCache = cacheManager.getCache(CacheConfig.PALINDROME_CACHE);
    if (palindromeCache != null) {
      log.info("Adding  entries to {} cache", CacheConfig.PALINDROME_CACHE);

      int pageNumber = 0;
      int pageSize = 2000;
      boolean hasMorePages = true;

      while (hasMorePages) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Palindrome> page = palindromeService.findAll(pageable);

        page.getContent().forEach(p -> palindromeCache.putIfAbsent(p.getWord(), p.isPalindrome()));

        hasMorePages = page.hasNext();
        pageNumber++;
      }

      log.info("Cache sync completed for cache {}", CacheConfig.PALINDROME_CACHE);
    } else {
      log.error("{} cache could not be found", CacheConfig.PALINDROME_CACHE);
    }
  }
}
