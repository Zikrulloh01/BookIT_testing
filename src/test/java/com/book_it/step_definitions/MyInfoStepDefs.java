package com.book_it.step_definitions;


import com.book_it.pages.SelfPage;
import com.book_it.pages.SignInPage;
import com.book_it.utilities.BrowserUtils;
import com.book_it.utilities.ConfigurationReader;
import com.book_it.utilities.Driver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;


public class MyInfoStepDefs {

	@Given("user logs in using {string} {string}")
	public void user_logs_in_using(String email, String password) {
	    Driver.get().get(ConfigurationReader.get("url"));
	    Driver.get().manage().window().maximize();
	    SignInPage signInPage = new SignInPage();
	    signInPage.email.sendKeys(email);
	    signInPage.password.sendKeys(password);
		BrowserUtils.waitFor(1);
	    signInPage.signInButton.click();


	    	    
	}

	@When("user is on the my self page")
	public void user_is_on_the_my_self_page() {
	    SelfPage selfPage = new SelfPage();
	    selfPage.goToSelf();
	}


	
}
