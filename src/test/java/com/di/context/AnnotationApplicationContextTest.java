package com.di.context;


import com.di.exception.NoSuchBeanException;
import com.di.exception.NoUniqueBeanException;
import com.di.models.Bmw;
import com.di.models.Car;
import com.di.models.Mercedes;
import com.di.models.Tesla;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AnnotationApplicationContextTest {

  private static final AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext(
      "com.di");


  @Test
  void test_whenGetAllBeans_thenCheckAllBeanPresent() {
    Set<String> expectedBeanNames = Set.of("bmw-car", "mercedes-car");
    String expectedBmwBeanFieldName = "BMW";

    Map<String, Car> allBeans = annotationApplicationContext.getAllBeans(Car.class);

    Assertions.assertNotNull(allBeans);
    Assertions.assertTrue(expectedBeanNames.containsAll(allBeans.keySet()));
    Assertions.assertEquals(expectedBmwBeanFieldName, allBeans.get("bmw-car").getName());
  }

  @Test
  void test_whenGetBeanByType_ThenCheckReturnedBeanIsExpectedBean() {
    String expectedBmwBeanFieldName = "Mercedes";

    Car car = annotationApplicationContext.getBean(Mercedes.class);

    Assertions.assertNotNull(car);
    Assertions.assertEquals(car.getClass(), Mercedes.class);
    Assertions.assertEquals(expectedBmwBeanFieldName, car.getName());
  }

  @Test
  void test_whenGetBeanByCommonType_ThenCheckNoUniqueBeanExceptionThrown() {
    NoUniqueBeanException exception = Assertions.assertThrows(
        NoUniqueBeanException.class,
        () -> annotationApplicationContext.getBean(Car.class));

    Assertions.assertEquals(exception.getMessage(),
        "There more then one bean with type com.di.models.Car");
  }

  @Test
  void test_whenGetBeanWithoutBeanAnnotation_ThenCheckNoSuchBeanExceptionThrown() {
    NoSuchBeanException exception = Assertions.assertThrows(
        NoSuchBeanException.class,
        () -> annotationApplicationContext.getBean(Tesla.class));

    Assertions.assertEquals(exception.getMessage(), "No bean with type com.di.models.Tesla");
  }

  @Test
  void test_whenGetBeanByBeanNameType_ThenCheckReturnedBeanIsExpectedBean() {
    String expectedBmwBeanFieldName = "BMW";

    Car car = annotationApplicationContext.getBean("bmw-car", Car.class);

    Assertions.assertNotNull(car);
    Assertions.assertEquals(car.getClass(), Bmw.class);
    Assertions.assertEquals(expectedBmwBeanFieldName, car.getName());
  }

  @Test
  void test_whenGetBeanByNameAndTypeWithoutBeanAnnotation_ThenCheckNoSuchBeanExceptionThrown() {
    NoSuchBeanException exception = Assertions.assertThrows(
        NoSuchBeanException.class,
        () -> annotationApplicationContext.getBean("tesla-car", Tesla.class));

    Assertions.assertEquals(exception.getMessage(), "No bean with type com.di.models.Tesla");
  }

}