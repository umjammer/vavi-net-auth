/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.selenium;

import java.io.Closeable;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import vavi.util.Debug;


/**
 * SeleniumUtil.
 *
 * TODO https://sqa.stackexchange.com/a/22199
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/03 umjammer initial version <br>
 */
public class SeleniumUtil implements Closeable {

    /** */
    private WebDriver driver;

    /** */
    public WebDriver getWebDriver() {
        return driver;
    }

    /** headless */
    public SeleniumUtil() {

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        WebDriverFactory webDriverFactory = WebDriverFactory.newInstace();
        this.driver = webDriverFactory.getDriver(true);
    }

    /** windowed */
    public SeleniumUtil(int width, int height) {

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        WebDriverFactory webDriverFactory = WebDriverFactory.newInstace();
        this.driver = webDriverFactory.getDriver(false);
        driver.manage().window().setSize(new Dimension(width, height));
    }

    /** */
    public void waitFor() {
        waitFor(10);
    }

    /** */
    public void waitFor(int delay) {
        waitFor(driver, delay);
    }

    /** */
    public static void waitFor(WebDriver driver) {
        waitFor(driver, 10);
    }

    /**
     * @param delay in [milli seconds]
     */
    public static void waitFor(WebDriver driver, int delay) {
        new WebDriverWait(driver, Duration.ofMillis(delay)).until(d -> {
            if (d == null) {
                throw new IllegalStateException("browser might be closed");
            }
            String r = ((JavascriptExecutor) d).executeScript("return document.readyState;").toString();
//Debug.println(r);
            return "complete".equals(r);
        });
    }

    /** */
    public void setAttribute(WebElement element, String name, String value) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, name, value);
    }

    /** */
    public void showStats() {
        System.err.println("----------------------------");
        System.err.println(driver.getCurrentUrl());
        AtomicInteger c = new AtomicInteger();
        driver.getWindowHandles().forEach(h -> System.err.println(c.incrementAndGet() + ": " + h));
        System.err.println("----------------------------");
    }

    /** */
    public WebElement findElement(By by) {
        try {
            return driver.findElement(by);
        } catch (org.openqa.selenium.NoSuchElementException e) {
Debug.println("not found: " + by);
            return null;
        }
    }

    /** */
    public WebElement findElement(By by, int index) {
        try {
            return driver.findElements(by).get(index);
        } catch (org.openqa.selenium.NoSuchElementException e) {
Debug.println("not found: " + by);
            return null;
        }
    }

    /** */
    public List<WebElement> findElements(By by) {
        try {
            return driver.findElements(by);
        } catch (org.openqa.selenium.NoSuchElementException e) {
Debug.println("not found: " + by);
            return null;
        }
    }

    /** */
    public void click(WebElement element) {
        new Actions(driver).moveToElement(element).click().build().perform();
    }

    /** */
    public void get(String url) {
        driver.get(url);
    }

    /** */
    public void navigateTo(String url) {
        driver.navigate().to(url);
    }

    /** */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** */
    public String getPageSource() {
        return driver.getPageSource();
    }

    /** */
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    /** */
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    /** */
    public void close() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    /** */
    public void sleep(long miliseconds) {
        try { Thread.sleep(miliseconds); } catch (InterruptedException ignored) {}
    }
}

