package com.icon.sct.tests;

import org.testng.annotations.Test;
import com.icon.sct.base.BaseTest;
import com.icon.sct.utils.RetryAnalyzer;
import com.icon.sct.utils.Utils;


public class SP0151_Test_Sample extends BaseTest {
    private static String REQ = "3.4.6.2";
    private static String SUITE = "SP0151-SCT2_3.4.6.2_UI_General_Layout_Of_Configuration_RM_Configurations";
    private String manageFunctionalResourceManagerTab = "Manage Functional Resource Manager";
    private String manageOpenDemandStatusTab = "Manage Open Demand Status";


    @Test(priority = 1, description = "[SP0151-2538] 3.4.6.2 TC01 UI - General Layout of Configuration --> RM Configurations --> Manage Functional Resource Manager", groups = {"SCT2 V1.6", "UI", "3.4.6.2"}, retryAnalyzer = RetryAnalyzer.class)
    void UI_General_Layout_Of_Configuration_RM_Configurations_For_Manage_Functional_Resource_Manager_Tab() throws Exception{
        String serviceLine = "IBT";
        String resourceGroup = "Clinical Delivery";
        String newResourcer = "Duong, Nghia";
        String existingResourcer = "Bass, Brooklyn";
        Boolean active = true;

        // logger.step("Navigate to the Configuration > RM Configuration > Manage Functional Resource Manager");
        //     homePage.navigateToSpecificPage("RM Configuration");
        //     rmConfigurationPage.navigateToSpecificTab(manageFunctionalResourceManagerTab, "RM Configuration");

        // logger.documentation("Figma for Manage Resource: https://www.figma.com/design/XS8jX8E7WMjEN0DH6smmND/FORWARD%2B-Resourcing---WIP?node-id=6663-152938&t=FbFFmexpCruv1US4-0");
        // logger.documentation("Specs: https://iconplc.box.com/s/w94l7ehci1ezgw3h0j66re5bfk8wkrl9");
        // logger.step("Verify the General Layout of Manage Functional Resource Manager tab in the RM Configuration page is as the design");
        //     rmConfigurationPage.verifyThatRouteContainerOfRMConfigurationPageIsDisplayedAsDesign("24px", "32px", "32px", "32px");
        //     rmConfigurationPage.verifyThatHeaderOfRMConfigurationPageIsDisplayedAsDesign(manageFunctionalResourceManagerTab, "RM Configuration", "Select column");
        //     rmConfigurationPage.verifyThatTableOfRMConfigurationPageIsDisplayedWithCorrectBackgroundColorAsDesign(manageFunctionalResourceManagerTab, "#D1E9F6", "#E8F4FA", "#FFFFFF", "#F8F8F8");
        //     rmConfigurationPage.verifyThatTableOfRMConfigurationPageIsDisplayedWithCorrectPaddingAsDesign(manageFunctionalResourceManagerTab, "40px", "16px", "8px");
        //     rmConfigurationPage.verifyThatTheDistanceBetweenItemsInTheHeaderOfSpecificTabAreCorrectAsDesign("8px", "24px", "16px");

        // logger.step("Verify the 'Add Resourcer' dialog in the Manage Functional Resource Manager tab of RM Configuration page is displayed as the design");
        //     rmConfigurationPage.verifyThatTheAddResourcerDialogIsDisplayedAsDesign();
        
        // logger.step("Verify the 'Duplicate' toast message will be dispalyed when add new Resourcer with existing data");
        //     logger.passed("This point will be implemented in another improvement ticket as discussed");
        // //     rmConfigurationPage.addNewResourcer(existingResourcer, serviceLine, resourceGroup, active);    
        // //     rmConfigurationPage.verifyToastMessage("Duplicate", "The Service Line / Resource Group / Resourcer already exists.");
        // //     homePage.closeDialogIfExists();

        // logger.step("Verify the 'Success' toast message will be dispalyed when add new Resourcer with new data");
        //     rmConfigurationPage.addNewResourcer(newResourcer, serviceLine, resourceGroup, active);    
        //     rmConfigurationPage.verifyToastMessage("Success", String.format("Resourcer %s was created.", newResourcer));

        // logger.step("Verify the 'Duplicate' toast message will be dispalyed when edit Resourcer with existing data");
        //     logger.passed("This point will be implemented in another improvement ticket as discussed");
        // //     rmConfigurationPage.editNewResourcer(existingResourcer, serviceLine, resourceGroup, active, 1);    
        // //     rmConfigurationPage.verifyToastMessage("Duplicate", "The Service Line / Resource Group / Resourcer already exists.");
        // //     homePage.closeDialogIfExists();

        // logger.step("Verify the 'Success' toast message will be dispalyed when edit Resourcer with new data");
        //     rmConfigurationPage.editNewResourcer(newResourcer, serviceLine, resourceGroup, !active, 1);    
        //     rmConfigurationPage.verifyToastMessage("Success", String.format("Resourcer %s was saved.", newResourcer));
        
        // logger.step("Verify the 'No Record Found' message is displayed when the table does not have any records");
        //     rmConfigurationPage.verifyNoRecordFoundMessageIsDisplayedWhenTheTableDoesNotHaveAnyRecords(manageFunctionalResourceManagerTab);

        // locationHoursPage.verifyBreadCrumbIsRemoved();
    }
    

