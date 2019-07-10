/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.auth.oauth2.dropbox;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLInputElement;

import vavi.net.auth.oauth2.AuthUI;
import vavi.net.auth.oauth2.Authenticator;
import vavi.net.auth.oauth2.microsoft.SeleniumAuthUI;
import vavi.net.http.HttpServer;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


/**
 * DropBoxAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/02 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavifuse/credentials.properties")
public class DropBoxAuthenticator implements Authenticator<String> {

    /** */
    private final String authorizationUrl;
    /** */
    private final String redirectUrl;
    /** */
    @Property(name = "dropbox.password.{0}")
    private transient String password;
    /** */
    private transient String code;

    /** */
    public DropBoxAuthenticator(String authorizationUrl, String redirectUrl) {
        this.authorizationUrl = authorizationUrl;
        this.redirectUrl = redirectUrl;
    }

    /* @see Authenticator#get(java.lang.String) */
    @Override
    public String authorize(String email) throws IOException {

        PropsEntity.Util.bind(this, email);
//System.err.println("password for " + email + ": " + password);

        URL redirectUrl = new URL(this.redirectUrl);
        String host = redirectUrl.getHost();
        int port = redirectUrl.getPort();

        HttpServer httpServer = new HttpServer(host, port);
        httpServer.start();

        AuthUI<String> ui = new JavaFxAuthUI(email, password, this.authorizationUrl, this.redirectUrl);
        ui.auth();

        httpServer.stop();

        if (ui.getException() != null) {
            Debug.println(Level.WARNING, ui.getException().getMessage());
        }

        return ui.getResult();
    }
}

/* */
