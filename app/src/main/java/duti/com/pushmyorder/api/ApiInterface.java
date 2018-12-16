package duti.com.pushmyorder.api;


import duti.com.pushmyorder.model.ServerResponse;
import duti.com.pushmyorder.model.UserToken;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static duti.com.pushmyorder.config.Constants.mContentType;

public interface ApiInterface<T> {

    @POST("register.php")
    Call<ServerResponse> registerToken(
            @Header(mContentType) String contentType,
            @Body UserToken register);

}
