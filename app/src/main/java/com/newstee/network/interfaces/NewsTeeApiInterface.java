package com.newstee.network.interfaces;

import android.support.annotation.Nullable;

import com.newstee.model.data.DataAuthor;
import com.newstee.model.data.DataIds;
import com.newstee.model.data.DataNews;
import com.newstee.model.data.DataPost;
import com.newstee.model.data.DataTag;
import com.newstee.model.data.DataUpdateUser;
import com.newstee.model.data.DataUserAuthentication;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Arnold on 05.04.2016.
 */
public interface NewsTeeApiInterface {
    String BASE_URL = "http://213.231.4.68/music-web/app/php/";


    @GET("dispatcher.php?command=get_tags")
    Call<DataTag> getTags();

    @GET("dispatcher.php?command=get_authors")
    Call<DataAuthor> getAuthors();

    @GET("dispatcher.php?command=get_recommended")
    Call<DataIds> getRecommended();

    @GET("dispatcher.php?command=get_news")
    Call<DataNews> getNews();

    @FormUrlEncoded
    @POST("dispatcher.php?command=get_news")
    Call<DataNews> getNewsByIds(@Field("ids") String ids);
    @FormUrlEncoded
    @POST("dispatcher.php?command=get_news&sort=false")
    Call<DataNews> getNewsByIdsNoSort(@Field("ids") String ids);

    @FormUrlEncoded
    @POST("dispatcher.php?command=get_news")
    Call<DataNews> getNewsByAuthor(@Field("id_author") String id);

    @FormUrlEncoded
    @POST("dispatcher.php?command=get_news")
    Call<DataNews> getNewsByStory(@Field("idstory") String id);

    //   @GET("dispatcher.php?command=get_audio")
    //   Call<DataAudio> getAudio();
  /*  @FormUrlEncoded
    @POST("dispatcher.php?command=adding_tags_to_user")
    Call<DataPost> addTags(@Field("idtag") String idNews);*/

    @FormUrlEncoded
    @POST("dispatcher.php?command=subscribe")
    Call<DataPost> subscribe(@Field("tag_id") String idTag);
    @FormUrlEncoded
    @POST("dispatcher.php?command=unsubscribe")
    Call<DataPost> unsubscribe(@Field("tag_id") String idTag);

    @FormUrlEncoded
    @POST("dispatcher.php?command=like_news")
    Call<DataPost> likeNews(@Field("idnews") String idNews);

    @FormUrlEncoded
    @POST("dispatcher.php?command=add_to_playlist")
    Call<DataPost> addNews(@Field("idnews") String idNews);

    @FormUrlEncoded
    @POST("dispatcher.php?command=user_registration")
    Call<DataPost> createUser(@Field("username") String username, @Field("password") String password, @Field("repassword") String reenter_password, @Field("email") String email, @Field("language") String language);

    @FormUrlEncoded
    @POST("dispatcher.php?command=user_registration")
    Call<DataPost> createUserViaSocialNetwork(@Nullable @Field("gpid") String gId, @Nullable @Field("fbid") String fbId, @Nullable @Field("vkid") String vkId, @Nullable @Field("twid") String twId, @Field("language") String language);

    @FormUrlEncoded
    @POST("dispatcher.php?command=authentication")
    Call<DataUserAuthentication> signIn(@Nullable @Field("gpid") String gId,@Nullable @Field("fbid") String fbId,@Nullable @Field("vkid") String vkId,@Nullable @Field("twid") String twId, @Field("language") String language);

    @FormUrlEncoded
    @POST("dispatcher.php?command=authentication")
    Call<DataUserAuthentication> signIn(@Field("user_email") String email, @Field("user_password") String password, @Field("language") String language);

    @GET("dispatcher.php?command=logout")
    Call<DataPost> logOut();

    @FormUrlEncoded
    @POST("dispatcher.php?command=add_user_key")
    Call<DataPost> add_user_key(@Field("key")String key );
    @Multipart
    @POST("dispatcher.php?command=update_user")
    Call<DataUpdateUser> update_user(@Part("username") RequestBody username, @Part("email") RequestBody email, @Part("avatar\"; filename=\"pp.png\"") RequestBody avatar);


    //  @GET("your_endpoint") Call<YOUR_POJO> getWeather(@Query("from") String from);


}
