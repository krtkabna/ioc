package com.dzytsiuk.ioc.context;


import com.dzytsiuk.ioc.entity.Bean;
import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.BeanInstantiationException;
import com.dzytsiuk.ioc.exception.BeanNotFoundException;
import com.dzytsiuk.ioc.exception.DependencyInjectionException;
import com.dzytsiuk.ioc.exception.MultipleBeansForClassException;
import com.dzytsiuk.ioc.io.BeanDefinitionReader;
import com.dzytsiuk.ioc.io.XMLBeanDefinitionReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.dzytsiuk.ioc.context.cast.JavaNumberTypeCast.castPrimitive;

public class ClassPathApplicationContext implements ApplicationContext {
    private static final String SETTER_PREFIX = "set";
    private static final int SETTER_PARAMETER_INDEX = 0;

    private Map<String, Bean> beans;
    private BeanDefinitionReader beanDefinitionReader;

    public ClassPathApplicationContext() {

    }

    public ClassPathApplicationContext(String... path) {
        setBeanDefinitionReader(new XMLBeanDefinitionReader(path));
        start();
    }

    public void start() {
        beans = new HashMap<>();
        List<BeanDefinition> beanDefinitions = beanDefinitionReader.getBeanDefinitions();
        instantiateBeans(beanDefinitions);
        injectValueDependencies(beanDefinitions);
        injectRefDependencies(beanDefinitions);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        List<Object> beanList = beans.values().stream()
            .map(Bean::getValue)
            .filter(Objects::nonNull)
            .filter(bean -> clazz.equals(bean.getClass()))
            .collect(Collectors.toList());
        if (beanList.size() > 1) {
            throw new MultipleBeansForClassException("More than one bean found for class " + clazz.getName());
        }
        return (T) beanList.get(0);
    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        T bean = getBean(name);
        if (!bean.getClass().isAssignableFrom(clazz)) {
            throw new BeanInstantiationException("Could not instantiate bean of class " + clazz.getName());
        }
        return bean;
    }

    @Override
    public <T> T getBean(String name) {
        Bean bean = beans.get(name);
        if (bean != null) {
            return (T) bean.getValue();
        } else {
            throw new BeanNotFoundException("Could not find bean by name: " + name);
        }
    }

    @Override
    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        this.beanDefinitionReader = beanDefinitionReader;
    }

    private void instantiateBeans(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(this::instantiateBean);
    }


    private void instantiateBean(BeanDefinition beanDefinition) {
        try {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            String id = beanDefinition.getId();
            beans.put(id, new Bean(id, clazz));
        } catch (ClassNotFoundException e) {
            throw new BeanInstantiationException(
                "Could not find class for name: " + beanDefinition.getBeanClassName(), e);
        }
    }

    private void injectValueDependencies(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(beanDefinition ->
            injectDependencies(beanDefinition.getId(), beanDefinition.getDependencies()));
    }

    private void injectRefDependencies(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(beanDefinition ->
            injectDependencies(beanDefinition.getId(), beanDefinition.getRefDependencies()));
    }

    private void injectDependencies(String beanDefinitionId, Map<String, String> dependencies) {
        for (Map.Entry<String, String> property : dependencies.entrySet()) {
            Object bean = beans.get(beanDefinitionId).getValue();
            setFieldValues(bean, beanDefinitionId, property);
        }
    }

    private void setFieldValues(Object bean, String beanDefinitionId, Map.Entry<String, String> property) {
        String setterName = getSetterName(property.getKey());
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (setterName.equals(method.getName())) {
                Parameter parameter = method.getParameters()[SETTER_PARAMETER_INDEX];
                Class<?> parameterType = parameter.getType();
                String propertyValue = property.getValue();
                try {
                    if (parameterType.isPrimitive()) {
                        Object primitivePropertyValue = castPrimitive(propertyValue, parameterType);
                        method.invoke(bean, primitivePropertyValue);
                    } else {
                        method.invoke(bean, propertyValue);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new DependencyInjectionException("Could not inject dependencies for " + beanDefinitionId, e);
                }
            }
        }
    }

    private String getSetterName(String propertyName) {
        return SETTER_PREFIX + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }
}
