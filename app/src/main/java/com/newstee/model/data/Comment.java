package com.newstee.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by Arnold on 23.08.2016.
 */
@Generated("org.jsonschema2pojo")
public class Comment {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("id_news")
    @Expose
    private String idNews;
    @SerializedName("name_comment")
    @Expose
    private String nameComment;
    @SerializedName("text_comment")
    @Expose
    private String textComment;
    @SerializedName("yes")
    @Expose
    private int likeComment;
    @SerializedName("no")
    @Expose
    private int dlikeComment;
    @SerializedName("danger")
    @Expose
    private int danger;

    @SerializedName("date_comment")
    @Expose
    private String dateComment;

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The idNews
     */
    public String getIdNews() {
        return idNews;
    }

    /**
     * @param idNews The id_news
     */
    public void setIdNews(String idNews) {
        this.idNews = idNews;
    }

    /**
     * @return The nameComment
     */
    public String getNameComment() {
        return nameComment;
    }

    /**
     * @param nameComment The name_comment
     */
    public void setNameComment(String nameComment) {
        this.nameComment = nameComment;
    }

    /**
     * @return The textComment
     */
    public String getTextComment() {
        return textComment;
    }

    /**
     * @param textComment The text_comment
     */
    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }

    /**
     * @return The likeComment
     */
    public int getLikeComment() {
        return likeComment;
    }

    /**
     * @param likeComment The like_comment
     */
    public void setLikeComment(int likeComment) {
        this.likeComment = likeComment;
    }

    /**
     * @return The dlikeComment
     */
    public int getDlikeComment() {
        return dlikeComment;
    }

    /**
     * @param dlikeComment The dlike_comment
     */
    public void setDlikeComment(int dlikeComment) {
        this.dlikeComment = dlikeComment;
    }

    /**
     * @return The danger
     */
    public int getDanger() {
        return danger;
    }

    /**
     * @param danger The danger
     */
    public void setDanger(int danger) {
        this.danger = danger;
    }


    /**
     * @return The dateComment
     */
    public String getDateComment() {
        return dateComment;
    }

    /**
     * @param dateComment The date_comment
     */
    public void setDateComment(String dateComment) {
        this.dateComment = dateComment;
    }



    /**
     * @return The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return The avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar The avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}