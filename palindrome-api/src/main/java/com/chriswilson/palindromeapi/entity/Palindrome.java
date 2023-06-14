package com.chriswilson.palindromeapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "palindrome")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Palindrome {
  @Column @Id private String word;
  @Column private boolean palindrome;
}
