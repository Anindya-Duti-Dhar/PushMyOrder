package duti.com.pushmyorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;

import duti.com.pushmyorder.api.RefreshToken;
import duti.com.pushmyorder.config.Constants;
import duti.com.pushmyorder.library.BaseActivity;
import duti.com.pushmyorder.library.ItemList;
import duti.com.pushmyorder.library.LinkAdapter;
import duti.com.pushmyorder.library.Repository;
import duti.com.pushmyorder.library.SweetAlert;
import duti.com.pushmyorder.model.push.Data;
import duti.com.pushmyorder.util.NotificationUtils;

public class MainActivity extends BaseActivity<Data> {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;

    LinkAdapter<Data> recordListAdapter;
    ArrayList<Data> visitListArray = new ArrayList<Data>();
    Repository<Data> repo = new Repository<Data>(this, new Data());


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.register(this, R.string.app_name);

        dt.tools.printLog("Db Browser", DebugDB.getAddressLog());

        InitRecycler();
        LoadList(false, 0, "", "");

        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String title = intent.getStringExtra("title");
                    String message = intent.getStringExtra("message");
                    dt.alert.showSuccess(title, message, dt.gStr(R.string.thanks));
                    dt.alert.setAlertListener(new SweetAlert.AlertListener() {
                        @Override
                        public void onAlertClick(boolean isCancel) {
                            LoadList(false, 0, "","");
                        }
                    });
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();

        if(dt.droidNet.hasConnection()){
            if(!dt.pref.getBoolean("FcmTokenSent")){
                dt.alert.showProgress(dt.gStr(R.string.getting_ready));
                new RefreshToken(dt).sendRegistrationToServer(displayFirebaseRegId(), new RefreshToken.onTokenSentListener() {
                    @Override
                    public void onTokenSent(boolean success, String message, String token) {
                        dt.alert.hideDialog(dt.alert.progress);
                        dt.pref.set("FcmToken", token);
                        if(success){
                            dt.pref.set("FcmTokenSent", true);
                        }
                        else {
                            dt.pref.set("FcmTokenSent", false);
                        }
                    }
                });
            }
        }

    }

    public void InitRecycler() {
        dt.ui.listView.itemList.set(R.id.mRecylerView, visitListArray,
                R.layout.adapter_push_lsit, R.id.deleteRec, LinearLayout.VERTICAL, R.drawable.ic_action_room_service);

        dt.ui.listView.itemList.setRecyclerViewItemClickListener(new ItemList.onRecyclerViewItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                dt.msg("Clicked Item");
            }
        });

        recordListAdapter = dt.ui.listView.itemList.adapter;
    }

    private void LoadList(boolean isLoadMore, long recordId, String searchKey, String searchValue) {
        Data[] visitList = (Data[]) repo.getAll(queryStatement(isLoadMore, searchKey, searchValue, recordId), Data[].class);
        if (isLoadMore) visitListArray.addAll(new ArrayList<>(Arrays.asList(visitList)));
        else {visitListArray.clear();
            visitListArray = new ArrayList<>(Arrays.asList(visitList));
            dt.ui.listView.itemList.showHideNoRecordMessage(mContext, visitListArray, R.id.mRecylerView, R.id.mNoDataMessage);}
        if (visitListArray.size() > 0) {
            recordListAdapter.setItems(visitListArray);
            recordListAdapter.notifyDataSetChanged();
        }
        isLoading = false;
    }

    @Override
    protected void onOptionsItemClick(MenuItem item) {

    }

    @Override
    protected int onOptionsMenuInflate() {
        return 0;
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private String displayFirebaseRegId() {
        String regId = dt.pref.getString("FcmToken");
        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
        return regId;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
