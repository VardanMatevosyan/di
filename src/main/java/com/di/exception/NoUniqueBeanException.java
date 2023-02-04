package com.di.exception;

public class NoUniqueBeanException extends BeanException {

  public NoUniqueBeanException(String message) {
    super(message);
  }

  public NoUniqueBeanException(String message, Throwable cause) {
    super(message, cause);
  }
}
