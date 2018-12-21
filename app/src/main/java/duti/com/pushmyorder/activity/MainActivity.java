package duti.com.pushmyorder.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.Arrays;

import duti.com.pushmyorder.R;
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

    LinkAdapter<Data> recordListAdapter;
    ArrayList<Data> visitListArray = new ArrayList<Data>();
    Repository<Data> repo = new Repository<Data>(this, new Data());


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.register(this, R.string.app_name);

        dt.tools.printLog("Db Browser", DebugDB.getAddressLog());

        if(TextUtils.isEmpty(dt.pref.getString("ServerURL"))){
            setupServer(new onServerSetup() {
                @Override
                public void onSetup(boolean setup) {
                    if(setup)sendTokenToServer();
                }
            });
        }

        InitRecycler();
        LoadList(false, 0, "", "");

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);

                    sendTokenToServer();

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
                    Toast.makeText(getApplicationContext(), "Push notification: " + title, Toast.LENGTH_LONG).show();
                }
            }
        };

    }

    private void sendTokenToServer() {
        if(dt.droidNet.hasConnection()){
            if(!dt.pref.getBoolean("FcmTokenSent")){
                dt.alert.showProgress(dt.gStr(R.string.getting_ready));
                new RefreshToken(dt).sendRegistrationToServer(dt.pref.getString("FcmToken"), new RefreshToken.onTokenSentListener() {
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

    public interface onServerSetup {
        void onSetup(boolean setup);
    }

    Dialog mDialog;

    public void setupServer(onServerSetup listener){
        final onServerSetup serverSetup = listener;
        View dialogView = View.inflate(dt.c, R.layout.dialog_popup_server_url, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(dt.c);
        builder.setTitle("")
                .setView(dialogView)
                .setCancelable(true);

        mDialog = builder.create();
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

        final EditText serverUrlEt = (EditText) dialogView.findViewById(R.id.ServerUrl);

        Button cancelButton = (Button) dialogView.findViewById(R.id.mCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if(TextUtils.isEmpty(dt.pref.getString("ServerURL"))) {
                    dt.msg("Server Setup Failure");
                    dt.alert.showWarningWithOneButton("Server Setup Failure");
                    serverSetup.onSetup(false);
                }
                else {
                    serverSetup.onSetup(true);
                }
            }
        });

        Button saveButton = (Button) dialogView.findViewById(R.id.mSaveServer);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverUrl = serverUrlEt.getText().toString().trim();
                if ((!TextUtils.isEmpty(serverUrl))) {
                    mDialog.dismiss();
                    String completeUrl = "http://"+serverUrl+"/pushmyorder/";
                    dt.pref.set("ServerURL", completeUrl);
                    serverSetup.onSetup(true);
                } else dt.msg("Enter Server URL !! ");
            }
        });
    }

    public void InitRecycler() {
        dt.ui.listView.itemList.set(R.id.mRecylerView, visitListArray,
                R.layout.adapter_push_lsit, R.id.deleteRec, LinearLayout.VERTICAL, R.drawable.ic_action_room_service);

        dt.ui.listView.itemList.setRecyclerViewItemClickListener(new ItemList.onRecyclerViewItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                dt.msg("Clicked Item");
                new FinestWebView.Builder(dt.c).titleDefault("The Finest Artist")
                        .show("http://thefinestartist.com");
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
