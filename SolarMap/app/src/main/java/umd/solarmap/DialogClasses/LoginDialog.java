package umd.solarmap.DialogClasses;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.TextView;

import umd.solarmap.R;

/**
 * This class is used to display a message dialog when user login or sign up an account
 * Created by Nam Phung on 12/3/2016.
 */

public class LoginDialog {
    public static void showDialog(Activity activity, String message) {
        // Failed to login
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_activity_startup_login);
        dialog.setTitle("Failed to login");

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
