package com.absket.in.model;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
/*import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;*/

import java.io.File;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.mime.MultipartTypedOutput;

public interface  UploadImageInterface {

   /* @Multipart
    @POST("/imagefolder/index.php")
    Call<UploadObject> uploadFile(@Part MultipartBody.Part file, @Part("name") RequestBody name);*/


    @POST("/andhrabasketphp/profileimgupload.php")
    void submitData(@Body MultipartTypedOutput attachments, Callback<ResponsePojo> response);

    @FormUrlEncoded
    @POST("/andhrabasketphp/profileimgupload.php")
    void postProfilepic(@Field("customer_id") String sMainCategory,
                        @Field("profile_img") MultipartTypedOutput attachments, Callback<ResponsePojo> response);
}
