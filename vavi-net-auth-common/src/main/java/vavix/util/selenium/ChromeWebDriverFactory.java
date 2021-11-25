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
 * <li> "webdriver.chrome.profile_directory" defalut: null</li>
 * <li> "com.google.chrome.app" default: "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"</li>
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/09/21 umjammer initial version <br>
 */
public class ChromeWebDriverFactory implements WebDriverFactory {

    public static final String COM_GOOGLE_CHROME_APP = "com.google.chrome.app";
    public static final String DEFAULT_COM_GOOGLE_CHROME_APP = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";

    public static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";
    public static final String WEBDRIVER_CHROME_HEADLESS = "webdriver.chrome.headless";
    public static final String WEBDRIVER_CHROME_VERBOSE_LOGGING = "webdriver.chrome.verboseLogging";
    public static final String WEBDRIVER_CHROME_PROFILE_DIRECTORY = "webdriver.chrome.profile_directory";

    /** */
    private void setEnv() {
        if (System.getProperty(WEBDRIVER_CHROME_DRIVER) == null) {
            String pwd = System.getProperty("user.dir");
            System.setProperty(WEBDRIVER_CHROME_DRIVER, pwd + "/bin/chromedriver");
        }
Debug.println(WEBDRIVER_CHROME_DRIVER + ": " + System.getProperty(WEBDRIVER_CHROME_DRIVER));
        if (System.getProperty(WEBDRIVER_CHROME_VERBOSE_LOGGING) == null) {
            System.setProperty(WEBDRIVER_CHROME_VERBOSE_LOGGING, Boolean.FALSE.toString());
        }
Debug.println(WEBDRIVER_CHROME_VERBOSE_LOGGING + ": " + System.getProperty(WEBDRIVER_CHROME_VERBOSE_LOGGING));
    }

    @Override
    public WebDriver getDriver(boolean headless) {
        setEnv();

        ChromeOptions chromeOptions = new ChromeOptions();
        String app = System.getProperty(COM_GOOGLE_CHROME_APP, DEFAULT_COM_GOOGLE_CHROME_APP);
Debug.println(COM_GOOGLE_CHROME_APP + ": " + System.getProperty(COM_GOOGLE_CHROME_APP));
        chromeOptions.setBinary(app);

        if (headless || Boolean.valueOf(System.getProperty(WEBDRIVER_CHROME_HEADLESS, Boolean.FALSE.toString()))) {
            chromeOptions.addArguments("--headless");
        }
        if (System.getProperty(WEBDRIVER_CHROME_PROFILE_DIRECTORY) != null) {
            String dir = System.getProperty(WEBDRIVER_CHROME_PROFILE_DIRECTORY);
            chromeOptions.addArguments("--user-data-dir=" + dir);
        }
        WebDriver driver = new ChromeDriver(chromeOptions);
        return driver;
    }
}

/* */
