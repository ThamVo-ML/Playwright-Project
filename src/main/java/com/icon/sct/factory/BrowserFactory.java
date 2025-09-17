package com.icon.sct.factory;

import java.util.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.qameta.allure.Step;
import com.microsoft.playwright.*;
import com.icon.sct.utils.Utils;
import com.icon.sct.utils.Loggers;
import com.icon.sct.utils.ConfigReader;


public class BrowserFactory {
    private Loggers logger = new Loggers();
    private Playwright playwright = null;
    private Browser browser;
    private Map<String, Page> pages = new HashMap<>();
    private Map<String, BrowserContext> contexts = new HashMap<>();
    private Map<String, Boolean> isTracingStarted = new HashMap<>();
    private Map<String, Path> recordPaths = new HashMap<>();
    private Map<String, Path> userDataDirs = new HashMap<>();


    @Step("Create new [{browserName}]")
    public Browser createBrowser(String browserName) {
        if (playwright == null){
            playwright = Playwright.create();
            logger.passed("Playwright has been created");
        }
        boolean headless = Boolean.parseBoolean(ConfigReader.getEnvironmentProperty("headless"));
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless).setArgs(Arrays.asList("--start-maximized"));
        if (browserName.equalsIgnoreCase("chromium")){
            browser = playwright.chromium().launch(launchOptions);
        }
        else if (browserName.equalsIgnoreCase("chrome")){
            browser = playwright.chromium().launch(launchOptions.setChannel("chrome"));
        }
        else if (browserName.equalsIgnoreCase("firefox")){
            browser = playwright.firefox().launch(launchOptions);
        }
        else if (browserName.equalsIgnoreCase("webkit")){
            browser = playwright.webkit().launch(launchOptions);
        }
        else{
            throw new IllegalArgumentException(String.format("Could not Launch Browser for type [%s]", browserName));
        }
        logger.passed(String.format("Create Browser with type [%s] successfully", browserName));
        System.setProperty("browserVersion", browser.version());
        return browser;
    }

    public Browser createBrowser() {
        return createBrowser(ConfigReader.getEnvironmentProperty("browser"));
    }

    @Step("Create new Page [{contextName}]")
    public void createPage(String contextName) {
        if (browser == null) {
            throw new IllegalArgumentException("The Browser must be created first");
        }
        // // Set view port size is the screen size or specific size
        // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // int screenWidth = screenSize.width;
        // int screenHeight = screenSize.height;
        // Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setIgnoreHTTPSErrors(true).setViewportSize(screenWidth, screenHeight);
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setIgnoreHTTPSErrors(true).setViewportSize(null)
        .setRecordVideoDir(Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"))).setRecordVideoSize(1280, 720);
        BrowserContext context = browser.newContext(contextOptions);
        contexts.put(contextName, context);
        // Auto start tracing
        startTracing(contextName);
        
        Page page = context.newPage();
        logger.passed(String.format("[Thread-%d] Create a new Page [%s - %s] from Browser with new Context (%s) successfully", Thread.currentThread().threadId(), contextName, context.hashCode(), contextName));
        pages.put(contextName, page);
    }

    @Step("Create new Page [{contextName}] with Guest mode")
    public void createNewPageWithGuestMode(String contextName) throws IOException {
        if (playwright == null){
            playwright = Playwright.create();
            logger.passed("Playwright has been created");
        }
        Path userDataDir = Paths.get(System.getProperty("java.io.tmpdir"), String.format("guest-profile-%s-%s", contextName, UUID.randomUUID()));
        BrowserType.LaunchPersistentContextOptions options = new BrowserType.LaunchPersistentContextOptions()
            .setHeadless(false)
            .setArgs(Arrays.asList("--guest", "--start-maximized"))
            .setViewportSize(null)
            .setChannel("chrome").setIgnoreHTTPSErrors(true).setViewportSize(null)
            .setRecordVideoDir(Paths.get(ConfigReader.getGlobalVariable("resultsDirectory"))).setRecordVideoSize(1280, 720);
        BrowserContext context = playwright.chromium().launchPersistentContext(userDataDir, options);
        userDataDirs.put(contextName, userDataDir);
        contexts.put(contextName, context);
        // Auto start tracing
        startTracing(contextName);
        Page page = context.pages().get(0);
        pages.put(contextName, page);
    }

    public Page getPage(String contextName) {
        return pages.get(contextName);
    }

    public BrowserContext getContext(String contextName){
        return contexts.get(contextName);
    }

    public void closeBrowserAndStopAllRecords() {
        for (Map.Entry<String, Page> entry : pages.entrySet()) {
            recordPaths.put(entry.getKey(), entry.getValue().video().path());
        }
        for (BrowserContext context : contexts.values()){
            for (Page page : context.pages()){
                page.close();
            }
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
        for (Map.Entry<String, Path> entry : recordPaths.entrySet()) {
            Utils.moveRecordFileToCorrectDirectory(entry.getValue(),  entry.getKey());
        }
        logger.passed("Close all pages and contexts of browser successfully");
        logger.passed("Close Playwright successfully");
    }

    public void startTracingAll(){
        for (String pageName : contexts.keySet()) {
            startTracing(pageName);
        }
    }

    public void stopTracingAll(){
        for (String pageName : contexts.keySet()) {
            stopTracing(pageName);
        }
    }

    public void startTracing(String contextName){
        if (!isTracingStarted(contextName)){
            getContext(contextName).tracing().start(new Tracing.StartOptions()
            .setScreenshots(true)
            .setSnapshots(true)
            .setSources(false) 
            );
            isTracingStarted.put(contextName, true);
        }
    }

    public void stopTracing(String contextName){
        if (isTracingStarted(contextName)){
            isTracingStarted.remove(contextName);
            getContext(contextName).tracing().stop(new Tracing.StopOptions().setPath(Utils.getTracePath(contextName)));
        }
    }

    private boolean isTracingStarted(String contextName) {
        return isTracingStarted.getOrDefault(contextName, false);
    }

    @Step("Close Page [{contextName}]")
    public void closePageAndContext(String contextName) throws IOException {
        Page page = getPage(contextName);
        Path recordPath = page.video().path();
        stopTracing(contextName);
        page.close();
        getContext(contextName).close();
        pages.remove(contextName);
        contexts.remove(contextName);
        Utils.moveRecordFileToCorrectDirectory(recordPath,  contextName);
        if (userDataDirs.keySet().contains(contextName)){
            Utils.deleteFolder(userDataDirs.get(contextName));
        }
    }
}