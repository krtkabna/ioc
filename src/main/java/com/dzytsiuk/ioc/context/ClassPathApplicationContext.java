package com.dzytsiuk.ioc.context;


import com.dzytsiuk.ioc.context.cast.JavaNumberTypeCast;
import com.dzytsiuk.ioc.entity.Bean;
import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.BeanInstantiationException;
import com.dzytsiuk.ioc.exception.BeanNotFoundException;
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
    }


    private void injectValueDependencies(List<BeanDefinition> beanDefinitions) {

    }

    private void injectRefDependencies(List<BeanDefinition> beanDefinitions) {

    }

    private String getSetterName(String propertyName) {
        return SETTER_PREFIX + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }
}
