package com.gpn.testcases;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import com.framework.utils.TestMetadata;
import com.gpn.pages.GPNHomePage;

@TestMetadata(
        name = "GPN Filter",
        description = "Verify user login and search file functionality in Drawing Manager",
        authors = "Rajesh",
        category = "Smoke"
)
public class TC001_VerifyFilterButton extends GPNBaseTest {

    @Factory(dataProvider = "accountRows", dataProviderClass = GPNBaseTest.class)
    public TC001_VerifyFilterButton(String companyUser, String companyPass,
                                    String userUser, String userPass) {
        super(companyUser, companyPass, userUser, userPass);
    }

    @Test(dataProvider = "accountCredentials")
    public void FilterButton(String companyUser, String companyPassword,
                             String userUser, String userPassword) {
        gpnPageFinder();
        new GPNHomePage().verifyGPNHomePage();
    }
}
