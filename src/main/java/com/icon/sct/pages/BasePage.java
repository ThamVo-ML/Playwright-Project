package com.icon.sct.pages;


import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.icon.sct.locators.CommonLocators;
import com.icon.sct.utils.DateTime;
import com.icon.sct.utils.Loggers;
import com.icon.sct.utils.PlaywrightActions;
import com.icon.sct.utils.Utils;


import io.qameta.allure.Step;


public class BasePage {
    protected final PlaywrightActions WebUI;
    protected final Loggers logger;

    public BasePage(PlaywrightActions WebUI) {
        this.WebUI = WebUI;
        this.logger = new Loggers();
    }
    

	@Step("Navigate to [{tabName}] tab from [{parentPage}] page")
    public void navigateToSpecificTab(String tabName, String parentPage) {
        if (!WebUI.checkLocatorIsVisible(CommonLocators.specificTab(tabName))){
            logger.failed(String.format("Not found the '%s' tab on the %s page", tabName, parentPage));
        }
        WebUI.click(CommonLocators.specificTab(tabName));
        waitForSpinnerToDisappearIfExists();
    }

    protected void verifyLocatorIsDisplayed(String locator, String locatorName, int timeout){
        if (!WebUI.checkLocatorIsVisible(locator, timeout)){
            logger.failed(String.format("The %s is NOT displayed", locatorName));
        }
        logger.passed(String.format("The %s is displayed", locatorName));
    }

    protected void verifyLocatorIsDisplayed(String locator, String locatorName){
        verifyLocatorIsDisplayed(locator, locatorName, 5);
    }

    public void verifyLocatorIsDisplayedWithCorrectContent(String locator, String locatorName, String expectedContent){
        String actualContent = WebUI.getText(locator).trim();
        if (!actualContent.equals(expectedContent)){
            logger.failed(String.format("Actual content of %s is '%s', different from expected '%s'", locatorName, actualContent, expectedContent));
        }
        logger.passed(String.format("Actual content of %s is '%s', same as expected '%s'", locatorName, actualContent, expectedContent));
    }

    protected void verifySizeOfLocatorIsDisplayedCorrectly(String sizeType, String locator, String locatorName, String expectedSize){
        Double actualSize = WebUI.getSizeOfLocator(locator).get(sizeType.toLowerCase());
        if (actualSize != Double.parseDouble(expectedSize.replace("px", ""))){
            logger.failed(String.format("Actual '%s' of %s is '%s', different from expected '%s'", sizeType, locatorName, String.format("%s px", actualSize), expectedSize));
        }
        logger.passed(String.format("Actual '%s' of %s is '%s', same as expected '%s'", sizeType, locatorName, String.format("%s px", actualSize), expectedSize));
    }

    protected void verifyCSSValueOfLocatorIsDisplayedCorrectly(String css, String locator, String locatorName, String expectedValue){
        String actualValue = WebUI.getCssValue(locator, css);
        if (css.contains("color")){
            WebUI.takeLocatorScreenshot(locator);
			WebUI.delay(2);
			actualValue = WebUI.getCssValue(locator, css);
            actualValue = Utils.convertRGBToHex(actualValue);
        }
        if (!actualValue.equals(expectedValue)){
			if (expectedValue.contains("px")){
				int actual = (int) Math.round(Double.parseDouble(actualValue.replace("px", "").trim()));
				int expected = (int) Math.round(Double.parseDouble(expectedValue.replace("px", "").trim()));
				if (actual == expected){
					logger.passed(String.format("Actual '%s' of the %s is '%s', same as expected '%s'", css, locatorName, actualValue, expectedValue));
					return;
				}
			}
            logger.failed(String.format("Actual '%s' of the %s is '%s', different from expected '%s'", css, locatorName, actualValue, expectedValue));
        }
		else{
			logger.passed(String.format("Actual '%s' of the %s is '%s', same as expected '%s'", css, locatorName, actualValue, expectedValue));
		}
    }

	@Step("Get value of field {fieldName}")
	public String getValueOfSpecificLable(String fieldName){
		String value=null;
		if (!WebUI.checkLocatorIsVisible(CommonLocators.valueOfSpecificLabel(fieldName))) {
            logger.error(String.format("Locator of field '%s' not available",fieldName));
        }else {
			 value = WebUI.getText(CommonLocators.valueOfSpecificLabel(fieldName));
			logger.info(String.format("Value of field '%s': %s",fieldName,value));
		}
		return value; 
	}

	@Step("Verify Content of {nameElement} display correctly")
	public void verifyContentByJavaScript(String script,String nameElement,String expectedContent){
		String actual=(String) WebUI.executeJavaScript(script);
		if(actual.equals(expectedContent)){
			logger.passed(String.format("%s : Actual '%s' , same as expected '%s'", nameElement, actual,expectedContent));
		}else {
			logger.error(String.format("%s : Actual '%s' , expected '%s'",nameElement, actual,expectedContent));
		}
	}

	public String getTableIdFromPageName(String pageName){
		if (pageName.toLowerCase().equals("allocations")){
			return "allocations";
		}
		else if (pageName.toLowerCase().equals("availabilities")){
			return "availabilities";
		}
		else if (pageName.toLowerCase().equals("demand")){
			return "demandRequests";
		}
		else if (pageName.toLowerCase().equals("employee")){
			return "employeeDetails";
		}
		else if (pageName.toLowerCase().equals("general configuration")){
			return "viewCountries";
		}
		else if (pageName.toLowerCase().equals("location hours")){
			return "viewLocationHours";
		}
		else if (pageName.toLowerCase().equals("manage resource groups")){
			return "manageResourceGroup";
		}
		else if (pageName.toLowerCase().equals("manage project roles")){
			return "projectRoles";
		}
		else if (pageName.toLowerCase().equals("resource group mapping")){
			return "rdafMapping";
		}
		else if (pageName.toLowerCase().equals("open demand")){
			return "openDemandsRequests";
		}
		else if (pageName.toLowerCase().equals("projects")){
			return "projectSearch";
		}
		else if (pageName.toLowerCase().equals("project assignments")){
			return "project-assignment";
		}
		else if (pageName.toLowerCase().equals("resources")){
			return "resourceSearch";
		}
		else if (pageName.toLowerCase().equals("resource assignments")){
			return "resource-assignment";
		}
		else if (pageName.toLowerCase().equals("manage functional resource manager")){
			return "viewFRM";
		}
		else if (pageName.toLowerCase().equals("manage open demand status")){
			return "viewOpenDeamndStatus";
		}
		else if (pageName.toLowerCase().equals("manage roles")){
			return "viewRoles";
		}
		else if (pageName.toLowerCase().equals("manage access")){
			return "role-assignment";
		}
		else{
			logger.failed(String.format("The table id for '%s' page does NOT defined in this function. Please add it and try again", pageName));
			return null;
		}
	}

	@Step("Verify that the table is displayed correctly on the UI of the [{pageName}] page")
    public void verifyThatTableIsDisplayedCorrectlyOnTheUIOfSpecificPage(String pageName) {
		String tableId = getTableIdFromPageName(pageName);
		String expectedHeight = "40px";
		String expectedHeaderAndPaginationPadding = "16px";
		String expectedTableCellPadding = "8px";
		String expectedHeightOfStatusItem = "24px";
        int numberOfColumn = WebUI.getNumberOfLocator(CommonLocators.allColumnsOfSpecificTable(tableId));
        int numberOfRow = WebUI.getNumberOfLocator(CommonLocators.allRowOfSpecificTable(tableId));
        int rowIndex = Utils.generateRandomNumber(1, numberOfRow);
		if (rowIndex > 10){
			rowIndex = Utils.generateRandomNumber(1, 10);
		}
        int columnIndex = Utils.generateRandomNumber(2, numberOfColumn - 1);
		String tableColumnLocator = CommonLocators.specificTableColumns(tableId);
		if (WebUI.getNumberOfLocator(CommonLocators.specificTableColumns(tableId)) > 1){
			tableColumnLocator = String.format("(%s)[1]", CommonLocators.specificTableColumns(tableId));
		}
        verifySizeOfLocatorIsDisplayedCorrectly("Height", CommonLocators.specificTableHeader(tableId), String.format("'Table Header' in the %s page", pageName), expectedHeight);
        verifySizeOfLocatorIsDisplayedCorrectly("Height", tableColumnLocator, String.format("'Table Columns' in the %s page", pageName), expectedHeight);
        verifySizeOfLocatorIsDisplayedCorrectly("Height", CommonLocators.specificTableRowWithIndex(tableId, rowIndex), String.format("'Table Row with index '%s' in the %s page", rowIndex, pageName), expectedHeight);
		if (pageName.toLowerCase().equals("resource assignments")){
			verifyCSSValueOfLocatorIsDisplayedCorrectly("Height", String.format("%s/*[contains(@class, container)]", CommonLocators.specificTablePagination(tableId)), String.format("'Table Pagination' in the %s page", pageName), expectedHeight);
		}
		else{
			verifyCSSValueOfLocatorIsDisplayedCorrectly("Height", CommonLocators.specificTablePagination(tableId), String.format("'Table Pagination' in the %s page", pageName), expectedHeight);
		}
		
		logger.info("Verify that the 'padding-left' and 'padding-right' in the table is correct");
		List<String> listCSS = Arrays.asList("padding-left", "padding-right");
		for (String css : listCSS){
            verifyCSSValueOfLocatorIsDisplayedCorrectly(css, CommonLocators.specificTableHeader(tableId), String.format("'Table Header' in the %s page", pageName), expectedHeaderAndPaginationPadding);
			verifyCSSValueOfLocatorIsDisplayedCorrectly(css, CommonLocators.specificTablePagination(tableId), String.format("'Table Pagination' in the %s page", pageName), expectedHeaderAndPaginationPadding);
			// verifyCSSValueOfLocatorIsDisplayedCorrectly(css, CommonLocators.specificCellInTableWithIndex(tableId, rowIndex, columnIndex), String.format("Cell with column '%s' and row '%s' in the %s page", columnIndex, rowIndex, pageName), expectedTableCellPadding);
			if (!pageName.equals("Project Assignments")){
				verifyCSSValueOfLocatorIsDisplayedCorrectly(css, CommonLocators.specificCellInTableWithIndex(tableId, rowIndex, columnIndex), String.format("Cell with column '%s' and row '%s' in the %s page", columnIndex, rowIndex, pageName), expectedTableCellPadding);
			}
		}

		logger.info("Verify that the 'padding-left' of First column and all first cells of row in the table is '16px' => Table body has 'padding-left' with '8px'");
		verifyCSSValueOfLocatorIsDisplayedCorrectly("padding-left", CommonLocators.specificColumnInTableWithIndex(tableId, 1), String.format("First column in the %s page", pageName), expectedHeaderAndPaginationPadding);
		for (int row = 1; row <= numberOfRow; row ++){
			verifyCSSValueOfLocatorIsDisplayedCorrectly("padding-left", CommonLocators.specificCellInTableWithIndex(tableId, rowIndex, 1), String.format("Cell with column '%s' and row '%s' in the %s page", 1, row, pageName), expectedHeaderAndPaginationPadding);
		}

		logger.info("Verify that 'Height' of all Status items in the table are '24px'");
		if (WebUI.checkLocatorPresent(CommonLocators.allStatusItemsInTable(tableId), 2)){
			for (int statusIndex = 1; statusIndex <= WebUI.getNumberOfLocator(CommonLocators.allStatusItemsInTable(tableId)); statusIndex ++){
				verifySizeOfLocatorIsDisplayedCorrectly("Height", String.format("(%s)[%s]", CommonLocators.allStatusItemsInTable(tableId), statusIndex), String.format("'Status item' with index '%s' in the %s page", statusIndex, pageName), expectedHeightOfStatusItem);
			}
		}
		else{
			logger.passed("There are no Status items are displayed in the table. No need to verify");
		}
	}

