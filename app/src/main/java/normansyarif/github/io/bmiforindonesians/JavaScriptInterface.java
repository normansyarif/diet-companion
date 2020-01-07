package normansyarif.github.io.bmiforindonesians;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
    Context mContext;
    String dates, data, goal;

    JavaScriptInterface(Context c, String dates, String data, String goal) {
        this.mContext = c;
        this.dates = dates;
        this.data = data;
        this.goal = goal;
    }
    @JavascriptInterface
    public String getDates() {
        return dates;
    }

    @JavascriptInterface
    public String getData() {
        return data;
    }

    @JavascriptInterface
    public String getGoal() {
        return goal;
    }
}
