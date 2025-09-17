## Project structure
1. src\main\java\com\icon\sct:
    - base: Contains the BaseTest.java, which defines annotations for TestNG and manages the test lifecycle, browser, page, and page objects
    - factory (BrowserFactory.java): Contains all start and stop playwright, browser, context, page functions. Used to manage browser, context, and page of Playwright
    - locators: Contains locator classes for pages, which define values for the locators of the web UI
    - pages:
        * Note: Action methods should be marked as @Step (correspond with a step on the report)
        + BasePage.java: Contains all common functions that can used from other page objects
        + PageObject.java: Each Page will correspond to one class (We will have multiple page objects on the project). Contains all action methods of tbe page
    - utils:
        + ConfigReader.java: Contains methods to get the value of variables from properties file (defined as src\main\resources)
        + DateTime.java: Contains methods to handle DateTime
        + Locators.java: Contains methods to get and set locator information
        + Loggers.java: Contains some custom methods to handle logger for Java frameworks and Allure Report
        + PlaywrightActions.java: Contains all web actions functions (custom based on methods provided by Playwright) => Same with WebUI class of Katalon
        + RetryAnalyzer.java: Includes retry method to set the retry number for test cases
        + Utils.java: Contains all utility functions like get date time with the format, handle files (create, copy, delete), get directories, encrypt or decrypt string,...
        
2. src\main\resources:
    - environments: Contains properties files for environments
    - META-INF\services: The following files in the META-INF/services directory are part of the Service Provider Interface (SPI) mechanism in Java. They play an important role when you use Allure Report with custom listeners in your project. Make sure that the locations of all files using the listener interfaces are defined on these files
    - config.properties: Contains all global variables that can be used anywhere in the project

3. src\test\java\com\icon\sct:
    - listenners (TestListener.java): Contains methods that allow you to execute custom logic in different stages of the test case like: take screenshots after each Step, ...
    - runner: 
        + Contains suite config files (to run suites based on "classes and methods" or "groups")
        + TestRunner.java: Responsible for executing test cases using TestNG based on configuration files
    - tests: Where test classes are defined

4. src\test\resources:
    - suites: Contains central configuration files (.xml) that allows you to manage and adjust how TestNG runs tests
    + data: Contains test data files (Excel, Json,...)

5. result:
    - Test class folder: Contains latest screenshots, records, and traces for test cases of specific test class
    - reports: Contains Allure Reports in HTML format, which are generated after running the test suite. These report files are archived based on the execution date

6. target: Is the default output directory in a Maven project. It contains all compiled files, packaged artifacts, and generated reports

7. pom.xml: Is the configuration hub when you use Maven as a project and dependencies management tool (Manage dependencies, configuration plugin, define profile)

8. runner.bat: Is a batch file designed to build a Maven project and execute the TestRunner class



## Usage
1. For runing test cases without testng.xml (** Please comment the maven-surefire-plugin configuration at the end of the pom.xml file **)
```JS
    - mvn clean test -Dtest=TestClassName    (run specific test class)
    - mvn clean test -Dtest=LoginTest#methodName    (run specific test case)
    - mvn clean test -Denv=formal     (run test with specific environment)
    - mvn clean test -Dtest=LoginTest -Denv=formal     (run test class with specific environment)
```
2. For running test cases with testng.xml (** Please uncomment the maven-surefire-plugin configuration at the end of the pom.xml file **)
```JS
- mvn clean test -DsuiteXmlFile="src/test/resources/suites/testng-Suite1.xml"
- mvn clean test    (make sure that the ${suiteXmlFile} is configured for maven-surefire-plugin in the pom.xml => replace ${suiteXmlFile} to path of .xml file)
```
3. For runing multiple test suite with TestRunner 
```JS
    1 - You need to add path of aspectjweaver to launch.json file if you using the Visual Studio Code:
        From VSC select Run > Add Configuration... > add "vmArgs": "-javaagent:C:\\Users\\yourUserName\\.m2\\repository\\org\\aspectj\\aspectjweaver\\1.9.20.1\\aspectjweaver-1.9.20.1.jar", to the configurations
    2 - Update configuration for the test-config.json or test-config-group.json in the runner folder:
        + trigger: set true to enable to run and false to disable
        + environment: input environment that you want to run test suites
        + parallelSuite: set true if you want to run parallel with suite level
        + parallelClass: set true if you want to run parallel with class level for each suite (Can combine run parallel suite and class)
        + suites: Provide the suite information that you need to run (Please follow the format of that file)
    3 - Run the command "mvn clean package -DskipTests" from main folder to compile the project
    4 - Open "TestRunner.java" from "src\test\java\com\icon\sct\runner" and click to "Run" button to run tests
    5 - *** You can also use the runner.bat file in the main folder after updating the configuration for the suite config files *** (With this way, you can skip steps 1, 3, and 4)
```

## Project Workflow
```JS
    1. Receive a new ticket
    2. Create a new local branch from the develop branch
    (Make sure to pull first to get the latest version)
    3. Write test scripts for the ticket on the newly created branch until completion
        3.1. During implementation, if there are new commits in develop that include changes needed for your current ticket, follow these steps:
            - Stash all current changes
            - Switch to the develop branch
            - Pull the latest version
            - Switch back to the ticket branch
            - Merge develop into the ticket branch
            - Apply Stash into ticket branch
        → You will now have the latest code from develop in your ticket branch
    4. Commit all changes and push the local branch to the remote repository
    5. Create a pull request with:
        - Source: the ticket branch
        - Destination: the develop branch
        - Note: Add a clear title and description (Ex: "Add automation scripts for ticket SP0151-2686")
        5.1. If conflicts occur after creating the pull request, the branch owner must resolve them immediately
    6. Run the test scripts using the ticket branch and attach the report in the pull request for the reviewer to review
    7. After the informal review is completed, the reviewer will approve and merge the ticket's pull request into the develop branch
    8. Formal execution will be executed on the develop branch
 
    *** Branch naming convention:
        Writing new automation test scripts: SCRIPTING/SP0151-2686
        Maintaining or updating existing test scripts for a specific ticket: MAINTENANCE/SP0151-2686
```