package com.gpn.testcases;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import com.framework.utils.TestMetadata;
import com.gpn.pages.GPNHomePage;
import com.gpnbeep.pages.GPNBeepHomePage;

@TestMetadata(
        name = "GPN Beep Application",
        description = "Settings BackUP",
        authors = "Rajesh",
        category = "Smoke"
)
public class TC000_GPN_BEEP_SettingsChange extends GPNBaseTest {

    @Factory(dataProvider = "accountRows", dataProviderClass = GPNBaseTest.class)
    public TC000_GPN_BEEP_SettingsChange(String companyUser, String companyPass,
                                    String userUser, String userPass) {
        super(companyUser, companyPass, userUser, userPass);
    }

    @Test(dataProvider = "accountCredentials")
    public void ApplicationTrigger(String companyUser, String companyPassword,
                             String userUser, String userPassword) {
    	gpnBeepPageFinder();
    	new GPNBeepHomePage().selectBeepSetting().selectSearch().scrollTill().temp().selectQrDropDown().enforceSingleSelection().selectUpdate();
    }
}
