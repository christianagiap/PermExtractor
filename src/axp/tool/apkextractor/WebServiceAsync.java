package axp.tool.apkextractor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class WebServiceAsync extends AsyncTask<String, Void, String> {

    private int serverResponseCode;
    MainActivity context;
    private ProgressDialog dialog;
    private String apk_name, apk_version, package_name, source_dir;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    final int id = 101;
    private final static String TAG = WebServiceAsync.class.getName();

    public WebServiceAsync(MainActivity mContext, String apk_name, String apk_version, String package_name, String source_dir) {
        dialog = new ProgressDialog(mContext);
        this.context = mContext;
        this.apk_name = apk_name;
        this.apk_version = apk_version;
        this.package_name = package_name;
        this.source_dir = source_dir;
    }

    @Override
    protected String doInBackground(String... params) {
        String apkName = this.apk_name;
        String apkVersion = this.apk_version;
        String packageName = this.package_name;

        StringBuilder sb = null;
        try {
            String data = URLEncoder.encode("apk_name", "UTF-8") + "=" +
                    URLEncoder.encode(apkName, "UTF-8");
            data += "&" + URLEncoder.encode("apk_version", "UTF-8") + "=" +
                    URLEncoder.encode(apkVersion, "UTF-8");
            data += "&" + URLEncoder.encode("package_name", "UTF-8") + "=" +
                    URLEncoder.encode(packageName, "UTF-8");

            URL url = new URL(Constants.URL_DB);
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

        String result = sb.toString();
        ////////////////////////////////////////////////////////////////////////////
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (result.equals("Not Found") == false) {
            Intent intent = new Intent(context, PermissionActivity.class);
            intent.putExtra("analysis", result);
            intent.putExtra("package_name", this.package_name);
            context.startActivity(intent);
        } else {
            initNotification();
            setStartedNotification();
            try {
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(this.source_dir);

                if (sourceFile.isFile()) {

                    try {
                        String upLoadServerUri = Constants.URL_UPLOAD_FILE;

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("apk", this.source_dir);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"apk\";filename=\""
                                + this.source_dir + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        if (serverResponseCode != 200) {
                            return "Not Executed";
                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } // End else block
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ///////////////////////////////////////////////////////////////////////////////////
            try {
                String data = URLEncoder.encode("apk_name", "UTF-8") + "=" +
                        URLEncoder.encode(apkName, "UTF-8");

                URL url = new URL(Constants.URL_DECOMPILE);
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
                    break;
                }

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ///////////////////////////////////////////////////////////////////////////////////

            if (sb.toString() != null) {
                try {
                    String data = URLEncoder.encode("apk_name", "UTF-8") + "=" +
                            URLEncoder.encode(apkName, "UTF-8");
                    data += "&" + URLEncoder.encode("apk_version", "UTF-8") + "=" +
                            URLEncoder.encode(apkVersion, "UTF-8");
                    data += "&" + URLEncoder.encode("package_name", "UTF-8") + "=" +
                            URLEncoder.encode(packageName, "UTF-8");

                    URL url = new URL(Constants.URL_ANALYZE);
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

                    setCompletedNotification();

                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute");
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    private void initNotification() {
        mNotifyManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("channelID", "Analysis", importance);
            channel.setDescription("channel");
            mNotifyManager.createNotificationChannel(channel);
        }
    }

    private void setStartedNotification() {
        mBuilder.setSmallIcon(R.drawable.background).setContentTitle("Analysis of " + this.apk_name)
                .setContentText("Analysis started..");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for
        // the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mBuilder.setChannelId("channelID");
        }
        mNotifyManager.notify(id, mBuilder.build());
    }

    private void setCompletedNotification() {
        mBuilder.setSmallIcon(R.drawable.background).setContentTitle("Analysis of " + this.apk_name)
                .setContentText("Analysis completed..");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        }
        mNotifyManager.notify(id, mBuilder.build());
    }


}
