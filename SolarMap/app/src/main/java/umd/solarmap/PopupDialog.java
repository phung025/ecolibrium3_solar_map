package umd.solarmap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Displays a dialog containing information about rooftop features and also provides options to the
 * user such as saving the location, getting more info about the rooftop, or ignoring the dialog.
 * Created by John on 11/30/2016.
 */

public class PopupDialog extends Activity {

    private String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callout_buttons);
    }

    public void SaveLocation(View view) {
        //Save Location
    }

    public void moreInfo(View view) {
        //Open Web Page
    }

    public void clear(View view) {
        finish();
    }

    public void setText(String Text) {
        data = data + Text;
    }

}
