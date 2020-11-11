/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;


/**
 * SafariWebDriverFactory.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/09/21 umjammer initial version <br>
 */
public class SafariWebDriverFactory implements WebDriverFactory {

    /**
     * @param headless no mean, safari doesn't have headless mode?
     */
    @Override
    public WebDriver getDriver(boolean headless) {
        SafariOptions options = new SafariOptions();

        WebDriver driver = new SafariDriver(options);
        return driver;
    }
}

/* */
