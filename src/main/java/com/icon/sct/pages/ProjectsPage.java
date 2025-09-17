package com.icon.sct.pages;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.Map;

import com.icon.sct.locators.AddEditProjectModal;
import com.icon.sct.locators.CommonLocators;
import com.icon.sct.locators.ProjectsLocators;
import com.icon.sct.locators.SCTBasePage;
import com.icon.sct.utils.DateTime;
import com.icon.sct.utils.DbUtils;
import com.icon.sct.utils.ExcelUtils;
import com.icon.sct.utils.PlaywrightActions;
import com.icon.sct.utils.Utils;
import java.util.regex.Pattern;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import oracle.jdbc.proxy.annotation.Methods;
import oracle.jdbc.proxy.annotation.Signature;


public class ProjectsPage extends BasePage{
    private String projectSearchTableID = "projectSearch";
    private String assignmentsTableID = "project-assignment";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public ProjectsPage(PlaywrightActions WebUI) {
        super(WebUI);
    }

    public void quickSearchProject(String searchType, String searchValue) {
        WebUI.click(CommonLocators.specificTab("Quick"));
        WebUI.delay(2);
        selectOptionFromDropdown(CommonLocators.searchTypeBox(), searchType);
        WebUI.setText(CommonLocators.specificSearchField("Enter a Project ID"), searchValue);
        Utils.takeWindowsScreenshot();
        WebUI.click(CommonLocators.actionBtn("Search"));
        WebUI.waitForLocatorVisible(CommonLocators.specificTableColumns(projectSearchTableID));
        logger.passed(String.format("Search Project '%s : %s' successfully", searchType, searchValue));
    }

    @Step("Verify Option State in Project")
    public void verifyOptionStateInProject(String locator, String attribute, String expectedValue, String locatorName, String expectedColor) {
        WebUI.verifyLocatorAttributeValue(locator, attribute, expectedValue);
        verifyCSSValueOfLocatorIsDisplayedCorrectly("background-color", locator, locatorName, expectedColor);
    }

    @Step("Navigate to Project Details page of Project ID [{projectID}]")
    public void navigateToProjectDetailsPageOfSpecificProjectID(String projectID) {
        quickSearchProject("ProjectID", projectID);
        WebUI.click(CommonLocators.viewButtonOfSpecificRowIndexInSpecificTable(projectSearchTableID, 1));
        WebUI.switchToNewPageAndClosePreviousPage();
        WebUI.waitForLocatorVisible(CommonLocators.specificPageTitle(String.format("Project Details: %s", projectID)));
        logger.passed(String.format("Navigated to Project Details page of Project ID '%s'", projectID));
    }

    @Step("Navigate to [{tab}] tab on Project Details Page")
    public void navigateToSpecificTabOnProjectDetailsPage(String tab, Boolean clickViewDetails) {
        if(clickViewDetails){
            WebUI.click(CommonLocators.viewButtonOfSpecificRowIndexInSpecificTable(projectSearchTableID, 1));
            WebUI.delay(3);
            WebUI.switchToNewPageAndClosePreviousPage();
        }
        WebUI.click(CommonLocators.specificTab(tab));
        waitForSpinnerToDisappearIfExists();
        logger.passed(String.format("Navigated to '%s' tab on Project Details page", tab));
    }

    @Step("Verify hyperlink on Project Details page")
    public void verifyHyperlinkOnProjectDetailsPage(int index, String state, String expectedColor) {
        if (state.equals("disabled")){
            if(WebUI.checkLocatorHasAttributeOrNot(ProjectsLocators.resourceHyperlink(assignmentsTableID, index, 4), "class",true)) {
                logger.passed(String.format("The Locator has attribute '%s'", state));
            } else {
                logger.failed(String.format("The Locator does not have attribute '%s'", state));
            }
            WebUI.verifyLocatorAttributeValue(ProjectsLocators.resourceHyperlink(assignmentsTableID, index, 4), "class", "navigation-link disabled-link");
        } else {
            if(WebUI.checkLocatorHasAttributeOrNot(ProjectsLocators.resourceHyperlink(assignmentsTableID, index, 4), "class",false)){
                logger.passed(String.format("The Locator is '%s'", state));
            } else {
                logger.failed(String.format("The Locator is NOT '%s'", state));
            }            
        }
        verifyCSSValueOfLocatorIsDisplayedCorrectly("background-color", ProjectsLocators.resourceHyperlink(assignmentsTableID, index, 4), "Hyperlink resource in Project Details", expectedColor);
    }

