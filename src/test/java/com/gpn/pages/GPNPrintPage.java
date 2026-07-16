package com.gpn.pages;

import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;

public class GPNPrintPage extends ProjectSpecificMethods {

    public GPNPrintPage verifyPrintDialogVisible() {
        verifyDisplayed(locateElement(Locators.XPATH,
            "//div[contains(@class,'print-gpn-data-dialog')]"));
        reportStep("Print dialog is visible", "pass");
        return this;
    }

    public GPNPrintPage clickPrintButton() {
        clickWithJs(locateElement(Locators.XPATH,
            "//button[contains(@class,'print-button')]"));
        reportStep("Clicked Print button", "pass");
        return this;
    }

    public GPNPrintPage clickDownloadButton() {
        clickWithJs(locateElement(Locators.XPATH,
            "//button[contains(@class,'download-button')]"));
        reportStep("Clicked Download button", "pass");
        return this;
    }

    public GPNPrintPage clickCancelButton() {
        clickWithJs(locateElement(Locators.XPATH,
            "//button[contains(@class,'cancel-button')]"));
        reportStep("Clicked Cancel button", "pass");
        return this;
    }
}
