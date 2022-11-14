/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.dropbox;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import vavi.net.auth.web.dropbox.DropBoxLocalUserCredential;
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

    @Property(name = "dropbox.email")
    private String email;

    @BeforeEach
    void setup() throws Exception {
        PropsEntity.Util.bind(this);
    }

    @Test
    void test1() throws Exception {
        DropBoxLocalUserCredential userCredential = new DropBoxLocalUserCredential(email);
        DropBoxLocalAppCredential appCredential = new DropBoxLocalAppCredential();

        String code = new DropBoxOAuth2(appCredential).authorize(userCredential);
    }
}