	@Step("Verify that the Filter is displayed correctly on the UI of the [{pageName}] page")
    public void verifyThatFilterIsDisplayedCorrectlyOnTheUIOfSpecificPage(String pageName) {
		String tableId = getTableIdFromPageName(pageName);
		String expectedColorOfFilteredIcon = "#41B34E";
		String expectedColorOfSelectedCheckbox = "#1790D0";
		String expectedTextColorOfSelectAllFilterOption = "#4D4354";
		List<String> allFilterableColumnNames = getAllFilterableColumnsOfSpecificTable(tableId);
		Collections.shuffle(allFilterableColumnNames);
		String filterField = "";
		for (String column : allFilterableColumnNames){
			WebUI.scrollToLocator(CommonLocators.specificFilterButtonOnSpecificTable(column, tableId));
			WebUI.enhancedClick(CommonLocators.specificFilterButtonOnSpecificTable(column, tableId));
			if (!WebUI.checkLocatorIsVisible(CommonLocators.specificButton("Clear"), 1)){
				WebUI.enhancedClick(CommonLocators.specificFilterButtonOnSpecificTable(column, tableId));
			}
			if (WebUI.getNumberOfLocator(CommonLocators.allFilterOptions()) > 1){
				filterField = column;
				break;
			}
			WebUI.click(CommonLocators.specificButton("Cancel"));
		}
		if (filterField.isEmpty()){
			logger.failed("Not found any filterable column has multiple filter options");
		}
		logger.info(String.format("Verify that Filter container of the '%s' column is displayed as design", filterField));
		WebUI.click(CommonLocators.specificButton("Clear"), 60);
		List<String> allFilterOptions = WebUI.getAllTextContents(CommonLocators.allFilterOptions());
		String randomFilterOption = allFilterOptions.get(Utils.generateRandomNumber(0, allFilterOptions.size() - 1));
		for (int i = 0; i < 10; i ++){
			if (!randomFilterOption.isBlank()){
				break;
			}
			randomFilterOption = allFilterOptions.get(Utils.generateRandomNumber(0, allFilterOptions.size() - 1));
		}
		WebUI.click(CommonLocators.specificCheckBoxInFilterOption(randomFilterOption));
		verifyCSSValueOfLocatorIsDisplayedCorrectly("background-color", CommonLocators.specificCheckBoxBackgroundInFilterOption("Select All"), "'Select All' checkbox in the filter", expectedColorOfSelectedCheckbox);
		verifyCSSValueOfLocatorIsDisplayedCorrectly("background-color", CommonLocators.specificCheckBoxBackgroundInFilterOption(randomFilterOption), "'Selected checkbox' in the list filter option", expectedColorOfSelectedCheckbox);

		verifyCSSValueOfLocatorIsDisplayedCorrectly("color", CommonLocators.buttonLabelOfSelectAllFilterOption(), "'Select All' text in the filter", expectedTextColorOfSelectAllFilterOption);
		
		if (allFilterOptions.size() > 1){
			if (!WebUI.checkLocatorCheckedOrNot(CommonLocators.specificCheckBoxInFilterOption("Select All"), false)){
				logger.failed("The 'Select All' option is checked when all filter options are NOT checked");
			}
			logger.passed("The 'Select All' option is NOT checked when all filter options are NOT checked");
		}
		else{
			if (!WebUI.checkLocatorCheckedOrNot(CommonLocators.specificCheckBoxInFilterOption("Select All"), true)){
				logger.failed("The 'Select All' option is NOT checked when all filter options are checked");
			}
			logger.passed("The 'Select All' option is checked when all filter options are checked");
		}
		Utils.takeWindowsScreenshot();
		WebUI.click(CommonLocators.specificButton("OK"));
		waitForSpinnerToDisappearIfExists();
		
		logger.info("Verify that the 'Filtered icon' is dispalyed  with green color after filter");
		if (!WebUI.checkLocatorPresent(CommonLocators.specificFilteredIconOnSpecificTable(filterField, tableId))){
			logger.failed(String.format("The 'Filtered icon' is NOT dislayed after filter on the '%s' column", filterField));
		}
		logger.passed(String.format("The 'Filtered icon' is dislayed after filter on the '%s' column", filterField));
		WebUI.verifyLocatorAttributeValue(CommonLocators.specificFilteredIconOnSpecificTable(filterField, tableId), "fill", expectedColorOfFilteredIcon);
	}

	@Step("Verify that Accordio is displayed correctly")
	public void verifyThatAccordionIsDisplayedCorrectly(String expectedHeight, String expectedPadding) {
		int numberOfAccordions = WebUI.getNumberOfLocator(CommonLocators.accordionHeader());
		for (int i = 1; i <= numberOfAccordions; i ++){
			logger.info(String.format("Verify that 'Height' of Accordion header (%s) is '%s'", i, expectedHeight));
			verifySizeOfLocatorIsDisplayedCorrectly("Height", String.format("(%s)[%s]", CommonLocators.accordionHeader(), i), "Accordion header", expectedHeight);
	
			logger.info(String.format("Verify that 'padding-left', 'padding-right', 'padding-top', and 'padding-bottom' of Accordion body (%s) is '%s'", i, expectedPadding));
			verifyCSSValueOfLocatorIsDisplayedCorrectly("padding-left", String.format("(%s)[%s]", CommonLocators.accordionBody(), i), "Accordion body", expectedPadding);
			verifyCSSValueOfLocatorIsDisplayedCorrectly("padding-right", String.format("(%s)[%s]", CommonLocators.accordionBody(), i), "Accordion body", expectedPadding);
			verifyCSSValueOfLocatorIsDisplayedCorrectly("padding-top", String.format("(%s)[%s]", CommonLocators.accordionBody(), i), "Accordion body", expectedPadding);
			verifyCSSValueOfLocatorIsDisplayedCorrectly("padding-bottom", String.format("(%s)[%s]", CommonLocators.accordionBody(), i), "Accordion body", expectedPadding);
		}
	}

	@Step("Verify that Breadcrumb is removed")
	public void verifyBreadCrumbIsRemoved() {
        if (WebUI.checkLocatorIsVisible(CommonLocators.breadCrumb())) {
            logger.failed("Breadcrumb still displayed");
        }
        logger.passed("Breadcrumb is removed");
    }

