# Ionic Cordova Plugin InappUpdate

Cordova Plugin for Android In-App Update support.
https://developer.android.com/guide/playcore/in-app-updates

### Features

- compatible with ionic 4/5
- check available update in Play Store
- download and install update

### Platforms
- android

### Installation
```sh
$ ionic cordova plugin add [PLUGIN.GIT OR FOLDER]
```

### Usage

```typescript
// declare plugin after imports
declare const InappUpdate: any;

// check for update
InappUpdate.check((success: any) => {
  if (success !== 'false') {
    // success = versionCode to update
    console.log('available versionCode:', success);
    InappUpdate.update('immediate' /* OR 'flexible' */, (success: any) => {
      console.log(success);
    }, (error: any) => {
      console.log(error);
    });
  } else {
    console.log('no update available');
  }
}, (error: any) => {
    console.log(error);
});
```