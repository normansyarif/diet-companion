package normansyarif.github.io.bmiforindonesians;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText inputHeight, inputWeight;
    TextView textCaption1, textCaption2, textCaption3, textProgressLabelLeft, textProgressLabelRight;
    ProgressBar progressBar;
    LinearLayout linearProgressWrapper;
    RelativeLayout relativeLaunchHealth, relativeWatch;
    SharedPreferences pref;

    float currentWeight;

    // These are the top threshold values for each category.
    // The bottom threshold value of one category equals to the top threshold value of the category
    // before it.
    // For example, the bottom threshold value of pre obese equals the top threshold value of overweight.
    float tUnderweight, tIdeal, tOverweight, tPreObese;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);
        textCaption1 = findViewById(R.id.textCaption1);
        textCaption2 = findViewById(R.id.textCaption2);
        textCaption3 = findViewById(R.id.textCaption3);
        textProgressLabelLeft = findViewById(R.id.textProgressLabelLeft);
        textProgressLabelRight = findViewById(R.id.textProgressLabelRight);
        progressBar = findViewById(R.id.progressBar);
        linearProgressWrapper = findViewById(R.id.linearProgressWrapper);
        relativeLaunchHealth = findViewById(R.id.relativeLaunchHealth);
        relativeWatch = findViewById(R.id.relativeWatch);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String height = pref.getString("height", null);
        String weight = pref.getString("weight", null);

        if(height != null) {
            inputHeight.setText(height);
        }

        if(weight != null) {
            inputWeight.setText(weight);
        }

        if(!inputHeight.getText().toString().equals("") && !inputWeight.getText().toString().equals("")) {
            calculate();
        }

        relativeLaunchHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHealth();
            }
        });

        relativeWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watch();
            }
        });

    }

    private void calculate() {
        float bmiUnderweight = 18.5f;
        float bmiIdeal = 22.9f;
        float bmiOverweight = 24.9f;
        float bmiPreObese = 29.9f;

        float height = Float.parseFloat(inputHeight.getText().toString()) / 100;
        float weight = Float.parseFloat(inputWeight.getText().toString());
        float bmi = weight / (height * height);

        // Set global variables
        this.currentWeight = weight;
        this.tUnderweight = bmiUnderweight * (height * height);
        this.tIdeal = bmiIdeal * (height * height);
        this.tOverweight = bmiOverweight * (height * height);
        this.tPreObese = bmiPreObese * (height * height);

        if(bmi < bmiUnderweight) {
            setUnderweight();
        }else if(bmi >= bmiUnderweight && bmi <= bmiIdeal) {
            setIdeal();
        }else if(bmi > bmiIdeal && bmi <= bmiOverweight) {
            setOverweight();
        }else if(bmi > bmiOverweight && bmi <= bmiPreObese) {
            setPreObese();
        }else{
            setObese();
        }

        textCaption3.setText("Your BMI score is " + format(bmi) + ".");
    }

    private void setUnderweight() {
        linearProgressWrapper.setVisibility(View.GONE);
        textCaption1.setTextColor(Color.parseColor("#e52727"));
        textCaption1.setText("You are underweight!");
        textCaption2.setText("You need " + format(tUnderweight - currentWeight) + " kg more to be classified as \"Ideal\".");
    }

    private void setIdeal() {
        linearProgressWrapper.setVisibility(View.VISIBLE);
        progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#00a908"), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setMin(Math.round(tUnderweight));
        progressBar.setMax(Math.round(tIdeal));
        progressBar.setProgress(Math.round(currentWeight));
        textProgressLabelLeft.setText("Underweight (" + format(tUnderweight) + " kg)");
        textProgressLabelRight.setText("Overweight (" + format(tIdeal) + " kg)");

        textCaption1.setTextColor(Color.parseColor("#00a908"));
        textCaption1.setText("You are perfect!");
        textCaption2.setText("You'll become \"Underweight\" if you lose more than " + format(currentWeight - tUnderweight)
                + " kg, and become \"Overweight\" if you gain more than " + format(tIdeal - currentWeight) + " kg.");
    }

    private void setOverweight() {
        linearProgressWrapper.setVisibility(View.VISIBLE);
        progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#e52727"), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setMin(Math.round(tIdeal));
        progressBar.setMax(Math.round(tOverweight));
        progressBar.setProgress(Math.round(currentWeight));
        textProgressLabelLeft.setText("Ideal (" + format(tIdeal) + " kg)");
        textProgressLabelRight.setText("Pre-Obese (" + format(tOverweight) + " kg)");

        textCaption1.setTextColor(Color.parseColor("#e52727"));
        textCaption1.setText("You are overweight!");
        textCaption2.setText("You need " + format(currentWeight - tIdeal) + " kg more to be classified as \"Ideal\". " +
                "You, however, will be classified as \"Pre-Obese\" if you gain more than " + format(tOverweight - currentWeight) + " kg.");
    }

    private void setPreObese() {
        linearProgressWrapper.setVisibility(View.VISIBLE);
        progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#e52727"), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setMin(Math.round(tOverweight));
        progressBar.setMax(Math.round(tPreObese));
        progressBar.setProgress(Math.round(currentWeight));
        textProgressLabelLeft.setText("Overweight (" + format(tOverweight) + " kg)");
        textProgressLabelRight.setText("Obese (" + format(tPreObese) + " kg)");

        textCaption1.setTextColor(Color.parseColor("#e52727"));
        textCaption1.setText("You are pre-obese!");
        textCaption2.setText("You need to lose " + format(currentWeight - tOverweight) + " kg to be classified as \"Overweight\", " +
                "and " + format(currentWeight - tIdeal) + " kg to be classified as \"Ideal\". However, you'll be classified as \"Obese\"" +
                " if you gain more than " + format(tPreObese - currentWeight) + " kg.");
    }

    private void setObese() {
        linearProgressWrapper.setVisibility(View.GONE);
        textCaption1.setTextColor(Color.parseColor("#e52727"));
        textCaption1.setText("You are obese!");
        textCaption2.setText("You need to lose " + format(currentWeight - tPreObese) + " kg to be classified as \"Pre-Obese\", " +
                "and " + format(currentWeight - tIdeal) + " kg to be classified as \"Ideal\".");
    }

    private String format(float number) {
        DecimalFormat df = new DecimalFormat("0.#");
        return df.format(number);
    }

    private void saveHeightAndWeight(String height, String weight) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("height", height);
        editor.putString("weight", weight);
        editor.apply();
    }

    public void onCalculateClicked(View v) {
        String height = inputHeight.getText().toString();
        String weight = inputWeight.getText().toString();

        if(!height.equals("") && !weight.equals("")) {
            calculate();

            if(v.getId() == R.id.save) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");
                LocalDateTime now = LocalDateTime.now();
                push( dtf.format(now), Float.parseFloat(weight));
                saveHeightAndWeight(height, weight);

                String goal = pref.getString("goal", "0");
                if(!goal.equals("0") && Float.parseFloat(weight) <= Float.parseFloat(goal)) {
                    showNotification(goal);
                }
            }

        }else{
            Toast.makeText(this, "Height and weight cannot be empty", Toast.LENGTH_SHORT).show();
        }

    }

    private void launchHealth() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sec.android.app.shealth");
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(MainActivity.this, "Samsung Health is not installed on your phone", Toast.LENGTH_LONG).show();
        }
    }

    private void watch() {
        File file = new File(getExternalFilesDir(null), "workout.mp4");
        if(file.exists()) {
            Uri path = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setDataAndType(path, "video/*");
            try {
                startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(this, e + "", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "File not found. Please copy your workout video to /Android/data/normansyarif.github.io.bmiforindonesians/files/workout.mp4.", Toast.LENGTH_LONG).show();;
        }
    }


    private void push(String newDate, float newData) {
        String dates = pref.getString("dates", "[]");
        String data = pref.getString("data", "[]");

        if(!dates.equals("[]") && !data.equals("[]")) {
            dates = dates.substring(0, dates.length() - 1) + ", \"" + newDate + "\"]";
            data = data.substring(0, data.length() - 1) + ", " + newData + "]";
        }else{
            dates = "[\"" + newDate + "\"]";
            data = "[" + newData + "]";
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("dates", dates);
        editor.putString("data", data);
        editor.apply();
    }

    private void showNotification(String goal) {
        String NOTIFICATION_CHANNEL_ID = "channel_androidnotif";
        Context context = this.getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Android Notif Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent mIntent = new Intent(this, ChartActivity.class);
        Bundle bundle = new Bundle();
        mIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("Congratulations!")
                .setContentText("You've reached " + goal + " kg. Keep up the good work!");

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(115, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.go_to_chart) {
            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
