/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.google;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import vavi.net.auth.AuthUI;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.Debug;

import vavix.util.selenium.SeleniumUtil;


/**
 * GoogleSeleniumAuthUI.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/27 umjammer initial version <br>
 * @see SeleniumUtil
 */
public class GoogleSeleniumAuthUI implements AuthUI<String> {

    private String email;
    private String password;
    private String url;
    private String redirectUrl;
    private String totpSecret;

    /** */
    public GoogleSeleniumAuthUI(OAuth2AppCredential appCredential, WithTotpUserCredential userCredential) {
        this.email = userCredential.getId();
        this.password = userCredential.getPassword();
        this.url = appCredential.getOAuthAuthorizationUrl();
        this.redirectUrl = appCredential.getRedirectUrl();
        this.totpSecret = userCredential.getTotpSecret();
Debug.println(Level.FINER, "totpSecret: " + totpSecret);
    }

    /** */
    private transient String code;
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
        su.waitFor(10);
        su.get(url);

        Deque<String> tasks = new ArrayDeque<>();

        boolean login = false;
        while (!login) {
            try {
                su.sleep(300);
                su.waitFor();
                String location = su.getCurrentUrl();
Debug.println("location: " + location);
                if (location.indexOf("accounts.google.com") > -1) {
                    try {
                        WebElement element = null;
//Debug.println("element: name = " + element.getTagName() + ", class = " + element.getAttribute("class") + ", id = " + element.getAttribute("id") + ", type = " + element.getAttribute("type"));
                        if (!tasks.contains("email") && (element = su.findElement(By.id("identifierId"))) != null) {
                            element.sendKeys(email);
                            tasks.add("email");
                            su.click(su.findElement(By.id("identifierNext")));
Debug.println("set " + tasks.peekLast());
                            su.sleep(300);
                        } else if (!tasks.contains("password") && (element = su.findElement(By.name("password"))) != null) {
                            if (password != null) {
                                element.sendKeys(password);
                                tasks.add("password");
                                su.click(su.findElement(By.id("passwordNext")));
Debug.println("set " + tasks.peekLast());
                                su.sleep(300);
                            } else {
Debug.println("no password");
                                continue;
                            }
//                        } else if (!tasks.contains("totp") && (element = su.findElement(By.name("Email"))) != null) {
//                            if (totpSecret != null) {
//Debug.println("here");
//                                String pin = PinGenerator.computePin(totpSecret, null);
//Debug.println("pin: " + pin);
//                                element.sendKeys(pin);
//                                tasks.add("totp");
//                            } else {
//Debug.println("no pin");
//                                continue;
//                            }
                        } else {
                            continue;
                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
Debug.println(Level.WARNING, e.getMessage());
                    }
                } else if (location.indexOf(redirectUrl) > -1) {
//                    code = location.substring(location.indexOf("code=") + "code=".length(), location.indexOf("&"));
                    code = location;
Debug.println(Level.FINE, "code: " + code);
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
    public String getResult() {
        return code;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    protected void finalize() {
        su.quit();
    }
}

/* */
