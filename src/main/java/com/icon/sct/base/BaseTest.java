package com.icon.sct.base;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.icon.sct.factory.BrowserFactory;
import com.icon.sct.pages.HomePage;
import com.icon.sct.pages.LoginPage;
import com.icon.sct.pages.ProjectsPage;
import com.icon.sct.utils.Loggers;
import com.icon.sct.utils.PlaywrightActions;
import com.icon.sct.utils.Utils;

import io.qameta.allure.Step;


public class BaseTest {
    private ThreadLocal<HomePage> threadLocalHomePage = new ThreadLocal<>();
    private ThreadLocal<LoginPage> threadLocalLoginPage = new ThreadLocal<>();
    private ThreadLocal<ProjectsPage> threadLocalProjectsPage = new ThreadLocal<>();
    private static ThreadLocal<Loggers> threadLocalLogger = new ThreadLocal<>();
    private ThreadLocal<BrowserFactory> threadLocalBrowserFactory = new ThreadLocal<>();
    private static ThreadLocal<String> threadLocalCurrentPageName = new ThreadLocal<>();
    private static ThreadLocal<PlaywrightActions> threadLocalWebUI = new ThreadLocal<>();
    private static final ThreadLocal<String> threadLocalTestCaseName = new ThreadLocal<>();
    private static final ThreadLocal<String> threadLocalTestClassName = new ThreadLocal<>();
    private static final ThreadLocal<String> threadLocalTestSuiteName = new ThreadLocal<>();
    private static final AtomicBoolean isAllureCleaned = new AtomicBoolean(false);
    private static ThreadLocal<Integer> threadLocalNumberOfScreenshots = ThreadLocal.withInitial(() -> 0);
    private static ThreadLocal<Integer> threadLocalNumberOfTestCases = ThreadLocal.withInitial(() -> 0);
    private static ThreadLocal<String> threadLocalREQ = new ThreadLocal<>();
    private static ThreadLocal<Boolean> threadLocalNeedReplaceSuiteName = ThreadLocal.withInitial(() -> false);
    protected static final String defaultContextName = "Default";
    protected Loggers logger;
    protected HomePage homePage;
    protected LoginPage loginPage;
    protected ProjectsPage projectsPage;


    @BeforeSuite
    public void beforeSuite(){
        if (isAllureCleaned.compareAndSet(false, true)) {
            Utils.cleanupAllureResultsDirectory();
        }
    }

    @BeforeClass
    public void initBasePage(ITestContext context) {
        String testClassName = this.getClass().getName();
        testClassName = testClassName.substring(testClassName.lastIndexOf('.') + 1);
        threadLocalTestSuiteName.set(context.getSuite().getName());
        threadLocalTestClassName.set(testClassName);
        threadLocalNeedReplaceSuiteName.set(false);
        threadLocalNumberOfTestCases.set(0);
        //Create browser, page
        getBrowserFactory().createBrowser();
        createNewPageWithNewContext(defaultContextName);

        //Get page objects
        getPlaywrightActions();
        logger = getLogger();
        homePage = getHomePage();
        loginPage = getLoginPage();
        projectsPage = getProjectsPage();

        //Get REQ and SUITE of Test Class
        Class<?> clazz = this.getClass();
        try {
            Field field = clazz.getDeclaredField("REQ");
            field.setAccessible(true);
            threadLocalREQ.set((String) field.get(null));
            logger.info(String.format("REQ of Test Class [%s] is [%s]", clazz.getName(), threadLocalREQ.get()));
        } catch (Exception e) {
            logger.warn("Not found the REQ in the Test Class: " + clazz.getName());
        }
        if (getTestSuiteName().equals("Surefire suite")){
            try {
                Field suite = clazz.getDeclaredField("SUITE");
                suite.setAccessible(true);
                threadLocalTestSuiteName.set((String) suite.get(null));
                threadLocalNeedReplaceSuiteName.set(true);
                logger.info(String.format("SUITE of Test Class [%s] is [%s]", clazz.getName(), threadLocalREQ.get()));
            } catch (Exception e) {
                logger.warn("Using the Surefire suite because not found the SUITE in the Test Class: " + clazz.getName());
            }
        }
    }

