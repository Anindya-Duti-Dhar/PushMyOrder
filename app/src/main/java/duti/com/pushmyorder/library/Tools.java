package duti.com.pushmyorder.library;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Locale;

import static duti.com.pushmyorder.config.Constants.LOG;

/**
 * Created by Administrator on 6/7/2018.
 */

public class Tools {

    DroidTool dt;

    public Tools(DroidTool dt) {
        this.dt = dt;
        //gps = new LocationTracker(dt.c);
    }

    public int repoNotSyncCount(final Class[] dotClass) {
        int totalCount = 0;
        for (int i = 0; i <dotClass.length ; i++) {
            Object obj = null;
            try {
                Class<?> cls = dotClass[i].forName(dotClass[i].getCanonicalName());
                obj = cls.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Repository<Object> repo = new Repository<Object>(dt.c, obj);
            int count = repo.getNotSyncDataCount();
            totalCount += count;
        }
        return totalCount;
    }


    public int repoCount(final Class[] dotClass) {
        int totalCount = 0;
        for (int i = 0; i <dotClass.length ; i++) {
            Object obj = null;
            try {
                Class<?> cls = dotClass[i].forName(dotClass[i].getCanonicalName());
                obj = cls.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Repository<Object> repo = new Repository<Object>(dt.c, obj);
            int count = repo.getAllDataCount();
            totalCount += count;
        }
        return totalCount;
    }

    public void removeMultiRepo(final Class[] dotClass){
        for (int i = 0; i <dotClass.length ; i++) {
            Object obj = null;
            try {
                Class<?> cls = dotClass[i].forName(dotClass[i].getCanonicalName());
                obj = cls.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Repository<Object> repo = new Repository<Object>(dt.c, obj);
            repo.removeAll();
        }
    }

    public void printJson(String tableName, Object object) {
        Log.w(LOG, tableName + " JSON: " + new Gson().toJson(object));
    }

    public void printLog(String tag, String message) {
        Log.i(LOG, tag + ": " + message);
    }

    public void printErrorLog(String tag, String message) {
        Log.e(LOG, tag + ": " + message);
    }


    public boolean isServiceRunning(Class<?>[] serviceClass) {
        boolean result = false;
        ActivityManager manager = (ActivityManager) dt.c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            for(int i = 0; i<serviceClass.length; i++){
                if (serviceClass[i].getName().equals(service.service.getClassName())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    // get app version code
    public String getAppVersionCode(){
        PackageManager manager = dt.c.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(dt.c.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(info.versionCode);
    }

    // get app version name
    public String getAppVersionName(){
        PackageManager manager = dt.c.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(dt.c.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(info.versionName);
    }

    public boolean IsNumber(String value) {
        if (value.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
            return true;
        } else {
            return false;
        }
    }

    public Drawable getImage(int resId){
        return dt.c.getResources().getDrawable(resId);
    }


    public String getAppDataUsages() {
        int uid = android.os.Process.myUid();
        long mobleTraffic = 0L;
        if (uid == -1) {
            return "0.00";
        } else {
            mobleTraffic = (TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED) ? 0
                    : TrafficStats.getUidRxBytes(uid);
            mobleTraffic += (TrafficStats.getUidTxBytes(uid) == TrafficStats.UNSUPPORTED) ? 0
                    : TrafficStats.getUidTxBytes(uid);
            //String traffic = String.valueOf(mobleTraffic / (float) (1024 * 1024));
            String traffic = String.format(Locale.ENGLISH, "%.3f", (mobleTraffic / (float) (1024 * 1024)));
            return traffic.substring(0, traffic.indexOf(".") + 3);
        }

    }

    public String getMobileDataUsages(){
        long mobileDataUsage = 0L;
        //mobileDataUsage = TrafficStats.getMobileRxBytes() +TrafficStats.getMobileTxBytes();
        mobileDataUsage = (TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED) ? 0
                : TrafficStats.getMobileRxBytes();
        mobileDataUsage += (TrafficStats.getMobileTxBytes() == TrafficStats.UNSUPPORTED) ? 0
                : TrafficStats.getMobileTxBytes();
        //String dataSize = mobileDataUsage / (float) (1024 * 1024) + "M";
        String dataSize = String.format(Locale.ENGLISH, "%.3f",(mobileDataUsage / (float) (1024 * 1024)));
        return dataSize.substring(0, dataSize.indexOf(".") + 3);
    }

    public String getTotalDataUsages(){
        long totalDataUsage = 0L;
        //totalUsage = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
        totalDataUsage = (TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED) ? 0
                : TrafficStats.getTotalRxBytes();
        totalDataUsage += (TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED) ? 0
                : TrafficStats.getTotalTxBytes();
        String dataSize = String.format(Locale.ENGLISH, "%.3f",(totalDataUsage / (float) (1024 * 1024)));
        return dataSize.substring(0, dataSize.indexOf(".") + 3);
    }

    public void detectTouchOnUi(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                detectTouchOnUi(innerView);
            }
        }
    }

    public void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) ((Activity) dt.c).getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) ((Activity) dt.c).getSystemService(
                        ((Activity) dt.c).INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                ((Activity) dt.c).getCurrentFocus().getWindowToken(), 0);
    }

    public String getValue(View v) {
        String value = "";
        if (v instanceof TextView) {
            TextView et = (TextView) v;
            value = et.getText().toString().trim();
        }
        if (v instanceof EditText) {
            EditText et = (EditText) v;
            value = et.getText().toString().trim();
        }
        return value;
    }

}
