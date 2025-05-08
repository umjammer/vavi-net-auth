/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.google;

import java.io.Closeable;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayDeque;
import java.util.Deque;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import vavi.net.auth.AuthUI;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavix.util.selenium.SeleniumUtil;

import static java.lang.System.getLogger;


/**
 * GoogleSeleniumAuthUI.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/27 umjammer initial version <br>
 * @see SeleniumUtil
 */
public class GoogleSeleniumAuthUI implements AuthUI<String>, Closeable {

    private static final Logger logger = getLogger(GoogleSeleniumAuthUI.class.getName());

    private final String email;
    private final String password;
    private final String url;
    private final String redirectUrl;
    private final String totpSecret;

    /** */
    public GoogleSeleniumAuthUI(OAuth2AppCredential appCredential, WithTotpUserCredential userCredential) {
        this.email = userCredential.getId();
        this.password = userCredential.getPassword();
        this.url = appCredential.getOAuthAuthorizationUrl();
        this.redirectUrl = appCredential.getRedirectUrl();
        this.totpSecret = userCredential.getTotpSecret();
logger.log(Level.TRACE, "totpSecret: " + totpSecret);
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
        int retry = 0;
        while (!login) {
            try {
                su.sleep(300);
                su.waitFor();
                String location = su.getCurrentUrl();
logger.log(Level.DEBUG, "location: " + location);
                if (location.contains("accounts.google.com")) {
                    try {
                        WebElement element = null;
//logger.log(Level.TRACE, "element: name = " + element.getTagName() + ", class = " + element.getAttribute("class") + ", id = " + element.getAttribute("id") + ", type = " + element.getAttribute("type"));
                        if (!tasks.contains("email") && (element = su.findElement(By.id("identifierId"))) != null) {
                            element.sendKeys(email);
                            tasks.add("email");
                            su.click(su.findElement(By.id("identifierNext")));
logger.log(Level.DEBUG, "set " + tasks.peekLast());
                            su.sleep(300);
                        } else if (!tasks.contains("password") && (element = su.findElement(By.name("password"))) != null) {
                            if (password != null) {
                                element.sendKeys(password);
                                tasks.add("password");
                                su.click(su.findElement(By.id("passwordNext")));
logger.log(Level.DEBUG, "set " + tasks.peekLast());
                                su.sleep(300);
                            } else {
logger.log(Level.DEBUG, "no password");
                                continue;
                            }
//                        } else if (!tasks.contains("totp") && (element = su.findElement(By.name("Email"))) != null) {
//                            if (totpSecret != null) {
//logger.log(Level.TRACE, "here");
//                                String pin = PinGenerator.computePin(totpSecret, null);
//logger.log(Level.TRACE, "pin: " + pin);
//                                element.sendKeys(pin);
//                                tasks.add("totp");
//                            } else {
//logger.log(Level.TRACE, "no pin");
//                                continue;
//                            }
                        } else {
                            retry++;
                            if (retry == 10) {
                                throw new IllegalStateException("to many retries");
                            }
                            continue;
                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
logger.log(Level.WARNING, e.getMessage());
                    }
                } else if (location.contains(redirectUrl)) {
//                    code = location.substring(location.indexOf("code=") + "code=".length(), location.indexOf("&"));
                    code = location;
logger.log(Level.DEBUG, "code: " + code);
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
    public void close() {
        su.close();
    }
}
