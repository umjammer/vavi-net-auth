/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.box;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import vavi.net.auth.oauth2.AuthUI;
import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.auth.oauth2.UserCredential;
import vavi.util.Debug;


/**
 * Box SeleniumAuthUI.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/27 umjammer initial version <br>
 */
public class SeleniumAuthUI implements AuthUI<String> {

    private String email;
    private String password;
    private String url;
    private String redirectUrl;

    /** */
    public SeleniumAuthUI(BasicAppCredential appCredential, UserCredential userCredential) {
        this.email = userCredential.getId();
        this.password = userCredential.getPassword();
        this.url = appCredential.getOAuthAuthorizationUrl();
        this.redirectUrl = appCredential.getRedirectUrl();
    }

    /** */
    private CountDownLatch latch = new CountDownLatch(1);

    /** */
    private transient String code;
    /** */
    private volatile Exception exception;

    /* @see vavi.net.auth.oauth2.AuthUI#auth() */
    @Override
    public void auth() {
        SwingUtilities.invokeLater(() -> { openUI(url); });

        try { latch.await(); } catch (InterruptedException e) { throw new IllegalStateException(e); }

        closeUI();
    }

    private WebDriver driver;

    /** Create a JFrame with a JButton and a JFXPanel containing the WebView. */
    private void openUI(String url) {
        String pwd = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", pwd + "/bin/chromedriver");
        System.setProperty("webdriver.chrome.verboseLogging", "false");

        ChromeOptions chromeOptions = new ChromeOptions();
        String app = System.getProperty("com.google.chrome.app", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
        chromeOptions.setBinary(app);

        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().setSize(new Dimension(480, 640));

        process(url);
    }

    /** */
    private void closeUI() {
        driver.quit();
    }

    /** Creates a WebView and fires up */
    private void process(String url) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.get(url);

        Deque<String> tasks = new ArrayDeque<>();

        boolean login = false;
        while (!login) {
            try {
                sleep();
                wait.until(d -> {
                    if (d == null) {
                        throw new IllegalStateException("browser maight be closed");
                    }
                    String r = ((JavascriptExecutor) d).executeScript("return document.readyState;").toString();
//Debug.println(r);
                    return "complete".equals(r);
                });
                String location = driver.getCurrentUrl();
//Debug.println("location: " + location);
                if (location.indexOf("account.box.com") > -1) {
                    try {
                        WebElement element = null;
                        if (!tasks.contains("email") && (element = findElement(By.name("login"), 0)) != null) {
                            element.sendKeys(email);
                            tasks.add("email");
Debug.println("set " + tasks.peekLast());
                            sleep();
                        }
                        if (!tasks.contains("password") && (element = findElement(By.name("password"))) != null) {
                            if (password != null) {
                                element.sendKeys(password);
                                tasks.add("password");
Debug.println("set " + tasks.peekLast());
                                sleep();
                            } else {
Debug.println("no password");
                                continue;
                            }
                        }
                        if (tasks.contains("email") && tasks.contains("password") && !tasks.contains("login") && (element = findElement(By.className("login_submit"))) != null) {
                            click(element);
                            tasks.add("login");
Debug.println("set " + tasks.peekLast());
                            sleep();
                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
Debug.println(Level.WARNING, e.getMessage());
                    }
                } else if (location.indexOf("app.box.com") > -1) {
                    WebElement element = null;
                    if (!tasks.contains("consent") && (element = findElement(By.name("consent_accept"))) != null) {
                        click(element);
                        tasks.add("consent");
Debug.println("set " + tasks.peekLast());
                        sleep();
                    }
                } else if (location.indexOf(redirectUrl) > -1) {
                    code = location;
Debug.println("code: " + code);
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

        latch.countDown();
    }

    /** */
    private void click(WebElement element) {
        new Actions(driver).moveToElement(element).click().build().perform();
    }

    /** */
    private WebElement findElement(By by) {
        try {
            return driver.findElement(by);
        } catch (org.openqa.selenium.NoSuchElementException e) {
Debug.println("not found: " + by);
            return null;
        }
    }

    /** */
    private WebElement findElement(By by, int index) {
        try {
            return driver.findElements(by).get(index);
        } catch (org.openqa.selenium.NoSuchElementException e) {
Debug.println("not found: " + by);
            return null;
        }
    }

    /** */
    private static void sleep() {
        try { Thread.sleep(300); } catch (InterruptedException e) {}
    }

    /* @see vavi.net.auth.oauth2.AuthUI#getResult() */
    @Override
    public String getResult() {
        return code;
    }

    /* @see vavi.net.auth.oauth2.AuthUI#getException() */
    @Override
    public Exception getException() {
        return exception;
    }

    /** */
    protected void finalize() {
        driver.quit();
    }
}

/* */
