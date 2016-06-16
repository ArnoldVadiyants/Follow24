package com.newstee.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arnold on 18.04.2016.
 */
public class UserRegister {
    @Expose
    @SerializedName("username_register")
    String mUsername;
    @Expose
    @SerializedName("password_register")
    String mPassword;
    @Expose
    @SerializedName("reenter_password_register")
    String mRptPassword;
    @Expose
    @SerializedName("email_register")
    String mEmail;
    public UserRegister(String mUsername, String mPassword, String mRptPassword, String mEmail) {
        this.mUsername = mUsername;
        this.mPassword = mPassword;
        this.mRptPassword = mRptPassword;
        this.mEmail = mEmail;
    }
}