package com.gpn.pages;

import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;

public class GPNToolbarPage extends ProjectSpecificMethods {

    private static final String TOOLBAR_DIV = "ToolBarDiv";

    public GPNToolbarPage verifyToolbarVisible() {
        verifyDisplayed(locateElement(Locators.ID, TOOLBAR_DIV));
        reportStep("Toolbar is visible", "pass");
        return this;
    }

    public GPNHomePage clickHomeButton() {
        clickWithJs(locateElement(Locators.XPATH, "//div[contains(@class,'home-lg')]"));
        reportStep("Clicked Home button", "pass");
        return new GPNHomePage();
    }

    public GPNToolbarPage clickLogoutButton() {
        clickWithJs(locateElement(Locators.XPATH, "//div[contains(@class,'logout-btn')]"));
        reportStep("Clicked Logout button", "pass");
        return this;
    }

    public GPNToolbarPage clickFilterButton() {
        clickWithJs(locateElement(Locators.XPATH, "//i[@title='フィルタ']"));
        reportStep("Clicked Filter button", "pass");
        return this;
    }

    public GPNToolbarPage clickMoreButton() {
        clickWithJs(locateElement(Locators.ID, "moreButton"));
        reportStep("Clicked More options button", "pass");
        return this;
    }

    public GPNToolbarPage clickMenuItem(String menuItemText) {
        clickWithJs(locateElement(Locators.XPATH,
            "//span[contains(@class,'menu-item') and text()='" + menuItemText + "']"));
        reportStep("Clicked menu item: " + menuItemText, "pass");
        return this;
    }

    public GPNToolbarPage clickMoSheetButton() {
        clickWithJs(locateElement(Locators.XPATH,
            "//button[.//span[contains(text(),'MO Sheet')]]"));
        reportStep("Clicked MO Sheet button", "pass");
        return this;
    }

    public GPNToolbarPage verifyUserInfoVisible() {
        verifyDisplayed(locateElement(Locators.ID, "user-info"));
        reportStep("User info panel is visible", "pass");
        return this;
    }
}
