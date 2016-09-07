package com.newstee.model.data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class StateComment {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("id_comment")
    @Expose
    private int idComment;
    @SerializedName("ip_user")
    @Expose
    private String ipUser;
    @SerializedName("selected")
    @Expose
    private String selected;
    @SerializedName("yes")
    @Expose
    private int yes;
    @SerializedName("no")
    @Expose
    private int no;
    @SerializedName("danger")
    @Expose
    private int danger;

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
     * The idComment
     */
    public int getIdComment() {
        return idComment;
    }

    /**
     *
     * @param idComment
     * The id_comment
     */
    public void setIdComment(int idComment) {
        this.idComment = idComment;
    }
    /**
     *
     * @return
     * The ipUser
     */
    public String getIpUser() {
        return ipUser;
    }

    /**
     *
     * @param ipUser
     * The ip_user
     */
    public void setIpUser(String ipUser) {
        this.ipUser = ipUser;
    }
    /**
     *
     * @return
     * The selected
     */
    public String getSelected() {
        return selected;
    }

    /**
     *
     * @param selected
     * The selected
     */
    public void setSelected(String selected) {
        this.selected = selected;
    }

    /**
     *
     * @return
     * The yes
     */
    public int getYes() {
        return yes;
    }

    /**
     *
     * @param yes
     * The yes
     */
    public void setYes(int yes) {
        this.yes = yes;
    }

    /**
     *
     * @return
     * The no
     */
    public int getNo() {
        return no;
    }

    /**
     *
     * @param no
     * The no
     */
    public void setNo(int no) {
        this.no = no;
    }
    /**
     *
     * @return
     * The danger
     */
    public int getDanger() {
        return danger;
    }

    /**
     *
     * @param danger
     * The danger
     */
    public void setDanger(int danger) {
        this.danger = danger;
    }
}