    @Step("Verify Assignment is displayed with correct infomation '{assignmentInfo}'")
    public void verifyAssignmentIsDisplayedWithCorrectInfomation(Map<String, String> assignmentInfo, boolean skipVerifyIfNoRecordFound) {
        navigateToSpecificTabOnProjectDetailsPage("Assignments", false);
        WebUI.uncheck(CommonLocators.specificCheckbox("Active Assignments"));
        waitForSpinnerToDisappearIfExists();
        Utils.takeWindowsScreenshot();
        for (Map.Entry<String, String> info : assignmentInfo.entrySet()) {
            String filterField = info.getKey();
            if (info.getKey().toLowerCase().equals("project role")){
                filterField = "Role";
            }
            else if (info.getKey().toLowerCase().equals("service line")){
                filterField = "Resource Service Line";
            }
            filterOnTable(assignmentsTableID, filterField, info.getValue());
        }
        if (WebUI.checkLocatorPresent(CommonLocators.noRecordFoundMessageOfSpecificTable(assignmentsTableID), 1)){
            if(!skipVerifyIfNoRecordFound){
                Utils.takeWindowsScreenshot();
                logger.failed(String.format("Not found any Assignments with information '%s'. Skip verifycation", assignmentInfo));
            }
            Utils.takeWindowsScreenshot();
            logger.info(String.format("Not found any Assignments with information '%s'", assignmentInfo));
        }
        else{
            int numberOfAssignments = WebUI.getNumberOfLocator(CommonLocators.allRowOfSpecificTable(assignmentsTableID));
            Utils.takeWindowsScreenshot();
            logger.passed(String.format("Found '%s' Assignment with information '%s'", numberOfAssignments, assignmentInfo));
        }
    }

    public void verifyAssignmentIsDisplayedWithCorrectInfomation(Map<String, String> assignmentInfo){
        verifyAssignmentIsDisplayedWithCorrectInfomation(assignmentInfo, false);
    }
    
    @Step("Verify that the [{filterValue}] is Not displayed on the filter of [{filterField}] column on the Project Assignments table")
	public void verifyThatSpecificValueIsNotDisplayedInTheFilter(String filterField, String filterValue) {
		verifyThatSpecificValueIsDisplayedInTheFilterOfSpecificTableOrNOT(assignmentsTableID, filterField, filterValue, false);
	}


    @Step("Verify that the '{fieldName}' is displayed on Project Details view.")
	public void verifyThatSpecificFieldDisplayOnDetailsView(String fieldName) {
		verifyLocatorIsDisplayed(ProjectsLocators.specificDetailField(fieldName),fieldName);
	}

    @Step("Verify that the value of '{fieldName}' is displayed on Project Details view.")
	public void verifyThatTheValueSpecificFieldDisplayOnDetailsView(String fieldName,String expectedvalue) {
		verifyLocatorIsDisplayedWithCorrectContent(ProjectsLocators.valueForSpecificDetailField(fieldName),fieldName,expectedvalue);
	}

    @Step("Verify that the tooltip of '{fieldName}' is displayed on Project Details view.")
	public void verifyThatTheTooltipSpecificFieldDisplayOnDetailsView(String fieldName, String tooltipcontent) {
        WebUI.hover(ProjectsLocators.tooltipIconSpecificDetailField(fieldName));
		verifyLocatorIsDisplayedWithCorrectContent(ProjectsLocators.tooltipContent(),fieldName,tooltipcontent);
	}

    @Step("Verify that the tooltip value of Burn Rate is displayed on Project Details view.")
	public void verifyThatTheTooltipOfBurnRateWithValueDisplayCorrectly(String fieldName) {
        String valueBurnRate=WebUI.getText(ProjectsLocators.valueForSpecificDetailField(fieldName));
        if(!valueBurnRate.trim().equals("--")){
            WebUI.hover(ProjectsLocators.valueForSpecificDetailField(fieldName));
            float floatvalue=Float.parseFloat(valueBurnRate.trim());
            if(floatvalue>0.1){
                verifyLocatorIsDisplayedWithCorrectContent(ProjectsLocators.tooltipContent(),fieldName,"Over Burn");
            } else {
                verifyLocatorIsDisplayedWithCorrectContent(ProjectsLocators.tooltipContent(),fieldName,"Under Burn");
            }
        } else {
            logger.passed("The Burn Rate was empty!");
        }
	}

