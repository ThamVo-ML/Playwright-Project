package com.icon.sct.locators;
import com.icon.sct.utils.Locators;

public class SCTBasePage {

    public static String addBtn(){
        return Locators.create(String.format("//button[@name='Add']"));
    }

    public static String successAlert(){
        return Locators.create(String.format("//div[contains(@class,'alert-success')]"));
    }

}
