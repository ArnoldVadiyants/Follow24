package com.newstee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputRepeatPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

  /*  private Gson gson = new GsonBuilder().create();
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(NewsTeeApiInterface.BASE_URL)
            .build();
    private NewsTeeApiInterface newsTeeApiInterface = retrofit.create(NewsTeeApiInterface.class);*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputRepeatPassword = (EditText) findViewById(R.id.repeat_password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
  /*      if (session.isLoggedIn()) {
            // UserRegister is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }*/

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String repeat_password = inputRepeatPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !repeat_password.isEmpty()) {
                    if (password.equals(repeat_password)) {

                        registerUser(name, password, repeat_password, email);
                      /*  Intent intent = new Intent(RegisterActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();*/
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Введенные пароли не совпадают!", Toast.LENGTH_LONG)
                                .show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Пожалуйста, заполните все поля!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String name, final String password, final String repeat_password, final String email) {
        // Tag used to cancel the request


        pDialog.setMessage("Registering ...");
        showDialog();
        Call<DataPost> call = FactoryApi.getInstance(this).createUser(name, password, repeat_password, email, "ru");
        call.enqueue(new Callback<DataPost>() {
            @Override
            public void onResponse(Call<DataPost> call, retrofit2.Response<DataPost> response) {
                String result = response.body().getResult();
                Log.d(TAG, "@@@@@ json "+ new Gson().toJson(response));
                String msgRegister = response.body().getMessage();
                         /*    Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                             Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + " Result: " + result + " Data " + data + " Message " + msg);
                             Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@");*/
                if (result.equals(Constants.RESULT_SUCCESS)) {
                    Toast.makeText(getApplicationContext(), R.string.register_msg, Toast.LENGTH_LONG).show();
                    Call<DataUserAuthentication> auth = FactoryApi.getInstance(getApplicationContext()).signIn(email, password, "ru");
                    auth.enqueue(new Callback<DataUserAuthentication>() {
                            @Override
                        public void onResponse(Call<DataUserAuthentication> call, Response<DataUserAuthentication> response) {
                            String result = response.body().getResult();
                                if (result.equals(Constants.RESULT_SUCCESS)) {
                                    FirebaseMessaging.getInstance().subscribeToTopic("news");
                                    Call<DataPost>keyCall  =  FactoryApi.getInstance(getApplicationContext()).add_user_key(FirebaseInstanceId.getInstance().getToken());
                                    keyCall.enqueue(new Callback<DataPost>() {
                                        @Override
                                        public void onResponse(Call<DataPost> call, Response<DataPost> response) {}
                                        @Override
                                        public void onFailure(Call<DataPost> call, Throwable t) {}});
                                    User data = response.body().getData();;
                                    db.addUser(data.getId(),data.getUserLogin(), email, password,null, null);
                                    UserLab.getInstance().setUser(data);
                                    UserLab.isLogin = true;
                                    UserLab.getInstance().setIsUpdated(false);
                                    hideDialog();
                                    session.setLogin(true);
                                Intent intent = new Intent(
                                        RegisterActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),response.body().getMessage() , Toast.LENGTH_LONG).show();
                                hideDialog();
                                return;

                            }

                        }

                        @Override
                        public void onFailure(Call<DataUserAuthentication> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),
                                    "@Login: " +t.getMessage(), Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), msgRegister, Toast.LENGTH_LONG).show();
                    hideDialog();
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
                System.out.println( "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + t.getCause());
                t.printStackTrace();
                System.out.println( "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +t.getLocalizedMessage());

                Toast.makeText(getApplicationContext(),
                       "@Registered: " +t.getMessage() , Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

             /*   StringRequest strReq = new StringRequest(Method.POST,
                        AppConfig.URL_REGISTER, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Register Response: " + response.toString());
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                // UserRegister successfully stored in MySQL
                                // Now store the user in sqlite
                                String uid = jObj.getString("uid");

                                JSONObject user = jObj.getJSONObject("user");
                                String name = user.getString("name");
                                String email = user.getString("email");
                                String created_at = user
                                        .getString("created_at");

                                // Inserting row in users table
                                db.addUser(name, email);

                                Toast.makeText(getApplicationContext(), "UserRegister successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                                // Launch login activity
                                Intent intent = new Intent(
                                        RegisterActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {

                                // Error occurred in registration. Get the error
                                // message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Registration Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting params to register url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name", name);
                        params.put("email", email);
                        params.put("password", password);

                        return params;
                    }

                };
*/
        // Adding request to request queue
        //   AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}