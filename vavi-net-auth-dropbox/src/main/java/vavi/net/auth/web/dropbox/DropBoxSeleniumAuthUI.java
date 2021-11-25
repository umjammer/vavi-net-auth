/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.dropbox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

import org.openqa.selenium.WebElement;

import vavi.net.auth.AuthUI;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.http.HttpServer;
import vavi.util.Debug;

import vavix.util.selenium.SeleniumUtil;


/**
 * SeleniumAuthUI.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/27 umjammer initial version <br>
 * @see SeleniumUtil
 */
public class DropBoxSeleniumAuthUI implements AuthUI<String> {

    private String email;
    private String password;
    private String url;
    private String redirectUrl;

    /** */
    public DropBoxSeleniumAuthUI(UserCredential userCredential, OAuth2AppCredential appCredential) {
        this.email = userCredential.getId();
        this.password = userCredential.getPassword();
        this.url = appCredential.getOAuthAuthorizationUrl();
        this.redirectUrl = appCredential.getRedirectUrl();
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

            httpServer = new HttpServer(host, port);
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
                if (location.indexOf("www.dropbox.com") > -1) {
                    try {
                        WebElement element = null;
//Debug.println("element: name = " + element.getTagName() + ", class = " + element.getAttribute("class") + ", id = " + element.getAttribute("id") + ", type = " + element.getAttribute("type"));
//                        if (!tasks.contains("email") && (element = findElement(By.name("login_email"), 0)) != null) {
//                            element.sendKeys(email);
//                            tasks.add("email");
//Debug.println("set " + tasks.peekLast());
//                            sleep();
//                        }
//                        if (!tasks.contains("password") && (element = findElement(By.name("login_password"))) != null) {
//                            if (password != null) {
//                                element.sendKeys(password);
//                                tasks.add("password");
//Debug.println("set " + tasks.peekLast());
//                                sleep();
//                            } else {
//Debug.println("no password");
//                                continue;
//                            }
//                        }
//                        if (tasks.contains("email") && tasks.contains("password") && (element = findElement(By.className("login-button"))) != null) {
//                            click(element);
//                            sleep();
//                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
Debug.println(Level.WARNING, e.getMessage());
                    }
                } else if (location.indexOf(redirectUrl) > -1) {
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

/* */
