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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.dzytsiuk.ioc.context.cast.JavaNumberTypeCast.castPrimitive;

public class ClassPathApplicationContext implements ApplicationContext {
    private static final String SETTER_PREFIX = "set";

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
            .filter(bean -> (bean != null) && clazz.equals(bean.getClass()))
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
            Object object = Class.forName(beanDefinition.getBeanClassName()).getConstructor().newInstance();
            String id = beanDefinition.getId();
            beans.put(id, new Bean(id, object));
        } catch (ClassNotFoundException e) {
            throw new BeanInstantiationException("Could not find class for name: " + beanDefinition.getBeanClassName(), e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new BeanInstantiationException("Could not instantiate bean", e);
        }
    }

    private void injectValueDependencies(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(this::injectValueDependencies);
    }

    private void injectRefDependencies(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(this::injectRefDependencies);
    }

    private void injectValueDependencies(BeanDefinition beanDefinition) {
        Bean bean = beans.get(beanDefinition.getId());
        Map<String, String> dependencies = beanDefinition.getDependencies();
        injectDependencies(dependencies, bean);
    }

    private void injectRefDependencies(BeanDefinition beanDefinition) {
        Bean bean = beans.get(beanDefinition.getId());
        Map<String, String> refDependencies = beanDefinition.getRefDependencies();
        injectDependencies(refDependencies, bean);
    }

    private void injectDependencies(Map<String, String> dependencies, Bean bean) {
        if (dependencies != null) {
            dependencies
                .forEach(processDependency(bean));
        }
    }

    private BiConsumer<String, String> processDependency(Bean bean) {
        return (key, value) -> {
            try {
                Object beanValue = bean.getValue();
                Class<?> clazz = beanValue.getClass();
                Class<?> type = clazz.getDeclaredField(key).getType();
                Object valueObject = type.isPrimitive()
                    ? castPrimitive(value, type)
                    : isString(type) ? value : getBean(key);
                Method method = clazz.getMethod(getSetterName(key), type);
                method.invoke(beanValue, valueObject);
            } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new DependencyInjectionException("Could not inject value dependencies for bean " + bean.getId(), e);
            }
        };
    }

    private boolean isString(Class<?> type) {
        return String.class.isAssignableFrom(type);
    }

    private String getSetterName(String propertyName) {
        return SETTER_PREFIX + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }
}
