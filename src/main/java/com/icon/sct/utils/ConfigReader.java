package com.icon.sct.utils;

import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;


public class ConfigReader {
    private static final Properties environmentProperties = new Properties();
    private static final Properties globalVaribalesProperties = new Properties();
    private static final String DEFAULT_ENV = "informal";

    static {
        loadProperties();
    }
    

    public static void loadProperties() {
        String env = getSystemEnvironment();
        String environmentPropertiesFile = "src/main/resources/environments/" + env + ".properties";
        String globalVariablesPropertiesFile = "src/main/resources/globalVariables.properties";

        try (FileInputStream envFis = new FileInputStream(environmentPropertiesFile);
             FileInputStream configFis = new FileInputStream(globalVariablesPropertiesFile)) {
            environmentProperties.load(envFis);
            globalVaribalesProperties.load(configFis);
            System.out.println(String.format("----------------- Environment properties of the [%s] is loaded ----------------", env));
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties file for environment: " + env + ". Error: " + e.getMessage(), e);
        }
    }

    public static String getSystemEnvironment() {
        return System.getProperty("env", DEFAULT_ENV);
    }

    public static String getEnvironmentProperty(String key) {
        return environmentProperties.getProperty(key);
    }

    public static void setEnvironmentProperty(String key, String value) {
        environmentProperties.setProperty(key, value);
    }

    public static String getGlobalVariable(String key) {
        return globalVaribalesProperties.getProperty(key);
    }

    public static String getGlobalVariable(String key, String defaultValue) {
        return globalVaribalesProperties.getProperty(key, defaultValue);
    }

    public static void setGlobalVariable(String key, String value) {
        globalVaribalesProperties.setProperty(key, value);
    }

    public static void reloadProperties() {
        environmentProperties.clear();
        globalVaribalesProperties.clear();
        loadProperties();
    }
}