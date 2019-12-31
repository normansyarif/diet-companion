package normansyarif.github.io.bmiforindonesians;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
    Context mContext;
    String dates, data;

    JavaScriptInterface(Context c, String dates, String data) {
        this.mContext = c;
        this.dates = dates;
        this.data = data;
    }
    @JavascriptInterface
    public String getDates() {
        return dates;
    }

    @JavascriptInterface
    public String getData() {
        return data;
    }
}
