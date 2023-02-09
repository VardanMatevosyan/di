package com.di.models;

import com.di.annotations.Bean;

@Bean("bmw-car")
public class Bmw extends Car {

  public Bmw() {
    setName("BMW");
  }
}
