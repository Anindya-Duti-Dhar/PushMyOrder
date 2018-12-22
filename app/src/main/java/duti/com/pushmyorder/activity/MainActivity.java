package duti.com.pushmyorder.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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

//import com.amitshekhar.DebugDB;
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
import duti.com.pushmyorder.model.push.PushList;
import duti.com.pushmyorder.util.NotificationUtils;

public class MainActivity extends BaseActivity<PushList> {

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    LinkAdapter<PushList> recordListAdapter;
    ArrayList<PushList> visitListArray = new ArrayList<PushList>();
    Repository<PushList> repoPush = new Repository<PushList>(this, new PushList());


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        super.register(this, R.string.app_name);

        //dt.tools.printLog("Db Browser", DebugDB.getAddressLog());

        if (TextUtils.isEmpty(dt.pref.getString("ServerURL"))) {
            setupServer(new onServerSetup() {
                @Override
                public void onSetup(boolean setup) {
                    if (setup) sendTokenToServer();
                }
            });
        }

        InitRecycler();
        loadList();

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
                    final String webLink = intent.getStringExtra("webLink");
                    String title = intent.getStringExtra("title");
                    String message = intent.getStringExtra("message");
                    dt.alert.showGeneral(title, message + "\n\n" + "Want to Confirm Order?", dt.gStr(R.string.ok), dt.gStr(R.string.cancel));
                    dt.alert.setAlertListener(new SweetAlert.AlertListener() {
                        @Override
                        public void onAlertClick(boolean isCancel) {
                            loadList();
                            if (!isCancel) {
                                new FinestWebView.Builder(dt.c).titleDefault("Order And Eat")
                                        .show(webLink);
                            }
                        }
                    });
                }
            }
        };

    }

    private void sendTokenToServer() {
        if (dt.droidNet.hasConnection()) {
            if (!dt.pref.getBoolean("FcmTokenSent")) {
                dt.alert.showProgress(dt.gStr(R.string.getting_ready));
                new RefreshToken(dt).sendRegistrationToServer(dt.pref.getString("FcmToken"), new RefreshToken.onTokenSentListener() {
                    @Override
                    public void onTokenSent(boolean success, String message, String token) {
                        dt.alert.hideDialog(dt.alert.progress);
                        dt.pref.set("FcmToken", token);
                        if (success) {
                            dt.pref.set("FcmTokenSent", true);
                        } else {
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

    public void setupServer(onServerSetup listener) {
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
                if (TextUtils.isEmpty(dt.pref.getString("ServerURL"))) {
                    dt.msg("Server Setup Failure");
                    dt.alert.showWarningWithOneButton("Server Setup Failure");
                    serverSetup.onSetup(false);
                } else {
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
                    String completeUrl = "http://" + serverUrl + "/pushmyorder/";
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
                final PushList push = (PushList) o;
                dt.alert.showWarning("Order Confirmation", "Want to Confirm Order?");
                dt.alert.setAlertListener(new SweetAlert.AlertListener() {
                    @Override
                    public void onAlertClick(boolean isCancel) {
                        if (!isCancel) {
                            new FinestWebView.Builder(dt.c).titleDefault("Order And Eat")
                                    .show(push.getWebLink());
                        }
                    }
                });
            }
        });

        recordListAdapter = dt.ui.listView.itemList.adapter;
    }

    private void loadList(){
        new GetRepo().execute();
    }

    @Override
    protected void onOptionsItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                exit();
                break;
            default:
                break;
        }
    }

    @Override
    protected int onOptionsMenuInflate() {
        return R.menu.main_page_menu;
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
        exit();
    }

    private void exit(){
        dt.alert.showWarning("Want to Exit?");
        dt.alert.setAlertListener(new SweetAlert.AlertListener() {
            @Override
            public void onAlertClick(boolean isCancel) {
                if(!isCancel)finish();
            }
        });
    }

    public class GetRepo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            dt.alert.showProgress(dt.gStr(R.string.getting_ready));
        }

        @Override
        protected String doInBackground(String... strings) {
            repoPush.addInCustomTableMap("PushList", "Data");
            PushList[] visitList = (PushList[]) repoPush.getWithTableName("inner join Payload on Data.RecordId = Payload.RecordId and Data.RecordId > 0 order by Data.RecordId desc", "", PushList[].class);
            visitListArray.clear();
            visitListArray = new ArrayList<>(Arrays.asList(visitList));
            return "done";
        }

        @Override
        protected void onPostExecute(String result) {
            dt.alert.hideDialog(dt.alert.progress);
            dt.ui.listView.itemList.showHideNoRecordMessage(mContext, visitListArray, R.id.mRecylerView, R.id.mNoDataMessage);
            if (visitListArray.size() > 0) {
                recordListAdapter.setItems(visitListArray);
                recordListAdapter.notifyDataSetChanged();
            }
            isLoading = false;
        }

    }

}
