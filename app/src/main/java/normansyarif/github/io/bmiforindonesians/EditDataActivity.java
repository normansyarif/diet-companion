package normansyarif.github.io.bmiforindonesians;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EditDataActivity extends AppCompatActivity {

    SharedPreferences pref;
    EditText inputDates, inputData, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        inputDates = findViewById(R.id.dates);
        inputData = findViewById(R.id.data);
        inputPassword = findViewById(R.id.password);

        parseData();
    }

    private void parseData() {
        String dates = pref.getString("dates", "[]");
        String data = pref.getString("data", "[]");
        inputDates.setText(trimBrackets(dates));
        inputData.setText(trimBrackets(data));
    }

    public void onSaveClicked(View v) {
        if(inputPassword.getText().toString().equals("Hmiku")) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("dates", "[" + inputDates.getText().toString() + "]");
            editor.putString("data", "[" + inputData.getText().toString() + "]");
            editor.apply();
            finish();
        }else{
            Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClearClicked(View v) {
        inputDates.setText("");
        inputData.setText("");
    }

    public void onShareClicked(View v) {
        String dates = "[" + inputDates.getText().toString() + "]";
        String weightData = "[" + inputData.getText().toString() + "]";

        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
            LocalDateTime now = LocalDateTime.now();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Data from Diet Companion App");
            String body = "Created at: " + dtf.format(now) + "\n\nDates:\n" + dates + "\n\nWeight data:\n" + weightData;
            intent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(intent, "Share data"));
        }catch(Exception e) {
            Toast.makeText(this, "Hmm.. That's weird.", Toast.LENGTH_SHORT).show();
        }
    }

    private String trimBrackets(String stringWithBrackets) {
        return stringWithBrackets.substring(1, stringWithBrackets.length() - 1);
    }

}
