/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import vavi.util.Debug;


/**
 * SeleniumUtil.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/03 umjammer initial version <br>
 */
public class SeleniumUtil {

    /** */
    private static void setEnv() {
        String pwd = System.getProperty("user.dir");
        if (System.getProperty("webdriver.chrome.driver") == null) {
            System.setProperty("webdriver.chrome.driver", pwd + "/bin/chromedriver");
        }
Debug.println("webdriver.chrome.driver: " + System.getProperty("webdriver.chrome.driver"));
        if (System.getProperty("webdriver.chrome.verboseLogging") == null) {
            System.setProperty("webdriver.chrome.verboseLogging", "false");
        }
Debug.println("webdriver.chrome.verboseLogging: " + System.getProperty("webdriver.chrome.verboseLogging"));

    }

    /** headless */
    public static WebDriver getDriver() {
        setEnv();

        ChromeOptions chromeOptions = new ChromeOptions();
        String app = System.getProperty("com.google.chrome.app", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
Debug.println("com.google.chrome.app: " + System.getProperty("com.google.chrome.app"));
        chromeOptions.setBinary(app);

        chromeOptions.addArguments("--headless");
        WebDriver driver = new ChromeDriver(chromeOptions);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> driver.quit()));

        return driver;
    }

    /** windowed */
    public static WebDriver getDriver(int width, int height) {
        setEnv();

        ChromeOptions chromeOptions = new ChromeOptions();
        String app = System.getProperty("com.google.chrome.app", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
Debug.println("com.google.chrome.app: " + System.getProperty("com.google.chrome.app"));
        chromeOptions.setBinary(app);

        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().setSize(new Dimension(width, height));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> driver.quit()));

        return driver;
    }

    /** */
    public static void waitFor(WebDriver driver) {
        new WebDriverWait(driver, 10).until(
            d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        try { Thread.sleep(300); } catch (InterruptedException e) {}
    }

    /** */
    public static void waitFor(WebDriver driver, int delay) {
        new WebDriverWait(driver, delay).until(d -> {
            if (d == null) {
                throw new IllegalStateException("browser maight be closed");
            }
            String r = ((JavascriptExecutor) d).executeScript("return document.readyState;").toString();
//Debug.println(r);
            return "complete".equals(r);
        });
    }

    /** */
    public static void setAttribute(WebDriver driver, WebElement element, String name, String value) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, name, value);
    }

    /** */
    static int c;

    /** */
    public static void showStats(WebDriver driver) {
        System.err.println("----------------------------");
        System.err.println(driver.getCurrentUrl());
        c = 0;
        driver.getWindowHandles().forEach(h -> { System.err.println(c++ + ": " + h); });
        System.err.println("----------------------------");
    }

    /** */
    public static WebElement findElement(WebDriver driver, By by) {
        try {
            return driver.findElement(by);
        } catch (org.openqa.selenium.NoSuchElementException e) {
Debug.println("not found: " + by);
            return null;
        }
    }
}

