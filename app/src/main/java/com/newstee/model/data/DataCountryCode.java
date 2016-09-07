package com.newstee.model.data;

/**
 * Created by Arnold on 06.09.2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class DataCountryCode {

    @SerializedName("data")
    @Expose
    private CountryCode data;
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
    public CountryCode getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(CountryCode data) {
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
