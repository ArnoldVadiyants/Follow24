package com.newstee.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by Arnold on 09.05.2016.
 */
@Generated("org.jsonschema2pojo")
public class DataUpdateUser {
    @SerializedName("data")
    @Expose
    private UpdateUser data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private String result;

    /**
     *
     * @return
     * The data
     */
    public UpdateUser getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(UpdateUser data) {
        this.data = data;
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

}