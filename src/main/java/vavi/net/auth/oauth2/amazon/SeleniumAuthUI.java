/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.amazon;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import vavi.net.auth.oauth2.AuthUI;
import vavi.util.Debug;

import static vavix.util.selenium.SeleniumUtil.findElement;

import vavix.util.selenium.SeleniumUtil;


/**
 * SeleniumAuthUI.
 *
 * TODO use SeleniumUtil
 *
 * properties
 * <ul>
 * <li> "webdriver.chrome.driver" default: $PWD + "/bin/chromedriver"</li>
 * <li> "webdriver.chrome.verboseLogging" defalut: false</li>
 * <li> "com.google.chrome.app" default: "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"</li>
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/27 umjammer initial version <br>
 */
public class SeleniumAuthUI implements AuthUI<WebDriver> {

    private String email;
    private String password;
    private String url;

    /** */
    public SeleniumAuthUI(String email, String password, String url) {
        this.email = email;
        this.password = password;
        this.url = url;
    }

    /** */
    private volatile Exception exception;

    /* @see vavi.net.auth.oauth2.AuthUI#auth() */
    @Override
    public void auth() {
        openUI(url);
    }

    private WebDriver driver;

    /** Create a JFrame with a JButton and a JFXPanel containing the WebView. */
    private void openUI(String url) {
        driver = SeleniumUtil.getDriver(480, 640);
        process(url);
    }

    /** Creates a WebView and fires up */
    private void process(String url) {
        driver.get(url);

        Deque<String> tasks = new ArrayDeque<>();

        boolean login = false;
        while (!login) {
            try {
                sleep();
                SeleniumUtil.waitFor(driver, 10);
                String location = driver.getCurrentUrl();
//Debug.println("location: " + location);
                if (location.indexOf("www.amazon.co.jp/ap/signin") > -1) {
                    try {
                        WebElement element = null;
//Debug.println("element: name = " + element.getTagName() + ", class = " + element.getAttribute("class") + ", id = " + element.getAttribute("id") + ", type = " + element.getAttribute("type"));
                        if (!tasks.contains("email") && (element = findElement(driver, By.id("ap_email"))) != null) {
                            element.sendKeys(email);
                            tasks.add("email");
                        } else if (!tasks.contains("password") && (element = findElement(driver, By.id("ap_password"))) != null) {
                            if (password != null) {
                                element.sendKeys(password);
                                tasks.add("password");
                            } else {
Debug.println("no password");
                                continue;
                            }
                        } else if (!tasks.contains("captcha") && (element = findElement(driver, By.id("ap_password"))) != null) {
                            if (password != null) {
                                element.sendKeys(password);
                                tasks.add("captcha");
                            } else {
Debug.println("no password");
                                continue;
                            }
                        } else {
                            continue;
                        }
                        new Actions(driver).moveToElement(driver.findElement(By.id("continue"))).click().build().perform();
Debug.println("set " + tasks.peekLast());
                        sleep();
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        new Actions(driver).moveToElement(driver.findElement(By.id("signInSubmit"))).click().build().perform();
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
Debug.println(Level.WARNING, e.getMessage());
                    }
                } else if (location.indexOf("www.amazon.co.jp") > -1 &&
                           location.indexOf("www.amazon.co.jp/ap/signin") == -1) {
                    login = true;
                }
            } catch (Exception e) {
e.printStackTrace();
                if (exception == null) {
                    exception = new IllegalStateException(e);
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
    }

    /** */
    private static void sleep() {
        try { Thread.sleep(300); } catch (InterruptedException e) {}
    }

    /* @see vavi.net.auth.oauth2.AuthUI#getResult() */
    @Override
    public WebDriver getResult() {
        return driver;
    }

    /* @see vavi.net.auth.oauth2.AuthUI#getException() */
    @Override
    public Exception getException() {
        return exception;
    }
}

/* */
