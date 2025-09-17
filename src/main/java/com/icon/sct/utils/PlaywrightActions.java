package com.icon.sct.utils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.text.DecimalFormat;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import io.qameta.allure.model.StepResult;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.Locator.ClickOptions;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;


public class PlaywrightActions {
    private Page page = null;
    private Page previousPage = null;
    private Map<Integer, Integer> numberOfPages = new HashMap<>();
    private BrowserContext context;
    private Loggers logger = new Loggers();
    DecimalFormat timeFormat = new DecimalFormat("#.##");
    private int defaultTimeout = Integer.parseInt(ConfigReader.getGlobalVariable("defaultTimeout", "5"));


    public PlaywrightActions() { }

    public PlaywrightActions(BrowserContext context, Page page) {
        this.context = context;
        this.page = page;
        numberOfPages.put(context.hashCode(), 1);
    }
    
    public void switchToSpecificContextWithPageIndex(BrowserContext context, int pageIndex){
        this.page = context.pages().get(0);
        this.context = context;
        page.bringToFront();
        if (!numberOfPages.keySet().contains(page.context().hashCode())){
            numberOfPages.put(page.context().hashCode(), 1);
        }
    }

    public void switchToSpecificContextWithFirstPage(BrowserContext context){
        switchToSpecificContextWithPageIndex(context, 0);
    }

    public void switchToSpecificContextWithLastPage(BrowserContext context){
        switchToSpecificContextWithPageIndex(context, context.pages().size() - 1);
    }

    public void takeFullPageScreenshot() {
        Allure.getLifecycle().startStep(UUID.randomUUID().toString(), new StepResult().setName("Screenshot"));
        try{
            Path screenshotPath = Utils.getScreenshotPath();
            String imageName = screenshotPath.toString().substring(screenshotPath.toString().lastIndexOf('\\') + 1); 
            Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions().setFullPage(true).setPath(screenshotPath);
            Allure.addAttachment(imageName.replace(".png", ""), new ByteArrayInputStream(this.page.screenshot(screenshotOptions)));
            logger.objectInfo(String.format("A full page screenshot was taken - [%s]", imageName));
        } catch (Exception ex) {
            logger.failed(String.format("Take full page screenshot failed. Details : %s", ex));
        }
        finally{
            Allure.getLifecycle().stopStep();
        }
    }

    public void takeVisiblePageScreenshot() {
        Allure.getLifecycle().startStep(UUID.randomUUID().toString(), new StepResult().setName("Screenshot"));
        try{
            Path screenshotPath = Utils.getScreenshotPath();
            String imageName = screenshotPath.toString().substring(screenshotPath.toString().lastIndexOf('\\') + 1); 
            Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions().setPath(screenshotPath);
            Allure.addAttachment(imageName.replace(".png", ""), new ByteArrayInputStream(this.page.screenshot(screenshotOptions)));
            logger.objectInfo(String.format("A visible page screenshot was taken - [%s]", imageName));
        } catch (Exception ex) {
            logger.failed(String.format("Take visible page screenshot failed. Details : %s", ex));
        }
        finally{
            Allure.getLifecycle().stopStep();
        }
    }

    public void takeLocatorScreenshot(String locator) {
        Allure.getLifecycle().startStep(UUID.randomUUID().toString(), new StepResult().setName("Screenshot"));
        try{
            Path screenshotPath = Utils.getScreenshotPath();
            String imageName = screenshotPath.toString().substring(screenshotPath.toString().lastIndexOf('\\') + 1); 
            byte[] screenshotOptions = page.locator(locator).screenshot(new Locator.ScreenshotOptions().setPath(screenshotPath));
            Allure.addAttachment(imageName.replace(".png", ""), new ByteArrayInputStream(screenshotOptions));
            logger.objectInfo(String.format("A screenshot of Locator '%s' has been taken - [%s]", Locators.getLocatorName(), imageName));
        } catch (Exception ex) {
            logger.failed(String.format("Take screenshot of Locator '%s' failed. Details : %s", Locators.getLocatorName(), ex));
        }
        finally{
            Allure.getLifecycle().stopStep();
        }
    }

    public Path getRecordPath() {
        return page.video().path();
    }

    public void authenticate(String username, String password) {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        page.route("**/*", route -> {
            route.fetch(new Route.FetchOptions().setHeaders(Map.of("Authorization", "Basic " + encodedCredentials)));
        });
    }
    
    public void navigateToUrl(String url) throws Exception {
        page.navigate(url);
        if (url.contains("@rdaf")){
            logger.objectPassed(String.format("Navigated to URL '%s'", url.replaceAll("(https?://[^:]+):.*?@rdaf", "$1:********@rdaf")));
        }
        else{
            logger.objectPassed(String.format("Navigated to URL '%s'", url.replaceAll("(https?://[^:]+):.*?@", "$1:********@")));
        }
        waitForPageLoad();
    }

    public Locator findLocator(String locator){
        return page.locator(locator);
    }

    public List<Locator> findLocators(String locator){
        return page.locator(locator).all();
    }

    public Locator findLocatorFromLocator(Locator locator, String childLocator){
        return locator.locator(childLocator);
    }

    public String getPageTitle(){
        return page.title();
    }

    public String getCurrentURL(){
        return page.url();
    }

    public int getPageIndex() {
        List<Page> pages = context.pages();
        return pages.indexOf(page);
    }

    public void delay(int timeout){
        page.waitForTimeout(timeout * 1000);
    }

    public void delay(Double timeout){
        page.waitForTimeout(timeout * 1000);
    }

