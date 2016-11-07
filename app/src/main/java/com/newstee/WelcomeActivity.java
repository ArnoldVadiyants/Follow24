package com.newstee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.newstee.helper.SQLiteHandler;
import com.newstee.helper.SessionManager;
import com.newstee.model.data.DataPost;
import com.newstee.model.data.DataUserAuthentication;
import com.newstee.model.data.User;
import com.newstee.model.data.UserLab;
import com.newstee.network.FactoryApi;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arnold on 03.03.2016.
 */
public class WelcomeActivity extends AppCompatActivity {
    private enum SocialKeys {GOOGLE, FACEBOOK, VK, TWITTER}

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "WelcomeActivity";

    private SQLiteHandler db;
    private SessionManager session;
    private CallbackManager callbackmanager;
    private ProgressBar mProgress;
    private ImageButton btnFacebookReg;
    private ImageButton btnTwitterReg;
    private ImageButton btnGoogleReg;
    private ImageButton btnVkReg;
    private Button btnRegistration;
    private Button btnResume;
    private FrameLayout layoutSignIn;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_layout);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (isFirstTime()) {
            // What you do when the Application is Opened First time Goes here
        }
        if (session.isLoggedIn()) {
            Intent intent = new Intent(WelcomeActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
            return;

        }
///
        mProgress = (ProgressBar) findViewById(R.id.welcome_progress);
        mProgress.setVisibility(View.GONE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        callbackmanager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                update_ui("login successfully");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        if (response.getError() != null) {
                            update_ui("error");

                        } else {
                            String jsonresult = String.valueOf(json);
                            Log.d(TAG, "@@@@@@@ jsonResult " + jsonresult);
                            System.out.println("@@@@@@@ JSON Result " + jsonresult);
                            try {
                                final String id = json.getString("id");
                                CreateUserViaSocialNet(id, SocialKeys.FACEBOOK);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            update_ui(jsonresult);
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                update_ui("CANCELED");
            }

            @Override
            public void onError(FacebookException exception) {
                update_ui("ERROR");
            }
        });
        show_facebook_keyhash();

        btnFacebookReg = (ImageButton) findViewById(R.id.facebook_imageButton);
        btnTwitterReg = (ImageButton) findViewById(R.id.twitter_imageButton);
        btnVkReg = (ImageButton) findViewById(R.id.vk_imageButton);
        btnGoogleReg = (ImageButton) findViewById(R.id.google_imageButton);
        btnRegistration = (Button) findViewById(R.id.registration_via_email_button);
        layoutSignIn = (FrameLayout) findViewById(R.id.sign_in_layout);
        btnResume = (Button) findViewById(R.id.resume_button);
        //  btnFacebookReg.setBackgroundResource(R.drawable.ic_facebook);
        //btnFacebookReg.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
       /* btnFacebookReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        btnFacebookReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(WelcomeActivity.this, Arrays.asList("public_profile", "user_friends"));

            }
        });


        btnGoogleReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        btnVkReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(WelcomeActivity.this, Constants.SCOPE);
                //     Log.d(TAG, "@@@@@@@@@@ InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
            }
        });
        btnTwitterReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //     FirebaseMessaging.getInstance().subscribeToTopic("news");
                Log.d(TAG, "Subscribed to news topic");
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this,
                        RegisterActivity.class);
                startActivity(intent);

            }
        });
        layoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this,
                        LoginActivity.class);
                startActivity(intent);
            }
        });
        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }


    private void update_ui(String message) {
        Log.d(TAG, "@@@@@@ LOG MESSAGE " + message);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                //        Toast.makeText(WelcomeActivity.this,"Hello "+res.userId,Toast.LENGTH_LONG).show();
                res.saveTokenToSharedPreferences(getApplicationContext(), VKAccessToken.ACCESS_TOKEN);
                CreateUserViaSocialNet(res.userId, SocialKeys.VK);
// Пользователь успешно авторизовался
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(WelcomeActivity.this, "Error" + error.errorMessage, Toast.LENGTH_LONG).show();
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        }))

            super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String gpId = acct.getEmail();
                Log.d(TAG, "@@@@@@@@@ gpId = " + gpId);
                CreateUserViaSocialNet(gpId, SocialKeys.GOOGLE);
            }
        }
        callbackmanager.onActivityResult(requestCode, resultCode, data);

    }

    private void showContent() {
        btnFacebookReg.setVisibility(View.GONE);
        btnTwitterReg.setVisibility(View.GONE);
        btnVkReg.setVisibility(View.GONE);
        btnGoogleReg.setVisibility(View.GONE);
        btnRegistration.setVisibility(View.GONE);
        layoutSignIn.setVisibility(View.GONE);
        btnResume.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideContent() {
        btnFacebookReg.setVisibility(View.GONE);
        btnTwitterReg.setVisibility(View.GONE);
        btnVkReg.setVisibility(View.GONE);
        btnGoogleReg.setVisibility(View.GONE);
        btnRegistration.setVisibility(View.GONE);
        layoutSignIn.setVisibility(View.GONE);
        btnResume.setVisibility(View.GONE);
        mProgress.setVisibility(View.GONE);
    }

    private boolean CreateUserViaSocialNet(final String id, final SocialKeys key) {
        Log.d(TAG, "@@@@@ Id = "+id + ", Key + "+key);
        Call<DataPost> callR = null;
        switch (key) {
            case GOOGLE:
                callR = FactoryApi.getInstance(getApplicationContext()).createUserViaSocialNetwork(id, null, null, null, "ru");
                break;
            case FACEBOOK:
                callR = FactoryApi.getInstance(getApplicationContext()).createUserViaSocialNetwork(null, id, null, null, "ru");
                break;
            case VK:
                callR = FactoryApi.getInstance(getApplicationContext()).createUserViaSocialNetwork(null, null, id, null, "ru");
                break;
            case TWITTER:
                callR = FactoryApi.getInstance(getApplicationContext()).createUserViaSocialNetwork(null, null, null, id, "ru");
                break;
        }
        if (callR == null) {
            return false;
        }
        callR.enqueue(new Callback<DataPost>() {
            @Override
            public void onResponse(Call<DataPost> call, retrofit2.Response<DataPost> response) {
                String result = response.body().getResult();
                String msgRegister = response.body().getMessage();
                //    Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                //   Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + " Result: " + result + " Data " + data + " Message " + msg);
                //  Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@");*//*
                if (result.equals(Constants.RESULT_SUCCESS)) {
                    //    Toast.makeText(getApplicationContext(), R.string.register_msg, Toast.LENGTH_LONG).show();
                    Call<DataUserAuthentication> auth = null;
                    switch (key) {
                        case GOOGLE:
                            auth = FactoryApi.getInstance(getApplicationContext()).signIn(id, null, null, null, "ru");
                            break;
                        case FACEBOOK:
                            auth = FactoryApi.getInstance(getApplicationContext()).signIn(null, id, null, null, "ru");
                            break;
                        case VK:
                            auth = FactoryApi.getInstance(getApplicationContext()).signIn(null, null, id, null, "ru");
                            break;
                        case TWITTER:
                            auth = FactoryApi.getInstance(getApplicationContext()).signIn(null, null, null, id, "ru");
                            break;
                    }

                    auth.enqueue(new Callback<DataUserAuthentication>() {
                        @Override
                        public void onResponse(Call<DataUserAuthentication> call, Response<DataUserAuthentication> response) {
                            String result = response.body().getResult();

                            if (result.equals(Constants.RESULT_SUCCESS)) {
                                FirebaseMessaging.getInstance().subscribeToTopic("news");
                                Call<DataPost> keyCall = FactoryApi.getInstance(getApplicationContext()).add_user_key(FirebaseInstanceId.getInstance().getToken());
                                keyCall.enqueue(new Callback<DataPost>() {
                                    @Override
                                    public void onResponse(Call<DataPost> call, Response<DataPost> response) {
                                    }

                                    @Override
                                    public void onFailure(Call<DataPost> call, Throwable t) {
                                    }
                                });
                                Log.d(TAG, "@@@@@ Author json "+ new Gson().toJson(response));
                                User data = response.body().getData();
                                switch (key) {
                                    case GOOGLE:
                                        db.addUser(data.getId(),data.getUserLogin(), null, null, SQLiteHandler.KEY_GG_ID, id);
                                        break;
                                    case FACEBOOK:
                                        db.addUser(data.getId(),data.getUserLogin(), null, null, SQLiteHandler.KEY_FB_ID, id);
                                        break;
                                    case VK:
                                        db.addUser(data.getId(),data.getUserLogin(), null, null, SQLiteHandler.KEY_VK_ID, id);
                                        break;
                                    case TWITTER:
                                        db.addUser(data.getId(),data.getUserLogin(), null, null, SQLiteHandler.KEY_TW_ID, id);
                                        break;
                                }
                                new SessionManager(getApplicationContext()).setCountrySettings(data.getLanguage());
                                UserLab.getInstance().setUser(data);

                                UserLab.isLogin = true;
                                session.setLogin(true);
                                Intent intent = new Intent(
                                        WelcomeActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Authorization: "+response.body().getMessage(), Toast.LENGTH_LONG).show();
                                return;

                            }

                        }

                        @Override
                        public void onFailure(Call<DataUserAuthentication> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),
                                    "@Login: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d(TAG, "@@@@@@Login: " + t.getMessage());

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(),"Registration: " +msgRegister, Toast.LENGTH_LONG).show();
                }
                // Launch login activity
                                     /*           Intent intent = new Intent(
                                        RegisterActivity.this,
                                                LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                        */

            }

            @Override
            public void onFailure(Call<DataPost> call, Throwable t) {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + t.getCause());
                t.printStackTrace();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + t.getLocalizedMessage());

                Toast.makeText(getApplicationContext(),
                        "@Registered: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void show_facebook_keyhash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
//uih ih; j o;
                md.update(signature.toByteArray());
                Log.d("Facebook KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }
}
