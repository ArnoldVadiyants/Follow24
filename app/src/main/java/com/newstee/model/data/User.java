package com.newstee.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class User {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String userLogin;
    @SerializedName("password")
    @Expose
    private String userPassword;
    @SerializedName("email")
    @Expose
    private String userEmail;


    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("news_liked_ids")
    @Expose
    private String newsLikedIds;
    @SerializedName("user_idvk")
    @Expose
    private Object userIdvk;
    @SerializedName("news_added_ids")
    @Expose
    private String newsAddedIds;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("tags_ids")
    @Expose
    private String tagsIds;
    @SerializedName("user_googleid")
    @Expose
    private Object userGoogleid;
    @SerializedName("user_facebookid")
    @Expose
    private Object userFacebookid;

    /**
     *
     * @return
     * The id
     */
    User()
    {

    }
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
     * The userLogin
     */
    public String getUserLogin() {
        return userLogin;
    }

    /**
     *
     * @param userLogin
     * The user_login
     */
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    /**
     *
     * @return
     * The userPassword
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     *
     * @param userPassword
     * The user_password
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     *
     * @return
     * The userEmail
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     *
     * @param userEmail
     * The user_email
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     *
     * @return
     * The newsLikedIds
     */
    public String getNewsLikedIds() {
        return newsLikedIds;
    }

    /**
     *
     * @param newsLikedIds
     * The news_liked_ids
     */
    public void setNewsLikedIds(String newsLikedIds) {
        this.newsLikedIds = newsLikedIds;
    }

    /**
     *
     * @return
     * The userIdvk
     */
    public Object getUserIdvk() {
        return userIdvk;
    }

    /**
     *
     * @param userIdvk
     * The user_idvk
     */
    public void setUserIdvk(Object userIdvk) {
        this.userIdvk = userIdvk;
    }

    /**
     *
     * @return
     * The newsAddedIds
     */
    public String getNewsAddedIds() {
        return newsAddedIds;
    }

    /**
     *
     * @param newsAddedIds
     * The news_added_ids
     */
    public void setNewsAddedIds(String newsAddedIds) {
        this.newsAddedIds = newsAddedIds;
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
     * The tagsIds
     */
    public String getTagsIds() {
        return tagsIds;
    }

    /**
     *
     * @param tagsIds
     * The tags_ids
     */
    public void setTagsIds(String tagsIds) {
        this.tagsIds = tagsIds;
    }

    /**
     *
     * @return
     * The userGoogleid
     */
    public Object getUserGoogleid() {
        return userGoogleid;
    }

    /**
     *
     * @param userGoogleid
     * The user_googleid
     */
    public void setUserGoogleid(Object userGoogleid) {
        this.userGoogleid = userGoogleid;
    }

    /**
     *
     * @return
     * The userFacebookid
     */
    public Object getUserFacebookid() {
        return userFacebookid;
    }

    /**
     *
     * @param userFacebookid
     * The user_facebookid
     */
    public void setUserFacebookid(Object userFacebookid) {
        this.userFacebookid = userFacebookid;
    }
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}