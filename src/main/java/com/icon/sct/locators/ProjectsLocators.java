package com.icon.sct.locators;

import com.icon.sct.utils.Locators;


public class ProjectsLocators{

    public static String resourceHyperlink(String tableId, int rowIndex, int columnIndex) {
        return Locators.create(String.format("//*[contains(@id, '%s')]//tbody//*[@role = 'row'][%s]/td[%s]//a", tableId, rowIndex, columnIndex));
    }
    
    public static String specificDetailField(String fieldsname) {
        return Locators.create(String.format("//div[@class='grid-container']/div//*[@class='labels' and  text()='%s']",fieldsname));
    }

    public static String valueForSpecificDetailField(String fieldsname) {
        return Locators.create(String.format("//div[@class='grid-container']/div//*[@class='labels' and  text()='%s']/following-sibling::*[contains(@class,'value')]/span",fieldsname));
    }

    public static String tooltipIconSpecificDetailField(String fieldsname) {
        return Locators.create(String.format("//div[@class='grid-container']/div//*[@class='labels' and  text()='%s']/*[contains(@class,'tooltip')]",fieldsname));
    }

    public static String tooltipContent() {
        return Locators.create("//*[contains(@class,'mat-mdc-tooltip')]/div[contains(@class,'mdc-tooltip')]");
    }

    public static String specificCheckBoxOfSpecificResourceWithIndex(String resourceName, int index){
        return Locators.create(String.format("(//*[text() = '%s']/ancestor::tr//input[@type = 'checkbox'])[%s]", resourceName, index));
    }

    public static String specificCellOfSpecificResourceWithIndex(String tableID, int rowIndex, int columnIndex){
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//tbody//*[@role = 'row'][%s]/td[%s]/a[@id='resourceId']", tableID, rowIndex, columnIndex));
    }
}
