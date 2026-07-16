package com.gpn.testcases;

import org.testng.ITestContext;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import com.framework.utils.TestMetadata;
import com.gpn.pages.GPNHomePage;

@TestMetadata(
		name = "GPN Settings",
		description = "Verify that clicking the Settings button redirects the user to the alfaDOCK Settings.",
		authors = "Rajesh",
		category = "Smoke"
		)
public class TC004_VerifyMODisplay extends GPNBaseTest {

	@Factory(dataProvider = "accountRows", dataProviderClass = GPNBaseTest.class)
	public TC004_VerifyMODisplay(String companyUser, String companyPass,
			String userUser, String userPass) {
		super(companyUser, companyPass, userUser, userPass);
	}

	@Test(dataProvider = "accountCredentials")
	public void verifyMoDisplay(String companyUser, String companyPassword,
			String userUser, String userPassword, ITestContext ctx) {
		gpnPageFinder();
		String env = ctx.getCurrentXmlTest().getAllParameters().getOrDefault("environment", "default");
		new GPNHomePage().selectMode()
				.captureCanvasScreenshot(env, getClass().getSimpleName(), companyUser);
	}
}
