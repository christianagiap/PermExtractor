package axp.tool.apkextractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static axp.tool.apkextractor.PreferencesActivity.sharedPreferences;

public class PermissionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<PermItem> permList;
    private ArrayList<String> permList2;
    public static String package_name;
    public static List<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); //each item has its fixed size on recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        permList = new ArrayList<>();
        permList2 = new ArrayList<>();

        Intent intent = getIntent();
        String analysis = intent.getStringExtra("analysis");
        package_name = intent.getStringExtra("package_name");

        if (analysis.equals("NO")) {
            PermItem permItem = new PermItem("NO PERMISSIONS USED BY THIS APP", "");
            permList.add(permItem);
        } else {
            createPermissionItems(analysis);
        }

        PermListAdapter adapter = new PermListAdapter(permList, this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_rcmnd) {
            SharedPreferences sharedPreferences = PreferencesActivity.sharedPreferences;

            permissions = new ArrayList<>();

            notAllowedPermissions();

            new SimilarAppsAsync(this).execute(package_name);
        }
        return true;
    }

    public void notAllowedPermissions() {
        if (!load("calendar")) {
            permissions.add("calendar");
        }
        if (!load("contacts")) {
            permissions.add("contacts");
            permissions.add("accounts");
        }
        if (!load("camera")) {
            permissions.add("camera");
        }
        if (!load("call_log")) {
            permissions.add("call log");
        }
        if (!load("location")) {
            permissions.add("location");
        }
        if (!load("microphone")) {
            permissions.add("microphone");
            permissions.add("record audio");
        }
        if (!load("phone")) {
            permissions.add("phone");
            permissions.add("voicemail");
            permissions.add("phone calls");
        }
        if (!load("sms")) {
            permissions.add("sms");
            permissions.add("wap");
            permissions.add("mms");
        }
        if (!load("sensors")) {
            permissions.add("sensors");
        }
        if (!load("storage")) {
            permissions.add("storage");
        }
    }

    private boolean load(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void createPermissionItems(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.getJSONArray("fields");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json_data = jsonArray.getJSONObject(i);
                String group = json_data.getString("group");
                permList2.add(group);
                group = group.substring(0, 1).toUpperCase() + group.substring(1);
                String permissions = json_data.getString("permissions");
                PermItem permItem = new PermItem(group, permissions);
                permList.add(permItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