	public void verifyThatSpecificDialogIsDisplayedAsDesign(String dialogTitle, List<String> allRequiredFields, List<String> allNormalFields, List<String> allCheckboxes, List<String> allButtons) {
        try{
			if (!WebUI.checkLocatorIsVisible(CommonLocators.specificDialog(dialogTitle))){
				logger.failed(String.format("The 'dialogTitle' dialog is not dislayed", dialogTitle));
			}
			logger.passed(String.format("The '%s' dialog is dislayed", dialogTitle));
			List<String> allFields = new ArrayList<>(allNormalFields);
			allFields.addAll(allRequiredFields);
			for (String fieldName : allFields){
				if (!WebUI.checkLocatorIsVisible(CommonLocators.specificInputFieldInDialog(fieldName), 2)){
					logger.failed(String.format("The '%s' field is not displayed in the '%s' dialog", fieldName, dialogTitle));
				}
				logger.passed(String.format("The '%s' field is displayed in the '%s' dialog", fieldName, dialogTitle));
			}
	
			for (String fieldName : allRequiredFields){
				if (!WebUI.checkLocatorIsVisible(CommonLocators.specificRequiredField(fieldName), 2)){
					logger.failed(String.format("The '%s' field is not displayed with required field in the '%s' dialog", fieldName, dialogTitle));
				}
				logger.passed(String.format("The '%s' field is displayed with required field in the '%s' dialog", fieldName, dialogTitle));
			}
	
			for (String checkboxName : allCheckboxes){
				WebUI.check(CommonLocators.specificCheckboxInDialog(checkboxName));
				if (!WebUI.checkLocatorCheckedOrNot(CommonLocators.specificCheckboxInDialog(checkboxName), true, 2)){
					logger.failed(String.format("The '%s' checkbox in the '%s' dialog is unchecked after check", checkboxName, dialogTitle));
				}
				logger.passed(String.format("The '%s' checkbox in the '%s' dialog is checked", checkboxName, dialogTitle));
				Utils.takeWindowsScreenshot();
				WebUI.uncheck(CommonLocators.specificCheckboxInDialog(checkboxName));
				if (!WebUI.checkLocatorCheckedOrNot(CommonLocators.specificCheckboxInDialog(checkboxName), false, 2)){
					logger.failed(String.format("The '%s' checkbox in the '%s' dialog is checked after check", checkboxName, dialogTitle));
				}
				logger.passed(String.format("The '%s' checkbox in the '%s' dialog is unchecked", checkboxName, dialogTitle));
				Utils.takeWindowsScreenshot();
			}
			for (String buttonName : allButtons){
				if (!WebUI.checkLocatorIsVisible(CommonLocators.specificButton(buttonName), 2)){
					logger.failed(String.format("The '%s' button is not displayed in the 'Add Resourcer' dialog", buttonName));
				}
				logger.passed(String.format("The '%s' button is displayed in the 'Add Resourcer' dialog", buttonName));
			}

			WebUI.click(CommonLocators.specificButton("Save"));
			WebUI.delay(1);
			WebUI.click(CommonLocators.specificDialogTitle(dialogTitle));
			String expectedRedColor = "#F44336";
			for (String fieldName : allRequiredFields){
				verifyCSSValueOfLocatorIsDisplayedCorrectly("caret-color", CommonLocators.specificInputFieldInDialog(fieldName), String.format("Required field '%s'", fieldName), expectedRedColor);
			}
			WebUI.click(CommonLocators.specificButton("Cancel"));
			if (!WebUI.checkLocatorNotVisible(CommonLocators.specificDialog(dialogTitle), 5)){
				logger.failed(String.format("The 'dialogTitle' dialog still dislayed after click on the 'Cancel' button", dialogTitle));
			}
			logger.passed(String.format("The '%s' dialog is disappreared after click on the 'Cancel' button", dialogTitle));
		}
		finally{
			closeDialogIfExists();
		}
		
    }

    protected void waitForSpinnerToDisappearIfExists(int appearTimeout, int disappearTimeout) {
		if (WebUI.checkLocatorPresent(CommonLocators.spinner(), appearTimeout)){
			WebUI.waitForLocatorNotPresent(CommonLocators.spinner(), disappearTimeout);
		}
	}

    protected void waitForSpinnerToDisappearIfExists() {
		waitForSpinnerToDisappearIfExists(2, 30);
	}

	protected void verifyThatSpecificValueIsDisplayedInTheFilterOfSpecificTableOrNOT(String tableId, String filterField, String filterValue, boolean isDisplayed) {
		WebUI.scrollToLocator(CommonLocators.specificFilterButtonOnSpecificTable(filterField, tableId));
		WebUI.click(CommonLocators.specificFilterButtonOnSpecificTable(filterField, tableId));
		WebUI.setText(CommonLocators.filterInput(), filterValue);
		if (WebUI.checkLocatorIsVisible(CommonLocators.specificCheckBoxInFilterOption(filterValue), 1)) {
			if (!isDisplayed){
				logger.failed(String.format("Found filter value [%s] for the [%s] column of the [%s] table", filterValue, filterField, tableId));
			}
			logger.passed(String.format("Found filter value [%s] for the [%s] column of the [%s] table", filterValue, filterField, tableId));
		}
		else{
			if (isDisplayed){
				logger.failed(String.format("Not Found filter value [%s] for the [%s] column of the [%s] table", filterValue, filterField, tableId));
			}
			logger.passed(String.format("Not Found filter value [%s] for the [%s] column of the [%s] table", filterValue, filterField, tableId));
		}
		WebUI.click(CommonLocators.specificButton("OK"));
	}

	protected void verifyThatSpecificValueIsNotDisplayedInTheFilterOfSpecificTable(String tableId, String filterField, String filterValue) {
		verifyThatSpecificValueIsDisplayedInTheFilterOfSpecificTableOrNOT(tableId, filterField, filterValue, false);
	}

	protected void verifyThatSpecificValueIsDisplayedInTheFilterOfSpecificTable(String tableId, String filterField, String filterValue) {
		verifyThatSpecificValueIsDisplayedInTheFilterOfSpecificTableOrNOT(tableId, filterField, filterValue, true);
	}

    protected boolean filterOnTable(String tableId, String filterField, List<String> filterValue) {
		WebUI.scrollToLocator(CommonLocators.specificFilterButtonOnSpecificTable(filterField, tableId));
		WebUI.enhancedClick(CommonLocators.specificFilterButtonOnSpecificTable(filterField, tableId));
		if (!WebUI.checkLocatorIsVisible(CommonLocators.specificButton("Clear"), 1)){
			WebUI.enhancedClick(CommonLocators.specificFilterButtonOnSpecificTable(filterField, tableId));
		}
		WebUI.click(CommonLocators.specificButton("Clear"));		
		for (String value : filterValue) {
			WebUI.setTextOneByOne(CommonLocators.filterInput(), value);
			if (!WebUI.checkLocatorIsVisible(CommonLocators.specificCheckBoxInFilterOption(value))) {
                logger.info(String.format("Not found filter value [%s] for the [%s] column of the [%s] table", value, filterField, tableId));
				WebUI.click(CommonLocators.specificButton("OK"));
				waitForSpinnerToDisappearIfExists();
				return false;
			}
			WebUI.click(CommonLocators.specificCheckBoxInFilterOption(value));
		}
        logger.passed(String.format("Filtered '%s' by [%s] on the '%s' table", filterField, String.join(", ", filterValue), tableId));
		Utils.takeWindowsScreenshot();
		WebUI.click(CommonLocators.specificButton("OK"));
		waitForSpinnerToDisappearIfExists();
		return true;
	}


	protected boolean filterOnTable(String tableId, String filterField, String filterValue) {
		return filterOnTable(tableId, filterField, List.of(filterValue));
	}

	protected void filterMutiColumnsonTable(String idtable,Map<String,String> filterCriterial){
		for (Map.Entry<String, String> filteroption : filterCriterial.entrySet()){
			WebUI.enhancedClick(CommonLocators.specificFilterButtonOnSpecificTable(filteroption.getKey(), idtable));
			WebUI.delay(1);
			if (WebUI.checkLocatorIsVisible(CommonLocators.specificButton("Clear"), 1)){
				WebUI.click(CommonLocators.specificButton("Clear"));
				WebUI.delay(1);
			}
			WebUI.setTextOneByOne(CommonLocators.filterInput(), filteroption.getValue());
			WebUI.delay(2);
			if (!WebUI.checkLocatorIsVisible(CommonLocators.specificCheckBoxInFilterOption(filteroption.getValue()))) {
                logger.error(String.format("Not found filter value [%s] for the [%s] column on the table", filteroption.getValue(), filteroption.getKey()));
				WebUI.delay(1);	
			} else {
				WebUI.click(CommonLocators.specificCheckBoxInFilterOption(filteroption.getValue()));
				WebUI.delay(1);
				WebUI.takeFullPageScreenshot();
				WebUI.click(CommonLocators.specificButton("OK"));
				waitForSpinnerToDisappearIfExists();
				logger.info(String.format("filter value [%s] for the [%s] column on the table successfully", filteroption.getValue(), filteroption.getKey()));
			}
		}
	}

    @Step("Verify toast message is displayed correctly [{expectedTitle} - {expectedContent}]")
    public void verifyToastMessage(String expectedTitle, String expectedContent) {
		WebUI.waitForLocatorVisible(CommonLocators.toastMessageTitle(), 10);
		String toastMessageTitle = WebUI.getText(CommonLocators.toastMessageTitle()).trim();
		String toastMessageContent = WebUI.getText(CommonLocators.toastMessageContent()).trim();
		if (expectedContent != null && !expectedContent.trim().isEmpty()) {
			if (!toastMessageTitle.toLowerCase().equals(expectedTitle.toLowerCase()) && toastMessageContent != expectedContent) {
				logger.failed(String.format("Actual toast message displayed is [%s - %s], different from expected toast message title [%s - %s]", toastMessageTitle, toastMessageContent, expectedTitle, expectedContent));
			}
			else {
				logger.passed(String.format("Actual toast message displayed is [%s - %s], same as expected toast message title [%s - %s]", toastMessageTitle, toastMessageContent, expectedTitle, expectedContent));
			}
		}
		else {
			if (!toastMessageTitle.toLowerCase().equals(expectedTitle.toLowerCase())) {
				logger.failed(String.format("Actual toast message displayed is [%s - %s], different from expected toast message title [%s - %s]", toastMessageTitle, toastMessageContent, expectedTitle, expectedContent));
			}
			else {
				logger.passed(String.format("Actual toast message displayed is [%s - %s], same as expected toast message title [%s - %s]", toastMessageTitle, toastMessageContent, expectedTitle, expectedContent));
			}
		}
	}

    protected void verifyToastMessage(String expectedTitle) {
        verifyToastMessage(expectedTitle, null);
    }

    protected void verifyToastMessage() {
        verifyToastMessage("success", null);
    }

