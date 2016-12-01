package umd.solarmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Displays a dialog containing information about rooftop features and also provides options to the
 * user such as saving the location, getting more info about the rooftop, or ignoring the dialog.
 * Created by John on 11/30/2016.
 */

public class PopupDialog extends Activity {

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

    public void SaveLocation(View view) {
        //Save Location
    }

    public void moreInfo(View view) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("optimalRating", optimalInfo);
        intent.putExtra("moderateRating", moderateInfo);
        intent.putExtra("flat_pct", flatVal);
        startActivity(intent);
        finish();
    }

    public void clear(View view) {
        finish();
    }
}
