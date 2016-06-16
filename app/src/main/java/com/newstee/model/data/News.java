package com.newstee.model.data;

/**
 * Created by Arnold on 06.04.2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class News {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nameauthor")
    @Expose
    private Object nameauthor;
    @SerializedName("nametags")
    @Expose
    private Object nametags;
    @SerializedName("idtags")
    @Expose
    private String idtags;
    @SerializedName("linksong")
    @Expose
    private String linksong;
    @SerializedName("likes")
    @Expose
    private String likes;
    @SerializedName("addition_time")
    @Expose
        private String additionTime;
        @SerializedName("linkpicture")
    @Expose
    private String pictureNews;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("idauthor")
    @Expose
    private String idauthor;
    @SerializedName("idsong")
    @Expose
    private String idsong;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("content")
    @Expose
    private String content;

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
     * The nameauthor
     */
    public Object getNameauthor() {
        return nameauthor;
    }

    /**
     *
     * @param nameauthor
     * The nameauthor
     */
    public void setNameauthor(Object nameauthor) {
        this.nameauthor = nameauthor;
    }

    /**
     *
     * @return
     * The nametags
     */
    public Object getNametags() {
        return nametags;
    }

    /**
     *
     * @param nametags
     * The nametags
     */
    public void setNametags(Object nametags) {
        this.nametags = nametags;
    }

    /**
     *
     * @return
     * The idtags
     */
    public String getIdtags() {
        return idtags;
    }

    /**
     *
     * @param idtags
     * The idtags
     */
    public void setIdtags(String idtags) {
        this.idtags = idtags;
    }

    /**
     *
     * @return
     * The linksong
     */
    public String getLinksong() {
        return linksong;
    }

    /**
     *
     * @param linksong
     * The linksong
     */
    public void setLinksong(String linksong) {
        this.linksong = linksong;
    }

    /**
     *
     * @return
     * The likes
     */
    public String getLikes() {
        return likes;
    }
    public void addLike() {
        if(likes == null)
        {
            return;
        }
        int newLikes =0;
        try
        {
            newLikes = Integer.parseInt(likes.trim());
        }
        catch (NumberFormatException e)
        {

        }
        ++newLikes;
        likes = ""+newLikes;
    }
    public void removeLike() {
        if(likes == null)
        {
            return;
        }
        int newLikes =1;
        try
        {
            newLikes = Integer.parseInt(likes.trim());
        }
        catch (NumberFormatException e)
        {

        }
        --newLikes;
        likes = ""+newLikes;
    }
    /**
     *
     * @param likes
     * The likes
     */
    public void setLikes(String likes) {
        this.likes = likes;
    }

    /**
     *
     * @return
     * The additionTime
     */
    public String getAdditionTime() {
        return additionTime;
    }

    /**
     *
     * @param additionTime
     * The addition_time
     */
    public void setAdditionTime(String additionTime) {
        this.additionTime = additionTime;
    }

    /**
     *
     * @return
     * The pictureNews
     */
    public String getPictureNews() {
        return pictureNews;
    }

    /**
     *
     * @param pictureNews
     * The picture_news
     */
    public void setPictureNews(String pictureNews) {
        this.pictureNews = pictureNews;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The idauthor
     */
    public String getIdauthor() {
        return idauthor;
    }

    /**
     *
     * @param idauthor
     * The idauthor
     */
    public void setIdauthor(String idauthor) {
        this.idauthor = idauthor;
    }

    /**
     *
     * @return
     * The idsong
     */
    public String getIdsong() {
        return idsong;
    }

    /**
     *
     * @param idsong
     * The idsong
     */
    public void setIdsong(String idsong) {
        this.idsong = idsong;
    }

    /**
     *
     * @return
     * The category
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category
     * The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }
public ArrayList<String> getIdTags()
{
    ArrayList<String>idTags = new ArrayList<>();
    String addedNewsString = getIdtags();
    String mas[] = addedNewsString.split(",");
    for (int i = 0; i < mas.length; i++) {
     idTags.add(mas[i].trim());
    }
    return  idTags;
}


}