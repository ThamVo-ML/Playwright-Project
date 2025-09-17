package com.icon.sct.runner;

import java.io.File;
import java.net.URL;
import java.util.List;
import org.testng.TestNG;
import java.util.Optional;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.testng.xml.XmlTest;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import java.lang.reflect.Method;
import org.testng.xml.XmlInclude;
import com.icon.sct.utils.Utils;
import com.icon.sct.utils.ConfigReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class TestRunner {
    private static final String testsPackage = "com.icon.sct.tests";


    public static void main(String[] args) throws Exception {
        System.setProperty("allure.results.directory", ConfigReader.getGlobalVariable("allureResultsBaseDir"));
        setEnvironmentAndTrigger(ConfigReader.getGlobalVariable("configFilePath"));
        ConfigReader.loadProperties();
        if (Boolean.parseBoolean(System.getProperty("trigger", "false"))){
            System.out.printf("+++++   \n\t[TestRunner - %s] Start run all test suites from [test-config.json] with environment [%s]   \n+++++", 
                                Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), ConfigReader.getSystemEnvironment());
            System.setProperty("env", ConfigReader.getSystemEnvironment());
            List<TestSuiteConfig> suitesWithMethods = readConfigFromJson(ConfigReader.getGlobalVariable("configFilePath"));
            runTestBySuiteDetails(suitesWithMethods);
        }
        else{ //This condition ensures that only configFilePath or configGroupFilePath runs
            setEnvironmentAndTrigger(ConfigReader.getGlobalVariable("configGroupFilePath"));
            ConfigReader.loadProperties();
            if (Boolean.parseBoolean(System.getProperty("trigger", "false"))){
                System.out.printf("+++++   \n\t[TestRunner - %s] Start run all test suites from [test-config-group.json] with environment [%s]   \n+++++", 
                                    Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), ConfigReader.getSystemEnvironment());
                System.setProperty("env", ConfigReader.getSystemEnvironment());
                TestConfig suiteWithGroups = readConfigGroupsFromJson(ConfigReader.getGlobalVariable("configGroupFilePath"));
                System.out.println(suiteWithGroups.getSuites());
                runTestsByGroups(suiteWithGroups.getSuites());
            }
        }
    }

    private static void runTestBySuiteDetails(List<TestSuiteConfig> suites) throws Exception{
        List<XmlSuite> xmlSuites = new ArrayList<>();
        List<Integer> numberOfTestMethods = new ArrayList<>();
        List<List<String>> testMethodInfos = new ArrayList<>();
        for (TestSuiteConfig suiteConfig : suites) {
            List<String> testMethodInfo = new ArrayList<>();
            // Create suite and run with TestNG
            XmlSuite suite = new XmlSuite();
            // Set parallel execution with maximum 5 threads
            if (Boolean.parseBoolean(System.getProperty("parallelClass", "false"))){
                suite.setParallel(XmlSuite.ParallelMode.CLASSES);
                suite.setThreadCount(Math.min(suiteConfig.getClasses().size(), Integer.parseInt(ConfigReader.getGlobalVariable("maxParallelThreads"))));
            }
            suite.setName(suiteConfig.getSuiteName());
            XmlTest test = new XmlTest(suite);
            test.setName(suiteConfig.getSuiteName() + " Tests");
            List<XmlClass> classes = new ArrayList<>();
            int numberOfTestMethod = 0;
            for (TestClassConfig classConfig : suiteConfig.getClasses()) {
                String originalClassName = classConfig.getClassName();
                String resolvedClassName = originalClassName;
                try {
                    Class.forName(originalClassName);
                } catch (ClassNotFoundException e) {
                    List<String> foundClasses = getAllTestClasses(testsPackage);
                    Optional<String> correctClass = foundClasses.stream().filter(c -> c.endsWith("." + originalClassName.substring(originalClassName.lastIndexOf('.') + 1))).findFirst();
                    if (correctClass.isPresent()) {
                        resolvedClassName = correctClass.get();
                        classConfig.setClassName(resolvedClassName);
                    } else {
                        System.err.println("Class not found: " + originalClassName);
                        continue;
                    }
                }
                XmlClass xmlClass = new XmlClass(resolvedClassName);
                if (classConfig.getMethods().size() > 0) {
                    List<XmlInclude> methods = new ArrayList<>();
                    for (String methodName : classConfig.getMethods()) {
                        methods.add(new XmlInclude(methodName));
                        numberOfTestMethod += 1;
                    }
                    xmlClass.setIncludedMethods(methods);
                    testMethodInfo.add(String.format("\n\t\t - %s:\n\t\t\t+ %s", resolvedClassName, String.join("\n\t\t\t+ ", classConfig.getMethods())));
                }
                else { // Get all test methods in case methods of class from json file is []
                    try {
                        List<String> allTestMethods = new ArrayList<>();
                        Class<?> clazz = Class.forName(resolvedClassName);
                        for (Method method : clazz.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(org.testng.annotations.Test.class)) {
                                System.out.print("\nFound Test method : "+ method.getName());
                                allTestMethods.add(method.getName());
                                numberOfTestMethod += 1;
                            }
                        }
                        testMethodInfo.add(String.format("\n\t\t - %s:\n\t\t\t+ %s", resolvedClassName, String.join("\n\t\t\t+ ", allTestMethods)));
                    } catch (ClassNotFoundException e) {
                        System.err.println("Class not found: " + classConfig.getClassName());
                    }
                }
                classes.add(xmlClass);
            }
            test.setXmlClasses(classes);

            numberOfTestMethods.add(numberOfTestMethod);
            testMethodInfos.add(testMethodInfo);
            xmlSuites.add(suite);
        }
        TestNG testNG = new TestNG();
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------------\n");
        if (Boolean.parseBoolean(System.getProperty("parallelSuite", "false"))){
            String message = "";
            int numberOfTestCaseRunning = 0;
            List<String> suiteNames = new ArrayList<>();
            for (int i = 0; i < suites.size(); i++){
                numberOfTestCaseRunning += numberOfTestMethods.get(i);
                suiteNames.add(suites.get(i).getSuiteName());
                message += String.format("\n\t     * [%s] test case(s) of suite [%s]:%s", numberOfTestMethods.get(i), suites.get(i).getSuiteName(), String.join("", testMethodInfos.get(i)));
            }
            System.out.printf("*****   \n\t[TestRunner - %s] Executing Parallel Suites: %s   \n*****\n", 
                Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), message);
            testNG.setXmlSuites(xmlSuites);
            testNG.setSuiteThreadPoolSize(xmlSuites.size());
            testNG.run();
            System.out.printf("***   [TestRunner - %s] Completed execution for [%s] test case(s) of suite(s) %s   ***\n",
                        Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), numberOfTestCaseRunning, suiteNames);
            System.out.println("\n----------------------------------------------------------------\n");
        }
        else{
            for (int i = 0; i < suites.size(); i++){
                System.out.printf("*****   \n\t[TestRunner - %s] Executing [%s] test case(s) of suite [%s]:%s   \n*****\n", 
                        Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), numberOfTestMethods.get(i), suites.get(i).getSuiteName(), String.join("", testMethodInfos.get(i)));
                List<XmlSuite> suitesList = new ArrayList<>();
                suitesList.add(xmlSuites.get(i));
                testNG.setXmlSuites(suitesList);
                testNG.run();
                System.out.printf("***   [TestRunner - %s] Completed execution for [%s] test case(s) of suite: %s   ***\n", 
                    Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), numberOfTestMethods.get(i), suites.get(i).getSuiteName());
                System.out.println("\n----------------------------------------------------------------\n");
            }
        }
        System.out.printf("======   [TestRunner - %s] All suites have been executed successfully   ======\n", Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"));
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------------\n");
    }

    private static void runTestsByGroups(List<TestSuiteWithGroupsConfig> suitesWithGroups) throws Exception {
        List<XmlSuite> xmlSuites = new ArrayList<>();
        List<List<String>> listTestCaseNames = new ArrayList<>();
        for (TestSuiteWithGroupsConfig suiteConfig : suitesWithGroups) {
            List<String> testCaseNames = new ArrayList<>();
            XmlSuite suite = new XmlSuite();
            suite.setName(suiteConfig.getSuiteName());
            suite.setParallel(XmlSuite.ParallelMode.TESTS);
            XmlTest test = new XmlTest(suite);
            test.setName(suiteConfig.getSuiteName() + " Tests");
            List<XmlClass> classes = new ArrayList<>();
            List<String> allTestClassNames = getAllTestClasses(testsPackage);
            for (String className : allTestClassNames) {
                try {
                    Class<?> clazz = Class.forName(className);
                    XmlClass xmlClass = new XmlClass(className);
                    List<XmlInclude> methodsToInclude = getMethodsByGroups(clazz, suiteConfig.getGroups());
                    if (!methodsToInclude.isEmpty()) {
                        xmlClass.setIncludedMethods(methodsToInclude);
                        classes.add(xmlClass);
                        methodsToInclude.forEach(method -> testCaseNames.add(className + "." + method.getName()));
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Class not found: " + className);
                }
            }
            // Set parallel execution with maximum 5 threads
            if (Boolean.parseBoolean(System.getProperty("parallelClass", "false"))){
                suite.setParallel(XmlSuite.ParallelMode.CLASSES);
                suite.setThreadCount(Math.min(allTestClassNames.size(), Integer.parseInt(ConfigReader.getGlobalVariable("maxParallelThreads"))));
            }
            test.setXmlClasses(classes);
            listTestCaseNames.add(testCaseNames);
            xmlSuites.add(suite);
        }
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------------\n");
        TestNG testNG = new TestNG();
        if (Boolean.parseBoolean(System.getProperty("parallelSuite", "false"))){
            String message = "";
            int numberOfTestCaseRunning = 0;
            List<String> groups = new ArrayList<>();
            for (int i = 0; i < xmlSuites.size(); i++){
                groups.addAll(suitesWithGroups.get(i).getGroups());
                numberOfTestCaseRunning += listTestCaseNames.get(i).size();
                message += String.format("\n\t\t- [%s] test case(s) of suite [%s] with group is %s:\n\t\t\t- %s", listTestCaseNames.get(i).size(), 
                        suitesWithGroups.get(i).getSuiteName(), suitesWithGroups.get(i).getGroups(), String.join("\n\t\t\t- ", listTestCaseNames.get(i)));
            }
            System.out.printf("*****   \n\t[TestRunner - %s] Executing Parallel Suites: %s   \n*****\n", 
                Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), message);
            testNG.setXmlSuites(xmlSuites);
            testNG.setSuiteThreadPoolSize(xmlSuites.size());
            testNG.run();
            System.out.printf("***   [TestRunner - %s] Completed execution for [%s] test case(s) of group(s) %s   ***\n",
                        Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), numberOfTestCaseRunning, groups);
            System.out.println("\n----------------------------------------------------------------\n");
        }
        else{
            for (int i = 0; i < suitesWithGroups.size(); i++){
                System.out.printf(String.format("*****   \n\t[TestRunner - %s] Executing [%s] test case(s) of suite [%s] with group is %s: \n\t\t- %s \n*****\n", 
                        Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), listTestCaseNames.get(i).size(), suitesWithGroups.get(i).getSuiteName(), 
                        suitesWithGroups.get(i).getGroups(),  String.join("\n\t\t- ", listTestCaseNames.get(i))));
                List<XmlSuite> suitesList = new ArrayList<>();
                suitesList.add(xmlSuites.get(i));
                testNG.setXmlSuites(suitesList);
                testNG.run();
                System.out.printf("***   [TestRunner - %s] Completed execution for [%s] test case(s) of suite [%s]   ***\n",
                        Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"), listTestCaseNames.get(i).size(), suitesWithGroups.get(i).getSuiteName());
                System.out.println("\n----------------------------------------------------------------\n");
            }
        }
        System.out.printf("======   [TestRunner - %s] All suites have been executed successfully   ======\n", Utils.getCurrentTimeWithSpecificFormat("yy-MM-dd HH:mm:ss"));
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------------\n");
    }

    private static List<XmlInclude> getMethodsByGroups(Class<?> clazz, List<String> groups) {
        List<XmlInclude> includedMethods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(org.testng.annotations.Test.class)) {
                org.testng.annotations.Test testAnnotation = method.getAnnotation(org.testng.annotations.Test.class);
                String[] methodGroups = testAnnotation.groups();
                for (String group : groups) {
                    for (String methodGroup : methodGroups) {
                        if (methodGroup.equals(group)) {
                            includedMethods.add(new XmlInclude(method.getName()));
                            break;
                        }
                    }
                }
            }
        }
        return includedMethods;
    }

    private static List<String> getAllTestClasses(String packageName) throws Exception {
        List<String> classes = new ArrayList<>();
        URL packageUrl = Thread.currentThread().getContextClassLoader().getResource(packageName.replace('.', '/'));
        if (packageUrl == null) {
            throw new ClassNotFoundException("Package not found: " + packageName);
        }
        File directory = new File(packageUrl.toURI());
        if (!directory.exists() || !directory.isDirectory()) {
            throw new ClassNotFoundException("Package not found in the classpath: " + packageName);
        }
        findClassesInDirectory(directory, packageName, classes);
        return classes;
    }

    private static void findClassesInDirectory(File directory, String packageName, List<String> classes) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClassesInDirectory(file, packageName + "." + file.getName(), classes);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    classes.add(Class.forName(className).getName());
                }
            }
        }
    }

    public static void setEnvironmentAndTrigger(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(filePath));
            Boolean trigger = rootNode.path("trigger").asBoolean(false);
            String environment = rootNode.path("environment").asText("informal");
            Boolean parallelSuite = rootNode.path("parallelSuite").asBoolean(false);
            Boolean parallelClass = rootNode.path("parallelClass").asBoolean(false);
            System.setProperty("trigger", Boolean.toString(trigger));
            System.setProperty("env", environment);
            System.setProperty("parallelSuite", Boolean.toString(parallelSuite));
            System.setProperty("parallelClass", Boolean.toString(parallelClass));
            System.out.printf("***************** Configuration of [%s] *****************\n", Paths.get(filePath).getFileName().toString());
            System.out.println("\t\tTrigger: " + trigger);
            System.out.println("\t\tEnvironment: " + environment);
            System.out.println("\t\tRun Parallel Suites: " + parallelSuite);
            System.out.println("\t\tRun Parallel Classes of Suite: " + parallelClass);
            System.out.println("*****************************************************************************");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<TestSuiteConfig> readConfigFromJson(String filePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            ObjectMapper objectMapper = new ObjectMapper();
            TestSuiteConfigWrapper configWrapper = objectMapper.readValue(jsonContent, TestSuiteConfigWrapper.class);
            if (configWrapper == null || configWrapper.getSuites() == null) {
                throw new RuntimeException("Parsed JSON is null!");
            }
            return configWrapper.getSuites();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing JSON: " + e.getMessage());
        }
    }

    private static TestConfig readConfigGroupsFromJson(String filePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonContent, TestConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing JSON: " + e.getMessage());
        }
    }

    static class TestConfig {
        private boolean trigger;
        private String environment;
        private boolean parallelSuite;
        private boolean parallelClass;
        
        private List<TestSuiteWithGroupsConfig> suites;
    
        public boolean isTrigger() { 
            return trigger; 
        }

        public void setTrigger(boolean trigger) { 
            this.trigger = trigger; 
        }

        public boolean isParallelSuite() { 
            return parallelSuite; 
        }

        public void setParallelSuite(boolean parallelSuite) { 
            this.parallelSuite = parallelSuite; 
        }
    
        public boolean isParallelClass() { 
            return parallelClass; 
        }

        public void setParallelClass(boolean parallelClass) { 
            this.parallelClass = parallelClass; 
        }
    
        public String getEnvironment() { 
            return environment; 
        }

        public void setEnvironment(String environment) { 
            this.environment = environment; 
        }
    
        public List<TestSuiteWithGroupsConfig> getSuites() { 
            return suites; 
        }

        public void setSuites(List<TestSuiteWithGroupsConfig> suites) { 
            this.suites = suites; 
        }
    }
    
    static class TestSuiteConfigWrapper {
        private List<TestSuiteConfig> suites;
        private String environment;
        private boolean trigger;
        private boolean parallelSuite;
        private boolean parallelClass;
    
    
        public TestSuiteConfigWrapper() {}
        
        public String getEnvironment() {
            return environment;
        }
    
        public void setEnvironment(String environment) {
            this.environment = environment;
        }
    
        public Boolean getTrigger() {
            return trigger;
        }
    
        public void setTrigger(Boolean trigger) {
            this.trigger = trigger;
        }

        public Boolean getParallelSuite() {
            return parallelSuite;
        }
    
        public void setParallelSuite(Boolean parallelSuite) {
            this.parallelSuite = parallelSuite;
        }

        public Boolean getParallelClass() {
            return parallelClass;
        }
    
        public void setParallelClass(Boolean parallelClass) {
            this.parallelClass = parallelClass;
        }

        public List<TestSuiteConfig> getSuites() {
            return suites;
        }
    
        public void setSuites(List<TestSuiteConfig> suites) {
            this.suites = suites;
        }
    }

    static class TestSuiteConfig {
        private String suiteName;
        private List<TestClassConfig> classes;

        public TestSuiteConfig() {}
    
        public String getSuiteName() {
            return suiteName;
        }
    
        public List<TestClassConfig> getClasses() {
            return classes;
        }
    
        public void setSuiteName(String suiteName) {
            this.suiteName = suiteName;
        }
    
        public void setClasses(List<TestClassConfig> classes) {
            this.classes = classes;
        }
    }

    static class TestClassConfig {
        private String className;
        private List<String> methods;
    
        public TestClassConfig() {}
    
        public String getClassName() {
            return className;
        }
    
        public List<String> getMethods() {
            return methods;
        }
    
        public void setClassName(String className) {
            this.className = String.format("%s.%s", testsPackage, className);
        }
    
        public void setMethods(List<String> methods) {
            this.methods = methods;
        }
    }

    public static class TestSuiteWithGroupsConfig {
        private String suiteName;
        private List<String> groups;

        public TestSuiteWithGroupsConfig() {}

        public String getSuiteName() {
            return suiteName;
        }
    
        public void setSuiteName(String suiteName) {
            this.suiteName = suiteName;
        }
    
        public List<String> getGroups() {
            return groups;
        }
    
        public void setGroups(List<String> groups) {
            this.groups = groups;
        }
    }
}