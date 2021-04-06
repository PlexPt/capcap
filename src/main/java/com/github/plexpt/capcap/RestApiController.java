package com.github.plexpt.capcap;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.img.ImgUtil;
import lombok.SneakyThrows;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/**
 * @author pengtao
 * @email plexpt@gmail.com
 * @date 2021-03-12 18:06
 */
@RestController
public class RestApiController {

    static WebDriver driver;

    @Autowired
    CapConfig config;


    @PostConstruct
    public void init() {
        //设置chrome.exe和chromedriver.exe的系统参数‪C:\chromedriver.exe""
        System.setProperty("webdriver.chrome.bin", config.getPath());
        System.setProperty("webdriver.chrome.driver", config.getDriver());
        System.setProperty("webdriver.chrome.whitelistedIps", "");

        //启动chrome实例
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920x1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    }


    @RequestMapping("/testshot")
    @SneakyThrows
    public String shot(String url, List<Cookie> cookies) {

        init();

        //访问
        driver.get(url);
        //删除所有cookie
        driver.manage().deleteAllCookies();
        for (Cookie cookie : cookies) {
            driver.manage().addCookie(cookie);
        }
        //访问“网站首页”
//        driver.get("url");

//        String xpath = "//*[@id=\"popup-ann-modal\"]/div/div/div[3]/button";
//        String xpath2 = "//*[@id=\"app\"]/div/nav/form/ul/li/a/i";
//        driver.findElement(By.xpath(xpath)).click();
//        driver.findElement(By.xpath(xpath2)).click();
        //打印cookie
        Thread.sleep(500);
        //指定了OutputType.FILE做为参数传递给getScreenshotAs()方法，其含义是将截取的屏幕以文件形式返回。
//        String base64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);


        Screenshot screenshot = new AShot()
                .shootingStrategy(ShootingStrategies.viewportPasting(1000))
                .takeScreenshot(driver);

        ImageIO.write(screenshot.getImage(), "jpg", new File("shot.jpg"));
//        driver.quit();
        String jpg = ImgUtil.toBase64(screenshot.getImage(), "jpg");

        return jpg;
    }


    @GetMapping("/shot64")
    @SneakyThrows
    public String shot64(String url) {

//        init();

        BufferedImage screenshot = getImage(url, new ArrayList<>());

        String jpg = ImgUtil.toBase64(screenshot, "jpg");

        return jpg;
    }


    @PostMapping("/shot64")
    @SneakyThrows
    public String shot64(String url, List<Cookie> cookies) {

        init();
        BufferedImage screenshot = getImage(url, cookies);

        String jpg = ImgUtil.toBase64(screenshot, "jpg");

        return jpg;
    }

    @RequestMapping("/screen.jpg")
    @SneakyThrows
    public void screen(String url,
                       HttpServletResponse httpServletResponse) {
//        init();

        // 返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage screenshot = getImage(url, new ArrayList<>());

        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        ImageIO.write(screenshot, "jpg", jpegOutputStream);

        // 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        byte[] jpgbyte = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(jpgbyte);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    @RequestMapping("/screen2.jpg")
    @SneakyThrows
    public void screen2(String url, @RequestBody List<Cookie> cookies,
                        HttpServletResponse httpServletResponse) {
//        init();

        // 返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage screenshot = getImage(url, cookies);

        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        ImageIO.write(screenshot, "jpg", jpegOutputStream);

        // 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        byte[] jpgbyte = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(jpgbyte);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    private BufferedImage getImage(String url, @RequestBody List<Cookie> cookies) {

        //访问
        driver.get(url);
        //删除所有cookie
        driver.manage().deleteAllCookies();
        for (Cookie cookie : cookies) {
            driver.manage().addCookie(cookie);
        }

        WebElement element = driver.findElement(By.xpath ("/html/body"));
        return new AShot()
                .shootingStrategy(ShootingStrategies.viewportPasting(1000))
                .takeScreenshot(driver,element)
                .getImage();
    }

}
