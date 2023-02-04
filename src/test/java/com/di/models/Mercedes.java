package com.di.models;

import com.di.annotations.Bean;

@Bean("mercedes-car")
public class Mercedes extends Car {

  public Mercedes() {
    setName("Mercedes");
  }
}
