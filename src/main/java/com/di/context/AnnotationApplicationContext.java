package com.di.context;

import static java.util.stream.Collectors.toMap;

import com.di.annotations.Bean;
import com.di.exception.BeanException;
import com.di.exception.NoSuchBeanException;
import com.di.exception.NoUniqueBeanException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.reflections.Reflections;

public class AnnotationApplicationContext implements ApplicationContext {

  private static final Map<String, Object> rootContextMap = new ConcurrentHashMap<>();

  public AnnotationApplicationContext(String... packages) {
    Set<Class<?>> beanAnnotatedClasses = scan(packages);
    register(beanAnnotatedClasses);
  }

  @Override
  public <T> T getBean(Class<T> beanType) {
    Map<String, T> allBeans = getAllBeans(beanType);
    validateFoundBeans(beanType, allBeans);
    return allBeans.values().iterator().next();
  }

  @Override
  public <T> T getBean(String name, Class<T> beanType) {
    return Optional.ofNullable(rootContextMap.get(name))
        .map(beanType::cast)
        .orElseThrow(() -> new NoSuchBeanException("No bean with type " + beanType.getName()));
  }

  @Override
  public <T> Map<String, T> getAllBeans(Class<T> beanType) {
    return rootContextMap.entrySet().stream()
        .filter(entry -> beanType.isAssignableFrom(entry.getValue().getClass()))
        .collect(toMap(Map.Entry::getKey, entry -> beanType.cast(entry.getValue())));
  }

  private static <T> void validateFoundBeans(Class<T> beanType, Map<String, T> allBeans) {
    if (allBeans.isEmpty()) {
      throw new NoSuchBeanException("No bean with type " + beanType.getName());
    } else if (allBeans.size() > 1) {
      throw new NoUniqueBeanException("There more then one bean with type " + beanType.getName());
    }
  }

  private static Set<Class<?>> scan(Object packages) {
    Reflections reflections = new Reflections(packages);
    return reflections.getTypesAnnotatedWith(Bean.class);
  }

  private void register(Set<Class<?>> beanAnnotatedClasses) {
    for (Class<?> clazz : beanAnnotatedClasses) {
      Constructor<?> constructor = getConstructor(clazz);
      Object bean = createInstance(constructor);
      String beanName = resolveBeanName(clazz);
      registerBeans(beanName, bean);
    }
  }

  private void registerBeans(String beanName, Object bean) {
    if (Objects.isNull(bean)) {
      throw new BeanException("Can't register bean instance. Bean shoutld not be null");
    }
    Object beanObject = rootContextMap.get(beanName);
    if (Objects.nonNull(beanObject)) {
      throw new BeanException(String.format(
          "Couldn't register bean instance [%s] with bean name '%s': there is already object [%s] bound",
          bean, beanName, beanObject));
    }
    rootContextMap.put(beanName, bean);
  }

  private String resolveBeanName(Class<?> type) {
    return Optional.ofNullable(type.getAnnotation(Bean.class))
        .map(Bean::value)
        .or(() -> Optional.of(resolveUSimpleClassBeanName(type)))
        .orElseThrow(() -> new BeanException("Can't resolve unique bean name"));
  }

  private String resolveUSimpleClassBeanName(Class<?> type) {
    String simpleName = type.getSimpleName();
    return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
  }

  private Constructor<?> getConstructor(Class<?> clazz) {
    try {
      return clazz.getConstructor();
    } catch (NoSuchMethodException e) {
      throw new BeanException("No default constructor in the class annotated Bean.", e);
    }
  }

  private Object createInstance(Constructor<?> constructor) {
    try {
      return constructor.newInstance();
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new BeanException("Can't create bean instance.", e);
    }
  }
}
