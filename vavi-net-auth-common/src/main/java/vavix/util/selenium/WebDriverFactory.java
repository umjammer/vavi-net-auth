/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.selenium;

import org.openqa.selenium.WebDriver;


/**
 * WebDriverFactory.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/09/21 umjammer initial version <br>
 */
public interface WebDriverFactory {

    /** */
    WebDriver getDriver(boolean headless);

    /** */
    static WebDriverFactory newInstace() {
        return new ChromeWebDriverFactory();
//        return new SafariWebDriverFactory();
    }
}
