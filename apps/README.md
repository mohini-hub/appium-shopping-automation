# apps/

Place the APK of the app under test in this folder.

The tests are written for the Sauce Labs **My Demo App** (Android). Download the latest APK from:
https://github.com/saucelabs/my-demo-app-android/releases

Then point the framework at it in `src/main/resources/config.properties`:

    appPath=apps/<your-apk-file-name>.apk

or pass it on the command line:

    mvn test -DappPath=apps/<your-apk-file-name>.apk
