package com.dzytsiuk.ioc.io;


import com.dzytsiuk.ioc.entity.BeanDefinition;
import com.dzytsiuk.ioc.exception.SourceParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {


    private static final SAXParserFactory SAX_PARSER_FACTORY = SAXParserFactory.newInstance();


    private List<String> contextFiles;
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();

    public XMLBeanDefinitionReader(String... path) {
        contextFiles = new ArrayList<>(Arrays.asList(path));
    }


    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        try {
            SAXParser saxParser = SAX_PARSER_FACTORY.newSAXParser();
            DefaultHandler beanDefinitionHandler = new BeanDefinitionHandler();
            for (String contextFile : contextFiles) {
                saxParser.parse(contextFile, beanDefinitionHandler);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new SourceParseException("Error parsing XML", e);
        }
        return beanDefinitions;
    }

    class BeanDefinitionHandler extends DefaultHandler {
        private static final String BEANS = "beans";
        private static final String BEAN = "bean";
        private static final String ID = "id";
        private static final String CLASS = "class";
        private static final String PROPERTY = "property";
        private static final String NAME = "name";
        private static final String VALUE = "value";
        private static final String REF = "ref";
        private boolean bbeans = false;

        private BeanDefinition beanDefinition = new BeanDefinition();

        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) {

            if (qName.equalsIgnoreCase(BEANS)) {
                bbeans = true;
            }

            if (qName.equalsIgnoreCase(BEAN)) {
                beanDefinition.setId(attributes.getValue(ID));
                beanDefinition.setBeanClassName(attributes.getValue(CLASS));
            }

            if (qName.equalsIgnoreCase(PROPERTY)) {
                String name = attributes.getValue(NAME);
                String value = attributes.getValue(VALUE);
                String ref = attributes.getValue(REF);
                if (ref != null) {
                    if (beanDefinition.getRefDependencies() != null) {
                        beanDefinition.getRefDependencies().put(name, ref);
                    } else {
                        beanDefinition.setRefDependencies(new HashMap<String, String>() {{
                            put(name, ref);
                        }});
                    }
                } else {
                    if (beanDefinition.getDependencies() != null) {
                        beanDefinition.getDependencies().put(name, value);
                    } else {
                        beanDefinition.setDependencies(new HashMap<String, String>() {{
                            put(name, value);
                        }});
                    }
                }
            }

        }

        public void endElement(String uri, String localName,
                               String qName) {
            if (qName.equals(BEAN)) {
                if (!bbeans) throw new SourceParseException("Root Element " + BEANS + " is not found");
                beanDefinitions.add(beanDefinition);
                beanDefinition = new BeanDefinition();
            }
        }
    }
}

