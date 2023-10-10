package tests;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.*;
import utilities.Constant;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.restassured.*;

public class BankScenarios {

	public static WebDriver driver;
	//(interface )

	@DataProvider(name = "add-customer")
	//for a test case ,iterate for different values
	public Object[][] addCustomer(){
		Object [][] customer_details=new Object[7][3];
		customer_details[0][0]="Christopher"; 	customer_details[0][1]="Connely"; 		customer_details[0][2]="L789C349";
		customer_details[1][0]="Frank"; 		customer_details[1][1]="Christopher"; 	customer_details[1][2]="A897N450";
		customer_details[2][0]="Christopher"; 	customer_details[2][1]="Minka"; 		customer_details[2][2]="M098Q585";
		customer_details[3][0]="Connely"; 		customer_details[3][1]="Jackson"; 		customer_details[3][2]="L789C349";
		customer_details[4][0]="Jackson"; 		customer_details[4][1]="Frank"; 		customer_details[4][2]="L789C349";
		customer_details[5][0]="Minka"; 		customer_details[5][1]="Jackson"; 		customer_details[5][2]="A897N450";
		customer_details[6][0]="Jackson"; 		customer_details[6][1]="Connely"; 		customer_details[6][2]="L789C349";
		return customer_details;
	}

	@DataProvider(name = "transaction")
	public Object[][] transactions(){
		Object [][] transction=new Object[7][2];
		transction[0][0]="50000"; 	transction[0][1]="Credit";
		transction[1][0]="3000"; 	transction[1][1]="Debit";
		transction[2][0]="2000"; 	transction[2][1]="Debit";
		transction[3][0]="5000"; 	transction[3][1]="Credit";
		transction[4][0]="10000"; 	transction[4][1]="Debit";
		transction[5][0]="15000"; 	transction[5][1]="Debit";
		transction[6][0]="1500"; 	transction[6][1]="Credit";
		return transction;
	}

	@AfterClass
	public static void tearDown(){
		driver.close();
	}

	@BeforeClass
	public void beforeClass()
	{

		if(System.getProperty("os.name").contains("Windows"))
		{
			System.setProperty("webdriver.chrome.driver", Constant.ChromeDriver_Windows);
		}
		else
		{
			System.setProperty("webdriver.chrome.driver", Constant.ChromeDriver_MAC);
		}

		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		//implicit,explicit wait ,fluent wait
	}

