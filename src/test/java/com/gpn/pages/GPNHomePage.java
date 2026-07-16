package com.gpn.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.alfaDOCK.pages.AlfaDockHomePage;
import com.alfaDOCK.pages.Settings;
import com.framework.selenium.api.design.Locators;
import com.framework.testng.api.base.ProjectSpecificMethods;
import com.framework.utils.ScreenshotStore;
import com.framework.utils.ScreenshotUtils;

public class GPNHomePage extends ProjectSpecificMethods {

	public GPNHomePage verifyGPNHomePage() {
		waitForPageToLoad();
		waitForApperance(locateElement(Locators.ID, "schedulerCanvas"));
		waitForSpinnerDisappear();
		reportStep("Successfully Entered GPN", "pass");
		return this;
	}

	public GPNHomePage selectFilter() {
		clickWithJs(locateElement(Locators.XPATH, "//i[@title='フィルタ']"));
		reportStep("Selected Filter Button", "pass");
		return this;
	}

	public GPNHomePage selectHome() {
		clickWithJs(locateElement(Locators.XPATH, "//span[@class='menu-item pi pi-home']"));
		reportStep("GPN Home button Selected", "Pass");
		return this;
	}

	public AlfaDockHomePage navigateToalfaDOCK() {
		boolean switched = switchToWindowByUrl("https://www.alfadock-pack.com/ver10/#/home");
		if (!switched) {
			reportStep("Failed to navigate to alfaDOCK Home Page: window not found", "fail", true);
			throw new RuntimeException("Navigation to alfaDOCK failed: no window URL containing '/ver10/#/home'");
		}
		reportStep("Succesfully navigate TO alfadock Home Page", "pass");
		return new AlfaDockHomePage();
	}

	public Settings clickDOCKSettingsButton() {
		try {
			waitForApperance(locateElement(Locators.XPATH, "//i[@title='Setting' or @title='設定']"));
			clickWithJs(locateElement(Locators.XPATH, "//i[@title='Setting' or @title='設定']"));
			reportStep("Clicked Settings button", "pass");
		} catch (Exception e) {
			reportStep("Failed to click Settings", "fail");
		}
		return new Settings();
	}

	public GPNHomePage selectMode() {
		waitForApperance(locateElement(Locators.XPATH, "//div[@class='show-content']//p-dropdown//label"));
		String current = safeGetText(locateElement(Locators.XPATH, "//div[@class='show-content']//p-dropdown//label"));
		switch (current) {
		case "管理員モード":
		case "Supervisor Mode":
			break;
		default:
			clickWithJs(locateElement(Locators.XPATH, "//div[@class='show-content']//p-dropdown//label"));
			clickWithJs(locateElement(Locators.XPATH,
					"//li[contains(@class, 'ui-dropdown-item')]//span[text()='管理員モード']"));
		}
		return this;
	}

	public GPNHomePage selectExpandMO() {
		try {
			waitForApperance(
					locateElement(Locators.XPATH, "//i[@title='Expand All MO' or @title='全手配詳細表示']"));
			clickWithJs(
					locateElement(Locators.XPATH, "//i[@title='Expand All MO' or @title='全手配詳細表示']"));
			reportStep("Expand Button selected", "pass");
		} catch (Exception e) {
			// element may not exist in all views — silent skip
		}
		return this;
	}

	// -------------------------------------------------------------------------
	// Element screenshot capture — routes through ScreenshotStore
	// -------------------------------------------------------------------------

	/**
	 * Captures the canvas element screenshot and stores it under
	 * {@code Screenshots/<ENV>/<testCaseName>/<accountKey>_canvas.jpg}.
	 *
	 * <p>Use this overload in <b>DataProvider</b> flow where each Excel row
	 * supplies a unique {@code accountKey} (companyUser).
	 *
	 * @param env          environment tag from XML parameter, e.g. "qa" or "dev"
	 * @param testCaseName simple name of the calling test class,
	 *                     e.g. {@code this.getClass().getSimpleName()}
	 * @param accountKey   per-account identifier used to pair QA/DEV screenshots
	 */
	public void captureCanvasScreenshot(String env, String testCaseName, String accountKey) {
		WebElement canvas = findCanvasElement();
		if (canvas == null) return;
		String path = ScreenshotStore.save(canvas, env, testCaseName, accountKey, "canvas");
		if (path != null) {
			reportStep("Canvas screenshot saved [" + env + "/" + accountKey + "]: " + path, "info", false);
		}
	}

	/**
	 * Captures the canvas element screenshot and stores it under
	 * {@code Screenshots/<ENV>/<testCaseName>/canvas.jpg}.
	 *
	 * <p>Use this overload in <b>non-DataProvider</b> flow (single account per run).
	 *
	 * @param env          environment tag from XML parameter, e.g. "qa" or "dev"
	 * @param testCaseName simple name of the calling test class
	 */
	public void captureCanvasScreenshot(String env, String testCaseName) {
		WebElement canvas = findCanvasElement();
		if (canvas == null) return;
		String path = ScreenshotStore.save(canvas, env, testCaseName, "canvas");
		if (path != null) {
			reportStep("Canvas screenshot saved [" + env + "]: " + path, "info", false);
		}
	}

	/**
	 * Generic element screenshot — captures any element and stores it with a
	 * caller-supplied element name.  Works for both DataProvider and
	 * non-DataProvider flows.
	 *
	 * @param element      the WebElement to capture
	 * @param env          environment tag
	 * @param testCaseName simple name of the calling test class
	 * @param accountKey   per-account key; pass {@code null} for non-DataProvider flow
	 * @param elementName  descriptive label, e.g. "header", "dropdown_value"
	 */
	public void captureElementScreenshot(WebElement element, String env,
			String testCaseName, String accountKey, String elementName) {
		if (element == null) {
			reportStep("captureElementScreenshot: element is null, skipping capture", "warning", false);
			return;
		}
		String path = (accountKey != null && !accountKey.isEmpty())
				? ScreenshotStore.save(element, env, testCaseName, accountKey, elementName)
				: ScreenshotStore.save(element, env, testCaseName, elementName);
		if (path != null) {
			reportStep("Element screenshot saved [" + elementName + "]: " + path, "info", false);
		}
	}

	// -------------------------------------------------------------------------

	private WebElement findCanvasElement() {
		try {
			WebElement canvas = getDriver().findElement(By.cssSelector("canvas"));
			return canvas;
		} catch (Exception e) {
			reportStep("Canvas element not found: " + e.getMessage(), "warning", false);
			return null;
		}
	}
}
