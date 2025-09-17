package com.icon.sct.pages;

import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter.Mode;
import com.icon.sct.utils.Utils;
import com.icon.sct.utils.ConfigReader;
import com.icon.sct.locators.LoginLocators;
import com.icon.sct.utils.PlaywrightActions;


public class LoginPage extends BasePage{

    public LoginPage(PlaywrightActions WebUI) {
        super(WebUI);
    }


    public void signIn(String email, String username, String password) throws Exception{
        String url = ConfigReader.getEnvironmentProperty("url");
        signIn(url, email, username, password);
    }

    public void signIn() throws Exception{
        String url = ConfigReader.getEnvironmentProperty("url");
        String email = ConfigReader.getEnvironmentProperty("email");
        String username = ConfigReader.getEnvironmentProperty("username");
        String password = ConfigReader.getEnvironmentProperty("password");
        signIn(url, email, username, password);
    }

    @Step("Signin to the SCT Application web page")
    public void signIn(String url, String email, String username, @Param(mode = Mode.MASKED) String password) throws Exception {
        if (Utils.isEncrypted(password)){
            password = Utils.decrypt(password);
        }
        WebUI.navigateToUrl(url);
        WebUI.waitForPageLoad();
        if (url.contains("sctdrun") || url.contains("sctuat2")){
            WebUI.setText(LoginLocators.LoginInput(), email);
            Utils.takeWindowsScreenshot();
            WebUI.click(LoginLocators.nextBtn());
            WebUI.waitForPageLoad();
            // WebUI.click(LoginLocators.continueBtn());
            // WebUI.setText(LoginLocators.email(), email);
            // WebUI.click(LoginLocators.nextBtn());
            // WebUI.delay(10);
            // String currentURL = WebUI.getCurrentURL();
            // WebUI.navigateToUrl(currentURL.replaceFirst("https://", String.format("https://%s:%s@", username, password)));
        }else {
            Utils.takeWindowsScreenshot();
            WebUI.click(LoginLocators.continueBtn());
            WebUI.waitForPageLoad();
        }
        WebUI.delay(10);
        logger.passed("Signed in to the SCT Application web page");
    }

    @Step("Signin to the Forward+ web page with Guest mode")
    public void signInWithGuestMode(String url, String email, String username, @Param(mode = Mode.MASKED) String password) throws Exception {
        if (Utils.isEncrypted(password)){
            password = Utils.decrypt(password);
        }
        WebUI.navigateToUrl(url);
        WebUI.setText(LoginLocators.email(), email);
        WebUI.click(LoginLocators.nextBtn());
        WebUI.delay(7);
        Utils.handleSTSAuthentication(username, password);
        logger.passed("Signed in to the Forward+ web page with Guest mode");
    }

    public void signInWithGuestMode(String email, String username, String password) throws Exception{
        String url = ConfigReader.getEnvironmentProperty("url");
        signInWithGuestMode(url, email, username, password);
    }

    public void signInWithGuestMode() throws Exception{
        String url = ConfigReader.getEnvironmentProperty("url");
        String email = ConfigReader.getEnvironmentProperty("email");
        String username = ConfigReader.getEnvironmentProperty("username");
        String password = ConfigReader.getEnvironmentProperty("password");
        signInWithGuestMode(url, email, username, password);
    }
}