	protected void waitForToastMessageIsDisappreared() {
        if (WebUI.checkLocatorPresent(CommonLocators.toastMessageContent(), 1)){
			WebUI.waitForLocatorNotVisible(CommonLocators.toastMessageContent(), 10);
		}
    }

	@Step("Verify no toast messages are displayed")
	protected void verifyNoToastMessagesAreDisplayed() {
		if (WebUI.checkLocatorIsVisible(CommonLocators.toastMessageTitle(), 2)) {
			String toastMessageTitle = WebUI.getText(CommonLocators.toastMessageTitle()).trim();
			String toastMessageContent = WebUI.getText(CommonLocators.toastMessageContent()).trim();
			logger.failed(String.format("Found a toast message [%s - %s]", toastMessageTitle, toastMessageContent));
		}
		else {
			logger.passed("There are no toast messsages are displayed");
		}
	}

	@Step("Verify the error message is displayed correctly in the dialog")
    public void verifyTheErrorMessageIsDisplayedCorrectlyInTheDialog(String expectedErrorMessage) {
        if (WebUI.checkLocatorIsVisible(CommonLocators.duplicateErrorMessageInDialog())){
            String actualErrorMessage = WebUI.getText(CommonLocators.duplicateErrorMessageInDialog());
            if (!actualErrorMessage.equals(expectedErrorMessage)){
                logger.failed(String.format("Actual error message is displayed in dialog is '%s', different from expected messsage '%s'", actualErrorMessage, expectedErrorMessage));
            }
            logger.passed(String.format("Actual error message is displayed in dialog is '%s', same as expected messsage '%s'", actualErrorMessage, expectedErrorMessage));
        }
    }

	
    public void verifyNoRecordFoundMessageIsDisplayedWhenTheTableDoesNotHaveAnyRecords(String tableID, String searchField, String searchValue) {
		waitForToastMessageIsDisappreared();
        selectOptionFromDropdown(CommonLocators.searchColumnOfSpecificTable(tableID), searchField);
		WebUI.delay(1);
        WebUI.setTextOneByOne(CommonLocators.searchValueOfSpecificTable(tableID), searchValue);
        if (!WebUI.checkLocatorIsVisible(CommonLocators.noRecordFoundMessageOfSpecificTable(tableID))){
            logger.failed(String.format("The 'No Record Found' message is NOT displayed when the table does not have any records"));
        }
        logger.passed(String.format("The 'No Record Found' message is displayed when the table does not have any records"));
    }
 
    protected boolean verifyTabSelected(String locator, String locatorName, Boolean stopOnFailed) {
        if (stopOnFailed == null) {
            stopOnFailed = true;
        }
        String isSelected = WebUI.getAttribute(locator, "aria-selected");
        if ("true".equals(isSelected)) {
            logger.passed(String.format("Tab '%s' is selected", locatorName));
            return true;
        }
        if (stopOnFailed) {
            logger.failed("Tab is not selected.");
        } else {
            logger.info(String.format("Tab '%s' is not selected", locatorName));
        }
        return false;
    }

	protected void waitForPageLoad(int timeout) {
        WebUI.waitForPageLoad(timeout);
    }
	protected String getIdOfspecificLocator(String locator){
		String id=null;
		if(WebUI.checkLocatorIsVisible(locator)){
		id =WebUI.getAttribute(locator, "id");
		}
		logger.info(String.format("ID of locator: %s", id));
		return id;
	}

    //=========================================================== Table Handle ===========================================================

    protected List<String> getAllSortableColumnsOfSpecificTable(String tableId) {
		WebUI.waitForLocatorVisible(CommonLocators.specificTableColumns(tableId));
		if (WebUI.getNumberOfLocator(CommonLocators.allSortableColumnsOfSpecificTable(tableId)) == 0) {
			logger.failed(String.format("There are no sortable columns are displayed in the [%s] table", tableId));
		}
		List<String> allSortColumns = WebUI.getAllTextContents(CommonLocators.allSortableColumnsOfSpecificTable(tableId));
		logger.objectInfo(String.format("All sortable columns of the [%s] table are: %s", tableId, String.join(", ", allSortColumns)));
		return allSortColumns;
	}

	protected List<String> getAllColumnsOfSpecificTable(String tableId) {
		WebUI.waitForLocatorVisible(CommonLocators.specificTableColumns(tableId));
		if (WebUI.getNumberOfLocator(CommonLocators.allColumnsOfSpecificTable(tableId)) == 0) {
			logger.failed(String.format("There are no columns are displayed in the [%s] table", tableId));
		}
		List<String> allColumns =  WebUI.getAllTextContents(CommonLocators.allColumnsOfSpecificTable(tableId)).stream()
                .map(s -> s.replace("arrow_drop_down", "").trim())
                .collect(Collectors.toList());
		logger.objectInfo(String.format("All columns of the [%s] table are: %s", tableId, String.join(", ", allColumns)));
		return allColumns;
	}

	protected List<String> getAllFilterableColumnsOfSpecificTable(String tableId) {
		WebUI.waitForLocatorVisible(CommonLocators.specificTableColumns(tableId));
		if (WebUI.getNumberOfLocator(CommonLocators.allFilterableColumnsOfSpecificTable(tableId)) == 0) {
			logger.failed(String.format("There are no filterable columns are displayed in the [%s] table", tableId));
		}
		List<String> allSortColumns = WebUI.getAllTextContents(CommonLocators.allFilterableColumnsOfSpecificTable(tableId)).stream()
				.map(s -> s.replace("arrow_drop_down", "").trim())
				.collect(Collectors.toList());
		logger.objectInfo(String.format("All filterable columns of the [%s] table are: %s", tableId, String.join(", ", allSortColumns)));
		return allSortColumns;
	}

	protected int getColumnIndexOfSpecificTable(String tableId, String columnName) {
		List<String> allColumns = getAllColumnsOfSpecificTable(tableId);
		if (!allColumns.contains(columnName)){
			logger.failed(String.format("Not found [%s] column from the [%s] table", columnName, tableId));
		}
		return allColumns.indexOf(columnName) + 1;
	}

	protected List<Integer> getSomeColumnIndexOfSpecificTable(String tableId, List<String> columnNames) {
		List<Integer> allColumnIndex = new ArrayList<>();
		List<String> allColumns = getAllColumnsOfSpecificTable(tableId);
		for (String columnName : columnNames){
			if (!allColumns.contains(columnName)){
				logger.failed(String.format("Not found [%s] column from the [%s] table", columnName, tableId));
			}
			allColumnIndex.add(allColumns.indexOf(columnName) + 1);
		}
		return allColumnIndex;
	}

