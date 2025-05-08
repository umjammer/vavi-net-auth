/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.web.box;

import java.io.Closeable;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import vavi.net.auth.AuthUI;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.http.HttpServer;
import vavix.util.selenium.SeleniumUtil;

import static java.lang.System.getLogger;


/**
 * Box Selenium AuthUI.
 * <p>
 * <ol>
 * <li> username, password in the same form
 * <li> sms authentication TODO
 * <li> confirm application grants
 * </ol>
 * </p>
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/27 umjammer initial version <br>
 * @see SeleniumUtil
 */
public class BoxSeleniumAuthUI implements AuthUI<String>, Closeable {

    private static final Logger logger = getLogger(BoxSeleniumAuthUI.class.getName());

    private final String email;
    private final String password;
    private final String url;
    private final String redirectUrl;

    /** */
    public BoxSeleniumAuthUI(OAuth2AppCredential appCredential, UserCredential userCredential) {
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

    /** Create a Selenium Driver. */
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
//logger.log(Level.TRACE, "location: " + location);
                if (location.contains("account.box.com")) {
                    try {
                        WebElement element = null;
                        if (!tasks.contains("email") && (element = su.findElement(By.name("login"), 0)) != null) {
                            element.sendKeys(email);
                            tasks.add("email");
logger.log(Level.DEBUG, "set " + tasks.peekLast());
                            su.sleep(300);
                        }
                        if (!tasks.contains("password") && (element = su.findElement(By.name("password"))) != null) {
                            if (password != null) {
                                element.sendKeys(password);
                                tasks.add("password");
logger.log(Level.DEBUG, "set " + tasks.peekLast());
                                su.sleep(300);
                            } else {
logger.log(Level.DEBUG, "no password");
                                continue;
                            }
                        }
                        if (tasks.contains("email") && tasks.contains("password") && !tasks.contains("login") && (element = su.findElement(By.className("login_submit"))) != null) {
                            su.click(element);
                            tasks.add("login");
logger.log(Level.DEBUG, "set " + tasks.peekLast());
                            su.sleep(300);
                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
logger.log(Level.WARNING, e.getMessage());
                    }
                } else if (location.contains("app.box.com")) {
                    WebElement element = null;
                    if (!tasks.contains("consent") && (element = su.findElement(By.name("consent_accept"))) != null) {
                        su.click(element);
                        tasks.add("consent");
logger.log(Level.DEBUG, "set " + tasks.peekLast());
                        su.sleep(300);
                    }
                } else if (location.contains(redirectUrl)) {
                    code = location;
logger.log(Level.DEBUG, "code: " + code);
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
    public void close() {
        su.close();
    }
}
