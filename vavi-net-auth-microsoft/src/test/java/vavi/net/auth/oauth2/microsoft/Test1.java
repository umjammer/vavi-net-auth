/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.microsoft;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import vavi.net.auth.WithTotpUserCredential;
import vavi.net.auth.web.microsoft.MicrosoftLocalUserCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * Test1.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-11-14 nsano initial version <br>
 */
@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
public class Test1 {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "onedrive.email")
    private String email;

    @BeforeEach
    void setup() throws Exception {
        PropsEntity.Util.bind(this);
    }

    /**
     * when failed...
     * <li>refresh token is expired (rm ~/.vavifuse/msgraph/email@address)</li>
     * <li>check secret is expired (<a href="https://portal.azure.com/#view/Microsoft_AAD_RegisteredApps/ApplicationsListBlade">azure.com</a>)</li>
     */
    @Test
    void test1() throws Exception {
        WithTotpUserCredential userCredential = new MicrosoftLocalUserCredential(email);
        MicrosoftGraphLocalAppCredential appCredential = new MicrosoftGraphLocalAppCredential();

        // TODO startTalkenRefresher = false cause npe
        String code = new MicrosoftGraphOAuth2(appCredential, true).authorize(userCredential);
    }
}
