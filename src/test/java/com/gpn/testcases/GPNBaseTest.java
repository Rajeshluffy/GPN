package com.gpn.testcases;

import com.alfaDOCK.pages.AlfaDockHomePage;
import com.alfaDOCK.testcases.AlfaDockBaseTest;
import com.gpn.pages.GPNHomePage;
import com.gpnbeep.pages.GPNBeepHomePage;

public abstract class GPNBaseTest extends AlfaDockBaseTest {

	/**
	 * Stores account credentials on the instance fields inherited from
	 * {@link AlfaDockBaseTest}.  Called by the {@code @Factory} constructor
	 * of each concrete test class, which receives its row from
	 * {@link AlfaDockBaseTest#accountRows}.
	 *
	 * <p>Both DataProviders ({@code accountRows} and {@code accountCredentials})
	 * and the login {@code @BeforeMethod} are defined in {@code AlfaDockBaseTest}
	 * and require no duplication here.
	 */
	protected GPNBaseTest(String companyUser, String companyPass,
			String userUser, String userPass) {
		this.companyUser = companyUser;
		this.companyPass = companyPass;
		this.userUser    = userUser;
		this.userPass    = userPass;
	}

	/**
	 * Navigates to the GPN module from wherever the browser currently is
	 * after login.  Handles two known post-login landing routes.
	 */
	public void gpnPageFinder() {
		String currentUrl = getCurrentUrl();
		int counter = 0;
		while ((currentUrl == null || currentUrl.isEmpty()) && counter <= 10) {
			pause(1000);
			currentUrl = getCurrentUrl();
			counter++;
		}

		if (currentUrl == null || currentUrl.isEmpty()) {
			reportStep("URL is empty or null — cannot navigate to GPN.", "Fail");
			return;
		}

		String urlLower = currentUrl.toLowerCase();

		if (urlLower.contains("#/userlogin")) {
			pause(3000);
			currentUrl = getCurrentUrl();
			urlLower = currentUrl != null ? currentUrl.toLowerCase() : "";
		}

		if (urlLower.contains("#/home")) {
			new AlfaDockHomePage()
			.verifyHomePage()
			.selectSoftwareLibary()
			.verifySoftwareLibary()
			.selectGPN()
			.navigateToGPN();
			new GPNHomePage().verifyGPNHomePage();
		} else if (urlLower.contains("sischeduler")) {
			new GPNHomePage().verifyGPNHomePage();
		} else {
			reportStep("Failed to reach GPN — unrecognized route: " + currentUrl, "Fail");
		}

		waitForSpinnerDisappear();
		waitForPageAndApiReady(60);

	}

	/**
	 * Navigates to the GPN module from wherever the browser currently is
	 * after login.  Handles two known post-login landing routes.
	 */
	public void gpnBeepPageFinder() {
		String currentUrl = getCurrentUrl();
		int counter = 0;
		while ((currentUrl == null || currentUrl.isEmpty()) && counter <= 10) {
			pause(1000);
			currentUrl = getCurrentUrl();
			counter++;
		}

		if (currentUrl == null || currentUrl.isEmpty()) {
			reportStep("URL is empty or null — cannot navigate to GPN.", "Fail");
			return;
		}

		String urlLower = currentUrl.toLowerCase();

		if (urlLower.contains("#/userlogin")) {
			pause(3000);
			currentUrl = getCurrentUrl();
			urlLower = currentUrl != null ? currentUrl.toLowerCase() : "";
		}

		if (urlLower.contains("#/home")) {
			new AlfaDockHomePage()
			.verifyHomePage()
			.selectSoftwareLibary()
			.verifySoftwareLibary()
			.selectGPNBeep()
			.navigateToGPNBeep();

			new GPNBeepHomePage().verifyGPNBeepHomePage();
		} else if (urlLower.contains("sischeduler")) {
			new GPNHomePage().selectHome().navigateToalfaDOCK();
			new AlfaDockHomePage()
			.verifyHomePage()
			.selectSoftwareLibary()
			.verifySoftwareLibary()
			.selectGPNBeep()
			.navigateToGPNBeep();
			new GPNBeepHomePage().verifyGPNBeepHomePage();
		} else {
			reportStep("Failed to reach GPN — unrecognized route: " + currentUrl, "Fail");
		}

		waitForPageAndApiReady(20);

	}
}
