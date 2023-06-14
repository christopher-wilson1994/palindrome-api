package com.chriswilson.palindromeapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class PalindromeRequestValidator
    implements ConstraintValidator<PalindromeRequestValid, String> {
  private static final Pattern alphaCharsPattern = Pattern.compile("[a-zA-Z]+");
  private static final Predicate<String> alphaCharsPredicate =
      value -> alphaCharsPattern.matcher(value).matches();
  private final List<Predicate<String>> validationRules;

  public PalindromeRequestValidator() {
    this.validationRules = new ArrayList<>();
    this.validationRules.add(alphaCharsPredicate);
    // add new validate rules here..

  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }
    return validationRules.stream().filter(pred -> pred.test(value)).count()
        == validationRules.size();
  }
}
