package com.chriswilson.palindromeapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PalindromeRequestValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PalindromeRequestValid {
  String message() default "provided word is invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
