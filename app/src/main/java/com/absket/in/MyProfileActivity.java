package com.absket.in;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.absket.in.model.ResponsePojo;
import com.absket.in.model.UploadImageInterface;
import com.absket.in.model.UploadObject;
import com.absket.in.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import pub.devrel.easypermissions.EasyPermissions;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
//import retrofit2.Call;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.facebook.login.widget.ProfilePictureView.TAG;
import android.Manifest;
/**
 */
public class MyProfileActivity extends Activity  {


    UserSessionManager session;
    String sMobile, sAddress;
    ProgressDialog progressDialog;
    ArrayList<String> arrayPincodes;
    ArrayAdapter adapterPincode;

    private static final int PERMISSION_REQUEST_CODE = 200;
    TextView tv_mobile;
    TextView tv_address;
    TextView tv_name;
    TextView tv_email;
    CardView card_edit;
    ImageView img_back;
    TextView tv_note;
    CardView card_change_password;
    CircleImageView profileImg;

    // private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://www.andhrabasket.com/main/andhrabasket";
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private Uri realUri;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private String userChoosenTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        session = new UserSessionManager(MyProfileActivity.this);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_note = (TextView) findViewById(R.id.tv_note);
        img_back = (ImageView) findViewById(R.id.img_back);
        card_edit = (CardView) findViewById(R.id.card_edit);
        profileImg = (CircleImageView) findViewById(R.id.profile_img);
        tv_name.setText(session.getUserName());
        tv_email.setText(session.getUserEmail());
        card_change_password = (CardView) findViewById(R.id.card_change_password);


//        if (checkPermission()) {
//            requestPermissionAndContinue();
//        }
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);*/

                selectImage();


            }
        });

        if (session.getUserMobile().equals("")) {
            tv_mobile.setText("Add Your Mobile Number");
        } else {
            tv_mobile.setText(session.getUserMobile());
        }
        if (session.getUserAddress().equals("")) {
            tv_address.setText("Add your address here");
        } else {
            tv_address.setText(session.getUserAddress());
        }

        card_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent in = new Intent(MyProfileActivity.this, ChangePassword.class);
                startActivity(in);
            }
        });
        CallingPincode();

        card_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open dialog box
                //call asynctask to update mobile and address
                final Dialog dialog = new Dialog(MyProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_myprofile);
                final EditText edt_mobile, edt_address;
                edt_mobile = (EditText) dialog.findViewById(R.id.edt_mobile);
                edt_address = (EditText) dialog.findViewById(R.id.edt_address);
                final AutoCompleteTextView act_pincode = (AutoCompleteTextView) dialog.findViewById(R.id.act_pincode);
                CardView card_update = (CardView) dialog.findViewById(R.id.card_update);

                adapterPincode = new ArrayAdapter(MyProfileActivity.this, android.R.layout.simple_dropdown_item_1line, arrayPincodes);
                act_pincode.setAdapter(adapterPincode);
                act_pincode.setThreshold(1);

                if (session.getUserMobile().equals("")) {
                    edt_mobile.setText("");
                } else if (!session.getUserMobile().equals("")) {
                    edt_mobile.setText(session.getUserMobile());
                    // edt_mobile.setText("");

                }
                if (!session.getUserAddress().equals("")) {
                    edt_address.setText(session.getUserAddress());
                    // edt_address.setText("");

                }


                card_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sMobile = edt_mobile.getText().toString();
                        sAddress = edt_address.getText().toString();
                        sAddress = sAddress + " - " + act_pincode.getText().toString();
                        if (sMobile.equals("") || sAddress.equals("") ||
                                act_pincode.getText().toString().equals("") || !arrayPincodes.contains(act_pincode.getText().toString())) {
                            Toast.makeText(MyProfileActivity.this, "Please Check Mobile Number or Address or Pincode", Toast.LENGTH_SHORT).show();
                        } else {
                            if (session.getUserMobile().equals(sMobile)) {
                                CallingProfileUpdateRetrofit();
                            } else {
                                dialog.dismiss();
                                CheckOTP();
                            }

                        }
                    }
                });


                dialog.show();
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }




    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MyProfileActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void galleryIntent()
    {
       /* Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);*/

        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }






    public void CallingProfileUpdateRetrofit() {
        progressDialog = new ProgressDialog(MyProfileActivity.this);
        progressDialog.setMessage("Updating...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "profileupdate.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.UpdateProfile(sMobile, sAddress, session.getCustomerId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    progressDialog.dismiss();
                    if (output.contains("true")) {
                        session.setUserMobile(sMobile);
                        session.setUserAddress(sAddress);
                        finish();
                        Toast.makeText(MyProfileActivity.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyProfileActivity.this, "Update Failed Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(MyProfileActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

            }
        });

    }


  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MyProfileActivity.this);
    }*/

    public void CallingPincode() {
        progressDialog = new ProgressDialog(MyProfileActivity.this);
        progressDialog.setMessage("Getting Available Location List...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        arrayPincodes = new ArrayList<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "getpincode.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.GetPinCodes(session.getUserId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String output = reader.readLine();
                    JSONArray jsonArray = new JSONArray(output);
                    String sNote = "We are currently available only these pinocodes ";
                    String spincode = "";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        arrayPincodes.add(jsonArray.getJSONObject(i).getString("pincode"));
                        spincode = jsonArray.getJSONObject(i).getString("pincode") + " " + spincode;
                    }

                    String sNotes = sNote + spincode;
                    tv_note.setText(sNotes);
                    progressDialog.dismiss();

                } catch (IOException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    card_edit.setClickable(false);
                    card_edit.setEnabled(false);
                    Toast.makeText(MyProfileActivity.this, "Cannot able to retrive pincode. Please come back later", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    card_edit.setClickable(false);
                    card_edit.setEnabled(false);
                    Toast.makeText(MyProfileActivity.this, "Cannot able to retrive pincode. Please come back later", Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                card_edit.setClickable(false);
                card_edit.setEnabled(false);
                Toast.makeText(MyProfileActivity.this, "Cannot able to retrive pincode. Please come back later", Toast.LENGTH_SHORT).show();

            }
        });

    }


    public void CheckOTP() {
        progressDialog = new ProgressDialog(MyProfileActivity.this);
        progressDialog.setMessage("Generating OTP...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        arrayPincodes = new ArrayList<>();
        RestAdapter Radapter = new RestAdapter.Builder().setEndpoint(DomainName.sURL + "checkotp.php").build();
        RetrofitAPI api = Radapter.create(RetrofitAPI.class);
        api.CheckOTP(sMobile, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    final String output = reader.readLine();
                    progressDialog.dismiss();
                    final Dialog dialog = new Dialog(MyProfileActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.otpnew);
                    final EditText edt_otp = (EditText) dialog.findViewById(R.id.edt_otp);
                    ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
                    img_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edt_otp.getText().toString().length() == 4) {
                                if (output.contains(edt_otp.getText().toString())) {
                                    dialog.dismiss();
                                    CallingProfileUpdateRetrofit();
                                } else {
                                    Toast.makeText(MyProfileActivity.this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MyProfileActivity.this, "Please Ente 4-Digit OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MyProfileActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_CODE ){



                //onSelectFromGalleryResult(data);

               realUri = data.getData();

                String filePath = getRealPathFromURIPath(realUri, MyProfileActivity.this);
                File file = new File(filePath);
                Log.d(TAG, "Filename " + file.getName());
                uploadProfilePic(file);

            }
                //onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA){

                onCaptureImageResult(data);

               /* realUri = data.getData();

                String filePath = getRealPathFromURIPath(realUri, MyProfileActivity.this);
                File file = new File(filePath);
                Log.d(TAG, "Filename " + file.getName());
                uploadProfilePic(file);*/
            }
                //onCaptureImageResult(data);
        }


    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        Log.d(TAG, "Filename " + destination.getName());
        uploadProfilePic(destination);

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
              //  realUri = data.getData();

                String filePath = getRealPathFromURIPath( data.getData(), MyProfileActivity.this);
                File file = new File(filePath);
                Log.d(TAG, "Filename " + file.getName());
                uploadProfilePic(file);
               // bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       // ivImage.setImageBitmap(bm);
    }






    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }



  /*  @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(realUri != null){
            String filePath = getRealPathFromURIPath(realUri, MyProfileActivity.this);
            File file = new File(filePath);
            uploadServer();
           // RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            //MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
           // RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
           *//* Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_PATH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
            Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);
            fileUpload.enqueue(new Callback<UploadObject>() {
                @Override
                public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
                    Toast.makeText(MyProfileActivity.this, "Success " + response.message(), Toast.LENGTH_LONG).show();
                    Toast.makeText(MyProfileActivity.this, "Success " + response.body().toString(), Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call<UploadObject> call, Throwable t) {
                    Log.d(TAG, "Error " + t.getMessage());

            });*//*
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }
*/

    private  void uploadServer( File file){

        progressDialog = new ProgressDialog(MyProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SERVER_PATH).build();

        // SubmitAPI is name of our interface which will send data to server.
        UploadImageInterface api = restAdapter.create(UploadImageInterface.class);
        api.submitData(attachFiles(  file), new Callback<ResponsePojo>() {
            @Override
            public void success(ResponsePojo responsePojo, Response response) {

                 // responsePojo is object of our getter setter class and getError is method i made to get server response.
                if ( responsePojo.getStatus()!=null){
                    Toast.makeText(getApplicationContext(),  responsePojo.getStatus(), Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                    builder.setMessage(responsePojo.toString()).setTitle("Response from Servers")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // do nothing
                                    progressDialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    Log.d(TAG,"Data not uploaded");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG,"unable to transfer data to server due to :"+ error.getMessage());
                progressDialog.dismiss();
            }
        });
    }



    private  void uploadProfilePic( File file){

        progressDialog = new ProgressDialog(MyProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(SERVER_PATH).build();

        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        // SubmitAPI is name of our interface which will send data to server.
        UploadImageInterface api = restAdapter.create(UploadImageInterface.class);

        api.postProfilepic("6374", attachFiles(file), new Callback<ResponsePojo>() {
            @Override
            public void success(ResponsePojo responsePojo, Response response) {

                if ( responsePojo.getStatus()!=null){
                    Toast.makeText(getApplicationContext(),  responsePojo.getStatus(), Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                    builder.setMessage(responsePojo.toString()).setTitle("Response from Servers")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // do nothing
                                    progressDialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    Log.d(TAG,"Data not uploaded");
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG,"unable to transfer data to server due to :"+ error.getMessage());
                progressDialog.dismiss();
            }
        });

       /* api.submitData(attachFiles(  file), new Callback<ResponsePojo>() {
            @Override
            public void success(ResponsePojo responsePojo, Response response) {

                // responsePojo is object of our getter setter class and getError is method i made to get server response.
                if ( responsePojo.getStatus()!=null){
                    Toast.makeText(getApplicationContext(),  responsePojo.getStatus(), Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                    builder.setMessage(responsePojo.toString()).setTitle("Response from Servers")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // do nothing
                                    progressDialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    Log.d(TAG,"Data not uploaded");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG,"unable to transfer data to server due to :"+ error.getMessage());
                progressDialog.dismiss();
            }
        });*/
    }


    private MultipartTypedOutput attachFiles( File file){
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();

        // this will add data to body to send via retrofit.
        multipartTypedOutput.addPart("customer_id ",new TypedString("6374"));
      //  multipartTypedOutput.addPart("website", new TypedString("https://www.learnpainless.com"));

        // this will make Retrofit file from gallery image
        multipartTypedOutput.addPart("profile_img ",  new TypedFile("image/*",file));
        return multipartTypedOutput;
    }







    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.read_file));
                alertBuilder.setMessage(R.string.read_file);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MyProfileActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(MyProfileActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

    private void openActivity() {
        //add your further process after giving permission or to download images from remote server.
    }
}