    public void switchToPageIndex(int index) {
        List<Page> pages = new ArrayList<>();
        for (int second = 0; second < 3; second ++){
            pages = context.pages();
            if (pages.size() > index){
                break;
            }
            page.waitForTimeout(1000);
        }
        if (index < 0 || index >= pages.size()) {
            logger.failed(String.format("Invalid page index. The number of total pages is '%s' but page index is '%s'", pages.size(), index));
        }
        previousPage = this.page;
        this.page = pages.get(index);
        logger.passed(String.format("Switched to page with index '%'", index));
    }

    public void switchToPageTitle(String title) {
        for (int second = 0; second < 3; second ++){ 
            for (Page page : context.pages()) {
                if (page.title().equals(title)) {
                    previousPage = this.page;
                    this.page = page;
                    page.bringToFront();
                    logger.passed(String.format("Switched to page with title '%'", title));
                    return;
                }
            }
            page.waitForTimeout(1000);
        }
        logger.failed(String.format("No page was found with title '%s' after '3' seconds", title));
    }

    public void switchToPageURL(String URL) {
        for (int second = 0; second < 3; second ++){ 
            for (Page page : context.pages()) {
                if (page.url().equals(URL)) {
                    previousPage = this.page;
                    this.page = page;
                    page.bringToFront();
                    logger.passed(String.format("Switched to page with URL '%'", URL));
                    return;
                }
            }
            page.waitForTimeout(1000);
        }
        logger.failed(String.format("No page was found with URL '%s' after '3' seconds", URL));
    }

    public void switchToPreviousPage() { 
        if (previousPage != null && page != previousPage){
            page = previousPage;
            page.bringToFront();
            previousPage = null;
            logger.passed("Switched to previous page");
        }
        else{
            logger.failed("There are no previous pages");
        }
    }

    public void switchToNewPage(boolean closeOldPage) {
        int currentNumberOfPages = numberOfPages.get(page.context().hashCode());
        List<Page> pages = new ArrayList<>();
        for (int second = 0; second < 3; second ++){ 
            pages = context.pages();
            if (pages.size() > currentNumberOfPages){
                break;
            }
            page.waitForTimeout(1000);
        }
        if (pages.size() == 1){
            logger.failed("No new pages was found after '3' seconds. Only one page is visible");
        }
        numberOfPages.put(page.context().hashCode(), currentNumberOfPages + 1);
        previousPage = this.page;
        this.page = pages.get(pages.size() - 1);
        page.bringToFront();
        logger.passed("Switched to new page");
        if (closeOldPage){
            previousPage.close();
            previousPage = null;
            numberOfPages.put(page.context().hashCode(), currentNumberOfPages);
        }
    }

    public void switchToNewPage(){
        switchToNewPage(false);
    }

    public void switchToNewPageAndClosePreviousPage(){
        switchToNewPage(true);
    }

    public void waitForLocatorVisible(String locator, int timeout) {
        long start = System.currentTimeMillis();
        try {
            page.waitForSelector(locator, new Page.WaitForSelectorOptions().setTimeout(timeout * 1000).setState(WaitForSelectorState.VISIBLE));
            double duration = (System.currentTimeMillis() - start) / 1000.0;
            logger.objectPassed(String.format("Locator '%s' is visible after '%.1f' seconds", Locators.getLocatorName(), duration));
        } catch (Exception ex) {
            logger.failed(String.format("Locator '%s' - [%s] is NOT visible after '%s' second(s). Details : %s", Locators.getLocatorName(), locator, timeout, ex));
        }
    }
    
    public void waitForLocatorVisible(String locator) {
        waitForLocatorVisible(locator, defaultTimeout);
    }

    public void verifyLocatorVisible(String locator, int timeout) {
        long start = System.currentTimeMillis();
        try {
            page.waitForSelector(locator, new Page.WaitForSelectorOptions().setTimeout(timeout * 1000).setState(WaitForSelectorState.VISIBLE));
            double duration = (System.currentTimeMillis() - start) / 1000.0;
            logger.passed(String.format("Locator '%s' is visible after '%.1f' seconds", Locators.getLocatorName(), duration));
        } catch (Exception ex) {
            logger.failed(String.format("Locator '%s' - [%s] is NOT visible after '%s' second(s). Details : %s", Locators.getLocatorName(), locator, timeout, ex));
        }
    }

    public void verifyLocatorVisible(String locator) {
        verifyLocatorVisible(locator, defaultTimeout);
    }

    public void waitForLocatorNotPresent(String locator, int timeout) {
        for (int second = 1; second <= timeout*10; second++) {
            if (page.locator(locator).count() == 0) {
                logger.passed(String.format("Locator '%s' is NOT present after '%s' seconds", Locators.getLocatorName(), second/10.0));
                return;
            }
            page.waitForTimeout(100);
        }
        logger.failed(String.format("Locator '%s' - [%s] still present after '%s' second(s)", Locators.getLocatorName(), locator, timeout));
    }

    public void waitForLocatorNotPresent(String locator) {
        waitForLocatorNotPresent(locator, defaultTimeout);
    }

