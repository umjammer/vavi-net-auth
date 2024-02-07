[![Release](https://jitpack.io/v/umjammer/vavi-net-auth.svg)](https://jitpack.io/#umjammer/vavi-net-auth)
[![Java CI](https://github.com/umjammer/vavi-net-auth/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-net-auth/actions)
[![CodeQL](https://github.com/umjammer/vavi-net-auth/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-net-auth/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)
[![Parent](https://img.shields.io/badge/Parent-vavi--apps--fuse-pink)](https://github.com/umjammer/vavi-apps-fuse)

# vavi-net-auth

Java OAuth2 and auto login and totp

## Status

| brand              | authorize | auto-login | totp | refresh | library |
|--------------------|-----------|------------|------|---------|---------|
| google             | ✅        | 🚧        |      | ?       | [google-api-java-client](https://developers.google.com/api-client-library/java/) |
| microsoft          | ✅        | 🚧        |      | ✅      | [oauth2-essentials](https://github.com/dmfs/oauth2-essentials) |
| dropbox            | ✅        | 🚧        |      | -       | [dropbox-core-sdk](https://github.com/dropbox/dropbox-sdk-java) |
| box                | ✅        | 🚧        |      | 🚧      | [box-java-sdk](https://github.com/box/box-java-sdk) |
| amazon (acd)       |           |            |      |         | []() |
| amazon (web)       | -         | ✅         |      | -       | []() |
| flickr             |           |            |      |         | []() |
| facebook           |           |            |      |         | []() |

## Installation

### jars

 * [maven](https://jitpack.io/#umjammer/vavi-net-auth)

### ~~selenium chrome driver~~ (obsolete, use os default browser)

 * Download the [chromedriver executable](https://chromedriver.chromium.org/downloads) and locate it into some directory.
   * Don't forget to run jvm with the jvm argument `-Dwebdriver.chrome.driver=/usr/local/bin/chromedriver`.

### each authenticator installation

[instruction wiki](https://github.com/umjammer/vavi-apps-fuse/wiki/Home#installation)

## Libraries

 * google
   * https://developers.google.com/drive/api/quickstart/java
   * https://search.maven.org/artifact/com.google.apis/google-api-services-drive
 * [oauth2-essentials](https://github.com/dmfs/oauth2-essentials) 🎯
 * [jersey](https://jersey.github.io/)

## TODO

 * google data store to prefs (wip)
 * refer to cyberduck profile
 * box when refresh token expired
 * [`GoogleCredentials#getApplicationDefault()`](https://cloud.google.com/docs/authentication/application-default-credentials) !!!
 * ~~only cookie.chrome.mac uses jna~~ 
