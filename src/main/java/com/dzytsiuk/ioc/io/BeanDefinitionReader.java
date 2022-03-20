package com.dzytsiuk.ioc.io;


import com.dzytsiuk.ioc.entity.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {

    List<BeanDefinition> getBeanDefinitions();

}
