package duti.com.pushmyorder.activity;


import android.os.Bundle;
import android.view.MenuItem;

import com.thefinestartist.finestwebview.FinestWebView;

import duti.com.pushmyorder.R;
import duti.com.pushmyorder.library.BaseActivity;
import duti.com.pushmyorder.library.DatabaseHandler;
import duti.com.pushmyorder.library.Threading;
import duti.com.pushmyorder.model.push.Data;

public class SplashActivity extends BaseActivity<Data> {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            super.register(this, 0);
            goToNext();
        }

        // Go to Home Page
    public void goToNext() {
        new DatabaseHandler(dt.c);
        dt.threading.delay(1500, new Threading.onDelayListener() {
            @Override
            public void onResume() {
                dt.activity.call(MainActivity.class, "");
            }
        });
    }

    @Override
    protected int onOptionsMenuInflate() {
        return 0;
    }

    @Override
    protected void onOptionsItemClick(MenuItem item) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
