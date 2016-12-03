package umd.solarmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Displays a dialog containing information about rooftop features and also provides options to the
 * user such as saving the location, getting more info about the rooftop, or ignoring the dialog. Note
 * that this class is technically an activity that is merely behaving as a dialog, therefore it is never
 * instantiated in MapFragment.java.
 * Created by John on 11/30/2016.
 */

public class PopupDialog extends Activity {

    //Private variables
    private TextView information;
    private String data, optimalInfo, moderateInfo, flatVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callout_buttons);
        information = (TextView) findViewById(R.id.dialogText);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        data = b.get("Data").toString();
        optimalInfo = b.get("Optimal").toString();
        moderateInfo = b.get("Moderate").toString();
        flatVal = b.get("Flat").toString();

        information.setText(data);
    }

    /**
     * Gets the data associated with the currently selected feature and saves that data if the user
     * clicks 'Save'.
     * @param view xml
     */
    public void SaveLocation(View view) {
        //Save Location
    }

    /**
     * Invokes the web page activity via a button on the dialog if the user clicks 'Info'.
     * @param view xml
     */
    public void moreInfo(View view) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("optimalRating", optimalInfo);
        intent.putExtra("moderateRating", moderateInfo);
        intent.putExtra("flat_pct", flatVal);
        startActivity(intent);
        finish();
    }

    /**
     * Ends the activity if user clicks 'Ignore'.
     * @param view xml
     */
    public void clear(View view) {
        finish();
    }
}
