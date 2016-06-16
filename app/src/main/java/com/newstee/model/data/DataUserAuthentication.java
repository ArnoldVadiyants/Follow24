package com.newstee.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

/**
 * Created by Arnold on 21.04.2016.
 */

@Generated("org.jsonschema2pojo")
public class DataUserAuthentication {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("data")
    @Expose
    private List<User> data = new ArrayList<User>();

    /**
     *
     * @return
     * The data
     */
    public List<User> getData() {
        return data ;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<User> data) {
        this.data = data;
    }

    /**
     *
     * @return
     * The result
     */
    public String getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}