	@BeforeMethod
	public void before()
	{
		driver.get("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
	}

	@Test(dataProvider = "add-customer")
	public void addCustomer(String customer_first_name,String customer_last_name , String postal_code) throws Exception{
		driver.findElement(By.xpath("//button[text()='Bank Manager Login']")).click();
		driver.findElement(By.xpath("//button[contains(text(),'Add Customer')]")).click();
		driver.findElement(By.xpath("//input[@placeholder='First Name']")).sendKeys(customer_first_name);
		driver.findElement(By.xpath("//input[@placeholder='Last Name']")).sendKeys(customer_last_name);
		driver.findElement(By.xpath("//input[@placeholder='Post Code']")).sendKeys(postal_code);
		driver.findElement(By.xpath("//button[@type='submit']")).click();

		driver.switchTo().alert().accept();
		//pop up accept
		driver.findElement(By.xpath("//button[contains(text(),'Customers')]")).click();

		String firstName="//tr/td[1][contains(text(),'"+customer_first_name+"')]";
		String lastName="//tr/td[2][contains(text(),'"+customer_last_name+"')]";
		String postalCode="//tr/td[3][contains(text(),'"+postal_code+"')]";

		Assert.assertTrue(driver.findElement(By.xpath(firstName)).isDisplayed());
		Assert.assertTrue(driver.findElement(By.xpath(lastName)).isDisplayed());
		Assert.assertTrue(driver.findElement(By.xpath(postalCode)).isDisplayed());

		System.out.println("First name :"+firstName+ " last name : "+lastName+ " postal : "+postal_code);

		if(customer_first_name.equals("Jackson") && customer_last_name.equals("Connely"))
		{
			//clicking delete
			driver.findElement(By.xpath("//tr/td[contains(text(),'"+customer_first_name+"')]/following-sibling::td[contains(text(),'"+customer_last_name+"')]/parent::tr/td[5]/button")).click();

			List<WebElement> ele = driver.findElements(By.xpath("//tr/td[contains(text(),'"+customer_first_name+"')]/following-sibling::td[contains(text(),'"+customer_last_name+"')]"));
			System.out.println("Ele size : "+ele.size());
			Assert.assertEquals(ele.size(),0);
		}
	}

	@Test(dataProvider = "transaction")
	public void performTransaction(String amount , String type) throws Exception{
		int trnsaction_amount = Integer.parseInt(amount);

		driver.findElement(By.xpath("//button[text()='Customer Login']")).click();
		driver.findElement(By.xpath("//label[text()='Your Name :']/following-sibling::select")).click();

		Select customer =new Select(driver.findElement(By.xpath("//label[text()='Your Name :']/following-sibling::select")));
		customer.selectByVisibleText("Hermoine Granger");

		driver.findElement(By.xpath("//button[text()='Login']")).click();

		Select account_no =new Select(driver.findElement(By.xpath("//select[@id='accountSelect']")));
		account_no.selectByVisibleText("1003");
//getting initial cust balance
		int current_Account_balance = Integer.parseInt(driver.findElement(By.xpath("//strong[@class='ng-binding'][2]")).getText());
		int final_amount=0;

		if(type.equals("Credit"))
		{
			System.out.println("Crediting amount :"+amount+" , current_Account_balance : "+current_Account_balance);
			driver.findElement(By.xpath("//button[contains(text(),'Deposit')]")).click();
			driver.findElement(By.xpath("//input[@placeholder='amount']")).sendKeys(amount);
			driver.findElement(By.xpath("//button[@type='submit']")).click();
			final_amount=trnsaction_amount+current_Account_balance;
			int after_transaction = Integer.parseInt(driver.findElement(By.xpath("//strong[@class='ng-binding'][2]")).getText());
			System.out.println("after_transaction : "+after_transaction+"  final_amount : "+final_amount);
			Assert.assertEquals(after_transaction,final_amount);
		}
		else
		{
			System.out.println("Debiting amount :"+amount+" current_Account_balance : "+current_Account_balance);
			driver.findElement(By.xpath("//button[contains(text(),'Withdrawl')]")).click();
			driver.findElement(By.xpath("//input[@placeholder='amount']")).sendKeys(amount);
			driver.findElement(By.xpath("//button[@type='submit']")).click();
			final_amount=current_Account_balance-trnsaction_amount;
			int after_transaction = Integer.parseInt(driver.findElement(By.xpath("//strong[@class='ng-binding'][2]")).getText());
			System.out.println("after_transaction : "+after_transaction+"  final_amount : "+final_amount);
			Assert.assertEquals(after_transaction,final_amount);
		}
	}


	@Test
	public void addUser()
	{
		RestAssured.baseURI = "https://petstore3.swagger.io/api/v3/";
		HashMap<String,Object> dataBody = new HashMap<String,Object>();

		dataBody.put("id", 10);
		dataBody.put("username", "Test_User_123");
		dataBody.put("firstName", "John");
		dataBody.put("lastName", "James");
		dataBody.put("email", "john@email.com");
		dataBody.put("password", "12345");
		dataBody.put("phone", "12345");
		dataBody.put("userStatus", 1);

		//Creating new user
		ResponseBody rbdy = RestAssured
				.given()
				.header("accept","application/json")
				.header("Content-Type","application/json")
				.contentType(ContentType.JSON)
				.body(dataBody)
				.when()
				.post("/user")
				.getBody();

		//Getting username

		ResponseBody response = RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/user/Test_User_123")
				.getBody();


		JsonPath read_user = new JsonPath(response.asString());
		String store_username = read_user.getString("username");
		Assert.assertEquals(store_username,"Test_User_123");


		// Updating user Test_User
		HashMap<String,Object> updateRequest = new HashMap<String,Object>();

		updateRequest.put("id", 10);
		updateRequest.put("username", "Test_User");
		updateRequest.put("firstName", "John_Test");
		updateRequest.put("lastName", "James");
		updateRequest.put("email", "john@email.com");
		updateRequest.put("password", "12345");
		updateRequest.put("phone", "12345");
		updateRequest.put("userStatus", 1);


		// updating previous user

		ResponseBody res = RestAssured.given().header("Content-Type", "application/json").body(updateRequest).put("/user/"+store_username).getBody();

		ResponseBody response_after_update = RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/user/Test_User")   //passing updated username here
				.getBody();

		JsonPath response_after_update_json = new JsonPath(response_after_update.asString());
		String userNameAfterUpdate = response_after_update_json.getString("username");

		//Delete request
		Response delete = RestAssured.given().header("Content-Type", "application/json") .delete("/user/"+ userNameAfterUpdate);

		//verify user is deleted

		String final_response = RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/user/"+userNameAfterUpdate)   //passing updated username here
				.then().extract().response().asString();

		Assert.assertFalse(final_response.contains(userNameAfterUpdate));

	}
}
