package normansyarif.github.io.bmiforindonesians;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

public class EditDataActivity extends AppCompatActivity {

    SharedPreferences pref;
    EditText inputDates, inputData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        inputDates = findViewById(R.id.dates);
        inputData = findViewById(R.id.data);

        parseData();
    }

    private void parseData() {
        String dates = pref.getString("dates", "[]");
        String data = pref.getString("data", "[]");
        inputDates.setText(dates);
        inputData.setText(data);
    }

    public void onSaveClicked(View v) {
        String iDates = inputDates.getText().toString();
        String iData = inputData.getText().toString();

        if(iDates.equals("")) {
            iDates = "[]";
        }

        if(iData.equals("")) {
            iData = "[]";
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("dates", iDates);
        editor.putString("data", iData);
        editor.apply();

        Intent intent = new Intent(EditDataActivity.this, ChartActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClearClicked(View v) {
        inputDates.setText(null);
        inputData.setText(null);
    }
}
