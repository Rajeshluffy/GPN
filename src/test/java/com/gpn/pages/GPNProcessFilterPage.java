package com.gpn.pages;

import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;

public class GPNProcessFilterPage extends ProjectSpecificMethods {

    public GPNProcessFilterPage selectProcessCategory(String categoryName) {
        clickWithJs(locateElement(Locators.XPATH,
            "//app-ui-process-filter-dialog//div[contains(@class,'listHeader')]//label[text()='" + categoryName + "']/preceding-sibling::*//input[@type='checkbox']"));
        reportStep("Selected process category: " + categoryName, "pass");
        return this;
    }

    public GPNProcessFilterPage selectSubProcess(String subProcessName) {
        clickWithJs(locateElement(Locators.XPATH,
            "//app-ui-process-filter-dialog//div[contains(@class,'listBody')]//label[text()='" + subProcessName + "']/preceding-sibling::*//input[@type='checkbox']"));
        reportStep("Selected sub-process: " + subProcessName, "pass");
        return this;
    }

    public GPNProcessFilterPage selectAllInCategory(String categoryName) {
        clickWithJs(locateElement(Locators.XPATH,
            "//app-ui-process-filter-dialog//div[contains(@class,'listHeader')][.//label[text()='" + categoryName + "']]//input[@type='checkbox']"));
        reportStep("Selected all processes in category: " + categoryName, "pass");
        return this;
    }

    public GPNProcessFilterPage clickApply() {
        clickWithJs(locateElement(Locators.XPATH, "//app-ui-process-filter-dialog//button[contains(@class,'applyBtn')]"));
        reportStep("Clicked Apply in Process Filter", "pass");
        return this;
    }

    public GPNProcessFilterPage openProcessInNewWindow(String processName) {
        clickWithJs(locateElement(Locators.XPATH,
            "//app-ui-advanced-search//div[contains(@class,'process-wrap')][.//span[contains(@class,'process-label') and text()='" + processName + "']]//div[contains(@class,'new-window-icon')]"));
        reportStep("Opened process in new window: " + processName, "pass");
        return this;
    }
}
