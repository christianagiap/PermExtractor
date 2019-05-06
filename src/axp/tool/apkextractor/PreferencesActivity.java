package axp.tool.apkextractor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;

public class PreferencesActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox cb_calendar, cb_call_log, cb_camera, cb_contacts, cb_location, cb_microphone, cb_phone, cb_sensors, cb_sms, cb_storage, skip;
    private Button btnSubmit;
    private ArrayList<Integer> prefList;
    public static SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "MyPrefs_Skip";
    public static final String PREFS_NAME2 = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        prefList = new ArrayList<>();

        cb_calendar = (CheckBox) findViewById(R.id.cb_calendar);
        cb_call_log = (CheckBox) findViewById(R.id.cb_call_log);
        cb_camera = (CheckBox) findViewById(R.id.cb_camera);
        cb_contacts = (CheckBox) findViewById(R.id.cb_contacts);
        cb_location = (CheckBox) findViewById(R.id.cb_location);
        cb_microphone = (CheckBox) findViewById(R.id.cb_microphone);
        cb_phone = (CheckBox) findViewById(R.id.cb_phone);
        cb_sensors = (CheckBox) findViewById(R.id.cb_sensors);
        cb_sms = (CheckBox) findViewById(R.id.cb_sms);
        cb_storage = (CheckBox) findViewById(R.id.cb_storage);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        loadValues();

        btnSubmit.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean skipMessage = settings.getBoolean("skipMessage", false);

        skip = (CheckBox) eulaLayout.findViewById(R.id.skip);
        alertBox.setView(eulaLayout);
        alertBox.setTitle("Instructions");
        alertBox.setMessage("Select the permission groups that are allowed to be granted by your apps.");

        alertBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Boolean checkBoxResult = false;
                if (skip.isChecked()) {
                    checkBoxResult = true;
                }

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putBoolean("skipMessage", checkBoxResult);
                editor.commit();
                return;
            }
        });

        if (skipMessage == false) {
            alertBox.show();
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        save(cb_calendar.isChecked(),"calendar");
        save(cb_call_log.isChecked(),"call_log");
        save(cb_camera.isChecked(),"camera");
        save(cb_contacts.isChecked(),"contacts");
        save(cb_location.isChecked(),"location");
        save(cb_microphone.isChecked(),"microphone");
        save(cb_phone.isChecked(),"phone");
        save(cb_sensors.isChecked(),"sensors");
        save(cb_sms.isChecked(),"sms");
        save(cb_storage.isChecked(),"storage");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    private void loadValues(){
        cb_calendar.setChecked(load("calendar"));
        cb_call_log.setChecked(load("call_log"));
        cb_camera.setChecked(load("camera"));
        cb_contacts.setChecked(load("contacts"));
        cb_location.setChecked(load("location"));
        cb_microphone.setChecked(load("microphone"));
        cb_phone.setChecked(load("phone"));
        cb_sensors.setChecked(load("sensors"));
        cb_sms.setChecked(load("sms"));
        cb_storage.setChecked(load("storage"));
    }

    private void save(final boolean isChecked, String key) {
        sharedPreferences = getSharedPreferences(PREFS_NAME2, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    private boolean load(String key) {
        sharedPreferences = getSharedPreferences(PREFS_NAME2, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }
}
