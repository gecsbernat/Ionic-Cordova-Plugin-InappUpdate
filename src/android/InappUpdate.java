package cordova.plugin.inappupdate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class InappUpdate extends CordovaPlugin {
    private final String TAG = " [ InappUpdate ] ";
    private PluginResult pluginResult;
    private CallbackContext callback;
    private AppUpdateManager appUpdateManager;
    private Task<AppUpdateInfo> appUpdateInfoTask;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = (cordova.getActivity()).getBaseContext();
        appUpdateManager = AppUpdateManagerFactory.create(context);
        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        callback = callbackContext;
        switch (action) {
            case "check":
                this.checkForUpdate();
                break;
            case "update":
                this.installUpdate();
                break;
            default:
                return false;
        }
        return true;
    }

    private void checkForUpdate() {
        cordova.getThreadPool().execute(() -> {
            try {
                Log.d(TAG, "Checking for update...");
                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    pluginResult = new PluginResult(PluginResult.Status.OK, String.valueOf(false));
                    int version = appUpdateInfo.availableVersionCode();
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                        Log.d(TAG, "Update available: " + version);
                        pluginResult = new PluginResult(PluginResult.Status.OK, String.valueOf(version));
                    } else {
                        Log.d(TAG, "No update available");
                        pluginResult = new PluginResult(PluginResult.Status.OK, String.valueOf(false));
                    }
                    pluginResult.setKeepCallback(true);
                    callback.sendPluginResult(pluginResult);
                });
            } catch (Throwable e) {
                e.printStackTrace();
                callback.error(e.getMessage());
            }
        });
    }

    private void installUpdate() {
        cordova.getThreadPool().execute(() -> {
            try {
                Log.d(TAG, "Installing update...");
                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    pluginResult = new PluginResult(PluginResult.Status.OK, String.valueOf(false));
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                        if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                            try {
                                cordova.setActivityResultCallback(this);
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, cordova.getActivity(), 99);
                                Log.d(TAG, "IMMEDIATE update started...");
                                pluginResult = new PluginResult(PluginResult.Status.OK, "IMMEDIATE_UPDATE_STARTED");
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                                pluginResult = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                            }
                        } else {
                            try {
                                cordova.setActivityResultCallback(this);
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, cordova.getActivity(), 99);
                                Log.d(TAG, "FLEXIBLE update started...");
                                pluginResult = new PluginResult(PluginResult.Status.OK, "FLEXIBLE_UPDATE_STARTED");
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                                pluginResult = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                            }
                        }
                    }
                    pluginResult.setKeepCallback(true);
                    callback.sendPluginResult(pluginResult);
                });
            } catch (Throwable e) {
                e.printStackTrace();
                callback.error(e.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (resultCode != Activity.RESULT_OK) {
                Log.d(TAG, "Update failed");
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "UPDATE_FAILED");
            } else {
                Log.d(TAG, "Update success");
                pluginResult = new PluginResult(PluginResult.Status.OK, "UPDATE_SUCCESS");
            }
            pluginResult.setKeepCallback(false);
            callback.sendPluginResult(pluginResult);
        }
    }

}