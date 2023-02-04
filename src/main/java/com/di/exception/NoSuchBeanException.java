package com.di.exception;

public class NoSuchBeanException extends BeanException {

  public NoSuchBeanException(String message) {
    super(message);
  }

  public NoSuchBeanException(String message, Throwable cause) {
    super(message, cause);
  }
}
