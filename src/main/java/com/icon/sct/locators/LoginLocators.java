package com.icon.sct.locators;

import com.icon.sct.utils.Locators;


public class LoginLocators{

    public static String email() {
        return Locators.create("//input[@type = 'email']");
    }

    public static String nextBtn() {
        return Locators.create("//input[@type = 'submit']");
    }

    public static String username() {
        return Locators.create("//input[@id= 'username']");
    }

    public static String password() {
        return Locators.create("//input[@name= 'password']");
    }

    public static String submitBtn() {
        return Locators.create("//button[@id= 'submit']");
    }

    public static String continueBtn() {
        return Locators.create("//input[@data-report-value='Submit']");
    }

    public static String LoginInput() {
        return Locators.create("//input[@type='email']");
    }

}
