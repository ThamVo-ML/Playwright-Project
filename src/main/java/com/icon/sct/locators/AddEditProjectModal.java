package com.icon.sct.locators;
import com.icon.sct.utils.Locators;

public class AddEditProjectModal {

    public static String addProjectHeader(){
        return Locators.create(String.format("//h5[text()='Add Project']"));
    }

    public static String selectBusinessLine(){
        return Locators.create(String.format("//select[@id ='drpBusinessLine']"));
    }

    public static String clickEnableSDBIntegration(){
        return Locators.create(String.format("//div[@id='praSafetyDatabaseSwitch']//label"));
    }

    public static String argusProjectCodeInput(){
        return Locators.create(String.format("//input[@name='inputArgusProject']"));
    }

    public static String findProjectCode(String value){
        return Locators.create(String.format("//div[@class='tt-suggestion tt-selectable' and text()='%s']", value));
    }

    public static String selectProjectCodeFormat(){
        return Locators.create(String.format("//select[@id='drpProjectCodeFormat']"));
    }

    public static String projectCodeInput(){
        return Locators.create(String.format("//input[@name='inputProjectCode']"));
    }

    public static String clientNameInput(){
        return Locators.create(String.format("//input[@name='inputClientName']"));
    }

    public static String altProjectCode(){
        return Locators.create(String.format("//input[@id='inputAltProjectCode']"));
    }

    public static String saveBtn(){
        return Locators.create(String.format("//button[@type ='submit' and text()='Save']"));
    }

    public static String searchBox(){
        return Locators.create(String.format("//input[@aria-controls='projectsTable']"));
    }

    public static String projectCodeLink(String projectCode){
        return Locators.create(String.format("//td/a[text()='%s']", projectCode));
    }

//////////////////////////protocolPage/////////////////////////////////////////
    public static String protocolHeader(){
        return Locators.create(String.format("//h5[contains(text(),'Protocol Management:')]"));
    }

    public static String protocolAddButton(){
        return Locators.create(String.format("//button[contains(@class,'add-protocol') and @name='Add']"));
    }

    public static String addProtocolHeader(){
        return Locators.create(String.format("//b[text()='Add Protocol ']"));
    }

    public static String selectProtocolType(){
        return Locators.create(String.format("//select[@id='dropdownProtocolType']"));
    }

    public static String argusProtocolNameInput(){
        return Locators.create(String.format("//input[@id='inputProto']"));
    }

    public static String argusProtocolSearchLink(String protocolCode){
        return Locators.create(String.format("//div[@class='tt-suggestion tt-selectable']/strong[text()='%s']", protocolCode));
    }

    public static String selectDocManagement(){
        return Locators.create(String.format("//select[@id='dropdownDocumentManagement']"));
    }

    public static String manualFolderPath(){
        return Locators.create(String.format("//input[@id='txtCaseFolderPath']"));
    }

    public static String selectContractType(){
        return Locators.create(String.format("//select[@id='dropdownContractType']"));
    }

    public static String integrationStartDateInput(){
        return Locators.create(String.format("//input[@id='txtIntegrationStartDate']"));
    }

    public static String queryManagementSwitch(){
        return Locators.create(String.format("//label[text()='Query Management Contracted']/parent::div//span"));
    }

    public static String protocolNameInput(){
        return Locators.create(String.format("//input[@id='txtProtocol']"));
    }

    public static String tmfFillingSwitch(){
        return Locators.create(String.format("//label[text()='TMF Filing Contracted']/parent::div//span"));
    }

    public static String successAlert(){
        return Locators.create(String.format("//div[contains(@class,'alert-success')]"));
    }
}
