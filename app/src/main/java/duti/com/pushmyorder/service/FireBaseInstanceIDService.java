package duti.com.pushmyorder.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import duti.com.pushmyorder.api.RefreshToken;
import duti.com.pushmyorder.config.Constants;
import duti.com.pushmyorder.library.DroidTool;


public class FireBaseInstanceIDService extends FirebaseInstanceIdService {

    DroidTool dt;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        dt = new DroidTool(this);

        // sending reg id to your server
        if (dt.droidNet.hasConnection()) {
            new RefreshToken(dt).sendRegistrationToServer(refreshedToken, new RefreshToken.onTokenSentListener() {
                @Override
                public void onTokenSent(boolean success, String message, String token) {
                    dt.pref.set("FcmToken", token);
                    if (success) {
                        dt.pref.set("FcmTokenSent", true);
                    } else {
                        dt.pref.set("FcmTokenSent", false);
                    }
                    // Notify UI that registration has completed, so the progress indicator can be hidden.
                    Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
                    registrationComplete.putExtra("token", refreshedToken);
                    LocalBroadcastManager.getInstance(dt.c).sendBroadcast(registrationComplete);
                }
            });
        }
    }

}

