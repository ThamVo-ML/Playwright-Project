package com.icon.sct.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.icon.sct.locators.CommonLocators;
import com.icon.sct.utils.PlaywrightActions;

import io.qameta.allure.Step;


public class HomePage extends BasePage{
    private final List<String> pagesInConfiguration = Arrays.asList("Employee", "Manage Resources", "General Configuration", "Location Hours", "RM Configuration", "Availabilities", "Allocations");
    private final List<String> pagesInAdministration = Arrays.asList("Security Roles & Permissions");
    private final List<String> allMenuList = Arrays.asList("Home", "Projects", "Resources", "Demand", "Open Demand", "Administration", "Configuration", "Forecasting", "Reports");

    public HomePage(PlaywrightActions WebUI) {
        super(WebUI);
    }


    @Step("Verify that can login to the SCT Application web page successfully")
    public void verifyThatLoginSuccessfully() {
        // WebUI.waitForPageLoad();
        WebUI.delay(5);
        if (!WebUI.checkLocatorPresent(CommonLocators.sctHomePage(), 30)){
            logger.failed("Login on the SCT Application failed. The 'Home' navigation was not found after signed in");
        }
        logger.passed("Login on the SCT Application web page successfully");
    }

    @Step("Logout of the SCT Application web page")
    public void logout() {
        try{
            WebUI.click(CommonLocators.userNavigation());
            WebUI.click(CommonLocators.logOff());
            WebUI.waitForLocatorVisible(CommonLocators.reLogin(), 15);
            logger.passed("Logout of the SCT Application web page successfully");
        }
        catch (Exception e){
            logger.info(String.format("Logout of the SCT Application web page failed. Details : %s", e));
        }
    }

    @Step("Navigate to the [{pageName}] page")
    public void navigateToSpecificPage(String pageName) {
        String pageTitle = pageName;
        if (pagesInAdministration.contains(pageName)){
            if (!WebUI.checkLocatorIsVisible(CommonLocators.specificMenuNavigation(pageName), 1)){
                WebUI.click(CommonLocators.specificMenuExpansion("Administration"));
            }
        }
        else if (pagesInConfiguration.contains(pageName)){
            if (!WebUI.checkLocatorIsVisible(CommonLocators.specificMenuNavigation(pageName), 1)){
                WebUI.click(CommonLocators.specificMenuExpansion("Configuration"));
            }
        }

        WebUI.click(CommonLocators.specificMenuNavigation(pageName));
        if (pageName.toLowerCase().trim().equals("projects")){
            pageTitle = "Project Search";
        }

        else  if (pageName.toLowerCase().trim().equals("resources")){
            pageTitle = "Resource Search";
        }
        else  if (pageName.toLowerCase().trim().equals("open demand")){
            pageTitle = "Open Demands";
        }
        if (!pageName.toLowerCase().equals("home") && !WebUI.checkLocatorIsVisible(CommonLocators.specificPageTitle(pageTitle))){
            logger.failed(String.format("Navigate to '%s' page failed. The page title '%s' was not found", pageName, pageTitle));
        }
        waitForSpinnerToDisappearIfExists();
        logger.passed(String.format("Navigate to '%s' page successfully", pageName));
    }

    @Step("Verify the available menus in the left pane")
    public void verifyAvailableMenu(List<String> pagesName) { 
        logger.info("Check all pages should be displayed");
        for(String checkPage: pagesName){
            if(WebUI.checkLocatorIsVisible(CommonLocators.specificMenuNavigation(checkPage))){
                logger.passed(String.format("'%s' is displayed correctly", checkPage));                
            } else {
                logger.failed(String.format("'%s' should be displayed", checkPage));
            }            
        }
        List <String> unAvailableMenuList = new ArrayList<>(allMenuList);
        unAvailableMenuList.removeAll(pagesName);
        logger.info("Check all pages should NOT be displayed");
        for(String checkPage: unAvailableMenuList){            
            if(WebUI.checkLocatorNotVisible(CommonLocators.specificMenuNavigation(checkPage), 5)){
                logger.passed(String.format("'%s' is NOT displayed as expected", checkPage));
            } else {
                logger.failed(String.format("'%s' should NOT be displayed", checkPage));            
            }            
        }        
    }

    @Step("Verify the Unauthorized Page")
    public void verifyUnauthorizedPage(){
        if (!WebUI.checkLocatorPresent(CommonLocators.unauthorizePage())){
            logger.failed(String.format("This page is NOT Unauthorize page"));
        }
        logger.passed(String.format("This page is Unauthorize page"));
    }

    @Step("Verify the [{menuName}] is Not Visible")
    public void verifySpecificMenuIsNotVisible(String menuName) { 
        if(WebUI.checkLocatorIsVisible(CommonLocators.specificMenuNavigation(menuName), 2) || WebUI.checkLocatorIsVisible(CommonLocators.specificMenuExpansion(menuName), 2)){
            logger.failed(String.format("The '%s' menu is Visible", menuName));                
        }
        logger.passed(String.format("The '%s' menu is Not Visible", menuName));       
    }

    @Step("Navigate to [{URL}]")
    public void navigateToURL(String URL) throws Exception{
        WebUI.navigateToUrl(URL);
    }
}