    public boolean checkLocatorPresent(String locator, int timeout, boolean verifyPresent) {
        for (int second = 1; second <= timeout*10; second++) {
            if (page.locator(locator).count() > 0) {
                if (!verifyPresent){
                    logger.passed(String.format("Locator '%s' is present after '%s' seconds", Locators.getLocatorName(), second/10.0));
                    return true;
                }
                logger.objectInfo(String.format("Locator '%s' is present after '%s' seconds", Locators.getLocatorName(), second/10.0));
                return true;
            }
            page.waitForTimeout(100);
        }
        if (verifyPresent){
            logger.failed(String.format("Locator '%s' - [%s] is NOT present after '%s' second(s)", Locators.getLocatorName(), locator, timeout));
        }
        logger.objectInfo(String.format("Locator '%s' - [%s] is NOT present after '%s' second(s)", Locators.getLocatorName(), locator, timeout));
        return false;
    }

    public boolean checkLocatorPresent(String locator) {
        return checkLocatorPresent(locator, defaultTimeout);
    }

    public boolean checkLocatorPresent(String locator, int timeout) {
        return checkLocatorPresent(locator, timeout, false);
    }

    public void waitForLocatorPresent(String locator, int timeout) {
        checkLocatorPresent(locator, timeout, true);
    }

    public void waitForLocatorPresent(String locator) {
        waitForLocatorPresent(locator, defaultTimeout);
    }

    public boolean checkLocatorIsVisible(String locator, int timeout) {
        for (int second = 1; second <= timeout*10; second++) {
            if (page.locator(locator).isVisible()) {
                logger.objectInfo(String.format("Locator '%s' is visible after '%s' seconds", Locators.getLocatorName(), second/10.0));
                return true;
            }
            page.waitForTimeout(100);
        }
        logger.objectInfo(String.format("Locator '%s' - [%s] is NOT visible after '%s' seconds", Locators.getLocatorName(), locator, timeout));
        return false;
    }

    public boolean checkLocatorIsVisible(String locator) {
        return checkLocatorIsVisible(locator, defaultTimeout);
    }

    public void waitForLocatorNotVisible(String locator, int timeout) {
        long start = System.currentTimeMillis();
        try {
            page.waitForSelector(locator, new Page.WaitForSelectorOptions().setTimeout(timeout * 1000).setState(WaitForSelectorState.HIDDEN));
            double duration = (System.currentTimeMillis() - start) / 1000.0;
            logger.objectPassed(String.format("Locator '%s' is NOT visible after '%.1f' seconds", Locators.getLocatorName(), duration));
        } catch (Exception ex) {
            logger.failed(String.format("Locator '%s' - [%s] is visible after '%s' second(s). Details : %s", Locators.getLocatorName(), locator, timeout, ex));
        }
    }

    public void verifyLocatorNotVisible(String locator, int timeout) {
        long start = System.currentTimeMillis();
        try {
            page.waitForSelector(locator, new Page.WaitForSelectorOptions().setTimeout(timeout * 1000).setState(WaitForSelectorState.HIDDEN));
            double duration = (System.currentTimeMillis() - start) / 1000.0;
            logger.passed(String.format("Locator '%s' is NOT visible after '%.1f' seconds", Locators.getLocatorName(), duration));
        } catch (Exception ex) {
            logger.failed(String.format("Locator '%s' - [%s] is visible after '%s' second(s). Details : %s", Locators.getLocatorName(), locator, timeout, ex));
        }
    }

    public void verifyLocatorNotVisible(String locator) {
        verifyLocatorNotVisible(locator, defaultTimeout);
    }

    public boolean checkLocatorNotVisible(String locator, int timeout) {
        for (int second = 1; second < timeout*10; second++) {
            if (!page.locator(locator).isVisible()) {
                logger.objectInfo(String.format("Locator '%s' is NOT visible after '%s' seconds", Locators.getLocatorName(), second/10.0));
                return true;
            }
            page.waitForTimeout(100);
        }
        logger.objectInfo(String.format("Locator '%s' - [%s] is Vissible after '%s' seconds", Locators.getLocatorName(), locator, timeout));
        return false;
    }

