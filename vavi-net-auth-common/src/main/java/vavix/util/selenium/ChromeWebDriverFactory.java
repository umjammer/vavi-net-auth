/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import vavi.util.Debug;


/**
 * ChromeWebDriverFactory.
 *
 * system properties
 * <ul>
 * <li> "webdriver.chrome.driver" default: $PWD + "/bin/chromedriver"</li>
 * <li> "webdriver.chrome.verboseLogging" defalut: false</li>
 * <li> "com.google.chrome.app" default: "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"</li>
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/09/21 umjammer initial version <br>
 */
public class ChromeWebDriverFactory implements WebDriverFactory {

    /** */
    private void setEnv() {
        if (System.getProperty("webdriver.chrome.driver") == null) {
            String pwd = System.getProperty("user.dir");
            System.setProperty("webdriver.chrome.driver", pwd + "/bin/chromedriver");
        }
Debug.println("webdriver.chrome.driver: " + System.getProperty("webdriver.chrome.driver"));
        if (System.getProperty("webdriver.chrome.verboseLogging") == null) {
            System.setProperty("webdriver.chrome.verboseLogging", "false");
        }
Debug.println("webdriver.chrome.verboseLogging: " + System.getProperty("webdriver.chrome.verboseLogging"));

    }

    @Override
    public WebDriver getDriver(boolean headless) {
        setEnv();

        ChromeOptions chromeOptions = new ChromeOptions();
        String app = System.getProperty("com.google.chrome.app", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
Debug.println("com.google.chrome.app: " + System.getProperty("com.google.chrome.app"));
        chromeOptions.setBinary(app);

        if (headless) {
            chromeOptions.addArguments("--headless");
        }
        WebDriver driver = new ChromeDriver(chromeOptions);
        return driver;
    }
}

/* */