    @BeforeMethod
    public void testSetup(ITestResult result) throws Exception{
        threadLocalNumberOfScreenshots.set(1);
        threadLocalNumberOfTestCases.set(threadLocalNumberOfTestCases.get() + 1);
        threadLocalTestCaseName.set(result.getMethod().getMethodName());
        Utils.cleanAndPrepareResultsDirectories();
        getBrowserFactory().startTracingAll();

        loginPage.signIn();
        homePage.verifyThatLoginSuccessfully();
    }

    @AfterMethod
    public void testTeardown(ITestResult result){
        switchToDefaultPage();
        // homePage.closeDialogIfExists();
        homePage.logout();

        if (result.getStatus() == ITestResult.FAILURE) { 
            getBrowserFactory().stopTracingAll();
        }
    }

    @AfterClass
    public void closeBrowserAndStopAllRecords() {
        getBrowserFactory().closeBrowserAndStopAllRecords();
        threadLocalCurrentPageName.remove();
        threadLocalBrowserFactory.remove();
        threadLocalLoginPage.remove();
        threadLocalHomePage.remove();
        threadLocalWebUI.remove();
        threadLocalLogger.remove();
    }

    @AfterSuite
    public synchronized void generateAllureReport(ITestContext context) throws InterruptedException{
        Utils.setUpEnvironment();
        Utils.generateAllureReport(context.getSuite().getName());
    }


    public static String getTestCaseName() {
        return threadLocalTestCaseName.get();
    }

    public static String getTestClassName() {
        return threadLocalTestClassName.get();
    }

    public static String getTestSuiteName() {
        return threadLocalTestSuiteName.get();
    }

    public static Boolean getNeedReplaceSuiteName() {
        return threadLocalNeedReplaceSuiteName.get();
    }

    public static PlaywrightActions getPlaywrightActionsObject() {
        return threadLocalWebUI.get();
    }

    public static String getCurrentPageName() {
        return threadLocalCurrentPageName.get();
    }

    public static int getNumberOfScreenshots(){
        int currentNumber = threadLocalNumberOfScreenshots.get();
        threadLocalNumberOfScreenshots.set(currentNumber + 1);
        return currentNumber;
    }

    public static int getNumberOfTestCases(){
        return threadLocalNumberOfTestCases.get();
    }

    public static String getREQ(){
        return threadLocalREQ.get();
    }


    // Client methods
    protected Loggers getLogger() {
        if (threadLocalLogger.get() == null) {
            threadLocalLogger.set(new Loggers());
        }
        return threadLocalLogger.get();
    }

    private PlaywrightActions getPlaywrightActions() {
        if (threadLocalWebUI.get() == null) {
            threadLocalWebUI.set(new PlaywrightActions(getBrowserFactory().getContext(defaultContextName), getBrowserFactory().getPage(defaultContextName)));
            threadLocalCurrentPageName.set(defaultContextName);
        }
        return threadLocalWebUI.get();
    }

    private BrowserFactory getBrowserFactory() {
        if (threadLocalBrowserFactory.get() == null) {
            threadLocalBrowserFactory.set(new BrowserFactory());
        }
        return threadLocalBrowserFactory.get();
    }

    protected LoginPage getLoginPage() {
        if (threadLocalLoginPage.get() == null) {
            threadLocalLoginPage.set(new LoginPage(threadLocalWebUI.get()));
        }
        return threadLocalLoginPage.get();
    }

    protected HomePage getHomePage() {
        if (threadLocalHomePage.get() == null) {
            threadLocalHomePage.set(new HomePage(threadLocalWebUI.get()));
        }
        return threadLocalHomePage.get();
    }

    
    protected ProjectsPage getProjectsPage() {
        if (threadLocalProjectsPage.get() == null) {
            threadLocalProjectsPage.set(new ProjectsPage(threadLocalWebUI.get()));
        }
        return threadLocalProjectsPage.get();
    }

    protected void createNewPageWithNewContext(String contextName) {
        getBrowserFactory().createPage(contextName);
    }

    protected void createNewPageWithGuestMode(String contextName) throws IOException {
        getBrowserFactory().createNewPageWithGuestMode(contextName);
    }

    protected void closePageAndContext(String contextName) throws IOException{
        getBrowserFactory().closePageAndContext(contextName);
    }

    @Step("Switch to [{contextName}] context")
    protected void switchToAnotherContext(String contextName){
        threadLocalWebUI.get().switchToSpecificContextWithFirstPage(getBrowserFactory().getContext(contextName));
        threadLocalCurrentPageName.set(contextName);
    }

    protected void switchToDefaultPage(){
        switchToAnotherContext(defaultContextName);
    }
}

