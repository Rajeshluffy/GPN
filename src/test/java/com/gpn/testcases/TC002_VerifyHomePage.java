package com.gpn.testcases;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import com.framework.utils.TestMetadata;
import com.gpn.pages.GPNHomePage;

@TestMetadata(
        name = "GPN Home Page",
        description = "Verify that clicking the Home button redirects the user to the alfaDOCK Home Page.",
        authors = "Rajesh",
        category = "Smoke"
)
public class TC002_VerifyHomePage extends GPNBaseTest {

    @Factory(dataProvider = "accountRows", dataProviderClass = GPNBaseTest.class)
    public TC002_VerifyHomePage(String companyUser, String companyPass,
                                String userUser, String userPass) {
        super(companyUser, companyPass, userUser, userPass);
    }

    @Test(dataProvider = "accountCredentials")
    public void HomeButton(String companyUser, String companyPassword,
                           String userUser, String userPassword) {
        gpnPageFinder();
        new GPNHomePage().selectHome().navigateToalfaDOCK().verifyHomePage();
    }
}
