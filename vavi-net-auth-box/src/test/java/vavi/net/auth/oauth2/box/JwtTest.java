package vavi.net.auth.oauth2.box;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.box.sdkgen.box.jwtauth.BoxJWTAuth;
import com.box.sdkgen.box.jwtauth.JWTConfig;
import com.box.sdkgen.client.BoxClient;
import com.box.sdkgen.schemas.folderfull.FolderFull;
import com.box.sdkgen.schemas.item.Item;
import com.box.sdkgen.schemas.userfull.UserFull;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public final class JwtTest {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Property
    String userId;

    private static final String PUBLIC_KEY_ID = "";
    private static final String PRIVATE_KEY_FILE = "";
    private static final String PRIVATE_KEY_PASSWORD = "";
    private static final int MAX_DEPTH = 1;
    private static final int MAX_CACHE_ENTRIES = 100;

    @Test
    @Disabled
    void test1() throws IOException {

        OAuth2AppCredential appCredential = new BoxLocalAppCredential();
        PropsEntity.Util.bind(appCredential);

        // Turn off logging to prevent polluting the output.
        Logger.getLogger("com.box.sdk").setLevel(Level.OFF);

        String privateKey = new String(Files.readAllBytes(Paths.get(PRIVATE_KEY_FILE)));

        JWTConfig config = new JWTConfig.Builder(
                appCredential.getClientId(), appCredential.getClientSecret(),
                PUBLIC_KEY_ID, privateKey, PRIVATE_KEY_PASSWORD)
                .userId(userId)
                .build();
        BoxJWTAuth auth = new BoxJWTAuth(config);
        BoxClient client = new BoxClient(auth);

        UserFull user = client.users.getUserMe();
        System.out.format("Welcome, %s!\n\n", user.getName());

        FolderFull rootFolder = client.folders.getFolderById("0");
        listFolder(rootFolder, 0);
    }

    private static void listFolder(FolderFull folder, int depth) {
        for (Item item : folder.getItemCollection().getEntries()) {
            StringBuilder indent = new StringBuilder();
            indent.append("    ".repeat(Math.max(0, depth)));

            System.out.println(indent + item.getName());
            if (item.getType().equals("folder")) {
                FolderFull childFolder = item.getFolderFull();
                if (depth < MAX_DEPTH) {
                    listFolder(childFolder, depth + 1);
                }
            }
        }
    }
}