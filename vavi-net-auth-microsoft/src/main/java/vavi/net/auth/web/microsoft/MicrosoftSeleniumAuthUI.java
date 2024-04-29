/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.microsoft;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import vavi.net.auth.AuthUI;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.auth.totp.PinGenerator;
import vavi.net.http.HttpServer;
import vavi.util.Debug;

import vavix.util.selenium.SeleniumUtil;


/**
 * MicrosoftSeleniumAuthUI.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/27 umjammer initial version <br>
 * @see SeleniumUtil
 */
public class MicrosoftSeleniumAuthUI implements AuthUI<String> {

    private String email;
    private String password;
    private String url;
    private String redirectUrl;
    private String totpSecret;

    /** */
    public MicrosoftSeleniumAuthUI(OAuth2AppCredential appCredential, WithTotpUserCredential userCredential) {
        this.email = userCredential.getId();
        this.password = userCredential.getPassword();
        this.url = appCredential.getOAuthAuthorizationUrl();
        this.redirectUrl = appCredential.getRedirectUrl();
        this.totpSecret = userCredential.getTotpSecret();
Debug.println("totpSecret: " + totpSecret);
    }

    /** */
    private transient String code;
    /** */
    private volatile Exception exception;

    /** almost dummy, just receive redirection to local */
    private HttpServer httpServer;

    @Override
    public void auth() {
        try {
            URL redirectUrl = new URL(this.redirectUrl);
            String host = redirectUrl.getHost();
            int port = redirectUrl.getPort();

            HttpServer httpServer = new HttpServer(host, port);
            httpServer.start();

            openUI(url);
        } catch (IOException e) {
            dealException(e);
        }
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
//Debug.println("location: " + location);
                if (location.contains("oauth20_authorize") ||
                        location.contains("microsoft") ||
                        location.contains("login.live.com")) {
                    try {
                        WebElement element = su.findElement(By.className("form-control"));
//Debug.println("element: name = " + element.getTagName() + ", class = " + element.getAttribute("class") + ", id = " + element.getAttribute("id") + ", type = " + element.getAttribute("type"));
                        if (!tasks.contains("email") && "email".equals(element.getAttribute("type"))) {
                            element.sendKeys(email);
                            tasks.add("email");
                        } else if (!tasks.contains("password") && "password".equals(element.getAttribute("type"))) {
                            if (password != null) {
                                element.sendKeys(password);
                                tasks.add("password");
                            } else {
Debug.println("no password");
                                continue;
                            }
                        } else if (!tasks.contains("totp") && "tel".equals(element.getAttribute("type"))) {
                            if (totpSecret != null) {
Debug.println("here");
                                String pin = PinGenerator.computePin(totpSecret, null);
Debug.println("pin: " + pin);
                                element.sendKeys(pin);
                                tasks.add("totp");
                            } else {
Debug.println("no pin");
                                continue;
                            }
                        } else {
                            continue;
                        }
                        su.click(su.findElement(By.className("btn-primary")));
Debug.println("set " + tasks.peekLast());
                        su.sleep(300);
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        WebElement element = su.findElement(By.className("btn-primary"));
                        if (!tasks.contains("accept") && "ucaccept".equals(element.getAttribute("name"))) {
                            su.click(element);
                            tasks.add("accept");
Debug.println("set " + tasks.peekLast());
                            su.sleep(300);
                        } else {
e.printStackTrace();
                            if (exception == null) {
                                exception = new IllegalStateException(e);
                            } else {
                                exception.addSuppressed(e);
                            }
                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
Debug.println(Level.WARNING, e.getMessage());
                    }
                } else if (location.contains(redirectUrl)) {
//                    code = location.substring(location.indexOf("code=") + "code=".length(), location.indexOf("&"));
                    code = location;
Debug.println(Level.FINE, "code: " + code);
                    login = true;
                }
            } catch (Exception e) {
e.printStackTrace();
                dealException(e);
            }
        }
    }

    /** */
    private void dealException(Exception e) {
        if (exception == null) {
            exception = new IllegalStateException(e);
        } else {
            exception.addSuppressed(e);
        }
    }

    @Override
    public String getResult() {
        try {
            httpServer.stop();
        } catch (IOException e) {
            dealException(e);
        }
        return code;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    protected void finalize() {
        su.close();
    }
}