	protected List<String> getValueOfSpecificColumn(String tableId, String columnName) {
        List<String> values = new ArrayList<>();
        if (WebUI.checkLocatorIsVisible(CommonLocators.noRecordFoundMessageOfSpecificTable(tableId), 1)) {
			logger.warn(String.format("'No Record Found' from the [%s] table", tableId));
            return values;
        }
        int columnIndex = getColumnIndexOfSpecificTable(tableId, columnName);
		while(true){
			values.addAll(WebUI.getAllTextContents(CommonLocators.allRowWithColumnIndexOfSpecificTable(tableId, columnIndex)));
			Utils.takeWindowsScreenshot();
			if (WebUI.checkLocatorIsVisible(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"))){
                if (WebUI.checkLocatorHasAttributeOrNot(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"), "disabled", true)){ 
					WebUI.click(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "First page"));
					break;
				}
                WebUI.click(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"));
            } else {
				break;
			}
		}
		
        return values;
    }

	protected List<String> getValueOfSpecificColumnToPage(String tableId, String columnName, int pageNo) {
        List<String> values = new ArrayList<>();
        if (WebUI.checkLocatorIsVisible(CommonLocators.noRecordFoundMessageOfSpecificTable(tableId), 1)) {
			logger.warn(String.format("'No Record Found' from the [%s] table", tableId));
            return values;
        }
        int columnIndex = getColumnIndexOfSpecificTable(tableId, columnName);
		int count = 0;
		while(count < pageNo){
			WebUI.delay(0.5);
			waitForSpinnerToDisappearIfExists();
			values.addAll(WebUI.getAllTextContents(CommonLocators.allRowWithColumnIndexOfSpecificTable(tableId, columnIndex)));
			Utils.takeWindowsScreenshot();
			if (WebUI.checkLocatorIsVisible(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"))){
                if (WebUI.checkLocatorHasAttributeOrNot(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"), "disabled", true)){ 
					WebUI.click(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "First page"));
					break; 
				}
                WebUI.click(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"));
				count += 1;
            } else { break;}
		}
		if (WebUI.checkLocatorHasAttributeOrNot(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "First page"), "disabled", false)){
			WebUI.click(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "First page"));
			WebUI.delay(0.5);
			waitForSpinnerToDisappearIfExists();
		}
		if (count < pageNo){
			logger.failed(String.format("The table [%s] does not have enough pages to get the data from page %s", tableId, pageNo));
		}
		else {
			logger.passed(String.format("The table [%s] has enough pages to get the data from page %s", tableId, pageNo));
		}
		if (values.size() == 0){
			logger.failed(String.format("Not found any values from the [%s] column of the [%s] table", columnName, tableId));
		}
        return values;
    }

	protected Map<String, List<String>> getValueOfSomeColumns(String tableId, List<String> columnNames) {
		Map<String, List<String>> allTableData = new HashMap<>();
        columnNames.forEach(key -> allTableData.put(key, new ArrayList<>()));
        if (WebUI.checkLocatorIsVisible(CommonLocators.noRecordFoundMessageOfSpecificTable(tableId))) {
			logger.warn(String.format("'No Record Found' from the [%s] table", tableId));
            return allTableData;
        }
		List<Integer> allColumnIndex = getSomeColumnIndexOfSpecificTable(tableId, columnNames);
		while(true){
			for (int i = 0; i < allColumnIndex.size(); i++){
				allTableData.get(columnNames.get(i)).addAll(WebUI.getAllTextContents(CommonLocators.allRowWithColumnIndexOfSpecificTable(tableId, allColumnIndex.get(i))));
			}
			if (!WebUI.checkLocatorIsVisible(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"), 1) 
					|| !WebUI.checkLocatorIsEnabled(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"), 1)){
				break;
			}
			WebUI.click(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"));
		}
        return allTableData;
    }

	public Map<String, List<String>> getValueAccordionTableOfSomeColumnsWithIndexRow(String Accordion,int row ,List<String> columnNames) {
		Map<String, List<String>> allTableData = new HashMap<>();
        columnNames.forEach(key -> allTableData.put(key, new ArrayList<>()));
		String tableId=getIdOfspecificLocator(CommonLocators.specificAccordionTableParent(Accordion,0));
        if (WebUI.checkLocatorIsVisible(CommonLocators.specificAccordionTableNoRecord(Accordion))) {
			logger.warn(String.format("'No Record Found' from the [%s] table", Accordion));
            return allTableData;
        }
		List<Integer> allColumnIndex = getSomeColumnIndexOfSpecificTable(tableId, columnNames);
		while(true){
			for (int i = 0; i < allColumnIndex.size(); i++){
				allTableData.get(columnNames.get(i)).addAll(WebUI.getAllTextContents(CommonLocators.specificAccordionTableWithColumnIndexAndRowIndex(Accordion,row,allColumnIndex.get(i))));
			}
			if (!WebUI.checkLocatorIsVisible(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"), 1) 
					|| !WebUI.checkLocatorIsEnabled(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"), 1)){
				break;
			}
			WebUI.click(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"));
		}
		WebUI.takeFullPageScreenshot();
		logger.info(String.format("Data value: %s", allTableData));
        return allTableData;
    }

	protected Map<String, List<String>> getAllTableData(String tableId) {
		Map<String, List<String>> allTableData = new HashMap<>();
		List<String> allColumns = getAllColumnsOfSpecificTable(tableId);
        allColumns.forEach(key -> allTableData.put(key, new ArrayList<>()));
        if (WebUI.checkLocatorIsVisible(CommonLocators.noRecordFoundMessageOfSpecificTable(tableId))) {
			logger.warn(String.format("'No Record Found' from the [%s] table", tableId));
            return allTableData;
        }
		while(true){
			int columnIndex = 0;
			for (List<String> columnData : allTableData.values()){
				columnIndex += 1;
				columnData.addAll(WebUI.getAllTextContents(CommonLocators.allRowWithColumnIndexOfSpecificTable(tableId, columnIndex)));
			}
			if (!WebUI.checkLocatorIsVisible(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"), 1) 
					|| !WebUI.checkLocatorIsEnabled(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"), 1)){
				break;
			}
			WebUI.click(CommonLocators.specificPaginationButtonOfSpecificTable(tableId, "Next page"));
		}
        return allTableData;
    }

	protected Map<String, String> getTableDataOfSpecificRow(String tableId, int rowIndex) {
		Map<String, String> rowTableData = new HashMap<>();
		List<String> allColumns = getAllColumnsOfSpecificTable(tableId);
        if (WebUI.checkLocatorIsVisible(CommonLocators.noRecordFoundMessageOfSpecificTable(tableId))) {
			logger.warn(String.format("'No Record Found' from the [%s] table", tableId));
            return rowTableData;
        }
		int columnIndex = 0;
		for (String columnName : allColumns){
			columnIndex += 1;
			rowTableData.put(columnName, WebUI.getText(CommonLocators.specificCellWithoutSelectBoxInTableWithIndex(tableId, rowIndex, columnIndex)));
		}
        return rowTableData;
    }

	protected String getTableDataOfSpecificRowIndexAndColumnName(String tableId, int rowIndex, String columnName) {
        if (WebUI.checkLocatorIsVisible(CommonLocators.noRecordFoundMessageOfSpecificTable(tableId))) {
			logger.warn(String.format("'No Record Found' from the [%s] table", tableId));
            return null;
        }
		int columnIndex = getColumnIndexOfSpecificTable(tableId, columnName);
		return WebUI.getText(CommonLocators.specificCellWithoutSelectBoxInTableWithIndex(tableId, rowIndex, columnIndex));
    }

	protected String getTableDataOfSpecificRowWithFilterDataAndColumnName(String tableId, String returnColumnName, String filterColumnName, String filterValue) {
        if (WebUI.checkLocatorIsVisible(CommonLocators.noRecordFoundMessageOfSpecificTable(tableId))) {
			logger.warn(String.format("'No Record Found' from the [%s] table", tableId));
            return null;
        }
		List<Integer> columnIndexs = getSomeColumnIndexOfSpecificTable(tableId, List.of(returnColumnName, filterColumnName));
		return WebUI.getText(CommonLocators.specificCellWithoutSelectBoxInTableWithFilterData(tableId, columnIndexs.get(0), columnIndexs.get(1), filterValue));
    }

	protected Map<Integer, Map<String, String>> getTableDataOfSomeRows(String tableId, List<Integer> allRowIndex) {
		Map<Integer, Map<String, String>> rowsTableData = new HashMap<>();
		List<String> allColumns = getAllColumnsOfSpecificTable(tableId);
        if (WebUI.checkLocatorIsVisible(CommonLocators.noRecordFoundMessageOfSpecificTable(tableId))) {
			logger.warn(String.format("'No Record Found' from the [%s] table", tableId));
            return rowsTableData;
        }
		int columnIndex = 0;
		for (int rowIndex : allRowIndex){
			Map<String, String> rowTableData = new HashMap<>();
			for (String columnName : allColumns){
				columnIndex += 1;
				rowTableData.put(columnName, WebUI.getText(CommonLocators.specificCellWithoutSelectBoxInTableWithIndex(tableId, rowIndex, columnIndex)));
			}
			rowsTableData.put(rowIndex, rowTableData);
		}
        return rowsTableData;
    }

	protected String locateTableFixedColumn(String tableId) {
		List<String> listHeader = getAllColumnsOfSpecificTable(tableId);
		String fixedColumn = "";
		int fixedColumnIndex = 0;
		for (String headerItem : listHeader) {
			if (headerItem.contains("Action")) {
				if (WebUI.getAttribute(CommonLocators.actionColumnInSpecificTable(tableId), "class").contains("mat-mdc-table-sticky-border-elem-left")) {
					fixedColumn = headerItem;
					break;
				}
			} else {
					if (WebUI.getAttribute(CommonLocators.specificColumnInTableWithName(tableId, headerItem), "class").contains("mat-mdc-table-sticky-border-elem-left")) {
					fixedColumn = headerItem;
					break;
				}
			}
		}
		if (fixedColumn != "") {
			logger.objectInfo(String.format("Fixed column of the [%s] table is: %s at index %s", tableId, fixedColumn, fixedColumnIndex));
		}
		else
			logger.objectInfo(String.format("The table [%s]: %s does not have a fixed column" , tableId));	
		return fixedColumn;
	}

	public void takeScreenshotsOfTableByHorizontal(String tableId) {
		String fixedColumn = locateTableFixedColumn(tableId);
		List<String> headers = getAllColumnsOfSpecificTable(tableId);
		int parentDepth = 1;
		String parent = CommonLocators.specificTableParent(tableId, parentDepth);
		while (!WebUI.getProperty(parent, "tagName").equals("div")) {
			parentDepth += 1;
			if (parentDepth > 5) {
				logger.failed(String.format("The parent of the table [%s] is not found", tableId));
				break;
			}
			parent = CommonLocators.specificTableParent(tableId, parentDepth);
		}
		int tableOffsetWidth = Integer.parseInt(WebUI.getProperty(parent, "offsetWidth"));
		int scrollWidth = Integer.parseInt(WebUI.getProperty(parent,"scrollWidth"));
		Utils.takeWindowsScreenshot();
		if(scrollWidth > tableOffsetWidth) {
			int noOfScroll = 0;
			int scrollOffsetWidth = 0;
			int offsetWidth = 0;
			int fixedOffsetWidth = 0;
			int colIndex = 0;
			if(fixedColumn != "") {
				if (fixedColumn.contains("Action"))
					fixedOffsetWidth = Integer.parseInt(WebUI.getProperty(CommonLocators.actionColumnInSpecificTable(tableId), "offsetWidth"));
				else {
					colIndex = getColumnIndexOfSpecificTable(tableId, fixedColumn);
					for(int i=1; i<=colIndex; i++) {
						fixedOffsetWidth += Integer.parseInt(WebUI.getProperty(CommonLocators.specificColumnInTableWithIndex(tableId, i), "offsetWidth"));
					}
				}
			}
			offsetWidth = tableOffsetWidth - fixedOffsetWidth;
			int offsetW = 0;
			for(int i=colIndex; i<headers.size(); i++) {
				if (i==0)
					offsetW= Integer.parseInt(WebUI.getProperty(CommonLocators.actionColumnInSpecificTable(tableId), "offsetWidth"));
				else
					offsetW= Integer.parseInt(WebUI.getProperty(CommonLocators.specificColumnInTableWithIndex(tableId, i), "offsetWidth"));
				if(offsetW + scrollOffsetWidth > offsetWidth) {
					break;
				}
				scrollOffsetWidth += offsetW;
			}
			noOfScroll = (int) Math.floor((scrollWidth - fixedOffsetWidth)/(scrollOffsetWidth + 1));
			for(int i=0; i<noOfScroll; i++) {
				WebUI.scrollElementToPosition(parent, scrollOffsetWidth, 0);
				WebUI.delay(1);
				Utils.takeWindowsScreenshot();
			}
			WebUI.scrollElementToPosition(parent, scrollWidth * -1, 0);
		}
	}

	public void takeScreenshotOfFullTable(String tableId, Boolean fullPage) {

	}

	protected void verifySortFunctionOfColumn(String tableId, String columnName, Boolean fullTable) {
		Boolean ascFirst = true;
		logger.step(String.format("Verify the sort ascendent function of the [%s] column in the [%s] table", columnName, tableId));
			WebUI.click(CommonLocators.specificColumnInTableWithName(tableId, columnName));
			waitForSpinnerToDisappearIfExists();
			List<String> allColumnData = new ArrayList<>();
			if (fullTable) {
				allColumnData = getValueOfSpecificColumn(tableId, columnName);
			} else {
				allColumnData = getValueOfSpecificColumnToPage(tableId, columnName, 2);
			}
			List<String> sortedColumnData = new ArrayList<>(allColumnData);
			sortedColumnData.sort((a, b) -> Utils.compareString(a,b));
			if (!allColumnData.equals(sortedColumnData)) {
				sortedColumnData = new ArrayList<>(allColumnData);
				sortedColumnData.sort((a, b) -> Utils.compareString(b,a));
				if (!allColumnData.equals(sortedColumnData)) {
					logger.failed(String.format("The column [%s] of the [%s] table is not sorted correctly", columnName, tableId));
				} else {
					ascFirst = false;
					logger.passed(String.format("The column [%s] of the [%s] table is sorted descendent correctly", columnName, tableId));
				}
			} else {
				logger.passed(String.format("The column [%s] of the [%s] table is sorted ascendent correctly", columnName, tableId));
			}

		logger.step(String.format("Verify the sort descendent function of the [%s] column in the [%s] table", columnName, tableId));
			WebUI.click(CommonLocators.specificColumnInTableWithName(tableId, columnName));
			waitForSpinnerToDisappearIfExists();
			if (fullTable) {
				allColumnData = getValueOfSpecificColumn(tableId, columnName);
			} else {
				allColumnData = getValueOfSpecificColumnToPage(tableId, columnName, 2);
			}
			sortedColumnData = new ArrayList<>(allColumnData);
			if (ascFirst == true)
				sortedColumnData.sort((a, b) -> Utils.compareString(b,a));
			else
				sortedColumnData.sort((a, b) -> Utils.compareString(a,b));
			if (!allColumnData.equals(sortedColumnData)) {
				if (ascFirst)
					logger.failed(String.format("The column [%s] of the [%s] table is not sorted descendent correctly", columnName, tableId));
				else
					logger.failed(String.format("The column [%s] of the [%s] table is not sorted ascendent correctly", columnName, tableId));
			} else {
				if (ascFirst)
					logger.passed(String.format("The column [%s] of the [%s] table is sorted descendent correctly", columnName, tableId));
				else
					logger.passed(String.format("The column [%s] of the [%s] table is sorted ascendent correctly", columnName, tableId));
			}
	}


    protected List<String> filterAndGetDataListFromTable(String tableID, Map<String, String> filterColumns, String getListColumn, boolean reset) {
        waitForSpinnerToDisappearIfExists();
		for (Map.Entry<String, String> info : filterColumns.entrySet()) {
			filterOnTable(tableID, info.getKey(), info.getValue());            
        }
        List<String> dataListFromTable = getValueOfSpecificColumn(tableID, getListColumn);
		if(reset){
			List<Map.Entry<String, String>> entries = new ArrayList<>(filterColumns.entrySet());
			Collections.reverse(entries);
			for (Map.Entry<String, String> info : entries) {
				WebUI.scrollToLocator(CommonLocators.specificFilterButtonOnSpecificTable(info.getKey(), tableID));
				WebUI.enhancedClick(CommonLocators.specificFilterButtonOnSpecificTable(info.getKey(), tableID));
				WebUI.click(CommonLocators.actionBtn("Select All"));
				WebUI.click(CommonLocators.specificButton("OK"));
	
			}
		}        
        return dataListFromTable;        
    }

	protected List<String> getDataListFromFilter(String tableID, String getListColumn) {
        waitForSpinnerToDisappearIfExists();        
        WebUI.scrollToLocator(CommonLocators.specificFilterButtonOnSpecificTable(getListColumn, tableID));
		WebUI.enhancedClick(CommonLocators.specificFilterButtonOnSpecificTable(getListColumn, tableID));

        List<String> dataListFromFilter = WebUI.getAllTextContents(CommonLocators.valueFromFilter());
		WebUI.click(CommonLocators.specificButton("OK"));
		return dataListFromFilter;        
    }

	protected List<String> filterAndGetDataListFromFilter(String tableID, Map<String, String> filterColumns, String getListColumn, boolean reset) {
        waitForSpinnerToDisappearIfExists();
        for (Map.Entry<String, String> info : filterColumns.entrySet()) {
			filterOnTable(tableID, info.getKey(), info.getValue());            
        }
        List<String> dataListFromFilter = getDataListFromFilter(tableID, getListColumn);
		if(reset){
			List<Map.Entry<String, String>> entries = new ArrayList<>(filterColumns.entrySet());
			Collections.reverse(entries);
			for (Map.Entry<String, String> info : entries) {
				WebUI.scrollToLocator(CommonLocators.specificFilterButtonOnSpecificTable(info.getKey(), tableID));
				WebUI.enhancedClick(CommonLocators.specificFilterButtonOnSpecificTable(info.getKey(), tableID));
				WebUI.click(CommonLocators.actionBtn("Select All"));
				WebUI.click(CommonLocators.specificButton("OK"));	
			}
		}  
		return dataListFromFilter;        
    }

	protected void verifyListValuesBetweenTableAndFilterWithFilter(String tableID, Map<String, String> filterColumns, String getListColumn, boolean resetTable, boolean resetFilter){
        List<String> listValueTable = filterAndGetDataListFromTable(tableID, filterColumns, getListColumn, resetTable);
        List<String> listValueFilter = filterAndGetDataListFromFilter(tableID, filterColumns, getListColumn, resetFilter);
        if (!new HashSet<>(listValueTable).equals(new HashSet<>(listValueFilter))) {
            logger.failed("List values between Table and Filter are NOT the same after filter");
        } logger.passed("List values between Table and Filter are the same after filter");                
    }

    protected void verifyListValuesBetweenTableAndFilterWithOutFilter(String tableID, String getListColumn){
        List<String> listValueTable = getValueOfSpecificColumn(tableID, getListColumn);
        List<String> listValueFilter = getDataListFromFilter(tableID, getListColumn);
        if (!new HashSet<>(listValueTable).equals(new HashSet<>(listValueFilter))) {
            logger.failed("List values between Table and Filter are NOT the same WITH OUT filter");
        } logger.passed("List values between Table and Filter are the same WITH OUT filter");                
    }

	protected int getNumberRecordOfTable(String tableID){
		String headerText = WebUI.getText(CommonLocators.specificTableHeader(tableID)).trim();
		int endIndex = headerText.lastIndexOf(")");
		int startIndex = headerText.lastIndexOf("(", endIndex);
		String countString = headerText.substring(startIndex + 1, endIndex);
		int numberRecord = Integer.parseInt(countString);
		logger.info(String.format("The total record of table %s is %s", tableID, countString));                
		return numberRecord;
	}

	protected void getNumberRecordOnTableHeaderAndCompare(String tableID, int compareNumber){
		int getNumber = getNumberRecordOfTable(tableID);
		if(!(getNumber == compareNumber)){
			logger.failed(String.format("The table header record %s is not the same with compare record %s", getNumber, compareNumber));
        } logger.passed(String.format("The table header record %s is the same with compare record %s", getNumber, compareNumber));
    }
	
    //====================================================================================================================================
	public void selectOptionFromDropdown(String locator, String option) {
		WebUI.click(locator);
		if (!WebUI.checkLocatorIsVisible(CommonLocators.listDropdownOptions(), 2)){
			WebUI.setTextOneByOne(locator, option);
		}
		if (!WebUI.checkLocatorPresent(CommonLocators.specificDropdownOption(option), 2)){
			logger.failed(String.format(""));
		}
		WebUI.click(CommonLocators.specificDropdownOption(option));
		logger.info(String.format("Selected option '%s' from dropdown", option));
		WebUI.takeFullPageScreenshot();
    }

	public void selectMultiOptionFromDropdown(String locator, List<String> options) {
		Robot robot;
	

		WebUI.click(locator);
		for(String option:options){
			if (!WebUI.checkLocatorIsVisible(CommonLocators.listDropdownOptions(), 2)){
				WebUI.setTextOneByOne(locator, option);
			}
			if (!WebUI.checkLocatorPresent(CommonLocators.specificDropdownOption(option), 2)){
				logger.failed(String.format(""));
			}
			WebUI.click(CommonLocators.specificDropdownOption(option));
			logger.info(String.format("Selected option '%s' from dropdown", option));
		}
		try {
			robot=new Robot();
			robot.keyPress(KeyEvent.VK_ESCAPE);
			WebUI.delay(1);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

	protected void selectOptionFromDropdownWithContainsValue(String locator, String option, int optionIndex) {
		WebUI.setTextOneByOne(locator, option);
		if (!WebUI.checkLocatorIsVisible(CommonLocators.specificDropdownOptionContainsWithIndex(option, optionIndex))){
			logger.failed(String.format("Not found any options contains '%s' with index '%s' from dropdown", option, optionIndex));
		}
		WebUI.click(CommonLocators.specificDropdownOptionContainsWithIndex(option, optionIndex));
		logger.info(String.format("Selected the option contains '%s' with index '%s' from dropdown", option, optionIndex));
    }

	protected void selectOptionFromDropdownWithContainsValue(String locator, String option) {
		selectOptionFromDropdownWithContainsValue(locator, option, 1);
    }

	protected void searchCriteria(Map<String, String> options) {
		for (Map.Entry<String, String> option : options.entrySet()) {
			WebUI.click(CommonLocators.specificSearchField(option.getKey()));
            WebUI.setTextOneByOne(CommonLocators.specificSearchField(option.getKey()), option.getValue());
			WebUI.waitForLocatorVisible(CommonLocators.specificDropdownOption(option.getValue()));
			WebUI.click(CommonLocators.specificDropdownOption(option.getValue()));
        }
		WebUI.delay(0.5);
		WebUI.click(CommonLocators.specificButton("Search"));
		waitForSpinnerToDisappearIfExists(5, 60);
		WebUI.delay(0.5); //Wait 0.5 second for table loaded
        logger.passed(String.format("Searched with info '%s'", options));
    }

	@Step("Quick search with [{searchType}] is [{searchValue}] in the [{pageName}] page")
	public void quickSearch(String pageName, String searchType, String searchValue) {
		String tableId = getTableIdFromPageName(pageName);
        WebUI.clickIfExists(CommonLocators.specificTab("Quick"), 2);
        selectOptionFromDropdown(CommonLocators.searchTypeBox(), searchType);
		if (WebUI.checkLocatorPresent(CommonLocators.specificSearchField("Enter a Project ID"), 1)){
			WebUI.setText(CommonLocators.specificSearchField("Enter a Project ID"), searchValue);
		}
        else{
			WebUI.setText(CommonLocators.specificSearchField("Enter Name"), searchValue);
		}
        Utils.takeWindowsScreenshot();
        WebUI.click(CommonLocators.actionBtn("Search"));
        WebUI.waitForLocatorVisible(CommonLocators.specificTableColumns(tableId));
        logger.passed(String.format("Quick search %s with '%s : %s' successfully", pageName, searchType, searchValue));
    }

	@Step("Navigate to View page of item [{index}] from the [{pagaName}] table")
    public void navigateToViewPageOfSpecificItemFromTheTable(String pagaName, int index) {
		String tableId = getTableIdFromPageName(pagaName);
        WebUI.click(CommonLocators.viewButtonOfSpecificRowIndexInSpecificTable(tableId, index));
        WebUI.switchToNewPageAndClosePreviousPage();
        logger.passed(String.format("Navigated to View page of item '%s' from the '%s' table", index, pagaName));
    }

	@Step("Navigate to [{tabName}] tab")
    public void navigateToSpecificTab(String tabName) {
		WebUI.click(CommonLocators.specificTab(tabName));
        waitForSpinnerToDisappearIfExists();
		if (tabName.toLowerCase().equals("assignments")){
			WebUI.uncheck(CommonLocators.specificCheckbox("Active Assignments"));
        	waitForSpinnerToDisappearIfExists();
		}
        logger.passed(String.format("Navigated to the '%s' tab", tabName));
    }

	public void closeDialogIfExists(){
        if (WebUI.checkLocatorIsVisible(CommonLocators.dialog(), 2)){
            WebUI.enhancedClick(CommonLocators.specificIconButton("close"));
        }
		WebUI.clickIfExists(CommonLocators.specificButton("Cancel"), 1);
    }

	@Step("Verify the {fieldName} dropdown list sort")
    public void verifyDropDownListSorted(String locator, boolean ascending, String fieldName, boolean select, List<String> selectList){
        WebUI.click(locator);
        List<String> listData = WebUI.getAllTextContents(CommonLocators.dropDownValue(""));
        logger.objectInfo(String.format("The data from application %s", String.join(", ", listData)));
        List<String> sortedData = sortStringList(listData, ascending);
        logger.objectInfo(String.format("The data after sort %s", String.join(", ", sortedData)));
        if(listData.equals(sortedData)){
            logger.passed(String.format("The data of %s is sorted",fieldName));
        } else {
            logger.failed(String.format("The data of %s NOT is sorted",fieldName));
        }
        if (select) {
            for(String value: selectList){
                WebUI.click(CommonLocators.dropDownValue(value));
            }
        }
        WebUI.click(locator);
    }

	//=========================================================== Calendar ===========================================================

	protected void setDayDateTime(String year, String month, String day){
		WebUI.clickIfExists(CommonLocators.chooseDateBtn(), 1);
        WebUI.enhancedClick(CommonLocators.chooseMonthYearBtn());
        WebUI.enhancedClick(CommonLocators.optionCalendar(year));
        WebUI.enhancedClick(CommonLocators.optionMonth(month.toUpperCase()));
        WebUI.enhancedClick(CommonLocators.optionInCalendar(day));
    }

	protected void setDateTime(String year, String month, String day){
        if(WebUI.checkLocatorIsVisible(CommonLocators.calendarPeriodBtn(),3)){
            WebUI.enhancedClick(CommonLocators.calendarPeriodBtn());
        }
        else {
            WebUI.enhancedClick(CommonLocators.chooseDateBtn());
            WebUI.enhancedClick(CommonLocators.chooseMonthYearBtn());
        }
        if(WebUI.checkLocatorIsVisible(CommonLocators.optionCalendar(year),3)){
            WebUI.enhancedClick(CommonLocators.optionCalendar(year));
        } else if (WebUI.checkLocatorIsVisible(CommonLocators.calendarPeriodBtn(),3)){
            WebUI.enhancedClick(CommonLocators.calendarPeriodBtn());
            WebUI.enhancedClick(CommonLocators.optionCalendar(year));
        }
        WebUI.enhancedClick(CommonLocators.optionMonth(month.toUpperCase()));
        if(day!=null){
            if(day.startsWith("0")){
                day = day.substring(1);
            }
            WebUI.enhancedClick(CommonLocators.optionInCalendar(day));
        }
    }

	protected void setMonthYear(String year,String month){
		do{
			WebUI.enhancedClick(CommonLocators.calendarPeriodBtn());
		}while ((!WebUI.checkLocatorIsVisible(CommonLocators.optionCalendar(year))));
		WebUI.enhancedClick(CommonLocators.optionCalendar(year));
		WebUI.enhancedClick(CommonLocators.optionMonth(month.toUpperCase()));
	}

	@Step("Verify CSS value of pseudo locator in {pageName} page is displayed correctly")
    public void verifyCSSValueOfPseudoLocatorInSpecificPageIsDisplayedCorrectly(String pageName, String locator, String cssAttribute, String locatorName, String expectedValue, boolean click, boolean checkTick, String pseudoElement) {
        if (click){
            WebUI.click(locator);
        }
        if (checkTick){
            String cssValue = WebUI.getCssValueOfPseudoElement(locator, pseudoElement, cssAttribute);
            cssValue = Utils.convertRGBToHex(cssValue);
            if(!cssValue.equals(expectedValue)){
                logger.failed(String.format("Actual '%s' of pseudo locator is '%s', different from expected '%s'", cssAttribute, cssValue, expectedValue));
            }
			logger.passed(String.format("Actual '%s' of pseudo locator is '%s', same as expected '%s'", cssAttribute, cssValue, expectedValue));
        } else {
            verifyCSSValueOfLocatorIsDisplayedCorrectly(cssAttribute, locator, locatorName, expectedValue);
        }
    }

	public void setDateTimeToSpecificLocator(String locator,String DateTimeInput){
		DateTimeInput=DateTime.convertDateFormatAdvanced(DateTimeInput,"yyyy-MMM-dd");
		
		//Format DateTime: yyyy-MMM-dd
		String[] dateInfo=DateTimeInput.split("-");
		
		// System.err.println(dateInfo);
		if(!(WebUI.checkLocatorIsVisible(locator))){
			logger.error(String.format("%s not visible", locator));
		}else {
			WebUI.click(locator);
			WebUI.delay(1);
			if(dateInfo.length==2){
				setMonthYear(dateInfo[0],dateInfo[1]);
				logger.passed(String.format("Set Date successfully:%s - %s",dateInfo[1],dateInfo[0]));
				WebUI.takeFullPageScreenshot();
			}else {
			setDateTime(dateInfo[0],dateInfo[1],dateInfo[2]);
			logger.passed(String.format("Set Date successfully: %s - %s - %s",dateInfo[2],dateInfo[1],dateInfo[0]));
			WebUI.takeFullPageScreenshot();
			}
			
		}
	}

	public void specificExpansionSessionAccordion(String nameSessionAccordion,boolean isExpand){
		String status=WebUI.getAttribute(CommonLocators.specificAccordion(nameSessionAccordion), "aria-expanded");
		if(isExpand==true){
			if(status.equals("false"))
			{
				WebUI.click(CommonLocators.specificAccordion(nameSessionAccordion));
			} else {
				logger.info(String.format("%s already expanded", nameSessionAccordion));
			}
		} else {
			if(status.equals("true"))
			{
				WebUI.click(CommonLocators.specificAccordion(nameSessionAccordion));
			} else {
				logger.info(String.format("%s already collapsed')", nameSessionAccordion));
			}
		}

	}

	public void clickSpecificLocator(String locator){
		WebUI.enhancedClick(locator);
		waitForSpinnerToDisappearIfExists();
	}
	
	@Step("Navigate to Advanced search")
    public void navigateToAdvancedSearch(String moduleName){
        WebUI.click(CommonLocators.specificTab("Advanced"));
        if(verifyTabSelected(CommonLocators.specificTab("Advanced"),"Advanced tab", true)){
            logger.passed(String.format("Navigated to %s Advanced search successfully!",moduleName));
            Utils.takeWindowsScreenshot();
        } else {
            logger.error(String.format("Navigated to %s Advanced search unsuccessfull!",moduleName));
        }
    }

	@Step("Verify the lable of calendar display correctly with Text and color")
	public void validateDatePickerTextandCss(String locator,String expectedBorderFieldColor,String expectedTitleColor,String expectedBorderCurrentDate,String expectedHoverDayColor,String expectedSelectedColor,String DateSelected){
		String currentmonthyear=DateTime.getCurrentDateFormatted("MMMM yyyy");
		String currentDay=DateTime.getCurrentDateFormatted("d");
		if(DateSelected.equals(currentDay)){
		 DateSelected=String.valueOf((Integer.parseInt(currentDay)+1));
		}
		//validate border color of field
		logger.info("Verify the border color of field");
		// if(!WebUI.checkLocatorIsVisible(locator)){
		WebUI.scrollToLocator(locator);
		// }
		WebUI.delay(2);
		clickSpecificLocator(locator);
		String colorBorderField=WebUI.getCssValue(locator, "--mdc-outlined-text-field-focus-outline-color");
		// String hexcolorBorderField=Utils.convertRGBToHex(colorBorderField);
		String hexcolorBorderField=colorBorderField.toUpperCase();
		logger.info(hexcolorBorderField);
		if(hexcolorBorderField.equals(expectedBorderFieldColor)){
            logger.passed(String.format("The color bordor of Date Field display correctly: %s",hexcolorBorderField));
        } else { 
            logger.failed(String.format("The color bordor of Date Field display incorrectly: Actual -  %s | Expected - %s",hexcolorBorderField,expectedBorderFieldColor));
        }
		Utils.takeWindowsScreenshot();

		//validate Text title month - year
		logger.info("Verify the lable of calendar display correctly with Text");
        String label_Name=WebUI.getText(CommonLocators.labelNameCalendar());
		String label_Name_formatted=label_Name.substring(0,1).toUpperCase()+label_Name.substring(1).toLowerCase();
		if(currentmonthyear.trim().equals(label_Name_formatted.trim())){
            logger.passed(String.format("The lable of calendar display correctly: %s",currentmonthyear));
        } else {
            logger.failed(String.format("The lable of calendar display incorrectly: Actual -  %s | Expected - %s",label_Name_formatted,currentmonthyear));
        }
		Utils.takeWindowsScreenshot();

		//validate color title month - year
		logger.info("Verify the lable of calendar display correctly color");
        String headerColorAttribute=WebUI.getCssValue(CommonLocators.labelNameCalendar(), "color");
		String hexColor=Utils.convertRGBToHex(headerColorAttribute);
        if(hexColor.equals(expectedTitleColor)){
            logger.passed(String.format("The color lable of calendar display correctly: %s",hexColor));
        } else { 
            logger.failed(String.format("The color lable of calendar display incorrectly: Actual -  %s | Expected - %s",hexColor,expectedTitleColor));
        }
		Utils.takeWindowsScreenshot();

		//Validate Date color when hovered
		logger.info("Verify background color of day hovered");
		WebUI.hover(CommonLocators.optionInCalendar(DateSelected));
		WebUI.delay(2);
		String hoverDayColor=WebUI.getCssValue(CommonLocators.optionInCalendar(DateSelected), "background-color");
		String hexhoverDayColor=Utils.convertRGBToHex(hoverDayColor);
		if(hexhoverDayColor.equals(expectedHoverDayColor)){
            logger.passed(String.format("The color hover date display correctly: %s",hexhoverDayColor));
        } else { 
            logger.failed(String.format("The color hover date display incorrectly: Actual -  %s | Expected - %s",hexhoverDayColor,expectedHoverDayColor));
        }
		Utils.takeWindowsScreenshot();

		//Validate Date color Selected
		logger.info("Verify background color of day selected");
		clickSpecificLocator(CommonLocators.optionInCalendar(DateSelected));
		Utils.takeWindowsScreenshot();
		WebUI.delay(2);
		logger.info("Reopen calendar");
		clickSpecificLocator(locator);
		String selectedColor=WebUI.getCssValue(CommonLocators.optionInCalendar(DateSelected), "background-color");
		String hexSelectedColor=Utils.convertRGBToHex(selectedColor);
		if(hexSelectedColor.equals(expectedSelectedColor)){
            logger.passed(String.format("The color day selected display correctly: %s",hexSelectedColor));
        } else { 
            logger.failed(String.format("The color hover date display incorrectly: Actual -  %s | Expected - %s",hexSelectedColor,expectedSelectedColor));
        }
		Utils.takeWindowsScreenshot();

				//Validate Date color
		logger.info("Verify border color of current date");
		String borderCurrentDayColor=WebUI.getCssValue(CommonLocators.optionInCalendar(currentDay), "border-color");
		String hexborderCurrentDayColor=Utils.convertRGBToHex(borderCurrentDayColor);
		if(hexborderCurrentDayColor.equals(expectedBorderCurrentDate)){
            logger.passed(String.format("The color border color of current date display correctly: %s",hexborderCurrentDayColor));
        } else { 
            logger.failed(String.format("The color border color of current date display incorrectly: Actual -  %s | Expected - %s",hexborderCurrentDayColor,expectedBorderCurrentDate));
        }
		Utils.takeWindowsScreenshot();
		//Close the dialog
		clickSpecificLocator(CommonLocators.optionInCalendar(DateSelected));
	}

	@Step("Get state of {type} button on calendar")
	public boolean isStateButtonDateOnCalendarDisabled(String locator,String inputDate,String type){
		String[]  splited=inputDate.split("-");
		Robot robot;
		String day;
		String month;
		String year;
		String Attribute="false";
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern(DateTime.detectDateFormatAdvanced(inputDate));
		DateTimeFormatter dayFormatter=DateTimeFormatter.ofPattern("d");
		DateTimeFormatter monthFormater=DateTimeFormatter.ofPattern("MMM");
		DateTimeFormatter yearFormatter=DateTimeFormatter.ofPattern("yyyy");
		if(splited.length==3){
       		LocalDate initialDate = LocalDate.parse(inputDate, dtf);
			day=initialDate.format(dayFormatter);
			month=initialDate.format(monthFormater);
			year=initialDate.format(yearFormatter);		
		}else {
			YearMonth initialDate = YearMonth.parse(inputDate, dtf);
			month=initialDate.format(monthFormater);
			year=initialDate.format(yearFormatter);
			day=null;
		}
		logger.info(String.format("Day: %s, Month: %s, Year: %s", day,month,year));
		WebUI.enhancedClick(locator);
		WebUI.delay(2);
		if(type.toLowerCase()=="day" && splited.length==3){
			do{
				WebUI.enhancedClick(CommonLocators.calendarPeriodBtn());
				WebUI.delay(1);
			}while ((!WebUI.checkLocatorIsVisible(CommonLocators.optionCalendar(year))));
			WebUI.enhancedClick(CommonLocators.optionCalendar(year));
			WebUI.delay(0.5);
			WebUI.enhancedClick(CommonLocators.optionMonth(month.toUpperCase()));
			WebUI.delay(0.5);
			Attribute=WebUI.getAttribute(CommonLocators.optionInCalendar(day), "class");
			WebUI.takeFullPageScreenshot();
		} else if(type.toLowerCase()=="month"){
			do{
				WebUI.enhancedClick(CommonLocators.calendarPeriodBtn());
				WebUI.delay(1);
			}while ((!WebUI.checkLocatorIsVisible(CommonLocators.optionCalendar(year))));
			WebUI.enhancedClick(CommonLocators.optionCalendar(year));
			WebUI.delay(0.5);
			Attribute=WebUI.getAttribute(CommonLocators.optionMonth(month.toUpperCase()), "class");
			WebUI.takeFullPageScreenshot();
		} else if(type.toLowerCase()=="year"){
			do{
				WebUI.enhancedClick(CommonLocators.calendarPeriodBtn());
				WebUI.delay(1);
			}while ((!WebUI.checkLocatorIsVisible(CommonLocators.optionCalendar(year))));
			Attribute=WebUI.getAttribute(CommonLocators.optionCalendar(year), "class");
			WebUI.takeFullPageScreenshot();
		} else {
			logger.error(String.format("Type %s not support with input Date %s ", type,inputDate));
		}
					try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ESCAPE);
			} catch (AWTException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if(Attribute.contains("disabled")){
			if(type=="day"){
				logger.info(String.format("State of button %s %s Disabled", type,day));
				} else if(type=="month"){
					logger.info(String.format("State of button %s %s Disabled", type,month));
					} else {
						logger.info(String.format("State of button %s %s Disabled", type,year));
					}
			return true;
		}else {
			if(type=="day"){
				logger.info(String.format("State of button %s %s Enabled", type,day));
				} else if(type=="month"){
					logger.info(String.format("State of button %s %s Enabled", type,month));
					} else {
						logger.info(String.format("State of button %s %s Enabled", type,year));
					}

			return false;
		}
		
		//Close the Calendar

	}

	//=========================================================== Accordion ===========================================================
	protected void expandAccordion(String accordionName) {
		if (WebUI.getAttribute(CommonLocators.specificAccordion(accordionName), "aria-expanded").equals("false")) {
			WebUI.click(CommonLocators.specificAccordion(accordionName));
			WebUI.delay(1);
		}
	}

	protected List<String> sortStringList(List<String> inputList, boolean ascending){
		List<String> outputList = new ArrayList<>(inputList);
		if (ascending){			
			outputList.sort((a, b) -> Utils.compareString(a,b));
		} else {			
			outputList.sort((a, b) -> Utils.compareString(b,a));
		}		
		return outputList;
	}
}