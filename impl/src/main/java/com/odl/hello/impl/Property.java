package com.odl.hello.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Property {
    private static Logger LOG = LoggerFactory.getLogger(Property.class);

    public Property() {
    }

    public static Map<String, String> getProperties(String fileName) {
        String fileFullName = System.getProperty("user.dir") + "/" + fileName;
        return readFile(fileFullName);
    }

    private static Map<String, String> readFile(String fileFullName) {
        HashMap map = new HashMap();

        try {
            FileInputStream inStream = new FileInputStream(fileFullName);
            Properties pros = new Properties();
            pros.load(inStream);
            Enumeration en = pros.propertyNames();

            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                map.put(key, pros.getProperty(key));
            }
        } catch (IOException var6) {
            LOG.error("Failed to read resource {}", var6);
        }

        return map;
    }
}
