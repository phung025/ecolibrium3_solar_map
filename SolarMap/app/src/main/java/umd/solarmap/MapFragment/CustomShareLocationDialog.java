package umd.solarmap.MapFragment;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import umd.solarmap.AccountManager.SolarAccountManager;
import umd.solarmap.HTTPAsyncTask;

/**
 * Created by user on 11/15/16.
 */

class CustomShareLocationDialog {

    /**
     * Instance fields
     */
    private android.app.AlertDialog locationActionDialog = null;
    private double longitude = 0;
    private double latitude = 0;

    // Prevent default constructor from being called
    private CustomShareLocationDialog(){};

    /**
     *
     * @param targetActivity
     * @param longitude
     * @param latitude
     */
    public CustomShareLocationDialog(Activity targetActivity, double longitude, double latitude) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(targetActivity);
        String[] optionsTitle = {"Share this place", "Save this place"};
        builder.setTitle("Set Location");
        builder.setItems(optionsTitle, (dialog, which) -> {
            if (which == 0) {
                System.out.println("You chose to share location");
                SolarAccountManager.appAccountManager().shareInterestedLocation("location name", longitude, latitude);
            } else {
                System.out.println("You chose to save location");
                SolarAccountManager.appAccountManager().saveInterestedLocation("location name", longitude, latitude);
            }
        });

        this.locationActionDialog = builder.create();
    }

    /**
     * Display the dialog
     */
    public void show() {
        locationActionDialog.show();
    }
}
