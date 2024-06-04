import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FirstTest {
    private WebDriver driver;
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private AlertExample alertExample;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com/");
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
        alertExample = new AlertExample(driver);


    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }


    @Test
    public void testLoginPositive() {
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();
        Assert.assertTrue(loginPage.isLoggedIn());
    }
    @Test
    public void testLoginNegative() {
        loginPage.enterUsername("locked_out_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();
        Assert.assertTrue(loginPage.isLoggedIn());
    }



    @Test
    public void testAddItemsToCartAndRemoveBackpackAndBoltTShirt() {
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        inventoryPage.addBackpackToCart();
        inventoryPage.addBoltTShirtToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isBackpackInCart());
        Assert.assertTrue(cartPage.isBoltTShirtInCart());

        cartPage.removeBackpackFromCart();
        cartPage.removeBoltTShirtFromCart();

        Assert.assertFalse(cartPage.isBackpackInCart());
        Assert.assertFalse(cartPage.isBoltTShirtInCart());
    }

    @Test
    public void testAddItemsToCartNegative() {
        loginPage.enterUsername("problem_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        inventoryPage.addBackpackToCart();
        inventoryPage.addBoltTShirtToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isBackpackInCart());
        Assert.assertTrue(cartPage.isBoltTShirtInCart());
    }

    @Test
    public void testAddItemsToCartPositive() {
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        inventoryPage.addBackpackToCart();
        inventoryPage.addBoltTShirtToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isBackpackInCart());
        Assert.assertTrue(cartPage.isBoltTShirtInCart());
    }

    @Test
    public void testCheckout() {
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        inventoryPage.addBackpackToCart();
        inventoryPage.addBoltTShirtToCart();
        inventoryPage.goToCart();
        cartPage.clickCheckout();

        checkoutPage.enterFirstName("Jan");
        checkoutPage.enterLastName("Kowalski");
        checkoutPage.enterPostalCode("12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete());
    }

    @Test
    public void testAddOnesieAndBikeLightToCart() {
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        inventoryPage.addOnesieToCart();
        inventoryPage.goToCart();
        Assert.assertTrue(cartPage.isOnesieInCart());

        driver.navigate().back();
        inventoryPage.addBikeLightToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isBikeLightInCart());
        Assert.assertTrue(cartPage.isOnesieInCart());

        cartPage.clickCheckout();

        checkoutPage.enterFirstName("Jan");
        checkoutPage.enterLastName("Marcin");
        checkoutPage.enterPostalCode("12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete());
    }

    @Test
    public void testSortByLowestPriceAndCheckoutNegative() {
        loginPage.enterUsername("error_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        inventoryPage.sortProductsBy("Price (low to high)");
        alertExample.handleAlert(driver);
        String lowestPriceProductName = inventoryPage.getFirstProductName();
        String lowestPrice = inventoryPage.getFirstProductPrice();
        inventoryPage.addFirstProductToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isProductInCart(lowestPriceProductName));


        cartPage.clickCheckout();
        checkoutPage.enterFirstName("Zenek");
        checkoutPage.enterLastName("Marian");
        checkoutPage.enterPostalCode("34/12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete());

    }

    @Test
    public void testSortByLowestPriceAndCheckout1Positive() {
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        inventoryPage.sortProductsBy("Price (low to high)");

        String lowestPriceProductName = inventoryPage.getFirstProductName();
        String lowestPrice = inventoryPage.getFirstProductPrice();
        inventoryPage.addFirstProductToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isProductInCart(lowestPriceProductName));


        cartPage.clickCheckout();
        checkoutPage.enterFirstName("Jan");
        checkoutPage.enterLastName("Kowalski");
        checkoutPage.enterPostalCode("/34/12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete());

    }


    @Test
    public void testSortByNameAndSelectMultipleProductsNegative() {
        loginPage.enterUsername("error_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        inventoryPage.sortProductsBy("Name (Z to A)");
        alertExample.handleAlert(driver);
        String firstProductName = inventoryPage.getFirstProductName();
        String lastProductName = inventoryPage.getLastProductName();
        inventoryPage.addFirstProductToCart();
        inventoryPage.addLastProductToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isProductInCart(firstProductName));
        Assert.assertTrue(cartPage.isProductInCart(lastProductName));

        driver.navigate().back();
        inventoryPage.sortProductsBy("Price (high to low)");
        String highestPriceProductName = inventoryPage.getFirstProductName();
        inventoryPage.addFirstProductToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isProductInCart(highestPriceProductName));

        cartPage.clickCheckout();
        checkoutPage.enterFirstName("Paweł");
        checkoutPage.enterLastName("Kamiński");
        checkoutPage.enterPostalCode("45/12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete());

    }
    @Test
    public void testSortByNameAndSelectMultipleProductsPositive() {
        loginPage.enterUsername("performance_glitch_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLoginButton();

        inventoryPage.sortProductsBy("Name (Z to A)");

        String firstProductName = inventoryPage.getFirstProductName();
        String lastProductName = inventoryPage.getLastProductName();
        inventoryPage.addFirstProductToCart();
        inventoryPage.addLastProductToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isProductInCart(firstProductName));
        Assert.assertTrue(cartPage.isProductInCart(lastProductName));

        driver.navigate().back();
        inventoryPage.sortProductsBy("Price (high to low)");
        String highestPriceProductName = inventoryPage.getFirstProductName();
        inventoryPage.addFirstProductToCart();
        inventoryPage.goToCart();

        Assert.assertTrue(cartPage.isProductInCart(highestPriceProductName));

        cartPage.clickCheckout();
        checkoutPage.enterFirstName("Paweł");
        checkoutPage.enterLastName("Kamiński");
        checkoutPage.enterPostalCode("45/12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        Assert.assertTrue(checkoutPage.isOrderComplete());

    }
}
