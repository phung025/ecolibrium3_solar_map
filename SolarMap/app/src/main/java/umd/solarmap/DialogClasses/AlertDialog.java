package umd.solarmap.DialogClasses;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import umd.solarmap.R;

/**
 * This class is used to display a message dialog when user login or sign up an account
 * Created by Nam Phung on 12/3/2016.
 */

public class AlertDialog {
    public static void showDialog(Activity activity, int headerColor, Drawable icon, int textMessageColor, String message, int buttonColor, int buttonTextColor) {

        // Failed to login
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_activity_startup_login);
        dialog.setTitle("Failed to login");

        // Set dialog header's color
        RelativeLayout header = (RelativeLayout) dialog.findViewById(R.id.loginDialogHeader);
        header.setBackgroundColor(headerColor);

        // Set dialog's icon
        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.loginDialogIcon);
        headerIcon.setBackground(icon);

        // Set button's color
        Button okButton = (Button) dialog.findViewById(R.id.loginDialogButtonOK);
        okButton.setBackgroundColor(buttonColor);

        // Set the dialog message
        TextView messageText = (TextView) dialog.findViewById(R.id.loginDialogMessageText);
        messageText.setText(message);

        Button dialogButton = (Button) dialog.findViewById(R.id.loginDialogButtonOK);
        dialogButton.setOnClickListener(senderView -> {
            dialog.dismiss();
        });

        // Display the dialog
        dialog.show();
    }
}
