package com.gpn.pages;

import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;

public class GPNBarcodeScanPage extends ProjectSpecificMethods {

    public GPNBarcodeScanPage selectBarcodeMode() {
        clickWithJs(locateElement(Locators.XPATH, "//p-radiobutton[@name='code']//input[@value='1']"));
        reportStep("Selected Barcode mode", "pass");
        return this;
    }

    public GPNBarcodeScanPage selectQrcodeMode() {
        clickWithJs(locateElement(Locators.XPATH, "//p-radiobutton[@name='code']//input[@value='0']"));
        reportStep("Selected QR Code mode", "pass");
        return this;
    }

    public GPNBarcodeScanPage closeDialog() {
        clickWithJs(locateElement(Locators.XPATH, "//span[@class='ui-dialog-title' and text()='Barcode Scan']/ancestor::div[contains(@class,'ui-dialog-titlebar')]//a[contains(@class,'ui-dialog-titlebar-close')]"));
        reportStep("Closed Barcode Scan dialog", "pass");
        return this;
    }

    public GPNBarcodeScanPage verifyDialogVisible() {
        verifyDisplayed(locateElement(Locators.ID, "barcode_scanner"));
        reportStep("Barcode Scan dialog is visible", "pass");
        return this;
    }
}
