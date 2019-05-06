package axp.tool.apkextractor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


import static axp.tool.apkextractor.PermissionActivity.permissions;

public class SimilarAppsAsync extends AsyncTask<String, Void, ArrayList<AppItem>> {

    PermissionActivity rActivity;
    private List<AppItem> packages;
    private List<String> package_names;
    private ArrayList<AppItem> final_packages;
    ProgressDialog progressDialog;


    public SimilarAppsAsync(PermissionActivity permissionActivity) {
        this.rActivity = permissionActivity;
        packages = new ArrayList<>();
        package_names = new ArrayList<>();
        final_packages = new ArrayList<>();
        progressDialog = new ProgressDialog(rActivity);
    }

    @Override
    protected ArrayList<AppItem> doInBackground(String... params) {
        String packageName = params[0];

        StringBuilder sb = null;
        try {
            String data = URLEncoder.encode("package_name", "UTF-8") + "=" +
                    URLEncoder.encode(packageName, "UTF-8");

            URL url = new URL(Constants.URL_SCRAPER);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result1 = sb.toString();

        getPackageNames(result1);

        String[] stringArray = package_names.toArray(new String[0]);
        String[] permissions2 = permissions.toArray(new String[0]);

        sb = null;
        try {
            String data = URLEncoder.encode("apps", "UTF-8") + "=" +
                    URLEncoder.encode(java.util.Arrays.toString(stringArray), "UTF-8");
            data += "&" + URLEncoder.encode("permissions", "UTF-8") + "=" +
                    URLEncoder.encode(java.util.Arrays.toString(permissions2), "UTF-8");

            URL url = new URL(Constants.URL_SCRAPER2);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result2 = sb.toString();

        result2 = result2.substring(1, result2.length() - 1);
        result2 = result2.replace("\"", "");
        String[] res = result2.split(",");

        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < packages.size(); j++) {
                if (packages.get(j).package_name.equals(res[i])) {
                    final_packages.add(packages.get(j));
                    break;
                }
            }
        }

        return final_packages;

    }

    public void getPackageNames(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < (jsonArray.length()/2); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                String pname = o.getString("appId");
                String icon = o.getString("icon");
                String url = o.getString("url");
                AppItem appItem = new AppItem(pname, icon, url);
                packages.add(appItem);
                package_names.add(pname);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Loading recommended applications.\nThis might take a while...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<AppItem> final_packages) {
        super.onPostExecute(final_packages);
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> icon = new ArrayList<>();
        ArrayList<String> url = new ArrayList<>();
        for(int i=0;i<final_packages.size();i++){
            name.add(final_packages.get(i).package_name);
            icon.add(final_packages.get(i).icon);
            url.add(final_packages.get(i).url);
        }
        Intent intent = new Intent(rActivity, RecommendActivity.class);
        intent.putStringArrayListExtra("names", name);
        intent.putStringArrayListExtra("icons", icon);
        intent.putStringArrayListExtra("urls", url);
        rActivity.startActivity(intent);
        progressDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

}