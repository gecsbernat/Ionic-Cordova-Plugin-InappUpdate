package cordova.plugin.inappupdate;

import android.content.Context;
import android.content.IntentSender;
import android.util.Log;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class InappUpdate extends CordovaPlugin {
    private String TAG = " [ InappUpdate ] ";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = (cordova.getActivity()).getBaseContext();
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        if (action.equals("check")) {
            Log.d(TAG, "checking...");
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                Log.d(TAG, appUpdateInfo.toString());
                int version = appUpdateInfo.availableVersionCode();
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    Log.d(TAG, "Update available");
                    callbackContext.success(version);
                } else {
                    Log.d(TAG, "No update available");
                    callbackContext.success("false");
                }
            });
            return true;
        }
        if (action.equals("update")) {
            Log.d(TAG, "updating...");
            String arg = args.getString(0);
            if (arg.equals("immediate")) {
                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        try {
                            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, cordova.getActivity(), 000);
                            Log.d(TAG, "update started...");
                            callbackContext.success("update_started");
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            String str = e.getMessage();
                            callbackContext.error(str);
                        }
                    } else {
                        callbackContext.success("false");
                    }
                });
            } else if (arg.equals("flexible")) {
                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        try {
                            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, cordova.getActivity(), 000);
                            Log.d(TAG, "update started...");
                            callbackContext.success("update_started");
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            String str = e.getMessage();
                            callbackContext.error(str);
                        }
                    } else {
                        callbackContext.success("false");
                    }
                });
            } else {
                callbackContext.error("Invalid argument");
                return false;
            }
        }
        return true;
    }
}
