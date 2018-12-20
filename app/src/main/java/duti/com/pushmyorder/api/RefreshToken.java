package duti.com.pushmyorder.api;

import android.text.TextUtils;

import duti.com.pushmyorder.library.DroidTool;
import duti.com.pushmyorder.model.ServerResponse;
import duti.com.pushmyorder.model.UserToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static duti.com.pushmyorder.config.Constants.ADMIN_EMAIL;
import static duti.com.pushmyorder.config.Constants.mApplicationType;



public class RefreshToken {

    DroidTool dt;

    public interface onTokenSentListener {
        void onTokenSent(boolean success, String message, String token);
    }

    public RefreshToken(DroidTool droidTool) {
        dt = droidTool;
    }

    public void sendRegistrationToServer(final String token, final onTokenSentListener listener) {
        final onTokenSentListener onTokenSentListener = listener;
        dt.tools.printLog("FCM Token", token);
        String url = dt.pref.getString("ServerURL");
        if(TextUtils.isEmpty(url)){
            onTokenSentListener.onTokenSent(false, "Url Not Found", token);
        } else {
            Call<ServerResponse> call = ApiClient.getClient(url).create(ApiInterface.class).registerToken(mApplicationType, new UserToken(ADMIN_EMAIL, token));
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    dt.tools.printLog("Server Response Token", response.toString());
                    if (response.isSuccessful()) {
                        if(response.body()!=null){
                            ServerResponse serverResponse = response.body();
                            dt.tools.printJson("TokenSend", serverResponse);
                            if(!serverResponse.isError()){
                                onTokenSentListener.onTokenSent(true, serverResponse.getMessage(), token);
                            } else {
                                onTokenSentListener.onTokenSent(false, serverResponse.getMessage(), token);
                            }
                        } else onTokenSentListener.onTokenSent(false, response.message(), token);
                    } else onTokenSentListener.onTokenSent(false, response.message(), token);
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    dt.tools.printLog("Server Response Token", t.getMessage());
                    onTokenSentListener.onTokenSent(false, t.getMessage(), token);
                }
            });
        }
    }

}
