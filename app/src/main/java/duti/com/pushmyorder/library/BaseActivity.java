package duti.com.pushmyorder.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.evernote.android.job.JobRequest;

import duti.com.pushmyorder.R;

import static duti.com.pushmyorder.config.Constants.mFirstTimeInstallation;
import static duti.com.pushmyorder.config.Constants.mLocalPageSize;
import static duti.com.pushmyorder.config.Constants.mRecordId;
import static duti.com.pushmyorder.config.Constants.mUserId;


public abstract class BaseActivity<T> extends AppCompatActivity {

    public interface RecordOperation {
        void AddRecord();

        void UpdateRecord(long recordId);
    }

    public interface detailsOperation {
        void addDetails();
        void updateDetails();
    }

    public Context mContext;
    public DroidTool dt;
    protected FloatingSearchView mSearchView;
    public boolean isLoading = false;
    protected String mUUID = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        dt = new DroidTool(mContext);

        if(!dt.pref.getBoolean(mFirstTimeInstallation)){

        }
    }

    public void register(Context context, int activityTitle) {
        mContext = context;
        dt = new DroidTool(mContext);
        if (activityTitle > 0) {
            setupToolbar(activityTitle);
        }
    }


    public ListManager ListManager(LinkAdapter<T> recordListAdapter, RecyclerView rv) {
        return new ListManager(recordListAdapter, rv);
    }

    public interface onListManagerCalled {
        void onTextChanged(final String newQuery);

        void onScroll();

        void onSearchMenuClicked(MenuItem item);
    }

    public interface onCustomListManagerCalled {
        void onSearchClick(String query);

        void onScroll();

        void onSearchMenuClicked(MenuItem item);
    }

    public class ListManager {
        LinkAdapter<T> recordListAdapter;
        RecyclerView rv;
        public int pastVisibleItems, visibleItemCount, totalItemCount;
        public onListManagerCalled listManagerListener = null;
        public onCustomListManagerCalled customListManagerListener = null;

        public ListManager(final LinkAdapter<T> recordListAdapter, RecyclerView rv) {
            this.recordListAdapter = recordListAdapter;
            this.rv = rv;
        }

        public void setListManagerListener(int resId, final int searchMenuRes, onListManagerCalled ListManagerListener) {

            this.listManagerListener = ListManagerListener;

            // recycler view scroll listener
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) //check for scroll down
                    {
                        LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            //Do pagination.. i.e. fetch new data
                            if (!isLoading) {
                                listManagerListener.onScroll();
                            }
                        }
                    }
                }
            });

            // init search listener
            mSearchView = (FloatingSearchView) findViewById(R.id.mSearchView);
            mSearchView.setSearchHint(getString(resId));
            if (searchMenuRes != 0) {
                mSearchView.inflateOverflowMenu(searchMenuRes);
            }

            mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
                @Override
                public void onSearchTextChanged(String oldQuery, final String newQuery) {
                    listManagerListener.onTextChanged(newQuery);
                }
            });
            mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
                @Override
                public void onFocus() {

                }

                @Override
                public void onFocusCleared() {
                    mSearchView.clearQuery();
                    listManagerListener.onTextChanged("");
                }
            });

            mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
                @Override
                public void onActionMenuItemSelected(MenuItem item) {
                    if (searchMenuRes != 0) {
                        listManagerListener.onSearchMenuClicked(item);
                    }
                }
            });
        }
    }

    public String queryStatement(boolean loadMore, String searchKey, String searchValue, long recordId) {
        isLoading = true;
        String queryStatement;
        if (!TextUtils.isEmpty(searchKey)) {
            queryStatement = "where " + searchKey + " = '" + searchValue + "'";
        } else {
            if (loadMore) {
                queryStatement = "where " + mRecordId + " < " + recordId + " order by " + mRecordId + " desc limit " + mLocalPageSize;
            } else {
                queryStatement = "where " + mRecordId + " > " + recordId + " order by " + mRecordId + " desc limit " + mLocalPageSize;
            }
        }
        return queryStatement;
    }

    public String queryStatemnt(boolean loadMore, String Sql, long recordId) {
        isLoading = true;
        String queryStatement = "";
        if (!TextUtils.isEmpty(Sql)) {
            if (loadMore) {
                queryStatement = "where " + Sql + " and " + mRecordId + " < " + recordId + " order by " + mRecordId + " desc limit " + mLocalPageSize;
            } else {
                queryStatement = "where " + Sql + " and " + mRecordId + " > " + recordId + " order by " + mRecordId + " desc limit " + mLocalPageSize;
            }
        } else {
            if (loadMore) {
                queryStatement = "where " + mRecordId + " < " + recordId + " order by " + mRecordId + " desc limit " + mLocalPageSize;
            } else {
                queryStatement = "where " + mRecordId + " > " + recordId + " order by " + mRecordId + " desc limit " + mLocalPageSize;
            }
        }
        return queryStatement;
    }

    public String queryStatementInnerJoin(boolean loadMore, String Sql, String masterTable, long recordId) {
        isLoading = true;
        String queryStatement = "";
        if (loadMore) {
            queryStatement = Sql + masterTable + "." + mRecordId + " < " + recordId + " order by "  + masterTable + "." +  mRecordId + " desc limit " + mLocalPageSize;
        } else {
            queryStatement = Sql + masterTable + "." + mRecordId + " > " + recordId + " order by "  + masterTable + "." +  mRecordId + " desc limit " + mLocalPageSize;
        }
        return queryStatement;
    }

    public String queryStatementLike(boolean loadMore, String searchKey, String searchValue, long recordId) {
        isLoading = true;
        String queryStatement;
        if (!TextUtils.isEmpty(searchKey)) {
            queryStatement = "where " + searchKey + " like '%" + searchValue + "%'";
        } else {
            if (loadMore) {
                queryStatement = "where " + mRecordId + " < " + recordId + " order by " + mRecordId + " desc limit " + mLocalPageSize;
            } else {
                queryStatement = "where " + mRecordId + " > " + recordId + " order by " + mRecordId + " desc limit " + mLocalPageSize;
            }
        }
        return queryStatement;
    }

    public String queryStatement(boolean loadMore, String searchKey, String searchValue, String extraSQL, String sortingKey, long sortingValue) {
        isLoading = true;
        String queryStatement;
        if (!TextUtils.isEmpty(searchKey)) {
            if(!TextUtils.isEmpty(extraSQL)) queryStatement = "where " + searchKey + " = '" + searchValue + "' and " + extraSQL;
            else queryStatement = "where " + searchKey + " = '" + searchValue + "'";
        } else {
            if (loadMore) {
                if(!TextUtils.isEmpty(extraSQL)) queryStatement = "where " + extraSQL + " and " + sortingKey + " < " + sortingValue + " order by " + sortingKey + " desc limit " + mLocalPageSize;
                else queryStatement = "where " + sortingKey + " < " + sortingValue + " order by " + sortingKey + " desc limit " + mLocalPageSize;
            } else {
                if(!TextUtils.isEmpty(extraSQL))queryStatement = "where " + extraSQL + " and " + sortingKey + " > " + sortingValue + " order by " + mRecordId + " desc limit " + mLocalPageSize;
                else queryStatement = "where " + sortingKey + " > " + sortingValue + " order by " + sortingKey + " desc limit " + mLocalPageSize;
            }
        }
        return queryStatement;
    }

    public void errorDialog(String message) {
        dt.alert.showError(dt.gStr(R.string.common_warning_title), message, dt.gStr(R.string.ok));
    }

    public boolean alreadyDownload(String fetchTable) {
        return dt.pref.getBoolean(fetchTable);
    }

    // bind toolbar
    public void setupToolbar(int titleResourceId) {
        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBarTitle(getString(titleResourceId), getString(R.string.default_bangla_font));

        // bind back arrow in toolbar
        ActionBar ctBr = getSupportActionBar();
        ctBr.setDisplayHomeAsUpEnabled(true);
        ctBr.setDisplayShowHomeEnabled(true);
        ctBr.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back);
    }

    // Set up the toolbar title
    public void setActionBarTitle(String title, String font) {
        TextView tv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(title);
        tv.setTextSize(22);
        tv.setTextColor(Color.parseColor("#616161"));
//        Typeface tf = Typeface.createFromAsset(getAssets(), font);
   //     tv.setTypeface(tf);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(onOptionsMenuInflate()>0) getMenuInflater().inflate(onOptionsMenuInflate(), menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected abstract void onOptionsItemClick(MenuItem item);

    protected abstract int onOptionsMenuInflate();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        onOptionsItemClick(item);
        return true;
    }

    // back arrow action
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //by imrose
    public void RecordsAddUpdate(String primaryKey, RecordOperation recordOperation) {
        // check gps and get location
            if (primaryKey.isEmpty()) recordOperation.AddRecord();
            else recordOperation.UpdateRecord(Long.parseLong(primaryKey));
    }


    protected void setUUID(Object o) {
        dt.dynamic.executeMethod(o, "setUUID", mUUID);
    }

    public String getTransactionUUID(){
        mUUID = dt.pref.getInt(mUserId)+dt.dateTime.getCurrentDateTimeMils();
        return mUUID;
    }

}