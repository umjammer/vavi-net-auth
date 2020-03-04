/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.selenium;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * SeleniumUtil.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/03/03 umjammer initial version <br>
 */
public class SeleniumUtil {

    /** */
    public static void waitFor(WebDriver driver) {
        new WebDriverWait(driver, 10).until(
            d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        try { Thread.sleep(300); } catch (InterruptedException e) {}
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
}

