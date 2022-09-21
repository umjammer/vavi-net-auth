/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.amazon;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import vavi.net.auth.AuthUI;
import vavi.util.Debug;

import vavix.util.selenium.SeleniumUtil;


/**
 * AmazonSeleniumAuthUI.
 *
 * web login
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/27 umjammer initial version <br>
 */
public class AmazonSeleniumAuthUI implements AuthUI<WebDriver> {

    private String email;
    private String password;
    private String url;

    /** */
    public AmazonSeleniumAuthUI(String email, String password, String url) {
        this.email = email;
        this.password = password;
        this.url = url;
    }

    /** */
    private volatile Exception exception;

    @Override
    public void auth() {
        openUI(url);
    }

    private SeleniumUtil su;

    /** Create a JFrame with a JButton and a JFXPanel containing the WebView. */
    private void openUI(String url) {
        su = new SeleniumUtil(480, 640);
        process(url);
    }

    /** Creates a WebView and fires up */
    private void process(String url) {
        su.get(url);

        Deque<String> tasks = new ArrayDeque<>();

        boolean login = false;
        while (!login) {
            try {
                su.sleep(300);
                su.waitFor(10);
                String location = su.getCurrentUrl();
//Debug.println("location: " + location);
                if (location.contains("www.amazon.co.jp/ap/signin")) {
                    try {
                        WebElement element = null;
//Debug.println("element: name = " + element.getTagName() + ", class = " + element.getAttribute("class") + ", id = " + element.getAttribute("id") + ", type = " + element.getAttribute("type"));
                        if (!tasks.contains("email") && (element = su.findElement(By.id("ap_email"))) != null) {
                            element.sendKeys(email);
                            tasks.add("email");
                        } else if (!tasks.contains("password") && (element = su.findElement(By.id("ap_password"))) != null) {
                            if (password != null) {
                                element.sendKeys(password);
                                tasks.add("password");
                            } else {
Debug.println("no password");
                                continue;
                            }
                        } else if (!tasks.contains("captcha") && (element = su.findElement(By.id("ap_password"))) != null) {
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
                        if ("email".equals(tasks.peekLast()) && (element = su.findElement(By.id("continue"))) != null) {
                            su.click(element);
Debug.println("set " + tasks.peekLast());
                            su.sleep(300);
                        }
                        if ("password".equals(tasks.peekLast()) && (element = su.findElement(By.id("signInSubmit"))) != null) {
                            su.click(element);
Debug.println("set " + tasks.peekLast());
                            su.sleep(300);
                        }
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        su.click(su.findElement(By.id("signInSubmit")));
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
Debug.println(Level.WARNING, e.getMessage());
                    }
                } else if (location.contains("www.amazon.co.jp") &&
                        !location.contains("www.amazon.co.jp/ap/signin")) {
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

    @Override
    public WebDriver getResult() {
        return su.getWebDriver();
    }

    @Override
    public Exception getException() {
        return exception;
    }
}

/* */
