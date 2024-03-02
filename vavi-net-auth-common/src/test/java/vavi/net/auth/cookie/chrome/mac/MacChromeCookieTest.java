/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.cookie.chrome.mac;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * MacChromeCookieTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-01-23 nsano initial version <br>
 */
class MacChromeCookieTest {

    @Test
    @EnabledOnOs(OS.MAC)
    @DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*") // cause a dialog will be shown
    void test1() throws Exception {
        MacChromeCookie chromeCookie = new MacChromeCookie();
        Map<String, String> cookie = chromeCookie.getCookie(".google.com");
        cookie.forEach((k, v) -> System.err.println(k + "=" + v));
Debug.println(cookie.get("cf_clearance"));
        assertNotNull(cookie.get("user_id"));
    }
}

/* */