    @Test(priority = 2, description = "[SP0151-2538] 3.4.6.2 TC02 UI - General Layout of Configuration --> RM Configurations --> Manage Open Demand Status", groups = {"SCT2 V1.6", "UI", "3.4.6.2"}, retryAnalyzer = RetryAnalyzer.class)
    void UI_General_Layout_Of_Configuration_RM_Configurations_For_Manage_Open_Demand_Status_Tab() throws Exception{
        String serviceLine = "IBT";
        String resourceGroup = "Clinical Delivery";
        String newStatus = String.format("Auto QA Status - %s", Utils.generateRandomNumber(1, 1000));
        String existingStatus = "Pending FM Input";
        Boolean active = true;

        // logger.step("Navigate to the Configuration > RM Configuration > Manage Open Demand Status");
        //     homePage.navigateToSpecificPage("RM Configuration");
        //     rmConfigurationPage.navigateToSpecificTab(manageOpenDemandStatusTab, "RM Configuration");

        // logger.documentation("Figma for Manage Open Demand Status: https://www.figma.com/design/XS8jX8E7WMjEN0DH6smmND/FORWARD%2B-Resourcing---WIP?node-id=6663-165701&t=FbFFmexpCruv1US4-0");
        // logger.documentation("Specs: https://iconplc.box.com/s/w94l7ehci1ezgw3h0j66re5bfk8wkrl9");
        // logger.step("Verify the General Layout of Manage Open Demand Status tab in the RM Configuration page is as the design");
        //     rmConfigurationPage.verifyThatRouteContainerOfRMConfigurationPageIsDisplayedAsDesign("24px", "32px", "32px", "32px");
        //     rmConfigurationPage.verifyThatHeaderOfRMConfigurationPageIsDisplayedAsDesign(manageOpenDemandStatusTab, "RM Configuration", "Select column");
        //     rmConfigurationPage.verifyThatTableOfRMConfigurationPageIsDisplayedWithCorrectBackgroundColorAsDesign(manageOpenDemandStatusTab, "#D1E9F6", "#E8F4FA", "#FFFFFF", "#F8F8F8");
        //     rmConfigurationPage.verifyThatTableOfRMConfigurationPageIsDisplayedWithCorrectPaddingAsDesign(manageOpenDemandStatusTab, "40px", "16px", "8px");
        //     rmConfigurationPage.verifyThatTheDistanceBetweenItemsInTheHeaderOfSpecificTabAreCorrectAsDesign("8px", "24px", "16px");

        // logger.step("Verify the 'Add Status' dialog in the Manage Open Demand Status tab of RM Configuration page is displayed as the design");
        //     rmConfigurationPage.verifyThatTheAddStatusDialogIsDisplayedAsDesign();
        
        // logger.step("Verify the 'Duplicate' toast message will be dispalyed when add new Status with existing data");
        //     logger.passed("This point will be implemented in another improvement ticket as discussed");
        //     // rmConfigurationPage.addNewStatus(existingStatus, serviceLine, resourceGroup, active);    
        //     // rmConfigurationPage.verifyToastMessage("Duplicate", "The Service Line / Resource Group / Resourcer already exists.");
        //     // homePage.closeDialogIfExists();

        // logger.step("Verify the 'Success' toast message will be dispalyed when add new Status with new data");
        //     rmConfigurationPage.addNewStatus(newStatus, serviceLine, resourceGroup, active);    
        //     rmConfigurationPage.verifyToastMessage("Success", String.format("Open Demand Status was created.", newStatus));

        // logger.step("Verify the 'Duplicate' toast message will be dispalyed when edit Status with existing data");
        //     logger.passed("This point will be implemented in another improvement ticket as discussed");
        //     // rmConfigurationPage.editStatus(existingStatus, serviceLine, resourceGroup, active, 1);    
        //     // rmConfigurationPage.verifyToastMessage("Duplicate", "The Service Line / Resource Group / Resourcer already exists.");
        //     // homePage.closeDialogIfExists();

        // logger.step("Verify the 'Success' toast message will be dispalyed when edit Status with new data");
        //     rmConfigurationPage.editStatus(newStatus, serviceLine, resourceGroup, !active, 1);    
        //     rmConfigurationPage.verifyToastMessage("Success", String.format("Open Demand Status was saved.", newStatus));

        // logger.step("Verify the 'No Record Found' message is displayed when the table does not have any records");
        //     rmConfigurationPage.verifyNoRecordFoundMessageIsDisplayedWhenTheTableDoesNotHaveAnyRecords(manageOpenDemandStatusTab);

        // locationHoursPage.verifyBreadCrumbIsRemoved();
    }
}