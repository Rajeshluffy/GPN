package com.gpn.pages;

import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;

public class GPNSchedulerPage extends ProjectSpecificMethods {

    private static final String SCHEDULER_CANVAS = "schedulerCanvas";
    private static final String TABLE_DIV        = "TableDiv";

    public GPNSchedulerPage verifySchedulerVisible() {
        verifyDisplayed(locateElement(Locators.ID, SCHEDULER_CANVAS));
        reportStep("Scheduler canvas is visible", "pass");
        return this;
    }

    public GPNSchedulerPage verifyTableVisible() {
        verifyDisplayed(locateElement(Locators.ID, TABLE_DIV));
        reportStep("Table div is visible", "pass");
        return this;
    }

    public GPNSchedulerPage clickTabByTitle(String tabTitle) {
        clickWithJs(locateElement(Locators.XPATH,
            "//li[contains(@class,'ui-tabview-nav')]//a[text()='" + tabTitle + "']"));
        reportStep("Clicked tab: " + tabTitle, "pass");
        return this;
    }

    public GPNSchedulerPage clickStartButton() {
        clickWithJs(locateElement(Locators.XPATH,
            "//button[contains(@class,'StartStopBtn') and .//span[contains(text(),'Start')]]"));
        reportStep("Clicked Start button", "pass");
        return this;
    }

    public GPNSchedulerPage clickStopButton() {
        clickWithJs(locateElement(Locators.XPATH,
            "//button[contains(@class,'StartStopBtn') and .//span[contains(text(),'Stop')]]"));
        reportStep("Clicked Stop button", "pass");
        return this;
    }

    public GPNSchedulerPage clickStopAllButton() {
        clickWithJs(locateElement(Locators.XPATH,
            "//button[contains(@class,'btn-stopall')]"));
        reportStep("Clicked Stop All button", "pass");
        return this;
    }

    public GPNSchedulerPage clickAcceptButton() {
        clickWithJs(locateElement(Locators.XPATH,
            "//button[contains(@class,'accept-button')]"));
        reportStep("Clicked Accept button", "pass");
        return this;
    }

    public GPNSchedulerPage clickRejectButton() {
        clickWithJs(locateElement(Locators.XPATH,
            "//button[contains(@class,'reject-button')]"));
        reportStep("Clicked Reject button", "pass");
        return this;
    }

    public GPNSchedulerPage enterEditText(String text) {
        type(locateElement(Locators.ID, "editTextBox"), text);
        reportStep("Entered text in edit box: " + text, "pass");
        return this;
    }

    public GPNSchedulerPage verifyDefectStatusTooltipVisible() {
        verifyDisplayed(locateElement(Locators.ID, "DefectStatusTooltip"));
        reportStep("Defect status tooltip is visible", "pass");
        return this;
    }

    public GPNSchedulerPage selectSortOption(String sortLabel) {
        clickWithJs(locateElement(Locators.XPATH,
            "//div[@id='avSortContainer']//*[contains(text(),'" + sortLabel + "')]"));
        reportStep("Selected sort option: " + sortLabel, "pass");
        return this;
    }
}
