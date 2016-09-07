package com.newstee.model.data;

/**
 * Created by Arnold on 23.08.2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class DataComment {

    @SerializedName("data")
    @Expose
    private Data data;
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
    public Data getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Data data) {
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
    @Generated("org.jsonschema2pojo")
   public static class Data {

        @SerializedName("commentData")
        @Expose
        private List<Comment> comment = new ArrayList<Comment>();
        @SerializedName("commentExtra")
        @Expose
        private CommentExtra commentExtra;

        /**
         *
         * @return
         * The comment
         */
        public List<Comment> getComment() {
            return comment;
        }

        /**
         *
         * @param comment
         * The comment
         */
        public void setComment(List<Comment> comment) {
            this.comment = comment;
        }

        /**
         *
         * @return
         * The commentExtra
         */
        public CommentExtra getCommentExtra() {
            return commentExtra;
        }

        /**
         *
         * @param commentExtra
         * The commentExtra
         */
        public void setCommentExtra(CommentExtra commentExtra) {
            this.commentExtra = commentExtra;
        }

    }
    @Generated("org.jsonschema2pojo")
   public static class CommentExtra {

        @SerializedName("commentCount")
        @Expose
        private int commentCount;
        @SerializedName("commentLimit")
        @Expose
        private int commentLimit;

        /**
         *
         * @return
         * The commentCount
         */
        public int getCommentCount() {
            return commentCount;
        }

        /**
         *
         * @param commentCount
         * The commentCount
         */
        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        /**
         *
         * @return
         * The commentLimit
         */
        public int getCommentLimit() {
            return commentLimit;
        }

        /**
         *
         * @param commentLimit
         * The commentLimit
         */
        public void setCommentLimit(int commentLimit) {
            this.commentLimit = commentLimit;
        }

    }

}
