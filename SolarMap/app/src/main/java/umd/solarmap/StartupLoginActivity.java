package umd.solarmap;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import umd.solarmap.DialogClasses.AlertDialog;
import umd.solarmap.AccountManager.SolarAccountManager;


/**
 * A login screen that offers login via email/password.
 */
public class StartupLoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mSignInButton;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_login);

        mEmailView = (EditText) findViewById(R.id.email_field);
        mPasswordView = (EditText) findViewById(R.id.password_field);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
    }

    public void onTouchSignInButton(View view) {

        if (areFieldsEmpty()) {
            displayErrorDialog("Please Fill in The Empty Text Fields.");
            return;
        }

        // Both text fields must not be empty
        String email_address = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if(SolarAccountManager.appAccountManager().login(email_address, password)) {

            // Start the main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // Display message dialog when user enter wrong username or password
            displayErrorDialog("Failed to Login. Wrong Username or Password.");
        }
    }

    public void onTouchSignUpButton(View view) {

        if (areFieldsEmpty()) {
            displayErrorDialog("Please Fill in The Empty Text Fields.");
            return;
        }

        // Both text fields must not be empty
        String email_address = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // If successfully register
        if (SolarAccountManager.appAccountManager().registerAccount(email_address, password)) {
            // Display message dialog when user enter wrong username or password
            displaySuccessDialog("Account Successfuly Registered.");

        } else { // If failed to register account
            // Display message dialog when user enter wrong username or password
            displayErrorDialog("Account Already Existed or Invalid Email.");
        }
    }

    /**
     * Check if the username & password text fields are empty. Return true if they're empty else false
     * @return boolean value indicating text fields are empty or not.
     */
    private boolean areFieldsEmpty() {
        return (mEmailView.getText().toString().isEmpty() || mPasswordView.getText().toString().isEmpty());
    }

    private void displayErrorDialog(String message) {
        AlertDialog.showDialog(this,
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getDrawable(this, R.drawable.failed),
                ContextCompat.getColor(this, R.color.colorBlackText),
                message,
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorWhiteText));
    }

    private void displaySuccessDialog(String message) {
        AlertDialog.showDialog(this,
                ContextCompat.getColor(this, R.color.colorSuccess),
                ContextCompat.getDrawable(this, R.drawable.success),
                ContextCompat.getColor(this, R.color.colorBlackText),
                message,
                ContextCompat.getColor(this, R.color.colorSuccess),
                ContextCompat.getColor(this, R.color.colorWhiteText));
    }
}

