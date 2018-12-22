package duti.com.pushmyorder.library;

import android.content.Context;

import duti.com.pushmyorder.model.push.Data;
import duti.com.pushmyorder.model.push.Payload;

import static duti.com.pushmyorder.config.Constants.mRecordId;
import static duti.com.pushmyorder.config.Constants.mRowId;


public class DatabaseHandler {

    Context mContext;

    public DatabaseHandler(Context context) {
        mContext = context;
        createTable();
    }

    public void createTable() {
        new Repository(mContext, new Data()).create(mRecordId, true);
        new Repository(mContext, new Payload()).create(mRowId, true);
    }
}
