package umd.solarmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.concurrent.Future;

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
        finish();
    }

    public void onTouchSignUpButton(View view) {

        // Both text fields must not be empty

        try {
            String email_address = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();

            JSONObject signUpInfo = new JSONObject();
            signUpInfo.put("email", email_address);
            signUpInfo.put("password", password);

            HTTPAsyncTask jsonResponse ;
            (jsonResponse = new HTTPAsyncTask() {
                @Override
                protected void onPostExecute(String result) {
                    System.out.println(result);
                }
            }).execute("http://10.0.2.2:4321/registerAccount", "POST", signUpInfo.toString());

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }
}

