package axp.tool.apkextractor;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ApkListAdapter apkListAdapter;
    private CheckBox skip;
    public static String PREFS_NAME = "MyPrefs_Skip2";

    private ProgressBar progressBar;
    private PermissionResolver permissionResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        RecyclerView listView = (RecyclerView) findViewById(android.R.id.list);

        apkListAdapter = new ApkListAdapter(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(apkListAdapter);

        progressBar = (ProgressBar) findViewById(android.R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        new Loader(this).execute();

        permissionResolver = new PermissionResolver(this);

        alertBoxFunction();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        RecyclerView listView = (RecyclerView) findViewById(android.R.id.list);

        apkListAdapter = new ApkListAdapter(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(apkListAdapter);

        progressBar = (ProgressBar) findViewById(android.R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        new Loader(this).execute();

        permissionResolver = new PermissionResolver(this);
    }

    public void alertBoxFunction() {
        android.support.v7.app.AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean skipMessage = settings.getBoolean("skipMessage", false);

        skip = (CheckBox) eulaLayout.findViewById(R.id.skip);
        alertBox.setView(eulaLayout);
        alertBox.setTitle("Instructions about items");
        alertBox.setMessage("WHITE:  App hasn't been analyzed yet.\n\nGREEN: Permissions of app are compatible with user's preferences. \n\nBLUE:    Permissions of app aren't compatible with user's preferences.");

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!permissionResolver.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void addItem(PackageInfo item) {
        apkListAdapter.addItem(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused && searchView.getQuery().length() < 1) {
                    getSupportActionBar().collapseActionView();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                apkListAdapter.setSearchPattern(s);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if(i == R.id.action_preferences){
            startActivity(new Intent(this, PreferencesActivity.class));
        }
        return true;
    }

    public void doExtract(final PackageInfo info) {
        if (!permissionResolver.resolve()) return;

        try {
            String sourceDir = info.applicationInfo.sourceDir;
            String apk_name = sourceDir.substring(sourceDir.lastIndexOf('/') + 1).trim();
            String apk_version = Integer.toString(info.versionCode);
            String package_name = info.packageName;

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                new WebServiceAsync(MainActivity.this, apk_name, apk_version, package_name, sourceDir).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else {
                new WebServiceAsync(MainActivity.this, apk_name, apk_version, package_name, sourceDir).execute();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class Loader extends AsyncTask<Void, PackageInfo, Void> {
        ProgressDialog dialog;
        MainActivity mainActivity;

        public Loader(MainActivity a) {
            dialog = ProgressDialog.show(a, getString(R.string.dlg_loading_title), getString(R.string.dlg_loading_body));
            mainActivity = a;
        }

        @Override
        protected Void doInBackground(Void... params) {
//            PackageManager pm = getPackageManager();
//            int flags = pm.GET_META_DATA;
//            List<PackageInfo> packages = new ArrayList<>();
//            List<ApplicationInfo> apps = pm.getInstalledApplications(flags);
//
//            for (ApplicationInfo applicationInfo : apps) {
//                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
//                    // System application
//                } else {
//                    try {
//                        PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, pm.GET_META_DATA);
//                        packages.add(packageInfo);
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
            for (PackageInfo packageInfo : packages) {
                    publishProgress(packageInfo);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(PackageInfo... values) {
            super.onProgressUpdate(values);
            mainActivity.addItem(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }
}
