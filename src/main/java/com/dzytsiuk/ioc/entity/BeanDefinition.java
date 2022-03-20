package com.dzytsiuk.ioc.entity;


import java.util.Map;
import java.util.Objects;

public class BeanDefinition {
    private String id;
    private String beanClassName;
    private Map<String, String> dependencies;
    private Map<String, String> refDependencies;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public Map<String, String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, String> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, String> getRefDependencies() {
        return refDependencies;
    }

    public void setRefDependencies(Map<String, String> refDependencies) {
        this.refDependencies = refDependencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanDefinition that = (BeanDefinition) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(beanClassName, that.beanClassName) &&
                Objects.equals(dependencies, that.dependencies) &&
                Objects.equals(refDependencies, that.refDependencies);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, beanClassName, dependencies, refDependencies);
    }
}
