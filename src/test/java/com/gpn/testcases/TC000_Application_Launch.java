package com.gpn.testcases;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import com.framework.utils.TestMetadata;
import com.gpn.pages.GPNHomePage;

@TestMetadata(
        name = "GPN Application",
        description = "Verify Account Opening",
        authors = "Rajesh",
        category = "Smoke"
)
public class TC000_Application_Launch extends GPNBaseTest {

    @Factory(dataProvider = "accountRows", dataProviderClass = GPNBaseTest.class)
    public TC000_Application_Launch(String companyUser, String companyPass,
                                    String userUser, String userPass) {
        super(companyUser, companyPass, userUser, userPass);
    }

    @Test(dataProvider = "accountCredentials")
    public void ApplicationTrigger(String companyUser, String companyPassword,
                             String userUser, String userPassword) {
        gpnPageFinder();
    }
}