    public String getText(String locator) {
        try {
            String text = page.locator(locator).textContent();
            logger.objectInfo(String.format("Text of Locator '%s' is: '%s'", Locators.getLocatorName(), text));
            return text;
        } catch (Exception ex) {
            logger.failed(String.format("Get Text of Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
            return null;
        }
    }

    public List<String> getAllTextContents(String locator) {
        try {
            waitForLocatorPresent(locator);
            List<String> allTextContents = page.locator(locator).allTextContents();
            allTextContents = allTextContents.stream().map(String::trim).collect(Collectors.toList());
            logger.objectInfo(String.format("All Text Contents of Locator '%s' is: '[%s]'", Locators.getLocatorName(), String.join(", ", allTextContents)));
            return allTextContents;
        } catch (Exception ex) {
            logger.failed(String.format("Get All Text Contents of Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
            return null;
        }
    }

    public int getNumberOfLocator(String locator) {
        try {
            int numberOfLocator = page.locator(locator).count();
            logger.objectInfo(String.format("Number of Locator '%s' is: '[%s]'", Locators.getLocatorName(), numberOfLocator));
            return numberOfLocator;
        } catch (Exception ex) {
            logger.failed(String.format("Get Number of Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
            return 0;
        }
    }

    public Map<String, Double> getSizeOfLocator(String locator) {
        try {
            Map<String, Double> locatorSize = new HashMap<>();
            BoundingBox box = page.locator(locator).boundingBox();
            if (box != null){
                locatorSize.put("width", box.width);
                locatorSize.put("height", box.height);
            }
            else{
                locatorSize.put("width", Double.parseDouble(getCssValue(locator, "width").replace("px", "")));
                locatorSize.put("height", Double.parseDouble(getCssValue(locator, "height").replace("px", "")));
            }
            logger.objectInfo(String.format("Size of Locator '%s' is: '[W=%s x H=%s]'", Locators.getLocatorName(), locatorSize.get("width"), locatorSize.get("height")));
            return locatorSize;
        } catch (Exception ex) {
            logger.failed(String.format("Get Size of Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
            return null;
        }
    }

    public String getCssValue(String locator, String cssName) {
        try {
            String cssValue = (String) page.locator(locator).evaluate(String.format("el => window.getComputedStyle(el).getPropertyValue('%s')", cssName));
            logger.objectInfo(String.format("The CSS '%s' of Locator '%s' is: '%s'", cssName, Locators.getLocatorName(), cssValue));
            return cssValue;
        } catch (Exception ex) {
            logger.failed(String.format("Get CSS value '%s' of Locator '%s' - [%s] failed. Details: %s", cssName, Locators.getLocatorName(), locator, ex));
            return null;
        }
    }

    public String getAttribute(String locator, String attributeName) {
        try {
            String attributeValue = page.locator(locator).getAttribute(attributeName);
            logger.objectInfo(String.format("The Attribute '%s' of Locator '%s' is: '%s'", attributeName, Locators.getLocatorName(), attributeValue));
            return attributeValue;
        } catch (Exception ex) {
            logger.failed(String.format("Get Attribute '%s' of Locator '%s' - [%s] failed. Details: %s", attributeName, Locators.getLocatorName(), locator, ex));
            return null;
        }
    }

    public String getInputValue(String locator) {
        try {
            String inputValue = (String) page.locator(locator).inputValue();
            logger.objectInfo(String.format("The input value of '%s' is: '%s'", Locators.getLocatorName(), inputValue));
            return inputValue;
        } catch (Exception ex) {
            logger.failed(String.format("Get input of '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
            return null;
        }
    }

    public String getProperty(String locator, String property) {
        try {
            String propertyValue = page.locator(locator).evaluate(String.format("element => element.%s", property)).toString();
            logger.objectInfo(String.format("The %s value of '%s' is: '%s'", property, Locators.getLocatorName(), propertyValue.toLowerCase()));
            return propertyValue.toLowerCase();
        } catch (Exception ex) {
            logger.failed(String.format("Get %s of '%s' - [%s] failed. Details: %s",  property, Locators.getLocatorName(), locator, ex));
            return null;
        }
    }

    public void clickIfExists(String locator, int timeout) {
        try {
            if (checkLocatorIsVisible(locator, timeout)) {
                click(locator);
            }
        } catch (Exception ex) {
            logger.failed(String.format("Click on Locator '%s' - [%s] if exists failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void enhancedClick(String locator) {
        try {
            page.locator(locator).evaluate("el => el.click()");
            logger.objectPassed(String.format("Locator '%s' is clicked (enhancedClick) on", Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Enhanced Click on Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void click(String locator){
        try {
            page.locator(locator).click();
            logger.objectPassed(String.format("Locator '%s' is clicked on", Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Click on Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void click(String locator, int timeout){
        try {
            ClickOptions options = new ClickOptions().setTimeout(timeout * 1000);
            page.locator(locator).click(options);
            logger.objectPassed(String.format("Locator '%s' is clicked on", Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Click on Locator '%s' - [%s] failed after '%s' second(s). Details: %s", Locators.getLocatorName(), locator, timeout, ex));
        }
    }

    public void doubleClick(String locator){
        try {
            page.locator(locator).dblclick();
            logger.objectPassed(String.format("Locator '%s' has been double-clicked", Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Double click on Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void rightClick(String locator){
        try {
            ClickOptions clickOptions = new ClickOptions().setButton(MouseButton.RIGHT);
            page.locator(locator).click(clickOptions);
            logger.objectPassed(String.format("Locator '%s' has been right-clicked", Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Right click on Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void clearText(String locator) {
        try {
            page.locator(locator).clear();
            logger.objectPassed(String.format("Text on Locator '%s' has been cleared",  Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Clear text on Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void setText(String locator, String text) {
        try {
            page.locator(locator).fill(text);
            logger.objectPassed(String.format("Text '%s' is set on Locator '%s'", text,  Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Set text on Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void setMaskedText(String locator, String text) {
        try {
            page.locator(locator).fill(text);
            logger.objectPassed(String.format("Text '%s' is set on Locator '%s'", "*".repeat(text.length()),  Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Set masked text on Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void setTextOneByOne(String locator, String text) {
        try {
            page.locator(locator).clear();
            page.locator(locator).click();
            for (char key : text.toCharArray()) {
                page.locator(locator).waitFor(new Locator.WaitForOptions().setTimeout(100));
                page.locator(locator).press(String.valueOf(key));
            }
            logger.objectPassed(String.format("Text '%s' is set on Locator '%s'", text,  Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Set text Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void setDatetime(String locator, String datetime) {
        try {
            page.locator(locator).click();
            page.keyboard().press("Control+A");
            page.locator(locator).press(datetime);
            logger.objectPassed(String.format("Datetime '%s' is set on Locator '%s'", datetime,  Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Set datetime on Locator '%s' - [%s] failed. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }


    // Methods that need to verify
    public int getNumberOfTotalOption(String selectLocator){
        try{
            int numberOfTotalOption = page.locator(selectLocator).locator("option").count();
            logger.objectInfo(String.format("Dropdown '%s' has '%s' option(s)", selectLocator, numberOfTotalOption));
            return numberOfTotalOption;
        }
        catch (Exception ex){
            logger.failed(String.format("Get total option number of Locator '%s' - [%s] failed. Details : %s", Locators.getLocatorName(), selectLocator, ex));
            return 0;
        }
    }

    public void selectOptionByValue(String locator, String value) {
        try {
            page.locator(locator).selectOption(new SelectOption().setValue(value));
            logger.objectPassed(String.format("Selected option by value '%s' from Locator '%s'", value, Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Failed to select option by value '%s' from Locator '%s' - [%s]. Details: %s", value, Locators.getLocatorName(), locator, ex));
        }
    }

    public void selectOptionByLabel(String locator, String label) {
        try {
            page.locator(locator).selectOption(new SelectOption().setLabel(label));
            logger.objectPassed(String.format("Selected option by label '%s' from Locator '%s'", label, Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Failed to select option by label '%s' from Locator '%s' - [%s]. Details: %s", label, Locators.getLocatorName(), locator, ex));
        }
    }
    
    public void selectOptionByIndex(String locator, int index) {
        try {
            page.locator(locator).selectOption(new SelectOption().setIndex(index));
            logger.objectPassed(String.format("Selected option by index '%s' from Locator '%s'", index, Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Failed to select option by index '%s' from Locator '%s' - [%s]. Details: %s", index, Locators.getLocatorName(), locator, ex));
        }
    }

    public void hover(String locator) {
        try {
            page.locator(locator).hover();
            logger.objectPassed(String.format("Hovered over Locator '%s'", Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Failed to hover over Locator '%s' - [%s]. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void scrollToLocator(String locator) {
        try {
            page.locator(locator).scrollIntoViewIfNeeded();
            logger.objectPassed(String.format("Scrolled to Locator '%s'", Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Failed to scroll to Locator '%s' - [%s]. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void pressKey(String locator, String key) {
        try {
            page.locator(locator).press(key);
            logger.objectPassed(String.format("Pressed key '%s' on Locator '%s'", key, Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Failed to press key '%s' on Locator '%s' - [%s]. Details: %s", key, Locators.getLocatorName(), locator, ex));
        }
    }

    public void check(String locator) {
        try {
            page.locator(locator).check();
            logger.objectPassed(String.format("Checked Locator '%s'", Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Failed to check Locator '%s' - [%s]. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void uncheck(String locator) {
        try {
            page.locator(locator).uncheck();
            logger.objectPassed(String.format("Unchecked Locator '%s'", Locators.getLocatorName()));
        } catch (Exception ex) {
            logger.failed(String.format("Failed to uncheck Locator '%s' - [%s]. Details: %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public Frame switchToFrame(String frameIdentifier) {
        Frame frame = page.frameByUrl(frameIdentifier);
        if (frame == null) {
            frame = page.frame(frameIdentifier);
        }
        frame = page.frame(frameIdentifier);
        if (frame == null) {
            for (Frame f : page.frames()) {
                String frameUrl = f.url();
                if (frameUrl.equals(frameIdentifier) || frameUrl.endsWith(frameIdentifier) || frameUrl.contains(frameIdentifier)) {
                    frame = f;
                    break;
                }
            }
        }
        if (frame != null) {

            return frame;
        }
        logger.failed("No frame found with identifier: " + frameIdentifier);
        return null;
    }

    public Object executeJavaScript(String script) {
        return page.evaluate(script);
    }

    public void acceptAlert(){
        try{
            page.onDialog(dialog -> dialog.accept());
            logger.objectPassed("Alert is accepted");
        }
        catch (Exception ex){
            logger.failed(String.format("Accept Alert failed. Details : %s", ex));
        }
    }

    public void dismisstAlert(){
        try{
            page.onDialog(dialog -> dialog.dismiss());
            logger.objectPassed("Alert is dismissed");
        }
        catch (Exception ex){
            logger.failed(String.format("Dismiss Alert failed. Details : %s", ex));
        }
    }

    public String getTextAlert(){
        try{
            final String[] alertText = {""};
            page.onDialog(dialog -> {
                alertText[0] = dialog.message();
                dialog.accept();
            });
            logger.objectInfo(String.format("Alert text is '%s'", alertText[0]));
            return alertText[0];
        }
        catch (Exception ex){
            logger.failed(String.format("Get Alert text failed. Details : %s", ex));
            return null;
        }
    }

    public void setAlertText(String text){
        try{
            page.onDialog(dialog -> dialog.accept(text));
            logger.objectPassed(String.format("Text '%s' is set on Alert", text));
        }
        catch (Exception ex){
            logger.failed(String.format("Set Alert text failed. Details : %s", ex));
        }
    }

    public void uploadFile(String locator, String filePath){
        try{
            page.locator(locator).setInputFiles(Paths.get(filePath));
            logger.objectPassed(String.format("File '%s' has been uploaded to Locator '%s'", filePath, Locators.getLocatorName()));
        }
        catch (Exception ex){
            logger.failed(String.format("Upload file to Locator '%s' - [%s] failed. Details : %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void verifyLocatorAttributeValue(String locator, String attribute, String expectedValue, int timeout) {
        try {
            page.locator(locator).waitFor(new Locator.WaitForOptions().setTimeout(timeout * 1000));
            String actualValue = page.locator(locator).getAttribute(attribute);
            if (expectedValue.equals(actualValue)) {
                logger.passed(String.format("Attribute '%s' of Locator '%s' is '%s'", attribute, Locators.getLocatorName(), actualValue));
            } else {
                logger.failed(String.format("Expected '%s' but found '%s' for attribute '%s' of Locator '%s'", expectedValue, actualValue, attribute, Locators.getLocatorName()));
            }
        } catch (Exception ex) {
            logger.failed(String.format("Locator '%s' - [%s] not found or does not have attribute '%s'. Details : %s", Locators.getLocatorName(), locator, attribute, ex));
        }
    }

    public void verifyLocatorAttributeValue(String locator, String attribute, String expectedValue) {
        verifyLocatorAttributeValue(locator, attribute, expectedValue, defaultTimeout);
    }

    public Boolean checkLocatorHasAttributeOrNot(String locator, String attribute, boolean checkHasAttribute, int timeout) {
        try {
            page.locator(locator).waitFor(new Locator.WaitForOptions().setTimeout(timeout * 1000));
            boolean hasAttribute = (Boolean) page.locator(locator).evaluate("(el, attr) => el.hasAttribute(attr)", attribute);
            if (hasAttribute && checkHasAttribute || !hasAttribute && !checkHasAttribute) {
                if (checkHasAttribute){
                    logger.objectInfo(String.format("Locator '%s' has attribute '%s'", Locators.getLocatorName(), attribute));
                }
                else{
                    logger.objectInfo(String.format("Locator '%s' has NO attribute '%s'", Locators.getLocatorName(), attribute));
                }
                return true;
            }
            if (checkHasAttribute){
                logger.objectInfo(String.format("Locator '%s' has NO attribute '%s'", Locators.getLocatorName(), attribute));
            }
            else{
                logger.objectInfo(String.format("Locator '%s' has attribute '%s'", Locators.getLocatorName(), attribute));
            }
            return false;
        } catch (Exception ex) {
            logger.error(String.format("Check Locator '%s' has attribute '%s' or not Failed. Details : %s", Locators.getLocatorName(), attribute, ex));
            return false;
        }
    }

    public Boolean checkLocatorHasAttributeOrNot(String locator, String attribute, boolean checkHasAttribute) {
        return checkLocatorHasAttributeOrNot(locator, attribute, checkHasAttribute, defaultTimeout);
    }

    public void verifyLocatorHasAttributeOrNot(String locator, String attribute, int timeout, boolean verifyHasAttribute) {
        try {
            for (int waitTime = 1; waitTime <= timeout*10; waitTime ++){
                boolean hasAttribute = (Boolean) page.locator(locator).evaluate("(el, attr) => el.hasAttribute(attr)", attribute);
                if (hasAttribute && verifyHasAttribute || !hasAttribute && !verifyHasAttribute) {
                    if (verifyHasAttribute){
                        logger.passed(String.format("Locator '%s' has attribute '%s' after '%s' second(s)", Locators.getLocatorName(), attribute, waitTime/10.0));
                        return;
                    }
                    else{
                        logger.passed(String.format("Locator '%s' has NO attribute '%s' after '%s' second(s)", Locators.getLocatorName(), attribute, waitTime/10.0));
                        return;
                    }
                }
                page.waitForTimeout(100);
            }
            if (verifyHasAttribute){
                logger.failed(String.format("Locator '%s' has NO attribute '%s' after '%s' second(s)", Locators.getLocatorName(), attribute, timeout));
            }
            logger.failed(String.format("Locator '%s' has attribute '%s' after '%s' second(s)", Locators.getLocatorName(), attribute, timeout));
        } catch (Exception ex) {
            logger.error(String.format("Verify Locator '%s' - [%s] has attribute '%s' or not failed. Details : %s", Locators.getLocatorName(), locator, attribute, ex));
        }
    }

    public void verifyLocatorHasAttribute(String locator, String attribute, int timeout) {
        verifyLocatorHasAttributeOrNot(locator, attribute, timeout, true);
    }

    public void verifyLocatorHasAttribute(String locator, String attribute) {
        verifyLocatorHasAttributeOrNot(locator, attribute, defaultTimeout, true);
    }

    public void verifyLocatorHasNoAttribute(String locator, String attribute, int timeout) {
        verifyLocatorHasAttributeOrNot(locator, attribute, timeout, false);
    }

    public void verifyLocatorHasNoAttribute(String locator, String attribute) {
        verifyLocatorHasAttributeOrNot(locator, attribute, defaultTimeout, false);
    }

    public boolean checkLocatorCheckedOrNot(String locator, boolean verifyChecked, int timeout) {
        try {
            page.locator(locator).waitFor(new Locator.WaitForOptions().setTimeout(timeout * 1000));
            boolean isChecked = page.locator(locator).isChecked();
            if (isChecked && verifyChecked || !isChecked && !verifyChecked) {
                if (verifyChecked){
                    logger.objectInfo(String.format("Locator '%s' is checked ", Locators.getLocatorName()));
                }
                else{
                    logger.objectInfo(String.format("Locator '%s' is unchecked", Locators.getLocatorName()));
                }
                return true;
            }
            if (verifyChecked){
                logger.objectInfo(String.format("Locator '%s' is unchecked ", Locators.getLocatorName()));
            }
            else{
                logger.objectInfo(String.format("Locator '%s' is checked", Locators.getLocatorName()));
            }
            return false;
        } catch (Exception ex) {
            logger.error(String.format("check Locator '%s' is checked or not Failed. Details : %s", Locators.getLocatorName(), ex));
            return false;
        }
    }

    public boolean checkLocatorCheckedOrNot(String locator, boolean verifyChecked) {
        return checkLocatorCheckedOrNot(locator, verifyChecked, defaultTimeout);
    }

    public void verifyLocatorCheckedOrNot(String locator, int timeout, boolean verifyChecked) {
        try {
            for (int waitTime = 1; waitTime <= timeout*10; waitTime ++){
                boolean isChecked = page.locator(locator).isChecked();
                if (isChecked && verifyChecked || !isChecked && !verifyChecked) {
                    if (verifyChecked){
                        logger.passed(String.format("Locator '%s' is checked after '%s' second(s)", Locators.getLocatorName(), waitTime/10.0));
                    }
                    else{
                        logger.passed(String.format("Locator '%s' is unchecked after '%s' second(s)", Locators.getLocatorName(), waitTime/10.0));
                    }
                }
                page.waitForTimeout(100);
            }
            if (verifyChecked){
                logger.failed(String.format(" Locator '%s' is unchecked after '%s' second(s)", Locators.getLocatorName(), timeout));
            }
            logger.failed(String.format(" Locator '%s' is checked after '%s' second(s)", Locators.getLocatorName(), timeout));
        } catch (Exception ex) {
            logger.error(String.format("Verify Locator '%s' - [%s] is checked or not failed. Details : %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void verifyLocatorChecked(String locator, int timeout) {
        verifyLocatorCheckedOrNot(locator, timeout, true);
    }

    public void verifyLocatorChecked(String locator) {
        verifyLocatorCheckedOrNot(locator, defaultTimeout, true);
    }

    public void verifyLocatorNotChecked(String locator, int timeout) {
        verifyLocatorCheckedOrNot(locator, timeout, false);
    }

    public void verifyLocatorNotChecked(String locator) {
        verifyLocatorCheckedOrNot(locator, defaultTimeout, false);
    }

    public boolean verifyOptionSelected(String locator, String expectedOption, boolean skipVerification) {
        try {
            String selectedLabel = page.locator(locator).evaluate("(el) => el.options[el.selectedIndex].textContent").toString().trim();
            if (!selectedLabel.equals(expectedOption)) {
                if (skipVerification){
                    logger.objectInfo(String.format("Expected selected option of locator '%s' is '%s' but found '%s' in '%s'", Locators.getLocatorName(), expectedOption, selectedLabel, locator));
                }
                else{
                    logger.failed(String.format("Expected selected option of locator '%s' is '%s' but found '%s' in '%s'", Locators.getLocatorName(), expectedOption, selectedLabel, locator));
                }
                return false;
            }
            if (skipVerification){
                logger.objectPassed(String.format("Option '%s' is selected in Locator '%s'", expectedOption, Locators.getLocatorName()));
            }
            else{
                logger.passed(String.format("Option '%s' is selected in Locator '%s'", expectedOption, Locators.getLocatorName()));
            }
            return true;
        } catch (Exception ex) {
            logger.failed(String.format("Failed to verify option '%s' selected in Locator '%s' - [%s]. Details: %s", expectedOption, Locators.getLocatorName(), locator, ex));
            return false;
        }
    }

    public void verifyOptionSelected(String locator, String expectedOption) {
        verifyOptionSelected(locator, expectedOption, false);
    }

    public boolean checkLocatorEnabledOrNot(String locator, boolean checkEnabled, int timeout) {
        try {
            page.locator(locator).waitFor(new Locator.WaitForOptions().setTimeout(timeout * 1000));
            if (page.locator(locator).isEnabled() && checkEnabled || page.locator(locator).isDisabled() && !checkEnabled) {
                if (checkEnabled){
                    logger.objectInfo(String.format("Locator '%s' is enabled", Locators.getLocatorName()));
                }
                else{
                    logger.objectInfo(String.format("Locator '%s' is disabled", Locators.getLocatorName()));
                }
            }
            if (checkEnabled){
                logger.objectInfo(String.format("Locator '%s' is disabled", Locators.getLocatorName()));
            }
            else{
                logger.objectInfo(String.format("Locator '%s' is enabled", Locators.getLocatorName()));
            }
            return false;
        } catch (Exception ex) {
            logger.error(String.format("Check Locator '%s' is enabled or not Failed. Details : %s", Locators.getLocatorName(), ex));
            return false;
        }
    }

    public boolean checkLocatorIsEnabled(String locator) {
        return checkLocatorEnabledOrNot(locator, true, defaultTimeout);
    }

    public boolean checkLocatorIsEnabled(String locator, int defaultTimeout) {
        return checkLocatorEnabledOrNot(locator, true, defaultTimeout);
    }

    public boolean checkLocatorIsDisabled(String locator) {
        return checkLocatorEnabledOrNot(locator, false, defaultTimeout);
    }

    public boolean checkLocatorIsDisabled(String locator, int defaultTimeout) {
        return checkLocatorEnabledOrNot(locator, false, defaultTimeout);
    }

    public void verifyLocatorEnabledOrNot(String locator, int timeout, boolean verifyEnabled) {
        try {
            // page.locator(locator).waitFor(new Locator.WaitForOptions().setTimeout(timeout * 1000));
            for (int waitTime = 1; waitTime <= timeout*10; waitTime ++){
                boolean isChecked = page.locator(locator).isEnabled();
                if (isChecked && verifyEnabled || !isChecked && !verifyEnabled) {
                    if (verifyEnabled){
                        logger.passed(String.format("Locator '%s' is enabled after '%s' second(s)", Locators.getLocatorName(), waitTime/10.0));
                    }
                    else{
                        logger.passed(String.format("Locator '%s' is disabled after '%s' second(s)", Locators.getLocatorName(), waitTime/10.0));
                    }
                }
                page.waitForTimeout(100);
            }
            if (verifyEnabled){
                logger.failed(String.format("Locator '%s' is disabled after '%s' second(s)", Locators.getLocatorName(), timeout));
            }
            else{
                logger.failed(String.format("Locator '%s' is enabled after '%s' second(s)", Locators.getLocatorName(), timeout));
            }
        } catch (Exception ex) {
            logger.error(String.format("Verify Locator '%s' - [%s] is enabled or not failed. Details : %s", Locators.getLocatorName(), locator, ex));
        }
    }

    public void verifyLocatorEnabled(String locator, int timeout) {
        verifyLocatorEnabledOrNot(locator, timeout, true);
    }

    public void verifyLocatorEnabled(String locator) {
        verifyLocatorEnabledOrNot(locator, defaultTimeout, true);
    }

    public void verifyLocatorDisabled(String locator, int timeout) {
        verifyLocatorEnabledOrNot(locator, timeout, false);
    }

    public void verifyLocatorDisabled(String locator) {
        verifyLocatorEnabledOrNot(locator, defaultTimeout, false);
    }

    public void waitForPageLoad(int timeout) {
        long start = System.currentTimeMillis();
        try {
            page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(timeout * 1000));
            page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(timeout * 1000));
            double duration = (System.currentTimeMillis() - start) / 1000.0;
            logger.objectInfo(String.format("Page is loaded after '%.1f' second(s)", duration));
        } catch (Exception ex) {
            logger.failed(String.format("Wait for page load failed. Details : %s", ex));
        }
    }

    public void waitForPageLoad() {
        waitForPageLoad(30);
    }

    public double getVerticalDistanceBetweenTwoLocators(String firstLocator, String secondLocator) {
        try {
			Locator locatorNo1 = findLocator(firstLocator);
			Locator locatorNo2 = findLocator(secondLocator);
            // Get the bounding box of both elements (includes padding and borders)
            BoundingBox box1 = locatorNo1.boundingBox();
            BoundingBox box2 = locatorNo2.boundingBox();
            if (box1 == null || box2 == null) {
                throw new Exception("One or both elements are not visible or rendered.");
            }
            // Get the computed styles to get the padding values (top and bottom padding)
            String paddingTopStr = (String) locatorNo1.evaluate("el => window.getComputedStyle(el).paddingTop");
            String paddingBottomStr = (String) locatorNo1.evaluate("el => window.getComputedStyle(el).paddingBottom");
            String paddingTopStr2 = (String) locatorNo2.evaluate("el => window.getComputedStyle(el).paddingTop");
            // Convert the padding values to integers
            double paddingTop1 = Integer.parseInt(paddingTopStr.replace("px", "").trim());
            double paddingBottom1 = Integer.parseInt(paddingBottomStr.replace("px", "").trim());
            double paddingTop2 = Integer.parseInt(paddingTopStr2.replace("px", "").trim());
            // Adjust the bounding box heights to exclude the padding
            double adjustedHeight1 = box1.height - paddingTop1 - paddingBottom1; // Height excluding padding
            double adjustedTop2 = box2.y + paddingTop2; // Top of the second element excluding its padding
            // Calculate the vertical space between the bottom of the first element and the top of the second element
            double verticalSpace = adjustedTop2 - (box1.y + adjustedHeight1);
            return verticalSpace >= 0 ? verticalSpace : 0; // Return 0 if elements overlap or no space
        } catch (Exception e) {
            System.err.println("Error calculating vertical space: " + e.getMessage());
            return -1;
        }
	}

    public void scrollElementToPosition(String locator, int x, int y) {
        page.locator(locator).evaluate(String.format("element => { element.scrollLeft += %s; element.scrollTop += %s; }", x, y));
    }

    public boolean areElementsVerticallyAligned(String firstLocation, String secondLocator) {
        // Get the bounding box of the first element
        BoundingBox bbox1 = findLocator(firstLocation).boundingBox();
        // Get the bounding box of the second element
        BoundingBox bbox2 = findLocator(secondLocator).boundingBox();
        // Check if bounding boxes are not null (element exists on the page)
        if (bbox1 != null && bbox2 != null) {
            // Compare the X coordinate of both elements (i.e., the left position)
            return Math.abs(bbox1.y - bbox2.y) < 10; // Allow some tolerance, e.g., 10px
        }
        return false;
    }

    public String getCssValueOfPseudoElement(String locator, String pseudoElement, String cssName) {
        try {
            //XPath, ::before or ::after, CSS attribute
            Object result = page.evaluate(
                "([locator, pseudoElement, cssName] ) => {" +
                "  const element = document.evaluate(locator, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                "  if (!element) return null;" +
                "  const style = window.getComputedStyle(element, pseudoElement);" +
                "  return style.getPropertyValue(cssName);" +
                "}",
                Arrays.asList(locator, pseudoElement, cssName)
            );
            logger.objectInfo(String.format("The CSS value '%s' of pseudo-element (%s) '%s' is %s", cssName, pseudoElement, Locators.getLocatorName(), result.toString()));
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            logger.failed(String.format("Get CSS value '%s' of pseudo-element (%s) '%s - [%s]' failed. Details: %s", cssName, pseudoElement, Locators.getLocatorName(), locator, e));
            return null;
        }
    }

    public void refresh() {
        try {
            page.reload();
            logger.objectPassed("Page is refreshed");
        } catch (Exception ex) {
            logger.failed(String.format("Refresh page failed. Details : %s", ex));
        }
    }

    public void enter(String locator){
        page.locator(locator).press("Enter");
    }
}