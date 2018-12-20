package duti.com.pushmyorder.config;

public class Constants {

    public static final String BASE_URL = "http://10.10.0.245/pushmyorder/"; // "http://192.168.0.100/pushmyorder/";
    public static final int DATABASE_VERSION = 1;
    public static final String DB_NAME = "pushMyOrderPref";
    public static final String PREF_NAME = "pushMyOrderPref";
    public static final String mContentType = "Content-Type";
    public static final String mApplicationType = "application/json";
    public static final int mSyncedValue = 1;
    public static final String mStatus = "Status";
    public static final String mRecordId = "RecordId";
    public static final String mServerRecordId = "ServerRecordId";
    public static final int mLocalPageSize = 10;
    public static final String mFirstTimeInstallation = "FirstTimeInstallation";
    public static final String mUserId = "UserId";

    public static final String LOG = "PushMyOrder";

    public static final String ADMIN_EMAIL = "admin@pushmyorder.org";


    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

}