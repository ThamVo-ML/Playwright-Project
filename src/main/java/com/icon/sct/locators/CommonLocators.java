package com.icon.sct.locators;

import com.icon.sct.utils.Locators;


public class CommonLocators{

    public static String userMenuContainer() {
        return Locators.create("#userMenuContainer");
    } 

    public static String logoutMessage(){
        return Locators.create("//*[contains(@class, 'title') and text() = 'Logout successful']");
    }

    public static String routerContainer() {
        return Locators.create("//*[contains(@class, 'router-container')]");
    }
    
    public static String searchContainer() {
        return Locators.create("//div[contains(@class, 'search-container')]");
    }

    public static String specificMenuExpansion(String menuExpansion) {
        return Locators.create(String.format("//*[contains(@class, 'expansion-panel-header') and normalize-space(text()) = '%s']", menuExpansion));
    }
    
    public static String specificMenuNavigation(String menu) {
        return Locators.create(String.format("//*[contains(@class, 'list-item') and normalize-space(text()) = '%s']", menu));
    }

    public static String specificPageTitle(String pageTitle) {
        return Locators.create(String.format("//*[(contains(@class, 'page-header') or contains(@id, 'Header') or contains(@class, 'title')) and normalize-space(text()) = '%s']", pageTitle));
    }

    public static String pageTitle() {
        return Locators.create("//*[(contains(@class, 'page-header') or contains(@id, 'Header')) and not(contains(@class, 'separator'))]");
    }

    public static String specificInputBasedOnMatlabel(String content){
        return Locators.create(String.format("//mat-label[contains(text(), '%s')]/ancestor::div[contains(@class, 'mat-mdc-form-field')]//input",content));
    }

    public static String specificMatlabelBasedOnText(String text){
        return Locators.create(String.format("//mat-label[contains(text(),'%s')]",text));
    }

    public static String specificMatErrorBasedOnFieldName(String fieldName){
        return Locators.create(String.format("//mat-error[contains(text(),'%s')]",fieldName));
    }

    public static String specificMatSelectBasedOnID(String ID, int index){
        return Locators.create(String.format("(//mat-select[contains(@id, '%s')])[%s]",ID,index));
    }

    public static String specificMatSelectField(String nameField){
        return Locators.create(String.format("//app-drop-down[.//mat-label[normalize-space()='%s']]//mat-select[contains(@id, 'mat-select')]",nameField));
    }

    public static String specificAccordion(String title){
        return Locators.create(String.format("//mat-accordion//mat-expansion-panel-header[.//mat-panel-title//div[normalize-space(text())='%s']]",title));
    }

    public static String demandComparison(String title){
        return Locators.create(String.format("//app-open-demand-comparison//a[contains(@id, 'compare-navigation-link') and normalize-space()='%s']", title));
    }

    public static String sctHomePage(){
        return Locators.create(String.format("//a[@id=\"adminConsole\" and @href=\"/Project/Index\"]"));
    }

    public static String userNavigation(){
        return Locators.create(String.format("//a[@class='nav-link dropdown-toggle']"));
    }

    public static String logOff(){
        return Locators.create(String.format("//a[text()='Log off']"));
    }

    public static String reLogin(){
        return Locators.create(String.format("//a[text()='Re-login']"));
    }

    //=========================================================== Table Locators ===========================================================
    public static String searchColumnOfSpecificTable(String tableId) {
        return Locators.create(String.format("//*[contains(@id, '%s') and contains(@id, 'search-col-label')]", tableId));
    }

    public static String searchValueOfSpecificTable(String tableId) {
        return Locators.create(String.format("//input[contains(@id, '%s') and contains(@id, 'search-input')]", tableId));
    }

    public static String specificTableHeader(String tableId) {
        return Locators.create(String.format("//mat-card-header[contains(@id, '%s')]", tableId));
    }

    public static String specificTableDivContainer(String tableId) {
        return Locators.create(String.format("//div[contains(@id, '%s-table-conteainer')]", tableId));
    }

