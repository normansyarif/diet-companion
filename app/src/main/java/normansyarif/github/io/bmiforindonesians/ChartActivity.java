package normansyarif.github.io.bmiforindonesians;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class ChartActivity extends AppCompatActivity {
    WebView myBrowser;
    RelativeLayout relativeEdit;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        relativeEdit = findViewById(R.id.relativeEdit);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        initWebview();

        relativeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChartActivity.this, EditDataActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initWebview() {
        String dates = pref.getString("dates", "[]");
        String data = pref.getString("data", "[]");

        myBrowser = findViewById(R.id.webview);
        myBrowser.setWebChromeClient(new WebChromeClient() {});
        WebSettings settings = myBrowser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        myBrowser.addJavascriptInterface(new JavaScriptInterface(this, dates, data), "Android");
        myBrowser.loadUrl("file:///android_asset/chart.html");
    }
}