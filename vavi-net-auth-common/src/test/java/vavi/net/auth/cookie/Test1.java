/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.cookie;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import vavi.net.auth.cookie.Cookie;
import vavi.net.auth.cookie.chrome.mac.MacChromeCookie;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test1.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-01-23 nsano initial version <br>
 */
class Test1 {

    @Test
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
