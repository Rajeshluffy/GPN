package com.gpn.pages;

import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;

public class GPNAdvancedSearchPage extends ProjectSpecificMethods {

    public GPNAdvancedSearchPage enterSearchText(String text) {
        type(locateElement(Locators.XPATH, "//app-ui-advanced-search//input[@pinputtext]"), text);
        reportStep("Entered advanced search text: " + text, "pass");
        return this;
    }

    public GPNAdvancedSearchPage enableAndSearch() {
        clickWithJs(locateElement(Locators.XPATH, "//p-checkbox//label[text()='And Search']/preceding-sibling::div//input[@type='checkbox']"));
        reportStep("Enabled And Search", "pass");
        return this;
    }

    public GPNAdvancedSearchPage clickSearch() {
        clickWithJs(locateElement(Locators.XPATH, "//p-button[contains(@class,'searchBtn')]//button"));
        reportStep("Clicked Search in Advanced Search", "pass");
        return this;
    }

    public GPNAdvancedSearchPage clickClear() {
        clickWithJs(locateElement(Locators.XPATH, "//p-button[contains(@class,'clearBtn')]//button"));
        reportStep("Clicked Clear in Advanced Search", "pass");
        return this;
    }

    public GPNAdvancedSearchPage selectSearchByField(String fieldName) {
        clickWithJs(locateElement(Locators.XPATH, "//app-ui-advanced-search//p-multiselect"));
        clickWithJs(locateElement(Locators.XPATH, "//li[.//label[text()='" + fieldName + "']]//div[contains(@class,'ui-chkbox-box')]"));
        reportStep("Selected search by field: " + fieldName, "pass");
        return this;
    }

    public GPNAdvancedSearchPage setDateFrom(String date) {
        type(locateElement(Locators.XPATH, "(//input[@type='date'])[1]"), date);
        reportStep("Set Date From: " + date, "pass");
        return this;
    }

    public GPNAdvancedSearchPage setDateTo(String date) {
        type(locateElement(Locators.XPATH, "(//input[@type='date'])[2]"), date);
        reportStep("Set Date To: " + date, "pass");
        return this;
    }

    public GPNAdvancedSearchPage selectDeliveryDateConstraint() {
        clickWithJs(locateElement(Locators.XPATH, "//input[@name='dateConstraints' and @value='moItemDate']"));
        reportStep("Selected Delivery Date constraint", "pass");
        return this;
    }

    public GPNAdvancedSearchPage selectCustomer(String customerName) {
        clickWithJs(locateElement(Locators.XPATH, "//label[text()='Customer']/following::p-multiselect[1]"));
        clickWithJs(locateElement(Locators.XPATH, "//li[.//label[text()='" + customerName + "']]//div[contains(@class,'ui-chkbox-box')]"));
        reportStep("Selected customer: " + customerName, "pass");
        return this;
    }

    public GPNAdvancedSearchPage selectProcessStatus(String status) {
        clickWithJs(locateElement(Locators.XPATH, "//li[.//label[text()='" + status + "']]//div[contains(@class,'ui-chkbox-box')]"));
        reportStep("Selected process status: " + status, "pass");
        return this;
    }

    public GPNAdvancedSearchPage enableIncludeOrdersWithoutProcess() {
        clickWithJs(locateElement(Locators.XPATH, "//input[@name='dateConstraints' and @value='emptymo']"));
        reportStep("Enabled Include Orders without Process", "pass");
        return this;
    }
}
