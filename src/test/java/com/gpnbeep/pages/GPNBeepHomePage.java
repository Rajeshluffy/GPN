package com.gpnbeep.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;

public class GPNBeepHomePage extends ProjectSpecificMethods{
	public GPNBeepHomePage verifyGPNBeepHomePage() {
		try {
			waitForPageToLoad();
			waitForApperance(locateElement(Locators.XPATH, "//app-cyber-kiosk"));
			reportStep("Successfully Entered GPN Beep", "pass");
		} catch (Exception e) {
			reportStep("Successfully Failed to Entered GPN Beep", "fails");
		}
		return this;
	}

	public GPNBeepHomePage selectBeepSetting(){
		try {
			waitForClickable(locateElement(Locators.XPATH, "//i[@class='fa fa-wrench']"));
			clickWithJs(locateElement(Locators.XPATH, "//i[@class='fa fa-wrench']"));
			reportStep("Successfully Select  GPN Beep Setting" , "pass");
		} catch (Exception e) {
			reportStep("Failed Select  GPN Beep Setting" , "fail");
		}
		return this;
	}

	public GPNBeepHomePage selectSearch(){
		try {
			waitForClickable(locateElement(Locators.XPATH,"(//span[@class='ui-menuitem-text'])[2]"));
			clickWithJs(locateElement(Locators.XPATH,"(//span[@class='ui-menuitem-text'])[2]"));
			reportStep("Successfully Select search button" , "pass");
		} catch (Exception e) {
			reportStep("Failed Select search button" , "fail");
		}
		return this;
	}
	public GPNBeepHomePage scrollTill() {
		try {
			scroll(locateElement(Locators.XPATH,"//p-radiobutton[@name='qrcodesearchfieldsmusthaveall']"));
			reportStep("Successfully scroll to the qr " , "pass");
		} catch (Exception e) {
			reportStep("Failed scroll to the qr " , "fail");
		}
		return this;
	}

	public GPNBeepHomePage selectQrDropDown(){

		try {
			clickWithJs(locateElement(Locators.XPATH,"//p-multiselect/div"));
			pause(500);
			reportStep("Successfully selected qr dropdown " , "pass");
		} catch (Exception e) {
			reportStep("Failed selected qr dropdown " , "fail");
		}
		return this;
	}

	public GPNBeepHomePage selectUpdate(){

		try {
			clickWithJs(locateElement(Locators.XPATH,"//button[@icon='pi pi-check']"));
			pause(2000);
			reportStep("Successfully selected qr dropdown " , "pass");
		} catch (Exception e) {
			reportStep("Failed selected qr dropdown " , "fail");
		}
		return this;
	}



	public GPNBeepHomePage temp() {

		try {
			boolean verifySelected = verifySelected(locateElement(Locators.XPATH,"//input[@name='qrcodesearchfieldsmusthaveall' and @value='true']"));
			if(verifySelected) {
				clickWithJs(locateElement(Locators.XPATH,"//input[@name='qrcodesearchfieldsmusthaveall' and @value='false']/parent::div/following-sibling::div[contains(@class, 'ui-radiobutton-box')]"));
				reportStep("Successfully to changed the state to いいえ。​ " , "pass");
			}
		} catch (Exception e) {
			reportStep("Failed to changed the state to いいえ。​ " , "fail");
		}
		return this;
	}

	public GPNBeepHomePage enforceSingleSelection() {
		try {
			List<WebElement> options = locateElements(Locators.CSS, "li.ui-multiselect-item");

			if (options.isEmpty()) {
				reportStep("No options found in the multi-select panel", "info", false);
				return this;
			}

			boolean foundFirstSelected = false;

			for (int i = 0; i < options.size(); i++) {
				try {
					// Re-fetch the list on each iteration to guard against stale references
					// after a deselect click causes a DOM update
					List<WebElement> currentOptions = locateElements(Locators.CSS, "li.ui-multiselect-item");
					if (i >= currentOptions.size()) break;

					WebElement option   = currentOptions.get(i);
					WebElement checkbox = option.findElement(By.cssSelector("div.ui-chkbox-box"));
					boolean    isSelected = checkbox.getAttribute("class").contains("ui-state-active");

					if (!isSelected) continue;

					if (!foundFirstSelected) {
						foundFirstSelected = true;
						reportStep("Retaining first selected option: " + safeGetText(option), "info", false);
					} else {
						reportStep("Deselecting extra option: " + safeGetText(option), "info", false);
						waitForClickable(checkbox);
						clickWithJs(checkbox);
					}

				} catch (StaleElementReferenceException e) {
					reportStep("Stale element encountered at option index " + i + ", skipping", "warning", false);
				}
			}

			reportStep(
				foundFirstSelected
					? "Single selection enforced successfully"
					: "No option selected — dropdown state is valid (zero selected)",
				"pass", false);

		} catch (Exception e) {
			reportStep("Failed to enforce single selection: " + e.getMessage(), "fail", false);
		}
		return this;
	}

	
	
	
}