    public static String specificTableContainer(String tableId) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]", tableId));
    }

    public static String specificTableParent(String tableId, int parentDepth) {
        StringBuilder sb = new StringBuilder(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]", tableId));
        for (int i = 0; i < parentDepth; i++) {
            sb.append("/..");
        }
        return Locators.create(sb.toString());
    }

    public static String specificAccordionTableParent(String Accordion, int parentDepth) {
        StringBuilder sb = new StringBuilder(String.format("//mat-expansion-panel[.//div[normalize-space()='%s']]//table[contains(@class,'mat-sort')]", Accordion));
        for (int i = 0; i < parentDepth; i++) {
            sb.append("/..");
        }
        return Locators.create(sb.toString());
    }

    public static String specificTableColumns(String tableId) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//*[contains(@class, 'header-row')]", tableId));
    }

    public static String specificAccordionTableColumns(String nameAccordion) {
        return Locators.create(String.format("//mat-expansion-panel[.//div[normalize-space()='%s']]//table//*[contains(@class, 'header-row hdear')]", nameAccordion));
    }
    

    public static String specificColumnInTableWithName(String tableId, String columnName) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//th[.//*[normalize-space(text()) = '%s']]", tableId, columnName));
    }

    public static String specificColumnInTableWithIndex(String tableId, int columnIndex) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//th[%s]", tableId, columnIndex));
    }

    public static String actionColumnInSpecificTable(String tableId) {
        return Locators.create(String.format("//table[contains(@id, '%s')]//th[contains(., 'Action')]", tableId));
    }

    public static String specificSortArrowOfColumnInTableWithName(String tableId, String columnName) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//th[.//*[normalize-space(text()) = '%s']]//*[contains(@class, 'sort-header-arrow')]", tableId, columnName));
    }

    public static String specificFilterButtonOnSpecificTable(String columnName, String tableID) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//button[contains(@id,'filter') and (./parent::*[normalize-space(text()) = '%s'] or ./preceding-sibling::*[normalize-space(text()) = '%s'])]", tableID, columnName, columnName));
    }

    public static String specificFilteredIconOnSpecificTable(String columnName, String tableID) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//button[contains(@id,'filter') and (./parent::*[normalize-space(text()) = '%s'] or ./preceding-sibling::*[normalize-space(text()) = '%s'])]//*[@svgicon = 'active_filter' and not(contains(@class, 'hide'))]//*[local-name()='circle']", tableID, columnName, columnName));
    }

    public static String allColumnsOfSpecificTable(String tableID) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//th[contains(@class, 'header') and not(contains(@class, 'checkbox'))]", tableID));
    }

    public static String allSortableColumnsOfSpecificTable(String tableID) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//th[contains(@class, 'sort-header')]//*[contains(@class, 'header-content')]", tableID));
    }

    public static String allFilterableColumnsOfSpecificTable(String tableID) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//th[contains(@class, 'sort-header') and .//*[contains(@id, 'filter')]]//*[contains(@class, 'header-content')]", tableID));
    }

    public static String specificTableRowWithIndex(String tableId, int rowIndex) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//tbody//*[@role = 'row'][%s]", tableId, rowIndex));
    }

    public static String specificCellInTableWithIndex(String tableId, int rowIndex, int columnIndex) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//tbody//*[@role = 'row'][%s]/td[%s]", tableId, rowIndex, columnIndex));
    }

    public static String specificCellWithoutSelectBoxInTableWithIndex(String tableId, int rowIndex, int columnIndex) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//tbody//*[@role = 'row'][%s]/td[not(contains(@class, 'checkbox'))][%s]", tableId, rowIndex, columnIndex));
    }

    public static String specificCellWithoutSelectBoxInTableWithFilterData(String tableId, int columnIndex, int filterColumnIndex, String filterValue) {
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//tbody//*[@role = 'row' and ./*[%s][normalize-space(text()) = '%s']]/td[not(contains(@class, 'checkbox'))][%s]", tableId, filterColumnIndex, filterValue, columnIndex));
    }

    public static String allStatusItemsInTable(String tableId){
        return Locators.create(String.format("//table[contains(@id, '%s') and contains(@class, 'mat-sort')]//tbody//*[@role = 'row']//td//*[contains(@class, 'indicator') and contains(@class, 'inserted')]", tableId));
    }

    public static String specificPaginationButtonOfSpecificTable(String tableID, String buttonName) { // buttonName should be First page, Previous page, Next page or Last page
        return Locators.create(String.format("//*[contains(@id, '%s')]//button[@aria-label = '%s']", tableID, buttonName));
    }

    public static String noRecordFoundMessageOfSpecificTable(String tableID) {
        return Locators.create(String.format("//table[contains(@id, '%s')]//td//*[normalize-space(text()) = 'No Record Found']", tableID));
    }

    public static String allRowOfSpecificTable(String tableID) {
        return Locators.create(String.format("//table[contains(@id, '%s')]//tbody//tr[@role = 'row']", tableID));
    }

    public static String allRowWithColumnIndexOfSpecificTable(String tableID, int columnIndex) {
        return Locators.create(String.format("//table[contains(@id, '%s')]//tbody//tr[@role = 'row']//td[not(contains(@class, 'checkbox'))][%s]", tableID, columnIndex));
    }

    public static String specificAccordionTableNoRecord(String nameAccordion) {
        return Locators.create(String.format("//mat-expansion-panel[.//div[normalize-space()='%s']]//table/tbody//td//*[normalize-space(text()) = 'No Record Found']", nameAccordion));
    }

    public static String specificAccordionTableWithColumnIndexAndRowIndex(String nameAccordion,int rowIndex ,int columnIndex) {
        return Locators.create(String.format("(//mat-expansion-panel[.//div[normalize-space()='%s']]//table/tbody/tr[%s]/td[not(contains(@class, 'checkbox'))])[%s]", nameAccordion, rowIndex, columnIndex));
    }

    public static String selectCheckboxOfSpecificRowInSpecificTable(String tableID, int rowIndex) {
        return Locators.create(String.format("(//table[contains(@id, '%s')]//tbody//tr[@role = 'row']//td[contains(@class, 'checkbox')]//input[%s]", tableID, rowIndex));
    }

    public static String editButtonOfSpecificRowIndexInSpecificTable(String tableID, int rowIndex) {
        return Locators.create(String.format("//*[@id='%s']//tbody/tr[%s]/td/button/*[@data-mat-icon-name='edit' or contains(@id, 'icon-edit')]", tableID, rowIndex));
    }

    public static String editButtonOfSpecificRowDataInSpecificTable(String tableID, String rowData) {
        return Locators.create(String.format("//table[contains(@id, '%s')]//tbody//td[normalize-space(text()) = '%s']/preceding-sibling::td//*[@data-mat-icon-name = 'edit' or contains(@id, 'icon-edit')]", tableID, rowData));
    }

    public static String viewButtonOfSpecificRowIndexInSpecificTable(String tableID, int rowIndex) {
        return Locators.create(String.format("//table[contains(@id, '%s')]//tbody//tr[%s]//*[@data-mat-icon-name = 'visibility']/parent::button", tableID, rowIndex));
    }

    public static String viewButtonOfSpecificRowDataInSpecificTable(String tableID, String rowData) {
        return Locators.create(String.format("//table[contains(@id, '%s')]//tbody//td[normalize-space(text()) = '%s']/preceding-sibling::td//*[@data-mat-icon-name = 'visibility']", tableID, rowData));
    }

    public static String historyNoteButtonOfSpecificRowIndexInSpecificTable(String tableID, int rowIndex, String buttonName) {
        return Locators.create(String.format("(//table[contains(@id, '%s')]//tbody//tr)[%s]//*[contains(text(),'%s')]/parent::button", tableID, rowIndex, buttonName));
    }

    public static String filterInput() {
        return Locators.create("#filter-input");
    }

    public static String valueFromFilter() {
        return Locators.create("//div[contains(@id, 'filter-list-container')]//label//span");
    }

    public static String specificTablePagination(String tableId) {
        return Locators.create(String.format("//*[contains(@id, '%s')]//*[@role = 'group' and contains(@id, 'paginator')]", tableId));
    }

    public static String specificTableCardContainer(String tableId) {
        return Locators.create(String.format("//table[contains(@id, '%s')]/ancestor::mat-card", tableId));
    }
    //======================================================================================================================================

    //=========================================================== Dialog Locators ==========================================================
    public static String dialog() {
        return Locators.create("//*[contains(@class, 'dialog-container')]");
    }

    public static String specificDialog(String dialogTitle) {
        return Locators.create(String.format("//*[contains(@class, 'dialog-container') and .//*[normalize-space(text()) = '%s']]", dialogTitle));
    }

    public static String specificDialogTitle(String dialogTitle) {
        return Locators.create(String.format("//*[contains(@class, 'header') and normalize-space(text()) = '%s']", dialogTitle));
    }

    public static String specificInputFieldInDialog(String fieldName) {
        return Locators.create(String.format("//*[contains(@class, 'dialog-container')]//mat-form-field[.//*[normalize-space(text()) = '%s']]//*[contains(@class, 'field-textarea') or contains(@class, 'field-input')]", fieldName));
    }

    public static String specificDropdownInDialog(String dropdownName) {
        return Locators.create(String.format("//*[contains(@class, 'dialog-container')]//mat-select[./ancestor::mat-form-field//*[normalize-space(text()) = '%s']]", dropdownName));
    }

    public static String specificCheckboxInDialog(String checkboxName) {
        return Locators.create(String.format("//*[contains(@class, 'dialog-container')]//mat-checkbox[.//*[normalize-space(text()) = '%s']]//input[@type = 'checkbox']", checkboxName));
    }

    public static String duplicateErrorMessageInDialog() {
        return Locators.create("//*[contains(@class, 'dialog-container')]//*[contains(@class, 'duplicateError')]");
    }

    public static String specificUserSystemInfo(String fieldName) {
        return Locators.create(String.format("//mat-form-field[.//*[normalize-space(text()) = '%s']]//span[contains(@class, 'user-info')]", fieldName));
    }

    public static String specificDateSystemInfo(String fieldName) {
        return Locators.create(String.format("//mat-form-field[.//*[normalize-space(text()) = '%s']]//span[contains(@class, 'date-info')]", fieldName));
    }


    //======================================================================================================================================
    public static String specificCheckbox(String checkboxName) {
        return Locators.create(String.format("//mat-checkbox[.//label[normalize-space(text()) = '%s']]//input", checkboxName));
    }

    public static String buttonLabelOfSelectAllFilterOption(){
        return Locators.create("//*[(contains(@class, 'filter-option') or contains(@class, 'filter-button')) and .//*[normalize-space(text()) = 'Select All']]//*[contains(@class, 'button') and contains(@class, 'label')]");
    }

    public static String allFilterOptions(){
        return Locators.create("//*[contains(@class, 'filter-option')]//*[contains(@class, 'content')]");
    }
    
    public static String specificCheckBoxInFilterOption(String option){

        return Locators.create(String.format("//*[(contains(@class, 'filter-option') or contains(@class, 'filter-button')) and .//*[normalize-space(text()) = '%s']]//input", option));
    }

    public static String specificCheckBoxBackgroundInFilterOption(String option){
        return Locators.create(String.format("//*[(contains(@class, 'filter-option') or contains(@class, 'filter-button')) and .//*[normalize-space(text()) = '%s']]//input/following-sibling::*[contains(@class, 'background')]", option));
    }
    
    public static String specificMenuPanelOption(String option) {
        return Locators.create(String.format("//*[contains(@class, 'item') and normalize-space(text()) = '%s' and ./ancestor::*[@role = 'menu']]", option));
    }

    public static String listDropdownOptions() {
        return Locators.create("//*[@role = 'listbox' and .//mat-option]");
    }
    
    public static String specificDropdownOption(String option) {
        return Locators.create(String.format("//mat-option[.//*[normalize-space(text()) = '%s']]", option));
    }

    public static String specificDropdownOptionContainsWithIndex(String option, int index) {
        return Locators.create(String.format("//mat-option[.//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]][%s]", option.toLowerCase(), index));
    }

    public static String spinner() {
        return Locators.create("//*[contains(@class, 'progress-spinner')]");
    }

    public static String toastMessageTitle() {
        return Locators.create("//*[contains(@id, 'toast')]//*[contains(@class, 'title')]");
    }

    public static String toastMessageContent() {
        return Locators.create("//*[contains(@id, 'toast')]//*[contains(@class, 'message')]");
    }

    public static String specificButton(String buttonName) {
        return Locators.create(String.format("//button[normalize-space(.) = '%s']", buttonName));
    }
 
    public static String specificIconButton(String buttonName) {
        return Locators.create(String.format("//*[contains(@class, 'icon') and normalize-space(text()) = '%s']", buttonName));
    }
    
    public static String specificButtonContainsText(String buttonName) {
        return Locators.create(String.format("//button[contains(normalize-space(.),'%s')]", buttonName));
    }

    public static String actionBtn(String action) {
        return Locators.create(String.format("//button//span[contains(text(),'%s')]", action));
    }

    public static String specificBtnInTableWithIndex(String tableId, int rowIndex, int columnIndex, int btnIndex) {
        return Locators.create(String.format("//*[contains(@id, '%s')]//tbody//*[@role = 'row'][%s]/td[%s]//button[%s]", tableId, rowIndex, columnIndex, btnIndex));
    }

    public static String specificTab(String tabName) {
        return Locators.create(String.format("//*[normalize-space(text()) = '%s']/ancestor::*[@role = 'tab']", tabName));
    }

    public static String splitBtn(String name) {
        return Locators.create(String.format("//button[contains(@class,'split-button-%s')]", name));
    }

    public static String actionBtnWithID(String name) {
        return Locators.create(String.format("//button[contains(@id,'%s')]", name));
    }

    public static String specificMatFormField(String fieldName) {
        return Locators.create(String.format("//mat-form-field[.//*[normalize-space(text()) = '%s']]", fieldName));
    }

    public static String specificInputField(String fieldName) {
        return Locators.create(String.format("//mat-form-field[.//*[normalize-space(text()) = '%s']]//input", fieldName));
    }

    public static String specificInputFieldId(String idInputField) {
        return Locators.create(String.format("//input[@id='%s']", idInputField));
    }

    public static String specificInputFieldIndex(String fieldName, int index) {
        return Locators.create(String.format("(//mat-form-field[.//*[normalize-space(text()) = '%s']]//input)[%s]", fieldName, index));
    }

    public static String specificButtonFieldIndex(String fieldName, int index) {
        return Locators.create(String.format("(//mat-form-field[.//mat-label[normalize-space(text())='%s']]//button)[%s]", fieldName, index));
    }

    public static String specificRequiredField(String fieldName){
        return Locators.create(String.format("//mat-form-field[.//*[normalize-space(text()) = '%s']]//*[contains(@class, 'mdc-form-field-required')]", fieldName));
    }

    public static String clearButtonOfspecificInputField(String fieldName) {
        return Locators.create(String.format("//mat-form-field[.//*[normalize-space(text()) = '%s']]//button[@aria-label = 'Clear']", fieldName));
    }

    public static String specificTextAreaField(String idFieldName) {
        return Locators.create(String.format("//textarea[contains(@id,'%s')]", idFieldName));
    }

    public static String specificTextAreaWithFieldName(String fieldName) {
        return Locators.create(String.format("//app-textarea[.//mat-label[normalize-space()='%s']]//textarea", fieldName));
    }
    
    public static String specificLocatorByID(String id) {
        return Locators.create(String.format("//*[@id='%s']", id));
    }

    //Search Fields
    public static String specificSearchField(String fieldName) {
        return Locators.create(String.format("//mat-expansion-panel//label[contains(@for, 'input') and ./*[normalize-space((.)) = '%s']]", fieldName));
    }

    public static String unauthorizePage() {
        return Locators.create("//mat-card-content//*[normalize-space((.))='You are not authorized to access this application.']");
    }

    public static String searchTypeBox() {
        return Locators.create("//mat-select[contains(@id, 'mat-select')]");
    }

    // 'View From' and 'View To' open calendar
  

    public static String monthPickerFrom() {
        return Locators.create("(//app-month-picker//button[@aria-label='Open calendar'])[1]");
    }
    public static String monthPickerTo() {
        return Locators.create("(//app-month-picker//button[@aria-label='Open calendar'])[2]");
    }

    public static String screenCalendarWindow(){
        return Locators.create("//*[contains(@id,'datepicker')]");
    }

    public static String labelNameCalendar(){
        return Locators.create("//*[contains(@class,'calendar-header')]//*[contains(@class,'label')]/span");
    }

    public static String viewFromCalendar() {
        return Locators.create("(//button[@aria-label='Open calendar'])[1]");
    }

    public static String viewToCalendar() {
        return Locators.create("(//button[@aria-label='Open calendar'])[2]");
    }

    public static String openCalendarButtonOfSpecificField(String fieldName){
        return Locators.create(String.format("//mat-form-field[.//*[normalize-space(text()) = '%s']]//*[@aria-label = 'Open calendar']", fieldName));
    }

    public static String openCalendarButtonOfSpecificId(String id){
        return Locators.create(String.format("//mat-form-field[.//*[contains(normalize-space(@id) ,'%s')]]//*[@aria-label = 'Open calendar']", id));
    }

    // Date calendar
    public static String calendarPeriodBtn(){
        return Locators.create("//button[contains(@class, 'calendar-period-button')]");
    }

    public static String chooseDateBtn(){
        return Locators.create("//button[@aria-label='Choose date']");
    }

    public static String chooseMonthYearBtn(){
        return Locators.create("//button[@aria-label='Choose month and year']");
    }

    public static String optionCalendar(String option){
        return Locators.create(String.format("//button[@aria-label='%s']", option));
    }

    public static String optionMonth(String var){
        return Locators.create(String.format("//mat-year-view//button[.//span[contains(normalize-space(),'%s')]]", var));
    }

    public static String optionInCalendar(String option){
        return Locators.create(String.format("//button[contains(@class,'calendar') and .//*[normalize-space()='%s' or text()='%s']]", option,option));
    }

    public static String breadCrumb() {
        return Locators.create("//ul[contains(@class, 'breadcrumb')]");
    }

    public static String actionTableContainer() {
        return Locators.create("//table[contains(@class, 'action-table-container')]");
    }

    public static String tabHeader() {
        return Locators.create("//mat-tab-header");
    }

    //Accordion
    public static String specificAccordionTitle(String title){
        return Locators.create(String.format("//app-accordion-item//*[contains(text(),'%s')]", title));
    }

    public static String specificAccordionHeader(String accordionHeader) {
        return Locators.create(String.format("//app-accordion-item//mat-expansion-panel-header[contains(., '%s')]", accordionHeader));
    }

    public static String accordionHeader() {
        return Locators.create("//*[contains(@class, 'accordion')]//*[contains(@id, 'panel-header')]");
    }

    public static String accordionBody() {
        return Locators.create("//*[contains(@class, 'accordion')]//*[contains(@class, 'panel-body')]");
    }

    public static String specificLabelWithValue(String label, String value) {
        return Locators.create(String.format("//*[contains(text(), '%s')]//following-sibling::*[contains(., '%s')]", label, value));
    }

    public static String valueOfSpecificLabel(String label) {
        return Locators.create(String.format("//*[contains(text(), '%s')]//following-sibling::*",label));
    }
    
    public static String specificAccordionTable(String idTable){
        return Locators.create(String.format("//mat-expansion-panel[contains(@id,'%s')]", idTable));
    }
    
    //Drop-down
    public static String dropDownArrow(String label) {
        return Locators.create(String.format("//span[contains(text(), '%s')]//ancestor::mat-select//div[contains(@class,'select-arrow')]/div", label));
    }

    public static String dropDownBox(String label) {
        return Locators.create(String.format("//span[contains(text(), '%s')]//ancestor::mat-form-field//div[contains(@class, 'outline--upgraded')]//div[contains(@class, 'trailing')]", label));
    }

    public static String dropDownValue(String value) {
        return Locators.create(String.format("//mat-option[.//*[normalize-space(text()) = '%s']]//span", value));
    }

    public static String dropDownSelectedTick(String value) {
        return Locators.create(String.format("//mat-option[.//*[normalize-space(text()) = '%s']]//mat-pseudo-checkbox", value));
    }

    //Combo-box
    public static String comboBoxArrow(String label) {
        return Locators.create(String.format("//mat-form-field[contains(@id,'%s')]//mat-icon[contains(text(), 'arrow_drop_down')]", label));
    }

    public static String comboBoxBox(String label) {
        return Locators.create(String.format("//mat-form-field[contains(@id,'%s')]//div[contains(@class, 'outline--upgraded')]//div[contains(@class, 'trailing')]", label));
    }
}