    @Step("Verify Background corlor of Burn Rate on Project Details view")
    public void verifyBackgroundColorCSSValueOfBurnRateWithValue(String fieldname)
    {   String RedColor="#FFD0D1";
        String Yellow="#FFE694";
        String valueBurnRate=WebUI.getText(ProjectsLocators.valueForSpecificDetailField(fieldname));
        if(!valueBurnRate.trim().equals("--")){
            float floatvalue=Float.parseFloat(valueBurnRate.trim());
            if(floatvalue>0.1){
                verifyCSSValueOfLocatorIsDisplayedCorrectly("background-color",ProjectsLocators.valueForSpecificDetailField(fieldname), "Burn Rate - Project Details view", RedColor);
            } else {
                verifyCSSValueOfLocatorIsDisplayedCorrectly("background-color",ProjectsLocators.valueForSpecificDetailField(fieldname), "Burn Rate - Project Details view", Yellow);
            }
        } else {
            logger.passed("The Burn Rate was empty so dont have the back-ground for value '--'!");
        }
    }

    public void selectLeaderAndResourcesAddAssignment(String searchType, List<String> manager, List<String> listOfResources){
        WebUI.click(CommonLocators.searchTypeBox());
        WebUI.click(CommonLocators.specificDropdownOption(searchType));
        Utils.takeWindowsScreenshot();
        if (!manager.isEmpty()){
            for(String tmp : manager){
                WebUI.setText(CommonLocators.specificInputBasedOnMatlabel("People Leader/Manager"),tmp);
                WebUI.waitForLocatorVisible(searchType);
                WebUI.click(CommonLocators.specificDropdownOption(tmp));
                WebUI.setText(CommonLocators.specificInputBasedOnMatlabel("People Leader/Manager"),"");
                Utils.takeWindowsScreenshot();
            }
        }
        
        for(String tmp : listOfResources){
            WebUI.setText(CommonLocators.specificInputBasedOnMatlabel("Resource Name"),tmp);
            WebUI.click(CommonLocators.specificDropdownOption(tmp));
            WebUI.setText(CommonLocators.specificInputBasedOnMatlabel("Resource Name"),"");
            Utils.takeWindowsScreenshot();
        }
        
        WebUI.click(CommonLocators.actionBtn("Add"));
    }

