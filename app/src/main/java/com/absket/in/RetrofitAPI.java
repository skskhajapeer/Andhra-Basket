package com.absket.in;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by Sreejith on 01-03-2017.
 */
public interface RetrofitAPI {



    @FormUrlEncoded
    @POST("/signup.php")
    public void NormalSignUp(
            @Field("username") String sName,
            @Field("email") String sMail,
            @Field("password") String sPSWD,
            @Field("referal_code") String sRefCode,
            @Field("fcmid") String sFCM,
            Callback<Response> callback);



    @FormUrlEncoded
    @POST("/signupfbgoogle.php")
    public void FbGoogleSignUp(
           /* @Field("email") String sMail,
            @Field("username") String sName,
            @Field("fcm") String sFCM,
            @Field("code") String sCode,*/
            @FieldMap Map<String,String> params,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/loginfbgoogle.php")
    public void FbGoogleLogin(
            @FieldMap Map<String,String> params,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/login.php")
    public void NormalLogin(
            @Field("email") String sMail,
            @Field("password") String sPassword,
            @Field("fcmid") String sFCM,
            Callback<Response> callback);

    @GET("/getmaincategory.php")
    public void GetMainCategory(Callback<Response> response);

    @GET("/getversion.php")
    public void GetVersionUpdate(Callback<Response> response);

    @FormUrlEncoded
    @POST("/getsubcategory.php")
    public void GetSubCategory(
            @Field("maincategory") String sMainCategory,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/getproducts.php")
    public void GetProducts(
            @Field("maincategory") String sMainCategory,
            @Field("subcategory") String sSubCategory,
            @Field("lastid") String sLastProductId,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/lastpurchase.php")
    public void LastPurhchase(
           // @Field("customer_id") String sCustomerId,
            @FieldMap Map<String,String> params,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/placeorder.php")
    public void PlaceOrder(
            @Field("cid") String sId,
            @Field("cname") String sName,
            @Field("cmail") String sMail,
            @Field("cmobile") String sMobile,
            @Field("pid[]")ArrayList<String> arrayPid,
            @Field("pname[]")ArrayList<String> arrayPname,
            @Field("pimage[]")ArrayList<String> arrayPimage,
            @Field("mrp[]")ArrayList<String> arrayMRP,
            @Field("discount[]")ArrayList<String> arrayDiscount,
            @Field("price[]")ArrayList<String> arrayPrice,
            @Field("qty[]")ArrayList<String> arrayQty,
            @Field("punit[]")ArrayList<String> arrayUnit,
            @Field("total_price") String sTotPrice,
            @Field("address") String sAddress,
            @Field("mop") String sMOP,
            @Field("dtime") String sDtime,
            @Field("firstpurchase") String firstpurchase,
            Callback<Response> callback);



    @FormUrlEncoded
    @POST("/sorting.php")
    public void SearchProducts(
            @Field("pname") String sProductName,
            @Field("filter") String sFilter,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/profileupdate.php")
    public void UpdateProfile(
            @Field("mobile") String sMobile,
            @Field("address") String sAddress,
            @Field("customer_id") String sCustomerId,
            Callback<Response> callback);



    @FormUrlEncoded
    @POST("/sorting_main.php")
    public void MainSort(
            @Field("main_category") String sMainCategory,
            @Field("sub_category") String sSubCategory,
            @Field("filter") String sFilter,
            @Field("filterop[]") ArrayList<String> arrayFilterOP,
            Callback<Response> callback);



    @FormUrlEncoded
    @POST("/sorting_main_new.php")
    public void MainSortNew(
            @Field("main_category") String sMainCategory,
            @Field("sub_category") String sSubCategory,
            @Field("filter[]") ArrayList<String> arrayFilter,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/getmyorders.php")
    public void GetMyOrders(
            @Field("customer_id") String sCustomerId,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/getmyordersfull.php")
    public void GetMyOrdersFull(
            @Field("order_id") String sOrderId,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/getsliderimages.php")
    public void GetSliderImages(
            @Field("customer_id") String sCustomerId,
            Callback<Response> callback);



    @FormUrlEncoded
    @POST("/lastpurchase.php")
    public void GetLastPurchase(
            @Field("productid[]") ArrayList<String> arrayProductIds,
            @Field("productname[]") ArrayList<String> arrayProductNames,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/getbrandlist.php")
    public void GetBrandList(
            @Field("maincategory") String sMainCategory,
            @Field("subcategory") String sSubCategory,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/applycoupon.php")
    public void CouponCode(
            @Field("cost") String sCost,
            @Field("promocode") String sPromocode,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/getspecialshops.php")
    public void GetSpecialShops(
            @Field("mainc") String sMainCategory,
            Callback<Response> callback);



    @FormUrlEncoded
    @POST("/getpincode.php")
    public void GetPinCodes(
            @Field("userid") String sUserId,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/checkotp.php")
    public void CheckOTP(
            @Field("mobile") String sMobile,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/checkfirstpurchase.php")
    public void CheckFirstPurchase(
            @Field("mobile") String sMobile,
            @Field("customer_id") String sCustomerId,
            @Field("amount") String sTotAmount,

            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/getnotify.php")
    public void GetNotify(
            @Field("mobile") String sMobile,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/refuse.php")
    public void RefUse(
            @Field("customerid") String sCustId,
            @Field("customername") String sCustName,
            @Field("refcode") String sRefCode,
            @Field("amount") String sAmountDeducted,
            @Field("totamt") String sTotalAmout,
            @Field("myref") String sMyRefCode,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/updatepayment.php")
    public void UpdatePaymentStatus(
            @Field("bno") String sOrderId,
            @Field("paymentid") String sPaymentId,

            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/payment.php")
    public void GetPaymentLink(
            @Field("username") String sUserName,
            @Field("mobile") String sMobile,
            @Field("product_name") String sProductName,
            @Field("tot_price") String sTotPrice,
            @Field("email") String sEmail,
            @Field("firstpurchase") String sFirstpuchase,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/referalcash.php")
    public void GetReferalCash(
            @Field("regref") String sRegRef,
            @Field("myref") String sMyRef,
            @Field("code") String sCode,
            @Field("price") String sPrice,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/getcash.php")
    public void GetCash(
            @Field("myref") String sRegRef,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/getaboutus.php")
    public void GetAboutUs(
            @Field("mobile") String sMobile,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/getfaq.php")
    public void GetFAQ(
            @Field("mobile") String sMobile,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/getcontacts.php")
    public void GetContacts(
            @Field("mobile") String sMobile,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/sendotp.php")
    public void SendOTP(
            @Field("email") String sEmail,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/changepassword.php")
    public void ChangePassword(
            @Field("email") String sEmail,
            @Field("oldpassword") String oldPassword,
            @Field("newpassword") String newPassword,
            Callback<Response> callback);


    @FormUrlEncoded
    @POST("/forgotpassword.php")
    public void ChangePassword_New(
            @Field("email") String sEmail,
            Callback<Response> callback);





}
