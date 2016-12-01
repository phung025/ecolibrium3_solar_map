package umd.solarmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Activity responsible for displaying the web page containing the calculator.
 * Created by John on 12/1/2016.
 */

public class WebViewActivity extends Activity {

    WebView webView;
    String opt, mod, flat;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webview);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        opt = b.get("optimalRating").toString();
        mod = b.get("moderateRating").toString();
        flat = b.get("flat_pct").toString();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

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
