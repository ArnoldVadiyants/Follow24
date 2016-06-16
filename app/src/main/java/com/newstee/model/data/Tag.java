package com.newstee.model.data;

/**
 * Created by Arnold on 06.04.2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Tag {


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name_tag")
    @Expose
    private String name;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The nameTag
     */
    public String getNameTag() {
        return name;
    }

    /**
     *
     * @param nameTag
     * The name_tag
     */
    public void setNameTag(String name) {
        this.name = name;
    }

}

