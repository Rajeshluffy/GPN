package com.gpn.pages;

import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;

public class GPNSearchBarPage extends ProjectSpecificMethods {

    private static final String SEARCH_INPUT = "testInputkeypad";

    public GPNSearchBarPage enterSearchText(String text) {
        type(locateElement(Locators.ID, SEARCH_INPUT), text);
        reportStep("Entered search text: " + text, "pass");
        return this;
    }

    public GPNSearchBarPage clickSearchButton() {
        clickWithJs(locateElement(Locators.XPATH, "//ui-searchbar//button[.//span[contains(@class,'glyphicon-search')]]"));
        reportStep("Clicked Search button", "pass");
        return this;
    }

    public GPNSearchBarPage clickClearButton() {
        clickWithJs(locateElement(Locators.XPATH, "//ui-searchbar//button[.//span[contains(@class,'glyphicon-remove')]]"));
        reportStep("Clicked Clear button", "pass");
        return this;
    }

    public GPNSearchBarPage clickBarcodeButton() {
        clickWithJs(locateElement(Locators.XPATH, "//ui-searchbar//button[.//span[contains(@class,'glyphicon-barcode')]]"));
        reportStep("Clicked Barcode button", "pass");
        return this;
    }

    public GPNSearchBarPage clickVoiceCommandButton() {
        clickWithJs(locateElement(Locators.XPATH, "//ui-searchbar//button[@title='Voice Command']"));
        reportStep("Clicked Voice Command button", "pass");
        return this;
    }

    public GPNSearchBarPage clickAdvancedSearchButton() {
        clickWithJs(locateElement(Locators.XPATH, "//ui-searchbar//span[contains(@class,'glyphicon-qrcode')]"));
        reportStep("Opened Advanced Search panel", "pass");
        return this;
    }
}
