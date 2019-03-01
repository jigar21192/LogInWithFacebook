package com.example.jp.loginwithfacebook;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.SendButton;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    ImageView iv;
    private static final int Camara_Request = 123;
    Button share;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    byte[] imgbyte;
    Bitmap p;
    TextView getname;

    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(MainActivity.this);
        setContentView(R.layout.activity_main);
        share = (Button) findViewById(R.id.btnshare);
        iv = (ImageView) findViewById(R.id.iv);
        getname = (TextView) findViewById(R.id.getname);


        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        loginButton.setPublishPermissions("publish_actions");
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, Camara_Request);
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "Log In SuccessFully", Toast.LENGTH_SHORT).show();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                // Application code
                                try {
                                    Log.d("tttttt", object.getString("id"));
                                    String birthday = "";
                                    if (object.has("birthday")) {
                                        birthday = object.getString("birthday"); // 01/31/1980 format
                                    }
                                    String fnm = object.getString("first_name");
                                    String lnm = object.getString("last_name");
                                    getname.setText(fnm);

                                } catch (JSONException e) {
                                    e.printStackTrace();


                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();





                shareDialog = new ShareDialog(MainActivity.this);
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(MainActivity.this, "Share SuccessFully", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Cancel SuccessFully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("errr", error.getMessage());
                        Toast.makeText(MainActivity.this, "Check Details", Toast.LENGTH_SHORT).show();

                    }
                });
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(p)
                                .build();
                      /*  SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .build();*/

                        //  String file="ff.mp4";
                        Uri vl = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Video/ff.mp4"));
                        ShareVideo video = new ShareVideo.Builder()
                                .setLocalUrl(vl)
                                .build();
                     /*   ShareVideoContent content = new ShareVideoContent.Builder()
                                .setVideo(video)
                                .build();*/

                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                                .build();

                        shareDialog.show(content);

                        ShareDialog.show(MainActivity.this, content);

                    }
                });


                try {
                    PackageInfo info = getPackageManager().getPackageInfo(
                            "com.example.jp.loginwithfacebook",
                            PackageManager.GET_SIGNATURES);
                    for (Signature signature : info.signatures) {
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());
                        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

                    }
                } catch (PackageManager.NameNotFoundException e) {

                } catch (NoSuchAlgorithmException e) {

                }


            }


            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Cancel SuccessFully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("errr", error.getMessage());
                Toast.makeText(MainActivity.this, "Check Details", Toast.LENGTH_SHORT).show();
            }

          /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == Camara_Request) {
                    p = (Bitmap) data.getExtras().get("data");
                    iv.setImageBitmap(p);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    p.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    imgbyte = stream.toByteArray();
                }
            }*/
        });
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == Camara_Request) {
                p = (Bitmap) data.getExtras().get("data");
                iv.setImageBitmap(p);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                p.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imgbyte = stream.toByteArray();
            }
        }
    }


