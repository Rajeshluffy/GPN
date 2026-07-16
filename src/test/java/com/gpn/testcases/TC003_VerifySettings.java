package com.gpn.testcases;

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
public class TC003_VerifySettings extends GPNBaseTest {

    @Factory(dataProvider = "accountRows", dataProviderClass = GPNBaseTest.class)
    public TC003_VerifySettings(String companyUser, String companyPass,
                                String userUser, String userPass) {
        super(companyUser, companyPass, userUser, userPass);
    }

    @Test(dataProvider = "accountCredentials")
    public void SettingsButton(String companyUser, String companyPassword,
                           String userUser, String userPassword) {
        gpnPageFinder();
        new GPNHomePage().clickDOCKSettingsButton().verifyDOCKSettings();
    }
}
