package com.newstee.model.data;

/**
 * Created by Arnold on 12.04.2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Author {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("author_name")
    @Expose
    private String name;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("quantity_subs")
    @Expose
    private String subCount;

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
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     *
     * @param avatar
     * The avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     *
     * @return
     * The subCount
     */
    public String getSubCount() {
        return subCount;
    }

    /**
     *
     * @param subCount
     * The sub_count
     */
    public void setSubCount(String subCount) {
        this.subCount = subCount;
    }

}