    @Step("Verify that the [{field}] is displayed")
    public void verifyThatAFieldIsDisplayed(List<String> manager, List<String> resource,String field){
        selectLeaderAndResourcesAddAssignment("Manager & Resource", manager, resource);
        for(int i = 1 ; i <= resource.size(); i++){
            WebUI.scrollToLocator(CommonLocators.specificInputFieldIndex("Customer ID", i));
            WebUI.checkLocatorIsVisible(CommonLocators.specificInputFieldIndex("Customer ID", i), 3);
            WebUI.checkLocatorEnabledOrNot(CommonLocators.specificInputFieldIndex("Customer ID", i), true, 3);
            Utils.takeWindowsScreenshot();
        }
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Step("Veriy the maximum length of [{field}] is [{length}] characters in UI")
    public void verifyTheMaximumLength(String field, int index, int length){
        String randomString = generateRandomString(length+1);

        WebUI.setText(CommonLocators.specificInputFieldIndex(field, index), randomString);
        Utils.takeWindowsScreenshot();
        WebUI.waitForLocatorVisible(CommonLocators.specificMatlabelBasedOnText(field));
        WebUI.click(CommonLocators.specificMatlabelBasedOnText(field));

        if(WebUI.getText(CommonLocators.specificMatErrorBasedOnFieldName(field)).contains(" "+field + " cannot have more than "+length+" characters. ")){
            Utils.takeWindowsScreenshot();
            logger.passed(String.format("The maxium length of '%s' is '%s' characters",field,length));
        } else { Utils.takeWindowsScreenshot();
            logger.failed(String.format("The maxium length of '%s' is not '%s' characters",field,length)); }
    }

    @Step("Verify the field [{field}] is Mandatory or Non-Mandatory")
    public boolean verifyFieldMandatoryOrNot(String field, int index){
        if (WebUI.checkLocatorHasAttributeOrNot(CommonLocators.specificInputFieldIndex(field, index),"required",true) &&
            WebUI.checkLocatorHasAttributeOrNot(CommonLocators.specificInputFieldIndex(field, index),"aria-required",true)){
            return true;
        }
        return false;
    }

    @Step("Add [{info}] into the [{index}] row")
    public void addAssignmentIndexRowThroughProject(Map<String, String> info, int index){

        logger.info("Select Service Line, Resource Group, Project Role, Country");
            List<String> assignmentSelectFields = List.of("Service Line","Resource Group","Project Role","Location");
            for(String item : assignmentSelectFields){
                WebUI.clearText(CommonLocators.specificInputFieldIndex(item,index));
                WebUI.setText(CommonLocators.specificInputFieldIndex(item,index), info.get(item));
                WebUI.delay(1);
                WebUI.click(CommonLocators.specificDropdownOption(info.get(item)));
                Utils.takeWindowsScreenshot();
            }

        logger.info("Select Lead, Billable, Contracted");
            List<String> checkboxFields = List.of("Lead","Billable","Contracted");
            for(int i = 0; i < checkboxFields.size();i++){
                if(info.get(checkboxFields.get(i)) != "No"){
                    WebUI.scrollToLocator(ProjectsLocators.specificCheckBoxOfSpecificResourceWithIndex(info.get("Resource Name"), i+1));
                    if(WebUI.checkLocatorCheckedOrNot(ProjectsLocators.specificCheckBoxOfSpecificResourceWithIndex(info.get("Resource Name"), i+1), false, 3)){
                        WebUI.click(ProjectsLocators.specificCheckBoxOfSpecificResourceWithIndex(info.get("Resource Name"), i+1));
                        Utils.takeWindowsScreenshot();
                    }
                } else {
                    WebUI.scrollToLocator(ProjectsLocators.specificCheckBoxOfSpecificResourceWithIndex(info.get("Resource Name"), i+1));
                    if(WebUI.checkLocatorCheckedOrNot(ProjectsLocators.specificCheckBoxOfSpecificResourceWithIndex(info.get("Resource Name"), i+1), true, 3)){
                        WebUI.click(ProjectsLocators.specificCheckBoxOfSpecificResourceWithIndex(info.get("Resource Name"), i+1));
                        Utils.takeWindowsScreenshot();
                    }
                }
            }
        
        logger.info("Set Assignment Effort and Assignment Value");
            WebUI.click(CommonLocators.specificMatSelectBasedOnID("effort",index));
            WebUI.click(CommonLocators.specificDropdownOption(info.get("Effort")));
            WebUI.delay(1);
            Utils.takeWindowsScreenshot();
            WebUI.setText(CommonLocators.specificInputFieldIndex("Assignment",index), info.get("Assignment"));
            Utils.takeWindowsScreenshot();
        
        logger.info("Set Start Date and End Date");
            String[] startDate = info.get("Start Date").split("-");
            String[] endDate = info.get("End Date").split("-");           
            WebUI.click(CommonLocators.specificButtonFieldIndex("Start Date",index));
            WebUI.delay(3);
            setDayDateTime(startDate[2], startDate[1].toUpperCase(), startDate[0]);
            Utils.takeWindowsScreenshot();
            WebUI.click(CommonLocators.specificButtonFieldIndex("End Date",index));
            WebUI.delay(3);
            setDayDateTime(endDate[2], endDate[1].toUpperCase(), endDate[0]);
            Utils.takeWindowsScreenshot();
          

        logger.info("Set Request ID and Customer ID");
            WebUI.clearText(CommonLocators.specificInputFieldIndex("Request ID", index));
            WebUI.setText(CommonLocators.specificInputFieldIndex("Request ID", index), info.get("Request ID"));
            Utils.takeWindowsScreenshot();
            WebUI.clearText(CommonLocators.specificInputFieldIndex("Customer ID", index));
            WebUI.setText(CommonLocators.specificInputFieldIndex("Customer ID", index), info.get("Customer ID"));
            Utils.takeWindowsScreenshot();
    }

    @Step("Save all inputs in adding assignment of Project Details page")
    public void saveAllAddAssignment(){
        logger.info("Save all inputs in adding assignment of Project Details page");
            WebUI.click(CommonLocators.specificButton("Save"));
            Utils.takeWindowsScreenshot();
            verifyToastMessage();
    }

    @Step("Select an random project ID")
    public String selectRandomProjectID() {
        boolean found = false, noMatchfound = false;
        String projectID = "", status = "", alternativeId = "", leadServiceLine = "", iconProjectId = "";
		while(!found) {
            WebUI.delay(1);
            while(!noMatchfound){
                projectID = String.valueOf(Utils.generateRandomNumber(10,99));
                WebUI.setTextOneByOne(CommonLocators.specificInputFieldIndex("",1), projectID);
                if (WebUI.checkLocatorNotVisible(CommonLocators.specificDropdownOption("No match found"),3)){
                    WebUI.click(CommonLocators.specificDropdownOptionContainsWithIndex(projectID, 1));
                    logger.info(String.format("Selected the option contains '%s' with index '%s' from dropdown", projectID, 1));
                    noMatchfound = true;
                }
            }
            WebUI.click(CommonLocators.actionBtn("Search"));
            WebUI.waitForLocatorVisible(CommonLocators.specificTableColumns(projectSearchTableID));
            Utils.takeWindowsScreenshot();
            
			status =  getTableDataOfSpecificRowIndexAndColumnName(projectSearchTableID, 1, "Status").strip();
            alternativeId = getTableDataOfSpecificRowIndexAndColumnName(projectSearchTableID, 1, "Project Id").strip();
            leadServiceLine = getTableDataOfSpecificRowIndexAndColumnName(projectSearchTableID, 1, "Lead Service Line").strip();
            iconProjectId = getTableDataOfSpecificRowIndexAndColumnName(projectSearchTableID, 1, "Alternative Project Id").strip();
            if((status.equals("Active") || status.equals("Bid")) && !(alternativeId.startsWith("PRARSCNG-")) && !(leadServiceLine.equals("ICON Strategic Solutions (ISS)")) &&  !(iconProjectId.startsWith("PRARSCNG-"))){
                found = true;
            }
            WebUI.enhancedClick(CommonLocators.specificAccordion("Search Criteria for Projects"));
		}
        if(alternativeId.equals("-")){
            logger.info(String.format("Project ID : %s",iconProjectId));
            return iconProjectId;
        }
        logger.info(String.format("Project ID : %s",alternativeId));
        return alternativeId;
    }

    public Map<String, String> selectRandomProjectForCriteria(Boolean hasIconProjectId, Boolean hasAltenativeProjectId, String requiredStatus) {
        boolean found = false;
        String projectID = "", alternativeId = "", iconProjectId = "", status = "";
        Boolean noMatchfound = true;
        while (!found) {
            expandAccordion("Search Criteria for Projects");
            WebUI.delay(1); 
            noMatchfound = true;
            while(noMatchfound){
                projectID = String.valueOf(Utils.generateRandomNumber(10,99));
                WebUI.setTextOneByOne(CommonLocators.specificInputFieldIndex("",1), projectID);
                WebUI.delay(2);
                if (WebUI.checkLocatorNotVisible(CommonLocators.specificDropdownOption("No match found"),3)){
                    WebUI.click(CommonLocators.specificDropdownOptionContainsWithIndex(projectID, 1));
                    noMatchfound = false;
                }
            }
            WebUI.click(CommonLocators.actionBtn("Search"));
            WebUI.waitForLocatorVisible(CommonLocators.specificTableColumns(projectSearchTableID));
            iconProjectId = getTableDataOfSpecificRowIndexAndColumnName(projectSearchTableID, 1, "Project Id").strip();
            alternativeId = getTableDataOfSpecificRowIndexAndColumnName(projectSearchTableID, 1, "Alternative Project Id").strip();
            status =  getTableDataOfSpecificRowIndexAndColumnName(projectSearchTableID, 1, "Status").strip();
            if (hasIconProjectId && iconProjectId.equals("--")) continue;
            if (!hasAltenativeProjectId && !iconProjectId.equals("--")) continue;
            if (hasAltenativeProjectId && alternativeId.equals("--")) continue;
            if (!hasAltenativeProjectId && !alternativeId.equals("--")) continue;
            if (requiredStatus != null && !requiredStatus.equals(status)) continue;
            found = true;
        }
        Map<String, String> result = Map.of(
            "Alternative Project ID", alternativeId,
            "Icon Project ID", iconProjectId
        );
        return result;
    }

    public void verifyBurnRateAccordionIsDisplayedAsExpected() {
        super.verifyTabSelected(CommonLocators.specificTab("Project Details"), "Project Detail tab", true);
        super.verifyLocatorIsDisplayed(CommonLocators.specificAccordionTitle("Burn Rate"), "Burn Rate Accordion");
        Utils.takeWindowsScreenshot();
        logger.passed("Burn Rate Accordion is displayed under Project details tab");
        double distanceFromProjectDetailsToBurnRateAccordion = WebUI.getVerticalDistanceBetweenTwoLocators(CommonLocators.specificTab("Project Details"), CommonLocators.specificAccordionTitle("Burn Rate"));
        double distanceFromProjectDetailsToLocationAccordion = WebUI.getVerticalDistanceBetweenTwoLocators(CommonLocators.specificTab("Project Details"), CommonLocators.specificAccordionTitle("Locations"));
        double distanceFromBurnRateAccordionToLocationAccordion = WebUI.getVerticalDistanceBetweenTwoLocators(CommonLocators.specificAccordionTitle("Burn Rate"), CommonLocators.specificAccordionTitle("Locations"));
        logger.info(String.format("Distance from Project details to Burn Rate Accordion: %s",distanceFromProjectDetailsToBurnRateAccordion));
        logger.info(String.format("Distance from Project details to Location Accordion: %s", distanceFromProjectDetailsToLocationAccordion));
        logger.info(String.format("Distance from Burn Rate to Location Accordion: %s", distanceFromBurnRateAccordionToLocationAccordion));
    
        if (distanceFromProjectDetailsToBurnRateAccordion < distanceFromProjectDetailsToLocationAccordion && distanceFromBurnRateAccordionToLocationAccordion + distanceFromProjectDetailsToBurnRateAccordion <= distanceFromProjectDetailsToLocationAccordion) {
            logger.passed("Burn Rate Accordion is displayed above the Locations Accordion");
        } else {
            logger.failed("Burn Rate Accordion is NOT displayed above the Locations Accordion");
        }
    }

    public void verifyBurnRateAccordionIsExpandedAsExpected() {
        WebUI.verifyLocatorAttributeValue(CommonLocators.specificAccordionHeader("Burn Rate"), "aria-expanded", "true");
        logger.passed("Burn Rate Accordion is expanded by default");
    }

    public void verifyBurnRateAccordionContainsTableListAsExpected(String projectID, String dataFile) throws IOException {
        // logger.step("a. Data range used for calculation: Last 6 months.");
        //     String lastMonth = DateTime.getDateStringAfterMonths(null, -1, "dd-MMM-yyyy", 0);
        //     logger.info(String.format("Previous month is %s", lastMonth));
        //     String lastSixMonth = DateTime.getDateStringAfterMonths(null, - 6, "dd-MMM-yyyy", 1);
        //     logger.info(String.format("Last six month is %s", lastSixMonth));
        //     super.verifyLocatorIsDisplayed(CommonLocators.specificLabelWithValue("Period", lastSixMonth), "Last month in Burn Rate"); 
        //     super.verifyLocatorIsDisplayed(CommonLocators.specificLabelWithValue("to", lastMonth), "Last six month in Burn Rate");   
        logger.step("b. Service Line - Project Assignments Service Line. Example: IBT, IPH, IOD etc.");
            logger.info(String.format("Data for project assignment is retrieved from the database manually with below query: \n" +//
                                "SELECT F1.ALLOCATION_ID, F1.YEAR_MONTH, F1.ASSIGNED_VALUE, F4.NAME AS SERVICE_LINE \r\n" + //
                                "FROM EMPLOYEE_ASSIGNMENT F1  \r\n" + //
                                "JOIN EMPLOYEE_ALLOCATIONS F2 ON F1.ALLOCATION_ID = F2.ID\r\n" + //
                                "JOIN PROJECT F3 ON F3.ID = F2.PROJECT_ID\r\n" + //
                                "JOIN SERVICE_LINE F4 ON F4.ID = F2.SERVICE_LINE_ID\r\n" + //
                                "WHERE F3.ICON_PROJECT_ID = '%s'", projectID));
            List<Map<String, String>> projectAssignmentData = ExcelUtils.getAllDataFromFile(dataFile, 0);
            logger.info(String.format("Project Assignment data is retrieved from the file '%s'", projectAssignmentData));

        logger.step("Burn Rate for Each Service Line");
        logger.step("Display the Burn Rate in the format of XX.XX");
    }

    @Step("Verify the Project window title")
    public void verifyPageTitleAsExpected(String projectID,String expectedPageTitle) {
        quickSearchProject("ProjectID", projectID);
        WebUI.click(CommonLocators.viewButtonOfSpecificRowIndexInSpecificTable(projectSearchTableID, 1));
        WebUI.switchToNewPageAndClosePreviousPage();
        WebUI.waitForPageLoad();
        WebUI.takeFullPageScreenshot();
        String actualPageTitle = WebUI.getPageTitle();
        if(!actualPageTitle.equals(expectedPageTitle))
            logger.failed(String.format("Page tilte DOES NOT show %s as expected %s", actualPageTitle, expectedPageTitle));
        logger.passed(String.format("Page tilte show %s as expected %s", actualPageTitle, expectedPageTitle));
    }

    @Step("Verify the Resource window title that navigated from Project Assignment")
    public void verifyResourcePageTitleAsExpectedFromProjectAssigment(String projectID, String filterField, String resourceName, String expectedPageTitle) {
        quickSearchProject("ProjectID", projectID);
        WebUI.click(CommonLocators.viewButtonOfSpecificRowIndexInSpecificTable(projectSearchTableID, 1));
        WebUI.switchToNewPageAndClosePreviousPage();
        WebUI.waitForPageLoad();        
        navigateToSpecificTabOnProjectDetailsPage("Assignments", false);
        WebUI.waitForPageLoad();
        filterOnTable(assignmentsTableID, filterField, resourceName);
        WebUI.click(ProjectsLocators.specificCellOfSpecificResourceWithIndex(assignmentsTableID, 1, 4));
        WebUI.switchToNewPageAndClosePreviousPage();
        WebUI.waitForPageLoad();
        WebUI.takeFullPageScreenshot();
        String actualPageTitle = WebUI.getPageTitle();
        if(!actualPageTitle.equals(expectedPageTitle))
            logger.failed(String.format("Page tilte DOES NOT show %s as expected %s", actualPageTitle, expectedPageTitle));
        logger.passed(String.format("Page tilte show %s as expected %s", actualPageTitle, expectedPageTitle));            
    }

    @Step("Create Project")
    public List<String> addProject(Integer loopIndex, Integer businessLineIndex, String sdbIntegration, Integer projectFormatIndex, String random, String argusProject, String projectCode, String altProject, String clientName) {
        List<String> projectCodeList = new ArrayList<String>();
        for(int i=1; i<= loopIndex; i++) {
			WebUI.enhancedClick(SCTBasePage.addBtn());
            WebUI.delay(5);
			// waitKeywords.waitForAjaxToComplete()
            WebUI.waitForLocatorVisible(AddEditProjectModal.addProjectHeader());
			WebUI.selectOptionByLabel(AddEditProjectModal.selectBusinessLine(), getBusinessLineList().get(businessLineIndex));
			if(sdbIntegration.equalsIgnoreCase("Yes") && projectFormatIndex == null) {
				WebUI.click(AddEditProjectModal.clickEnableSDBIntegration());
				if(argusProject !=null) {
					WebUI.setText(AddEditProjectModal.argusProjectCodeInput(), argusProject);
				}
				WebUI.click(AddEditProjectModal.findProjectCode(clientName));
			}
			if(sdbIntegration.equalsIgnoreCase("No")) {
				if(getProjectCodeFormatList().get(projectFormatIndex) == "XXXX-XXXX") {
					WebUI.selectOptionByLabel(AddEditProjectModal.selectProjectCodeFormat(), getProjectCodeFormatList().get(1));
					if(random =="Yes") {
						String formatCode=Utils.getRandomNumeric(4) + '-' + Utils.getRandomNumeric(4);
						// String pF=utilKeywords.addGlobalVariable("projectFormat"+ loopIndex, formatCode);
						WebUI.setText(AddEditProjectModal.projectCodeInput(), formatCode);
						projectCodeList.add(formatCode);
					}
					else {
						WebUI.setText(AddEditProjectModal.projectCodeInput(), argusProject);
					}
				}
				else  {
					WebUI.selectOptionByLabel(AddEditProjectModal.selectProjectCodeFormat(),getProjectCodeFormatList().get(2));
					if(random == "Yes") {
						String formatCode=Utils.getRandomNumeric(8) + '-' + Utils.getRandomNumeric(6);
						// String pF=utilKeywords.addGlobalVariable('projectFormat'+ loopIndex, formatCode)
						WebUI.setText(AddEditProjectModal.projectCodeInput(), formatCode);
						projectCodeList.add(formatCode);
					}
					else {
						WebUI.setText(AddEditProjectModal.projectCodeInput(), argusProject);
					}
				}
				WebUI.setText(AddEditProjectModal.clientNameInput(), clientName);
			}
			if(altProject != null) {
				WebUI.setText(AddEditProjectModal.altProjectCode(), altProject);
			}
			WebUI.enhancedClick(AddEditProjectModal.saveBtn());
			WebUI.delay(3);
            logger.passed("Project added successfully");
			WebUI.verifyLocatorVisible(SCTBasePage.successAlert());
			// waitKeywords.waitForToastMessageToDisappear()
		}
		return projectCodeList;

    }

    public List<String> getBusinessLineList() {
		return Arrays.asList("-- Select --","IBT","IPH","RWS","EDS","IOD");
	}

    public List<String> getProjectCodeFormatList() {
		return Arrays.asList("-- Select --","XXXX-XXXX","XXXXXXXX-XXXXXX");
	}

    @Step("Navigate to Protocol Management page")
    public void clickOnProjectLink(String projectCode){
		WebUI.setText(AddEditProjectModal.searchBox(), projectCode);
        WebUI.enter(AddEditProjectModal.searchBox());
		WebUI.waitForPageLoad(30);
		WebUI.delay(3);
		WebUI.click(AddEditProjectModal.projectCodeLink(projectCode));
		WebUI.waitForLocatorVisible(AddEditProjectModal.protocolHeader(),5);
	}

    @Step("Add Protocol")
	public List<String> addProtocol(Integer loopIndex,Integer protocolTypeIndex,String sdbIntegration, String random, List<String> argusProtocol, String protocolCode,Integer documentManagementIndex, String folderPath,Integer contractTypeIndex, String date, String queryManagement, String tmfFiling) {
		List<String> protocolList = new ArrayList<String>();
		for(int i=1; i<= loopIndex; i++) {
			clickOnAddProtocol();
			if(sdbIntegration.equalsIgnoreCase("Yes")) {
				if(getProtocolTypeList().get(protocolTypeIndex) == "Clinical Trial") {
					WebUI.selectOptionByLabel(AddEditProjectModal.selectProtocolType(),getProtocolTypeList().get(protocolTypeIndex));
					if(protocolCode !=null) {
						WebUI.setText(AddEditProjectModal.argusProtocolNameInput(), protocolCode);
						WebUI.click(AddEditProjectModal.argusProtocolSearchLink(protocolCode));
					}
				}
				else {
					WebUI.selectOptionByLabel(AddEditProjectModal.selectProtocolType(),getProtocolTypeList().get(protocolTypeIndex));
				}
				WebUI.selectOptionByLabel(AddEditProjectModal.selectDocManagement(),getDocumentManagementList().get(documentManagementIndex));
				if(getDocumentManagementList().get(documentManagementIndex).equals('2')) {
					WebUI.setText(AddEditProjectModal.manualFolderPath(), folderPath);
				}
				WebUI.selectOptionByLabel(AddEditProjectModal.selectContractType(),getContractTypeList().get(contractTypeIndex));
				if(date != null) {
					WebUI.setText(AddEditProjectModal.integrationStartDateInput(),date);
				}
				if(queryManagement == "disabled") {
					WebUI.click(AddEditProjectModal.queryManagementSwitch());
				}
			}
			if(sdbIntegration.equalsIgnoreCase("No")) {
				WebUI.selectOptionByLabel(AddEditProjectModal.selectProtocolType(),getProtocolTypeList().get(protocolTypeIndex));
				if(random == "Yes") {
					String formatCode = "Test Protocol" + Utils.getRandomNumeric(5);
					// String pF=utilKeywords.addGlobalVariable('protocolValue'+ loopIndex, formatCode)
					WebUI.setText(AddEditProjectModal.protocolNameInput(), formatCode);
					protocolList.add(formatCode);
				}
				else {
					WebUI.setText(AddEditProjectModal.protocolNameInput(), argusProtocol.get(i-1));
				}
				WebUI.selectOptionByLabel(AddEditProjectModal.selectDocManagement(),getDocumentManagementList().get(documentManagementIndex));
				if(getDocumentManagementList().get(documentManagementIndex).equals("SharePoint-Manual")) {
					WebUI.setText(AddEditProjectModal.manualFolderPath(), folderPath);
				}
				WebUI.selectOptionByLabel(AddEditProjectModal.selectContractType(),getContractTypeList().get(contractTypeIndex));
				if(queryManagement == "disabled") {
					WebUI.click(AddEditProjectModal.queryManagementSwitch());
				}
			}
			if(tmfFiling == "clicked") {
				WebUI.click(AddEditProjectModal.tmfFillingSwitch());
			}

			WebUI.delay(3);
			WebUI.enhancedClick(AddEditProjectModal.saveBtn());
			WebUI.delay(2);
			Utils.takeWindowsScreenshot();
			WebUI.verifyLocatorVisible(AddEditProjectModal.successAlert());
			// waitKeywords.waitForToastMessageToDisappear()
		}
		return protocolList;
	}

    public void clickOnAddProtocol() {
		WebUI.click(AddEditProjectModal.protocolAddButton());
		// waitKeywords.waitForAjaxToComplete()
        WebUI.waitForPageLoad();
		WebUI.waitForLocatorVisible(AddEditProjectModal.addProtocolHeader());
	}

    public List<String> getProtocolTypeList() {
		return Arrays.asList("-- Select --","Post-Marketing","Clinical Trial");
	}

    public List<String> getDocumentManagementList() {
		return Arrays.asList("-- Select --","SharePoint-Automated","SharePoint-Manual","File Server","OFF");
	}

	public List<String> getContractTypeList() {
		return Arrays.asList("-- Select --","Full Service","Postbox/Passthrough","Legal Rep","Query Management Only","Legal Rep + Postbox/Passthrough","Legal Rep + Query Management Only");
	}
}