package axp.tool.apkextractor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * This is the starting activity of the app.
 * It's used to launch the logo of the company.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent mInHome = new Intent(WelcomeActivity.this, PreferencesActivity.class);
                WelcomeActivity.this.startActivity(mInHome);
                WelcomeActivity.this.finish();
            }
        }, 3000);
    }
}
