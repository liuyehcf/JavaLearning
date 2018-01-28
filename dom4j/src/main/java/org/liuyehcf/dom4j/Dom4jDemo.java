package org.liuyehcf.dom4j;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dom4jDemo {
    public static final String FILE_PATH = "dom4j/src/main/resources/sample.xml";

    public static void main(String[] args) {
        writeXml();

        readXml();
    }

    private static void writeXml() {
        Document doc = DocumentHelper.createDocument();
        doc.addComment("a simple demo ");

        // 注意，xmlns只能在创建Element时才能添加，无法通过addAttribute添加xmlns属性
        Element beansElement = doc.addElement("beans", "http://www.springframework.org/schema/beans");
        beansElement.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        beansElement.addAttribute("xsi:schemaLocation", "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd");

        Element beanElement = beansElement.addElement("bean");
        beanElement.addAttribute("id", "sample");
        beanElement.addAttribute("class", "org.liuyehcf.dom4j.Person");
        beanElement.addComment("This is comment");

        Element propertyElement = beanElement.addElement("property");
        propertyElement.addAttribute("name", "nickName");
        propertyElement.addAttribute("value", "liuye");

        propertyElement = beanElement.addElement("property");
        propertyElement.addAttribute("name", "age");
        propertyElement.addAttribute("value", "25");

        propertyElement = beanElement.addElement("property");
        propertyElement.addAttribute("name", "country");
        propertyElement.addAttribute("value", "China");

        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileWriter(new File(FILE_PATH)), format);
            writer.write(doc);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readXml() {
        SAXReader saxReader = new SAXReader();

        Map<String, String> map = new HashMap<>();
        map.put("xmlns", "http://www.springframework.org/schema/beans");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);

        Document doc = null;
        try {
            doc = saxReader.read(new File(FILE_PATH));
        } catch (DocumentException e) {
            e.printStackTrace();
            return;
        }

        List list = doc.selectNodes("/beans/xmlns:bean/xmlns:property");
        System.out.println(list.size());

        list = doc.selectNodes("//xmlns:bean/xmlns:property");
        System.out.println(list.size());

        list = doc.selectNodes("/beans/*/xmlns:property");
        System.out.println(list.size());

        list = doc.selectNodes("//xmlns:property");
        System.out.println(list.size());

        list = doc.selectNodes("/beans//xmlns:property");
        System.out.println(list.size());

        list = doc.selectNodes("//xmlns:property/@value=liuye");
        System.out.println(list.size());

        list = doc.selectNodes("//xmlns:property/@*=liuye");
        System.out.println(list.size());

        list = doc.selectNodes("//xmlns:bean|//xmlns:property");
        System.out.println(list.size());

    }
}
