package com.chriswilson.palindromeapi.startup;

import com.chriswilson.palindromeapi.config.CacheConfig;
import com.chriswilson.palindromeapi.entity.Palindrome;
import com.chriswilson.palindromeapi.service.PalindromeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheSyncTest {
  @Mock private PalindromeService palindromeService;

  @Mock private CacheManager cacheManager;

  @InjectMocks private CacheSync cacheSync;

  @Test
  void populateCache_does_not_exist() {
    when(cacheManager.getCache(CacheConfig.PALINDROME_CACHE)).thenReturn(null);
    cacheSync.populateCache(mock(ContextRefreshedEvent.class));
    verifyNoInteractions(palindromeService);
  }

  @Test
  void populateCache_adds_items_to_cache() {
    Cache palindromeCacheMock = mock(Cache.class);
    Palindrome p1 = new Palindrome("word1", false);
    Palindrome p2 = new Palindrome("word2", false);
    Page<Palindrome> page = new PageImpl<>(List.of(p1, p2));

    when(cacheManager.getCache(CacheConfig.PALINDROME_CACHE)).thenReturn(palindromeCacheMock);
    when(palindromeService.findAll(any(Pageable.class))).thenReturn(page);

    cacheSync.populateCache(mock(ContextRefreshedEvent.class));

    page.get().forEach(p -> verify(palindromeCacheMock).putIfAbsent(p.getWord(), p.isPalindrome()));
    verifyNoMoreInteractions(palindromeService, palindromeCacheMock);
  }
}
