package ashotapi;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CaptureLogoImage {

    WebDriver wd;

    @BeforeSuite
    public void setup() throws InterruptedException {
        File screenshots = new File("./screenshots");
        File[] files = screenshots.listFiles();
        for (File file : files) {
            file.delete();
        }
        WebDriverManager.chromedriver().setup();
        wd = new ChromeDriver();
        wd.manage().window().maximize();
        wd.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
    }

    @Test
    public void start() throws IOException {
        wd.get("https://google.com/");
        wd.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        Screenshot screenshot = new AShot().takeScreenshot(wd);
        ImageIO.write(screenshot.getImage(), "png", new File("./screenshots/fullPage.png"));
    }

    @Test
    public void startTwo() throws IOException {
        wd.get("https://google.com/");
        wd.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        WebElement logoImage = wd.findElement(By.xpath("//img[@alt='Google']"));
//        WebElement logoImage = wd.findElement(By.xpath("//img[@alt='company-branding']"));
        Screenshot screenshot = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(wd, logoImage);
        ImageIO.write(screenshot.getImage(), "png", new File("./screenshots/logo.png"));
    }

    @Test
    public void elementsDiff() throws IOException {
        wd.get("https://www.google.com");
        wd.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        WebElement logoImage = wd.findElement(By.xpath("//div[@class='FPdoLc lJ9FBc']//input[@name='btnK']"));
        Screenshot screenshot = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(wd, logoImage);
        ImageIO.write(screenshot.getImage(), "png", new File("./screenshots/logoTwoLang.png"));
        wd.get("https://www.google.com/?hl=ru");
        wd.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        WebElement logoImage2 = wd.findElement(By.xpath("//div[@class='FPdoLc lJ9FBc']//input[@name='btnK']"));
        Screenshot screenshot2 = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(wd, logoImage2);
        ImageIO.write(screenshot2.getImage(), "png", new File("./screenshots/logoTwo.png"));

        BufferedImage expectedImage = ImageIO.read(new File("./screenshots/logoTwo.png"));

        BufferedImage actualImage = screenshot.getImage();

        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(actualImage, expectedImage);

        ImageIO.write(diff.getMarkedImage(), "png", new File("./screenshots/diff.png"));
        ImageIO.write(diff.getTransparentMarkedImage(), "png", new File("./screenshots/diffTwo.png"));

        Assert.assertTrue(diff.hasDiff());
    }

    @Test
    public void elementsOnPagesDiff() throws IOException {
        wd.get("https://google.com/");
        wd.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        Screenshot screenshot = new AShot().takeScreenshot(wd);
        ImageIO.write(screenshot.getImage(), "png", new File("./screenshots/fullPageOne.png"));
        wd.get("https://www.google.com/?hl=ru");
        wd.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        Screenshot screenshot2 = new AShot().takeScreenshot(wd);
        ImageIO.write(screenshot2.getImage(), "png", new File("./screenshots/fullPageTwo.png"));

        BufferedImage expectedImage = ImageIO.read(new File("./screenshots/fullPageTwo.png"));

        BufferedImage actualImage = screenshot.getImage();

        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(actualImage, expectedImage);

        ImageIO.write(diff.getMarkedImage(), "png", new File("./screenshots/diffPage.png"));
        ImageIO.write(diff.getTransparentMarkedImage(), "png", new File("./screenshots/diffPageTwo.png"));

        Assert.assertTrue(diff.hasDiff());
    }

    @AfterSuite
    public void tearDown() {
        wd.quit();
    }
}
