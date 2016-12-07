package umd.solarmap.DialogClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import umd.solarmap.AccountManager.SolarAccountManager;
import umd.solarmap.R;
import umd.solarmap.UtilitiesClasses.CallbackFunction;
import umd.solarmap.WebViewActivity;

/**
 * Displays a dialog containing information about rooftop features and also provides options to the
 * user such as saving the location, getting more info about the rooftop, or ignoring the dialog. Note
 * that this class is technically an activity that is merely behaving as a dialog, therefore it is never
 * instantiated in MapFragment.java.
 * Created by John on 11/30/2016.
 */

public class SolarRooftopDialog {

    /**
     *
     * @param activity
     * @param data
     * @param optimalInfo
     * @param moderateInfo
     * @param flatVal
     * @param objectID
     */
    public static void displaySolarDialog(Activity activity, String data, String optimalInfo, String moderateInfo, String flatVal, String objectID, CallbackFunction dismissAction) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_solar_rooftop);

        // Update the map when close dialog
        dialog.setOnDismissListener(dialogInterface -> {

        });

        // Set dialog's content message
        TextView informationView = (TextView) dialog.findViewById(R.id.dialogText);
        informationView.setText(data);

        Button showInterestButton = (Button) dialog.findViewById(R.id.showInterest);
        showInterestButton.setOnClickListener((view) -> {
            showInterest(dialog, objectID);
        });

        Button getMoreInfoButton = (Button) dialog.findViewById(R.id.moreInfo);
        getMoreInfoButton.setOnClickListener((view) -> {
            getMoreInfo(activity, optimalInfo, moderateInfo, flatVal);
        });

        Button okButton = (Button) dialog.findViewById(R.id.okSolarDialogButton);
        okButton.setOnClickListener((view) -> {
            dialog.dismiss();
            dismissAction.onPostExecute(null);
        });

        // Display the dialog
        dialog.show();
    }

    /**
     * Gets the data associated with the currently selected feature and saves that data if the user
     * clicks 'Save'.
     *
     * @param caller
     * @param objectID
     */
    private static void showInterest(Dialog caller, String objectID) {

        //Share interest in having solar panel installed on the building
        SolarAccountManager.appAccountManager().shareInterestInLocation(objectID, new CallbackFunction() {
            @Override
            public void onPostExecute(Object result) {
                HashMap<String, Integer> public_location_map = (HashMap<String, Integer>) result;

                // Set dialog's content message
                TextView informationView = (TextView) caller.findViewById(R.id.dialogText);
                String currentDialogMessageText = informationView.getText().toString();

                String totalPeopleInterested = String.valueOf(public_location_map.get(objectID));

                // Update message dialog text
                informationView.setText(currentDialogMessageText.replaceFirst("[0-9]* people like this\n",
                        (!totalPeopleInterested.equals("null")) ? totalPeopleInterested + " people like this\n" : "0 people like this\n"));
            }
        });
    }

    /**
     * Invokes the web page activity via a button on the dialog if the user clicks 'Info'.
     *
     * @param optimalInfo
     * @param moderateInfo
     * @param flatVal
     */
    private static void getMoreInfo(Activity activity, String optimalInfo, String moderateInfo, String flatVal) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("optimalRating", optimalInfo);
        intent.putExtra("moderateRating", moderateInfo);
        intent.putExtra("flat_pct", flatVal);
        activity.startActivity(intent);
    }
}
