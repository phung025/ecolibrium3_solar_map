package umd.solarmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import umd.solarmap.DialogClasses.LoginDialog;
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

        // Both text fields must not be empty
        String email_address = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if(SolarAccountManager.appAccountManager().login(email_address, password)) {

            // Start the main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // Display message dialog when user enter wrong username or password
            LoginDialog.showDialog(this, "Failed to Login. Wrong Username or Password.");
        }
    }

    public void onTouchSignUpButton(View view) {

        // Both text fields must not be empty
        String email_address = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // If successfully register
        if (SolarAccountManager.appAccountManager().registerAccount(email_address, password)) {

        } else { // If failed to register account
            // Display message dialog when user enter wrong username or password
            LoginDialog.showDialog(this, "Account Already Existed or Invalid Email.");
        }
    }
}

