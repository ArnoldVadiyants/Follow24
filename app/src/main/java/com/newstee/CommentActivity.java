package com.newstee;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.newstee.helper.InternetHelper;
import com.newstee.model.data.Comment;
import com.newstee.model.data.DataComment;
import com.newstee.model.data.DataPost;
import com.newstee.model.data.DataStateComment;
import com.newstee.model.data.StateComment;
import com.newstee.model.data.UserLab;
import com.newstee.network.FactoryApi;
import com.newstee.network.interfaces.NewsTeeApiInterface;
import com.newstee.utils.CircleTransform;
import com.newstee.utils.DisplayImageLoaderOptions;
import com.newstee.utils.MPUtilities;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arnold on 23.08.2016.
 */
public class CommentActivity extends AppCompatActivity {
private static final String TAG = "CommentActivity";
    public static final String ARG_NEWS_ID = "newIdForComment";
    public static final int MAX_PER_PAGE = 50;
    private int mPage = 0;
    private boolean isLoading = false;
    private static final int END_TRIGGER = 2;
    private int offset  =0;
    private int count = 10;
    private int totalComments = 0;
    Bitmap defaultAvatar;
    ArrayList<Comment>mComments = new ArrayList<>();
    String mNewsId;
    CommentAdapter mCommentAdapter;
    View mFooter;
    ListView mCommentsListView;
    EditText mCommentEditText;
    ImageButton mSentCommentBtn;
    SwipyRefreshLayout mSwipyRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.text_comment);
        setContentView(R.layout.activity_comment);
        mFooter = getLayoutInflater().inflate(R.layout.comment_feed, null);
        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.activity_comment_swipyrefreshlayout);
        mSwipyRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d(TAG, "Refresh triggered at "
                        + (direction.equals(SwipyRefreshLayoutDirection.TOP) ? "top" : "bottom"));
                if(direction.equals(SwipyRefreshLayoutDirection.TOP))
                {
                    new LoadCommentsTask().execute(LoadComments.LOAD_MORE);
                }
                else
                {
                    new LoadCommentsTask().execute(LoadComments.UPDATE_COMMENTS);
                }
            }
        });
        mNewsId = getIntent().getStringExtra(ARG_NEWS_ID);
        mCommentsListView = (ListView) findViewById(R.id.activity_comment_comments_listView);
        mCommentEditText = (EditText) findViewById(R.id.activity_comment_input_text);
        mSentCommentBtn = (ImageButton) findViewById(R.id.activity_comment_sent_button);
        mSentCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mCommentEditText.getText().toString();
                if(comment == null)
                {
                    return;
                }
                if(comment.isEmpty())
                {
                    return;
                }
                new AddCommentTask(comment).execute();

            }
        });
        mCommentAdapter = new CommentAdapter(this);
        mCommentsListView.setAdapter(mCommentAdapter);
       /* mCommentsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCommentsListView.getCount() != 0
                        && mCommentsListView.getLastVisiblePosition()== mCommentsListView.getCount() - 1 - END_TRIGGER) {
                    // Do what you need to get more content.
                    loadMore();
                }
            }

        });*/
        new LoadCommentsTask().execute(LoadComments.UPDATE_COMMENTS);
    }
  /*  public void updateFragment()
    {

    }*/

    private class ViewHolder {
        public ViewHolder(ImageView avatar, TextView name, TextView date, TextView comment, ImageButton likeBtn, ImageButton dislikeBtn, TextView likeCount, TextView dislikeCount, TextView complaintCount, ImageButton deleteBtn, ImageButton complaintBtn) {
            this.avatar = avatar;
            this.name = name;
            this.date = date;
            this.comment = comment;
            this.likeBtn = likeBtn;
            this.dislikeBtn = dislikeBtn;
            this.likeCount = likeCount;
            this.dislikeCount = dislikeCount;
            this.complaintCount = complaintCount;
            this.complaintBtn = complaintBtn;
            this.deleteBtn = deleteBtn;
        }

        public final ImageView avatar;
        public final TextView name;
        public final TextView date;
        public final TextView comment;
        public final ImageButton likeBtn;
        public final ImageButton dislikeBtn;
        public final TextView likeCount;
        public final TextView dislikeCount;
        public final TextView complaintCount;
        public final ImageButton deleteBtn;
        public final ImageButton complaintBtn;


    }

    private class CommentAdapter extends ArrayAdapter<Comment> {
        ImageView avatar;
        TextView name;
        TextView date;
        TextView comment;
        ImageButton likeBtn;
        ImageButton dislikeBtn;
        TextView likeCount;
        TextView dislikeCount;
        TextView complaintCount;
        ImageButton deleteBtn;
        ImageButton complaintBtn;

        public CommentAdapter(Context context) {
            super(context, R.layout.comment_feed, mComments);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.comment_feed, parent, false);
                avatar = (ImageView) view.findViewById(R.id.comment_feed_avatar);
                name = (TextView) view.findViewById(R.id.comment_feed_name);
                date = (TextView) view.findViewById(R.id.comment_feed_date);
                comment = (TextView) view.findViewById(R.id.comment_feed_text_comment);
                likeBtn = (ImageButton) view.findViewById(R.id.comment_feed_like_image_btn);
                dislikeBtn = (ImageButton) view.findViewById(R.id.comment_feed_dislike_image_btn);
                likeCount = (TextView) view.findViewById(R.id.comment_feed_like_count);
                dislikeCount = (TextView) view.findViewById(R.id.comment_feed_dislike_count);
                complaintCount = (TextView) view.findViewById(R.id.comment_feed_complaint_count);
                deleteBtn = (ImageButton) view.findViewById(R.id.comment_feed_delete_comment_btn);
                complaintBtn = (ImageButton) view.findViewById(R.id.comment_feed_complaint_image_btn);
                view.setTag(new ViewHolder(avatar, name, date, comment, likeBtn, dislikeBtn, likeCount, dislikeCount, complaintCount, deleteBtn, complaintBtn));
            }
            if (holder == null && view != null) {
                Object tag = view.getTag();
                if (tag instanceof ViewHolder) {
                    holder = (ViewHolder) tag;
                }
            }


            final Comment item = getItem(position);
            if (item != null && holder != null) {

            }
              /*  holder.description.setWebViewClient(new WebViewClient());
                holder.description.setWebChromeClient(new WebChromeClient());
                holder.description.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                holder.description.getSettings().setJavaScriptEnabled(true);
                holder.description.getSettings().setPluginState(WebSettings.PluginState.ON);
                holder.description.getSettings().setDefaultFontSize(18);
             //   holder.description.getSettings().setLoadWithOverviewMode(true);
             //   holder.description.getSettings().setUseWideViewPort(true);
                holder.description.loadData(item.getTitle() + '\n' + item.getContent(), "text/html; charset=utf-8", null);
               // holder.description.setOnClickListener(null);
              //  holder.description.setOnTouchListener(null);
             //   holder.description.setOnScrollChangeListener(null);*/
            final Animation animation = AnimationUtils.loadAnimation(
                    CommentActivity.this, R.anim.skale_animation);
            final String commentId = item.getId();

            final ViewHolder finalHolder = holder;
            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(animation);
                    changeCommentState(commentId, Constants.COMMENT_STATE_YES, finalHolder.likeCount,finalHolder.dislikeCount,finalHolder.complaintCount,item);
                }
            });
            holder.dislikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(animation);
                    changeCommentState(commentId, Constants.COMMENT_STATE_NO, finalHolder.likeCount,finalHolder.dislikeCount,finalHolder.complaintCount,item);
                }
            });

            holder.complaintBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(animation);
                    changeCommentState(commentId, Constants.COMMENT_STATE_DANGER, finalHolder.likeCount,finalHolder.dislikeCount,finalHolder.complaintCount,item);
                }
            });
          String comUserId = item.getUserId();
            String userId = UserLab.getInstance().getUser().getId();
            if (comUserId != null && userId != null && comUserId.equals(userId)) {
                holder.deleteBtn.setVisibility(View.VISIBLE);
            }
            else {
                holder.deleteBtn.setVisibility(View.GONE);
            }
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        v.startAnimation(animation);
                                                        deleteComment(commentId);
                                                    }
                                                }
            );
            holder.name.setText(item.getNameComment());
            holder.comment.setText(item.getTextComment());
            holder.likeCount.setText("" + item.getLikeComment());
            holder.dislikeCount.setText("" + item.getDlikeComment());
            holder.complaintCount.setText("" + item.getDanger());
            holder.date.setText(new MPUtilities().getDateOrTimeFormat(item.getDateComment()));
            String authorAvatar = item.getAvatar();
            if (authorAvatar == null)
            {
                authorAvatar = Constants.EMPTY_LINK_IMAGE;
                if(defaultAvatar== null)
                {
                    loadAvatar(authorAvatar,holder.avatar,true);
                }
                else
                {
                    holder.avatar.setImageBitmap(defaultAvatar);
                }
            }
            else
            {
                loadAvatar(authorAvatar,holder.avatar,false);
            }
            return view;
        }
    }
   /* private View.OnClickListener changeStateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeStateClickListener
        }
    }*/
    private void loadAvatar(String link, ImageView imageView, final boolean isDefaultAvatar)
    {
        link = InternetHelper.toCorrectLink(link);
        ImageLoader.getInstance().displayImage(link, imageView, DisplayImageLoaderOptions.getInstance(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage == null) {
                    loadedImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                }
                loadedImage = NewsListFragment.centerCrop(loadedImage);
                loadedImage =  new CircleTransform().transform(loadedImage);
                if(isDefaultAvatar)
                {
                    defaultAvatar = loadedImage;
                }
                ((ImageView) view).setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

    }
    private void deleteComment(final String idComment)
    {
        if(idComment == null)
        {
            return;
        }
        Call<DataPost> delCall = FactoryApi.getInstance(CommentActivity.this).removeComment(idComment);
        delCall.enqueue(new Callback<DataPost>() {
            @Override
            public void onResponse(Call<DataPost> call, Response<DataPost> response) {
                if(response.body().getResult().equals(Constants.RESULT_SUCCESS))
                {
                    Comment removedComment = getComment(idComment);
                    mComments.remove(removedComment);
                    mCommentAdapter.notifyDataSetChanged();
                    totalComments--;
                }
            }

            @Override
            public void onFailure(Call<DataPost> call, Throwable t) {
                Toast.makeText(getApplicationContext(),R.string.msg_error, Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
    private Comment getComment(String commentId)
    {
        for(Comment comment : mComments)
        {
            if(comment.getId().equals(commentId))
            {
                return comment;
            }
        }
        return null;
    }


    private  int updateComments()
    {
        if (mNewsId == null) {
            return mComments.size();
        }
        NewsTeeApiInterface api = FactoryApi.getInstance(CommentActivity.this);

        Call<DataComment> newsC = api.getComments(mNewsId, 0, 10);
        try {
            Response<DataComment> newsR = newsC.execute();
            DataComment.Data data =  newsR.body().getData();
            totalComments = data.getCommentExtra().getCommentCount();
            List<Comment>comments = data.getComment();
            Collections.reverse(comments);
             mComments.clear();
             mComments.addAll(comments);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return mComments.size();
    }
    private int loadComments() {
        if (mNewsId == null) {
            return 0;
        }
        NewsTeeApiInterface api = FactoryApi.getInstance(CommentActivity.this);
        int  offset = mComments.size();
        int count = 0;
        if(offset > 0)
        {
            count =100;
        }
        Call<DataComment> newsC = api.getComments(mNewsId,offset,count);
        try {
            Response<DataComment> newsR = newsC.execute();
            DataComment.Data data =  newsR.body().getData();
            List<Comment>comments = data.getComment();
            int commentCount = data.getCommentExtra().getCommentCount();
            if(commentCount > totalComments)
            {
                int difference = commentCount  -totalComments;
                for(int i =0; i<difference; i++)
                {
                   comments .remove(0);
                }
            }

            Collections.reverse(comments);
            mComments.addAll(0,comments);
            return comments.size();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private void changeCommentState(final String commentId, String commentState, @NonNull final TextView yesView, @NonNull final TextView noView, @NonNull final TextView complaintView, @NonNull final Comment comment)
    {
        if(mSwipyRefreshLayout != null)
        {
           mSwipyRefreshLayout.setRefreshing(true);
        }
        Call<DataStateComment>changeCall = FactoryApi.getInstance(CommentActivity.this).changeCommentState(commentId, commentState);
        changeCall.enqueue(new Callback<DataStateComment>() {
            @Override
            public void onResponse(Call<DataStateComment> call, Response<DataStateComment> response) {
                if(response.body().getResult().equals(Constants.RESULT_SUCCESS))
                {
                    StateComment stateComment = response.body().getData();
                    int yes = stateComment.getYes();
                    int no = stateComment.getNo();
                    int complaint = stateComment.getDanger();
                    comment.setLikeComment(yes);
                    comment.setDlikeComment(no);
                    comment.setDanger(complaint);
                    yesView.setText(""+yes);
                    noView.setText(""+no);
                    complaintView.setText(""+complaint);
                    if(mSwipyRefreshLayout != null)
                    {
                        mSwipyRefreshLayout.setRefreshing(false);
                    }
                }
                else
                {
                    if(mSwipyRefreshLayout != null)
                    {
                        mSwipyRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<DataStateComment> call, Throwable t) {

            }
        });
    }


    private class AddCommentTask extends AsyncTask<Boolean, Integer, Boolean> {
        ProgressDialog dialog;
        String comment;
        AddCommentTask(String comment) {
            this.comment = comment;
            dialog = new ProgressDialog(CommentActivity.this);
            dialog.setMessage("Отправка комментария...");
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {

            NewsTeeApiInterface api = FactoryApi.getInstance(CommentActivity.this);
            String name = UserLab.getInstance().getUser().getUserLogin();
            String avatar = UserLab.getInstance().getUser().getAvatar();
            Call<DataPost> newsC = api.addComment(mNewsId,name,avatar,comment );
            try {
                Response<DataPost> newsR = newsC.execute();

              String result =  newsR.body().getResult();
                if(result.equals(Constants.RESULT_SUCCESS))
                {
                   runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Комментарий добавлен",Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }
                else
                {
                    final String msg = newsR.body().getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                        }
                    });
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean isAdded) {
            super.onPostExecute(isAdded);
            if(isAdded)
            {
                mCommentEditText.setText("");
            }
            dialog.dismiss();
            new LoadCommentsTask().execute(LoadComments.UPDATE_COMMENTS);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }
    }


    private class LoadCommentsTask extends AsyncTask<LoadComments, Boolean, Integer> {
      //  ProgressDialog dialog;
        LoadCommentsTask() {
       //    dialog = new ProgressDialog(CommentActivity.this);
       //     dialog.setContentView();
            //dialog.setMessage("Загрузка комментариев...");

        }



        @Override
        protected Integer doInBackground(LoadComments... params) {
            int count = params.length;
            if(count == 0)
            {
                updateComments();
            }
            else {
                if(params[0].equals(LoadComments.LOAD_MORE))
                {
                    return  loadComments();
                }
                else if(params[0].equals(LoadComments.UPDATE_COMMENTS))
                {
                    return  updateComments();
                }
                else
                {
                    return  updateComments();
                }
            }
           return 0;
        }



        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
          //  mComments.clear();
           // mComments.addAll(comments);
       //     int index = mCommentsListView.getLastVisiblePosition();
         //   View v = mCommentsListView.getChildAt(0);
          //  int top = (v == null) ? 0 : v.getTop();
       //     int position = mCommentsListView.getSelectedItemPosition();
            mCommentAdapter.notifyDataSetChanged();
            mCommentsListView.setSelectionFromTop(result,50);
          //  mCommentsListView.smoothScrollToPosition(result);
       //     mCommentsListView.setSelectionFromTop(index, top);
       //     mCommentsListView.removeFooterView(mFooter);
       //    dialog.dismiss();

            mSwipyRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
       //     mCommentsListView.addFooterView(mFooter);
        //    dialog.show();
            mSwipyRefreshLayout.setRefreshing(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }
    private enum LoadComments
    {
        UPDATE_COMMENTS, LOAD_MORE
    }
}
