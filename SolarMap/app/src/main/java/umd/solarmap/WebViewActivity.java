package umd.solarmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

/**
 * Activity responsible for displaying the web page containing the calculator. Web page URL is what
 * determines the calculation variables within the displayed webpage.
 * Created by John on 12/1/2016.
 */

public class WebViewActivity extends Activity {

    //Private variables
    WebView webView;
    String opt, mod, flat;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webview);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        //This data originates from MapFragment.java and is sent to this class using intents.
        opt = b.get("optimalRating").toString(); //Optimal Solar Rating
        mod = b.get("moderateRating").toString(); //Moderate Solar Rating
        flat = b.get("flat_pct").toString(); //Flat value percentage


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        //URL is changed accordingly based on what rooftop is selected.
        webView.loadUrl("http://umd-cla-gis04.d.umn.edu/DuluthSolar/docs/solarcalc.html?primeval="
                + opt + "&goodval=" + mod + "&flatval=" + flat);
    }

    /**
     * Reloads the web page in the WebView.
     * @param view xml
     */
    public void reloadPage(View view) {
        webView.reload();
    }

    /**
     * Kills the activity to go back to map fragment.
     * @param view xml
     */
    public void goBack(View view) {
        finish();
    }
}
