package com.newstee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.newstee.helper.InternetHelper;
import com.newstee.helper.SessionManager;
import com.newstee.model.data.Author;
import com.newstee.model.data.AuthorLab;
import com.newstee.model.data.DataNews;
import com.newstee.model.data.DataPost;
import com.newstee.model.data.News;
import com.newstee.model.data.NewsLab;
import com.newstee.model.data.TagLab;
import com.newstee.model.data.UserLab;
import com.newstee.network.FactoryApi;
import com.newstee.network.interfaces.NewsTeeApiInterface;
import com.newstee.utils.CircleTransform;
import com.newstee.utils.DisplayImageLoaderOptions;
import com.newstee.utils.MPUtilities;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class NewsListFragment extends Fragment {
    public static final int MAX_PER_PAGE = 50;
    public static final int MAX_PER_PAGE_FOR_NEWS_BY_TAG= 15;
    private int mPage = 0;
    private boolean isLoading = false;
    private static final int END_TRIGGER = 2;
    private ArrayList<String> mFilterTagIds;
    private String mCategory;
private boolean firstUpdate = true;
    public  String getArgument() {
        return mArgument;
    }

    private String mArgument = Constants.ARGUMENT_NEWS_NONE;
    private String mArgumentTitle="";
    private String mIdForArgument;
    private SessionManager session;
  //  private ListView mListView;
    private boolean update = false;
    protected static final String ARG_NEWS_BY_ID = "news_by_id";
    protected static final String ARG_CATEGORY = "category";
    protected static final String ARG_PARAMETER = "parameter";
 //  private SwipyRefreshLayout mSwipyRefreshLayout;
     private RecyclerRefreshLayout mSwipyRefreshLayout;

    private RecyclerView mNewsRecyclerView;
private NewsFeedAdapter mNewsFeedAdapter;
    static ImageLoader imageLoader = ImageLoader.getInstance();
    private final static String TAG = "NewsListFragment";
    private List<News> mNews = new ArrayList<>();
    private List<IRVItem>mNewsFiltered = new ArrayList<>();
  //  ItemAdapter adapter;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mArgument.equals(Constants.ARGUMENT_NEWS_BY_TAG))
        {
            return;
        }
        if (isVisibleToUser) {
            Log.d("@@@@@@ " + TAG, isVisibleToUser + "" + " category " + mCategory + " argument " + mArgument);
            if(update)
            {
                Log.d("@@@@@@ "+TAG," isUpdate "+update);
                update = false;
                return;
            }
            if (mNewsFeedAdapter == null) {
                Log.d("@@@@@@ " + TAG," mNewsFeedAdapter = null");
                return;
            }

            updateFragment();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
      if (mNewsFeedAdapter == null) {
            return;
        }
        if(mArgument.equals(Constants.ARGUMENT_NEWS_BY_TAG))
        {
            return;
        }
        updateFragment();
        update=true;
    }
    public ArrayList<String> getFilterTagIds() {
        return mFilterTagIds;
    }

    public void setFilterTagIds(ArrayList<String> filterTagIds) {
        this.mFilterTagIds = filterTagIds;
    }
    public void applyFilter()
    {

        mNewsFiltered.clear();
        if(mFilterTagIds == null ){
            mNewsFiltered.addAll(mNews);

    }
        else
        {
            mNewsFiltered.addAll(NewsLab.getInstance().getNewsByTags(mFilterTagIds,mNews));
        }

        if (mNewsFeedAdapter != null) {
            mNewsFeedAdapter.notifyDataSetChanged();
        }

    }

    public void updateFragment() {
        mNews.clear();
        List<News> news = new ArrayList<>();

        if (mArgument.equals(Constants.ARGUMENT_NEWS_NONE)) {
            mArgumentTitle = getString(R.string.tab_stream);

            news = NewsLab.getInstance().getNews(mCategory);
        } else if (mArgument.equals(Constants.ARGUMENT_NEWS_ADDED)) {
            mArgumentTitle = getString(R.string.tab_play_list);
            news = UserLab.getInstance().getAddedNews();
            //  Collections.sort(news,new NewsComparator());
        } else if (mArgument.equals(Constants.ARGUMENT_NEWS_LIKED)) {
            mArgumentTitle = getString(R.string.my_likes);
           List<News> liked = UserLab.getInstance().getLikedNews();
             news.addAll(liked);
            UserLab.sortNewsReverse(news);
        } else if (mArgument.equals(Constants.ARGUMENT_NEWS_BY_CANAL)) {

        } else if (mArgument.equals(Constants.ARGUMENT_NEWS_BY_STORY)) {
            new LoadNewsByStoryTask().execute();
            if (mIdForArgument != null) {
                News streamNews = NewsLab.getInstance().getNewsItem(mIdForArgument);
                if (streamNews == null) {
                    News addedNews = UserLab.getInstance().getAddedNewsItem(mIdForArgument);
                    if (addedNews == null) {
                        mArgumentTitle = "";
                    } else {
                        mArgumentTitle = addedNews.getTitle();
                    }
                } else {
                    mArgumentTitle = streamNews.getTitle();
                }
            }
            return;
        } else if (mArgument.equals(Constants.ARGUMENT_NEWS_RECOMMENDED)) {
            mArgumentTitle = getString(R.string.car_mode_tab_recommend);
            news = NewsLab.getInstance().getRecommendedNews();
        } else if (mArgument.equals(Constants.ARGUMENT_NEWS_RECENT)) {
            mArgumentTitle = getString(R.string.car_mode_tab_recent);
            List<News> newsInverse = UserLab.getInstance().getRecentNews();
            for (int i = newsInverse.size() - 1; i >= 0; i--) {
                news.add(newsInverse.get(i));
            }
        } else if (mArgument.equals(Constants.ARGUMENT_PLAYLIST)) {
            mArgumentTitle = PlayList.getInstance().getListTitle();
            news = PlayList.getInstance().getNewsList();

        } else if (mArgument.equals(Constants.ARGUMENT_NEWS_BY_TAG))
        {

            if (mIdForArgument != null) {
                mArgumentTitle = TagLab.getInstance().getTag(mIdForArgument).getNameTag();
                mPage = 0;
                mNews.clear();
                new LoadNewsByTagTask().execute();
             /*   List<String> tags = new ArrayList<>();
                tags.add(mIdForArgument);
                news = NewsLab.getInstance().getNewsByTags(tags);
                if(news.size()<MAX_PER_PAGE_FOR_NEWS_BY_TAG)
                {
                    loadMore();
                }*/
            }


        }
        if (mArgument.equals(Constants.ARGUMENT_NEWS_BY_TAG))
        {

        }
       else {
            int count = NewsLab.getInstance().getNews(mCategory).size();
            if (count % MAX_PER_PAGE == 0) {
                mPage = (count / MAX_PER_PAGE) - 1;
            } else {
                mPage = count / MAX_PER_PAGE;
            }
        }
        sortByCategory(news);
        applyFilter();
    }

    public void sortByCategory(List<News> news) {
        NewsLab newsLab = NewsLab.getInstance();
        if(mArgument.equals(Constants.ARGUMENT_NEWS_NONE))
        {
            mNews.addAll(news);
           return;
        }
        if(mCategory.equals(Constants.CATEGORY_ALL))
        {int i =0;
            for (News n : news) {
                i++;
                if(n == null)
                {
                    Log.d(TAG, "@@@@@@@@@@@@@ n = null "+i);
                    continue;
                }
                mNews.add(n);
            }
        }
        else
        {
            int i =0;
            for (News n : news) {
                i++;
                if(n == null)
                {
                    Log.d(TAG, "@@@@@@@@@@@@@ n = null "+i);
                    continue;
                }
                if(n.getCategory() == null)
                {
                    Log.d(TAG, "@@@@@@@@@@@@@ category = null"+i);
                    continue;
                }
                if (n.getCategory().equals(mCategory))
                {
                    mNews.add(n);
                }
            }
        }


           /* int i =0;
            for (News n : news) {
                i++;
                if(n == null)
                {
                    Log.d(TAG, "@@@@@@@@@@@@@ n = null "+i);
                    continue;
                }
                if(n.getCategory() == null)
                {
                    Log.d(TAG, "@@@@@@@@@@@@@ category = null"+i);
                    continue;
                }
                if (n.getCategory().equals(mCategory))
                {
                    mNews.add(n);
                }
            }
        }*/
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session   = new SessionManager(getActivity());
        mCategory = getArguments().getString(ARG_CATEGORY, Constants.CATEGORY_NEWS);
        mArgument = getArguments().getString(ARG_PARAMETER, Constants.ARGUMENT_NEWS_NONE);
        mIdForArgument = getArguments().getString(ARG_NEWS_BY_ID);



    }



    /**
     * The fragment argument representing the section number for this
     * fragment.
     */


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */






   /* class MyAsyncTask extends AsyncTask<String,Integer, NewsLab>

    {
        ProgressDialog pDialog;


        @Override
        protected NewsLab doInBackground(String... params) {
            final Bitmap canalIcon = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Call<NewsLab> call = newsTeeApiInterface.getNews();
            NewsLab newsLab = new NewsLab();
            try {
                Response<NewsLab> response = call.execute();
                newsLab = response.body();
                List<News> news = newsLab.getNews();
                for(int i =0; i<50; i++)
                {
                    for(News n: news)
                    {
                        final Bitmap[] newsAvatar = new Bitmap[1];
                        int likeCount = 0;
                        try
                        {
                            likeCount = Integer.parseInt((n.getLikeCount().trim()));
                        }
                        catch (NumberFormatException nfe)
                        {
                        }

                        items.add(new Item(canalIcon,n.getAvatar() , "Vazgen.com", false, likeCount, 100747, n.getTitle(), Constants.STATUS_NOT_ADDED, n.getAudioIds()));

                        System.out.println("*************");
                        System.out.println("Id " + n.getId() +"avatar "+ n.getAvatar());
                    }
                  *//*      items.add(new Item(canalIcon, newPicture, "Vazgen.com", false, 777, 100747, "In the Democratic contest, Hillary Clinton beat Vermont Senator Bernie Sanders in a tight race in Nevada.", Constants.STATUS_NOT_ADDED));
                        items.add(new Item(canalIcon, newPicture, "UkraineNews", true, 123, 13257, "Will be key ahead of the  Super Tuesday round on 1 March, when a dozen more states make their choice.", Constants.STATUS_NOT_ADDED));
                        items.add(new Item(canalIcon, newPicture, "NightAmerica",false, 100, 351237, "Donald Trump has won the South Carolina primary in the Republican race for president.", Constants.STATUS_WAS_ADDED));
                        items.add(new Item(canalIcon, newPicture, "UkraineNews", true, 123, 13257, "Will be key ahead of the  Super Tuesday round on 1 March, when a dozen more states make their choice.", Constants.STATUS_NOT_ADDED));
                *//*    }
                //      mProgressDialog.dismiss();

            } catch (IOException e) {
                e.printStackTrace();
            }




            return newsLab;
        }

        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();        }

        @Override
        protected void onPostExecute(NewsLab newsLab) {
            super.onPostExecute(newsLab);
            pDialog.dismiss();
            if(adapter!=null)
            {
                adapter.notifyDataSetChanged();
            }

        }
    }*/

   /* private  class Item {
        public final String canalImage;
        public final String newsImage;
        public final String canalTitle;
        public final boolean isLiked;
        public final String likeCount;
        public final long millisecondsDuring;
        public final String description;
        public final int status;
        public final String audioId;


        public Item(String canalImage, String newsImage, String canalTitle,boolean isLiked, String likeCount, long millisecondsDuring, String description, int status, String audioId) {
            this.canalImage = canalImage;
            this.newsImage = newsImage;
            this.canalTitle = canalTitle;
            this.isLiked = isLiked;
            this.likeCount = likeCount;
            this.millisecondsDuring = millisecondsDuring;
            this.description = description;
            this.status = status;
            this.audioId = audioId;
        }
    }
*/
  /*  private class ViewHolder {
        public final LinearLayout newsHeader;
        public final RelativeLayout newsFeed;
        public final ImageView canalImage;
        public final ImageView newsImage;
        public final TextView canalTitle;
        public final ImageView likeView;
        public final TextView likeCount;
        public final TextView time;
        public final TextView description;
        public final ImageButton statusButton;


        public ViewHolder(ImageView canalImage, ImageView newsImage, TextView canalTitle, ImageView likeView, TextView likeCount, TextView time, TextView description, ImageButton statusButton, RelativeLayout newsFeed, LinearLayout newsHeader) {
            this.canalImage = canalImage;
            this.newsImage = newsImage;
            this.canalTitle = canalTitle;
            this.likeView = likeView;
            this.likeCount = likeCount;
            this.time = time;
            this.description = description;
            this.statusButton = statusButton;
            this.newsFeed = newsFeed;
            this.newsHeader = newsHeader;
        }
    }*/
    private class NewsViewHolder extends RecyclerView.ViewHolder {

        public final LinearLayout newsHeader;
        public final RelativeLayout newsFeed;
        public final ImageView canalImage;
        public final ImageView newsImage;
        public final TextView canalTitle;
        public final ImageView likeView;
        public final TextView likeCount;
        public final TextView time;
        public final TextView description;
        public final ImageButton statusButton;


        public NewsViewHolder(View view) {
            super(view);
            newsHeader = (LinearLayout) view.findViewById(R.id.news_header);
            newsFeed = (RelativeLayout) view.findViewById(R.id.news_feed);
            canalImage = (ImageView) view.findViewById(R.id.canal_icon_ImageVew);
            newsImage = (ImageView) view.findViewById(R.id.news_picture_imageView);
            canalTitle = (TextView) view.findViewById(R.id.canal_title_TextView);
            likeView = (ImageView) view.findViewById(R.id.like_ImageView);
            likeCount = (TextView) view.findViewById(R.id.like_count_TextView);
            time = (TextView) view.findViewById(R.id.news_date_TextView);
            description = (TextView) view.findViewById(R.id.news_description_textView);
            statusButton = (ImageButton) view.findViewById(R.id.news_status_ImageButton);


        }

        public void bind(final News item) {

            int textColor = getTextColor();
            int secondaryTextColor = getSecondaryTextColor();
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
           description.setText(item.getTitle());
           description.setTextColor(textColor);
           canalTitle.setTextColor(textColor);
           time.setTextColor(secondaryTextColor);
            int height =description.getHeight();
            int lineHeight =description.getLineHeight();
            if (  height> 0 &&description.getLineHeight() > 0) {
               description.setMaxLines(height / lineHeight);
            }
             /*   if (mCategory.equals(Constants.CATEGORY_STORY)) {
                   newsHeader.setVisibility(View.GONE);
                    imageLoader.displayImage(item.getPictureNews(),newsImage, DisplayImageLoaderOptions.getInstance());
                   newsFeed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        feedCategoryOnClick(item.getId(), item.getTitle());
                        }
                    });
                  *//* description.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            feedCategoryOnClick(item.getId(), item.getTitle());
                        }
                    });*//*
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)newsFeed.getLayoutParams();
                    params.setMargins(0, 0, 52, 0);
                   newsFeed.setLayoutParams(params);
                    FrameLayout.LayoutParams paramsBtn = (FrameLayout.LayoutParams)statusButton.getLayoutParams();
                    paramsBtn.setMargins(0, 0, 0, 0);
                   statusButton.setLayoutParams(paramsBtn);
                } else {*/
            Author author = AuthorLab.getInstance().getAuthor(item.getIdauthor());
            if (author == null) {
                author = new Author();
                author.setAvatar(Constants.EMPTY_LINK_IMAGE);
                author.setId("-1");
                author.setName("No canal");
            }
            String authorAvatar = author.getAvatar();
            String authorName = author.getName();
            if (authorAvatar == null) {
                authorAvatar = Constants.EMPTY_LINK_IMAGE;

            }
            if(authorName == null)
            {
                authorName = "No canal";
            }
            String  imageNews = item.getPictureNews();
            imageNews = InternetHelper.toCorrectLink(imageNews);
            authorAvatar = InternetHelper.toCorrectLink(authorAvatar);
            imageLoader.displayImage(imageNews,newsImage, DisplayImageLoaderOptions.getInstance());
            imageLoader.displayImage(authorAvatar,canalImage, DisplayImageLoaderOptions.getInstance(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (loadedImage == null) {
                        loadedImage = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                    }
                    loadedImage = centerCrop(loadedImage);
                    loadedImage = new CircleTransform().transform(loadedImage);
                    ((ImageView) view).setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            ///  Bitmap bitmapAvatar = imageLoader.loadImageSync(authorAvatar, DisplayImageOptions.createSimple().);
            //   if(bitmapAvatar == null)
            // {
            //     bitmapAvatar = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
            // }

            //   bitmapAvatar = centerCrop(bitmapAvatar);
            //    bitmapAvatar =  new CircleTransform().transform(bitmapAvatar);
            //  canalImage.setImageBitmap(bitmapAvatar);
            //     Picasso.with(getActivity())
            //           .load(item.newsImage)
            //         .into(newsImage);
            //      canalImage.setImageBitmap(item.canalImage);
            //     newsImage.setImageBitmap(item.newsImage);
           canalTitle.setText(authorName);
           likeCount.setText(item.getLikes());
           time.setText(new MPUtilities().getDateOrTimeFormat(item.getAdditionTime()));
     //      newsFeed.setTag(position);
            final String newsIds = (item.getId()).trim();
            Log.d(TAG, "@@@@@@ id = "+newsIds );
           newsFeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedOnClick(newsIds);
                }
            });
                    /*holder.description.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            feedOnClick(newsId);
                        }
                    });*/
            //holder.statusButton.setTag(position);
            setLikeView(likeCount,likeView, UserLab.getInstance().isLikedNews((item.getId()).trim()));

            //  getTime(item.millisecondsDuring)
            //   }//else
            //   statusButton.setTag(position);
            final String newsId = (item.getId()).trim();
           statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (!InternetHelper.getInstance(getActivity()).isOnline()) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(!session.isLoggedIn())
                    {
                        Toast.makeText(getContext(), R.string.msg_auth_pls, Toast.LENGTH_SHORT).show();
                        return;

                    }
               /*     News n = NewsLab.getInstance().getNewsItem(newsId);
                    UserLab userLab = UserLab.getInstance();
                    if(n == null)
                    {
                        userLab.deleteNews(newsId);
                    }
                    else
                    {
                        userLab.addNews(n);
                    }*/
                    int status = Constants.STATUS_NOT_ADDED;
                    UserLab userLab = UserLab.getInstance();
                    if (userLab.isAddedNews(newsId)) {
                        userLab.deleteNews(newsId);
                    }
                    else
                    {
                        userLab.addNews(item);
                        status = Constants.STATUS_WAS_ADDED;
                    }
                    setStatusImageButton((ImageButton) v, status);

                    NewsTeeApiInterface nApi = FactoryApi.getInstance(getActivity());
                    Call<DataPost> call = nApi.addNews(newsId);
                    call.enqueue(new Callback<DataPost>() {
                        @Override
                        public void onResponse(Call<DataPost> call, Response<DataPost> response) {
                            if (response.body().getResult().equals(Constants.RESULT_SUCCESS)) {


                            } else {
                                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DataPost> call, Throwable t) {
                            Toast.makeText(getContext(), "Отсутствует интернет соединение", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });


            int status = Constants.STATUS_NOT_ADDED;
            if (UserLab.getInstance().isAddedNews(newsId)) {
                status = Constants.STATUS_WAS_ADDED;
            }
            setStatusImageButton(statusButton, status);


        }
    }

    private class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context mContext;
        private List<IRVItem> data;

        private static final int TYPE_FEED = 0;
        private static final int TYPE_FOOTER = 1;

        public NewsFeedAdapter(@NonNull List<IRVItem> data) {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_FEED) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, null);
                NewsViewHolder rcv = new NewsViewHolder(layoutView);
                return rcv;
            } else {
                View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.tips_loading, parent, false);
                FooterViewHolder vh = new FooterViewHolder(row);
                return vh;
            }

        }
        @Override
        public int getItemViewType(int position) {
            if (data.get(position) instanceof News) {
                return TYPE_FEED;
            } else if (data.get(position) instanceof Footer) {
                return TYPE_FOOTER;
            }
            else
            {
                return TYPE_FEED;
            }
        }


      /*  @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, null);
            NewsViewHolder rcv = new NewsViewHolder(layoutView);
            return rcv;
        }*/


       @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
           if (holder instanceof NewsViewHolder) {

               final News item =(News) data.get(position);
               ((NewsViewHolder) holder).bind(item);

           }
       }
        @Override
        public int getItemCount() {
            return data.size();
        }
    }
   /* private class ItemAdapter extends ArrayAdapter<News> {
        LinearLayout newsHeader;
        RelativeLayout newsFeed;
        ImageView canalImage;
        ImageView newsImage;
        TextView canalTitle;
        ImageView likeView;
        TextView likeCount;
        TextView time;
        TextView description;
        ImageButton statusButton;

        public ItemAdapter(Context context) {
            super(context, R.layout.news_list_item,mNewsFiltered);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
                newsHeader= (LinearLayout) view.findViewById(R.id.news_header);
                newsFeed = (RelativeLayout) view.findViewById(R.id.news_feed);
                canalImage = (ImageView) view.findViewById(R.id.canal_icon_ImageVew);
                newsImage = (ImageView) view.findViewById(R.id.news_picture_imageView);
                canalTitle = (TextView) view.findViewById(R.id.canal_title_TextView);
                likeView = (ImageView) view.findViewById(R.id.like_ImageView);
                likeCount = (TextView) view.findViewById(R.id.like_count_TextView);
                time = (TextView) view.findViewById(R.id.news_date_TextView);
                description = (TextView) view.findViewById(R.id.news_description_textView);
                statusButton = (ImageButton) view.findViewById(R.id.news_status_ImageButton);
                view.setTag(new ViewHolder(canalImage, newsImage, canalTitle, likeView, likeCount, time, description, statusButton, newsFeed, newsHeader));
            }
            if (holder == null && view != null) {
                Object tag = view.getTag();
                if (tag instanceof ViewHolder) {
                    holder = (ViewHolder) tag;
                }
            }


            final News item = getItem(position);
            if (item != null && holder != null) {
                int textColor = getTextColor();
                int secondaryTextColor = getSecondaryTextColor();
              *//*  holder.description.setWebViewClient(new WebViewClient());
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
             //   holder.description.setOnScrollChangeListener(null);*//*
                holder.description.setText(item.getTitle());
                holder.description.setTextColor(textColor);
                holder.canalTitle.setTextColor(textColor);
                holder.time.setTextColor(secondaryTextColor);
                int height = holder.description.getHeight();
                int lineHeight = holder.description.getLineHeight();
                if (  height> 0 && holder.description.getLineHeight() > 0) {
                    holder.description.setMaxLines(height / lineHeight);
                }
             *//*   if (mCategory.equals(Constants.CATEGORY_STORY)) {
                    holder.newsHeader.setVisibility(View.GONE);
                    imageLoader.displayImage(item.getPictureNews(), holder.newsImage, DisplayImageLoaderOptions.getInstance());
                    holder.newsFeed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        feedCategoryOnClick(item.getId(), item.getTitle());
                        }
                    });
                  *//**//*  holder.description.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            feedCategoryOnClick(item.getId(), item.getTitle());
                        }
                    });*//**//*
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.newsFeed.getLayoutParams();
                    params.setMargins(0, 0, 52, 0);
                    holder.newsFeed.setLayoutParams(params);
                    FrameLayout.LayoutParams paramsBtn = (FrameLayout.LayoutParams) holder.statusButton.getLayoutParams();
                    paramsBtn.setMargins(0, 0, 0, 0);
                    holder.statusButton.setLayoutParams(paramsBtn);
                } else {*//*
                    Author author = AuthorLab.getInstance().getAuthor(item.getIdauthor());
                    if (author == null) {
                        author = new Author();
                        author.setAvatar(Constants.EMPTY_LINK_IMAGE);
                        author.setId("-1");
                        author.setName("No canal");
                    }
                    String authorAvatar = author.getAvatar();
                    String authorName = author.getName();
                    if (authorAvatar == null) {
                        authorAvatar = Constants.EMPTY_LINK_IMAGE;

                    }
                    if(authorName == null)
                    {
                        authorName = "No canal";
                    }
                    String  imageNews = item.getPictureNews();
                    imageNews = InternetHelper.toCorrectLink(imageNews);
                    authorAvatar = InternetHelper.toCorrectLink(authorAvatar);
                    imageLoader.displayImage(imageNews, holder.newsImage, DisplayImageLoaderOptions.getInstance());
                    imageLoader.displayImage(authorAvatar, holder.canalImage, DisplayImageLoaderOptions.getInstance(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (loadedImage == null) {
                                loadedImage = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                            }
                            loadedImage = centerCrop(loadedImage);
                            loadedImage = new CircleTransform().transform(loadedImage);
                            ((ImageView) view).setImageBitmap(loadedImage);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                  ///  Bitmap bitmapAvatar = imageLoader.loadImageSync(authorAvatar, DisplayImageOptions.createSimple().);
                 //   if(bitmapAvatar == null)
                   // {
                   //     bitmapAvatar = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                   // }

                     //   bitmapAvatar = centerCrop(bitmapAvatar);
                    //    bitmapAvatar =  new CircleTransform().transform(bitmapAvatar);
                 //   holder.canalImage.setImageBitmap(bitmapAvatar);
                    //     Picasso.with(getActivity())
                    //           .load(item.newsImage)
                    //         .into( holder.newsImage);
                    //       holder.canalImage.setImageBitmap(item.canalImage);
                    //      holder.newsImage.setImageBitmap(item.newsImage);
                    holder.canalTitle.setText(authorName);
                    holder.likeCount.setText(item.getLikes());
                    holder.time.setText(new MPUtilities().getDateOrTimeFormat(item.getAdditionTime()));
                    holder.newsFeed.setTag(position);
                    final String newsIds = (item.getId()).trim();
                    Log.d(TAG, "@@@@@@ id = "+newsIds );
                    holder.newsFeed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            feedOnClick(newsIds);
                        }
                    });
                    *//*holder.description.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            feedOnClick(newsId);
                        }
                    });*//*
                    //holder.statusButton.setTag(position);
                    setLikeView(holder.likeCount, holder.likeView, UserLab.getInstance().isLikedNews((item.getId()).trim()));

                    //  getTime(item.millisecondsDuring)
                    //   }//else
            //    holder.statusButton.setTag(position);
                final String newsId = (item.getId()).trim();
                holder.statusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (!InternetHelper.getInstance(getActivity()).isOnline()) {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(!session.isLoggedIn())
                        {
                            Toast.makeText(getContext(),"Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
                            return;

                        }
                        News n = NewsLab.getInstance().getNewsItem(newsId);
                        UserLab userLab = UserLab.getInstance();
                        if(n == null)
                        {
                            userLab.deleteNews(newsId);
                        }
                        else
                        {
                            userLab.addNews(n);
                        }
                        int status = Constants.STATUS_NOT_ADDED;
                        if (userLab.isAddedNews(newsId)) {
                            status = Constants.STATUS_WAS_ADDED;
                        }
                        setStatusImageButton((ImageButton) v, status);

                        NewsTeeApiInterface nApi = FactoryApi.getInstance(getActivity());
                        Call<DataPost> call = nApi.addNews(newsId);
                        call.enqueue(new Callback<DataPost>() {
                            @Override
                            public void onResponse(Call<DataPost> call, Response<DataPost> response) {
                                   if (response.body().getResult().equals(Constants.RESULT_SUCCESS)) {


                                } else {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<DataPost> call, Throwable t) {
                                Toast.makeText(getContext(), "Отсутствует интернет соединение", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });


                int status = Constants.STATUS_NOT_ADDED;
                if (UserLab.getInstance().isAddedNews(newsId)) {
                    status = Constants.STATUS_WAS_ADDED;
                }
                setStatusImageButton(holder.statusButton, status);

            }
            return view;
        }

        private View.OnClickListener mClickListenerItem = new View.OnClickListener() {

            public void onClick(View v) {
                if (v.getId() == statusButton.getId()) {
//test
                    Log.d(TAG, "ids");
                } else {

                }

            }
        };

    }*/

    public class Footer implements IRVItem
    {}

  public class FooterViewHolder extends RecyclerView.ViewHolder {
       public FooterViewHolder(View itemView) {
           super(itemView);
       }
   }
    public static Bitmap centerCrop(Bitmap srcBmp ) {
        Bitmap dstBmp;

        if (srcBmp.getWidth() >= srcBmp.getHeight()) {
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }

    private void feedCategoryOnClick(String newsId, String title)
    {
        Intent i = new Intent(getContext(), NewsByArgumentActivity.class);
        i.putExtra(NewsByArgumentActivity.ARG_ID, newsId);
        i.putExtra(NewsByArgumentActivity.ARG_TITLE, title);
        i.putExtra(NewsByArgumentActivity.ARG_SORT_BY_ARGUMENT, Constants.ARGUMENT_NEWS_BY_STORY);
        startActivity(i);

     //   Log.d(TAG, "List_item onCLick" + " " + (v.getTag()));
    }
    private void feedOnClick(String newsId)
    {
        if (!InternetHelper.getInstance(getActivity()).isOnline()) {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
            return;
        }
        PlayList playList =   PlayList.getInstance();
        playList.setNewsList((List<News>)(List<?>) mNewsFiltered, mArgumentTitle);
        playList.setArgument(mArgument);
        // add news if clicked feed
    /*    if(session.isLoggedIn())
        {
            if(!UserLab.getInstance().isAddedNews(newsId))
            {
                UserLab.getInstance().addNews(NewsLab.getInstance().getNewsItem(newsId));
                NewsTeeApiInterface nApi = FactoryApi.getInstance(getActivity());
                Call<DataPost> call = nApi.addNews(newsId);
                call.enqueue(new Callback<DataPost>() {
                    @Override
                    public void onResponse(Call<DataPost> call, Response<DataPost> response) {
                        //    if (response.body().getResult().equals(Constants.RESULT_SUCCESS)) {
                        //     } else {
                        //        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        //         UserLab.getInstance().addNews(NewsLab.getInstance().getNewsItem(newsId));
                        // }
                                       *//*     int status = Constants.STATUS_NOT_ADDED;
                                            if (UserLab.getInstance().isAddedNews(newsId)) {
                                                status = Constants.STATUS_WAS_ADDED;
                                            }
                                            setStatusImageButton(statusButton, status);
                                            Intent i = new Intent(getActivity(), MediaPlayerFragmentActivity.class);
                                            i.putExtra(MediaPlayerFragmentActivity.ARG_AUDIO_ID, newsId);
                                            startActivity(i);
*//*
                    }

                    @Override
                    public void onFailure(Call<DataPost> call, Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        //      UserLab.getInstance().addNews(NewsLab.getInstance().getNewsItem(newsId));

                    }
                });

            }
                              *//*  else
                                {
                                  //  PlayList.getInstance().setNewsList(UserLab.getInstance().getAddedNewsAndArticles());
                                    Intent i = new Intent(getActivity(), MediaPlayerFragmentActivity.class);
                                   i.putExtra(MediaPlayerFragmentActivity.ARG_AUDIO_ID, newsId);
                                    startActivity(i);
                                }*//*
        }*/
        Activity activity = getActivity();
        if (activity instanceof PlaylistActivity) {
            Intent intent = new Intent();
            intent.putExtra(PlaylistActivity.DATA_EXTRA_NEWS_ID,newsId);
            ((PlaylistActivity) activity).setResultClick(intent);
        } else {

            Intent i = new Intent(activity, MediaPlayerFragmentActivity.class);
            i.putExtra(MediaPlayerFragmentActivity.ARG_AUDIO_ID, newsId);
            startActivity(i);
        }

    }
    private List<News> loadNewsByTag()
    {
        if(mIdForArgument == null)
        {
            Log.d(TAG,"@@@@@ idStory = "+ null);
            return new ArrayList<>();
        }
       // String countryValue = new SessionManager(getActivity()).getCountrySettings();
        NewsTeeApiInterface api = FactoryApi.getInstance(getActivity());
        Call<DataNews> newsC = api.getNewsByTag(mIdForArgument,MAX_PER_PAGE,mPage/*,countryValue*/);
        Log.d(TAG,"@@@@@ idStory = "+ mIdForArgument);
        try {
            Response<DataNews>  newsR = newsC.execute();

            return   newsR.body().getNews();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
private List<News> loadNewsByStory()
{
    if(mIdForArgument == null)
    {
        Log.d(TAG,"@@@@@ idStory = "+ null);
        return new ArrayList<>();
    }
   // String countryValue = new SessionManager(getActivity()).getCountrySettings();
    NewsTeeApiInterface api = FactoryApi.getInstance(getActivity());
    Call<DataNews> newsC = api.getNewsByStory(mIdForArgument/*,countryValue*/);
    Log.d(TAG,"@@@@@ idStory = "+ mIdForArgument);
    try {
        Response<DataNews>  newsR = newsC.execute();

    return   newsR.body().getNews();


    } catch (IOException e) {
        e.printStackTrace();
    }
    return new ArrayList<>();
}
 /*   private List<News> loadNewsRecommended()
    {

        NewsTeeApiInterface api = FactoryApi.getInstance(getActivity());
        Call<DataIds> idsC = api.getRecommended();
        try {
            Response<DataIds>  idsR = idsC.execute();
          String ids =   idsR.body().getData();

            if(ids == null)
            {

                return new ArrayList<>();
            }
            if(ids.equals(""))
            {
                return new ArrayList<>();
            }
            Call<DataNews> newsC = api.getNewsByIds(ids);
            try {
                Response<DataNews>  newsR = newsC.execute();
                return newsR.body().getNews();
            } catch (IOException e) {
                e.printStackTrace();
            }
            } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }*/


    public String getTime(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    abstract void setStatusImageButton(ImageButton statusImageButton, final int newsStatus);

  /*  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }*/

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        mSwipyRefreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.fragment_refreshlayout);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        mSwipyRefreshLayout.setRefreshView(new MaterialRefreshView(getActivity()), layoutParams);      //  mSwipyRefreshLayout.
        mSwipyRefreshLayout.setRefreshStyle(RecyclerRefreshLayout.RefreshStyle.NORMAL);

        //  mSwipyRefreshLayout.setAnimateToRefreshInterpolator(new );

      /*  mSwipyRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);*/
        mNewsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
        mNewsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mNewsRecyclerView.setLayoutManager(mLayoutManager);
        mNewsFeedAdapter = new NewsFeedAdapter(mNewsFiltered);
        mNewsRecyclerView.setAdapter(mNewsFeedAdapter);
      //  mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
        //mSwipyRefreshLayout.
        mNewsRecyclerView.addOnScrollListener(new AutoLoadEventDetector());
        mSwipyRefreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String argument;
                if(!mCategory.equals(Constants.CATEGORY_ALL))
                {
                    argument = mCategory;
                }
                else
                {
                    argument = mArgument;
                }
                new MainLoadAsyncTask(getActivity()) {
                    @Override
                    void hideContent() {
                        mSwipyRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    void showContent() {
                        updateFragment();
                        mSwipyRefreshLayout.setRefreshing(false);
                    }
                }.execute(Constants.ARGUMENT_AUTHORS, argument);
            }
        });
/*
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d(TAG, "Refresh triggered at "
                        + (direction.equals(SwipyRefreshLayoutDirection.TOP) ? "top" : "bottom"));
                if(direction.equals(SwipyRefreshLayoutDirection.TOP))
                {
                    String argument;
                    if(!mCategory.equals(Constants.CATEGORY_ALL))
                    {
                        argument = mCategory;
                    }
                    else
                    {
                        argument = mArgument;
                    }
                    new MainLoadAsyncTask(getActivity()) {
                        @Override
                        void hideContent() {
                            mSwipyRefreshLayout.setRefreshing(true);
                        }

                        @Override
                        void showContent() {
                            updateFragment();
                            mSwipyRefreshLayout.setRefreshing(false);
                        }
                    }.execute(Constants.ARGUMENT_AUTHORS, argument);
                }
                else
                {
                    loadMore();
                }
            }
        });*/
        if(mArgument.equals(Constants.ARGUMENT_NEWS_BY_TAG))
        {
            updateFragment();
        }
            return view;
        }
   /* @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ItemAdapter(getActivity());
        setListAdapter(adapter);
        mListView = getListView();
       *//* TextView     eptyTextView = (TextView) getListView().getEmptyView();
        //    TextView tv = (TextView)view.findViewById(R.id.empty);
        eptyTextView.setText(getEmpty());
        eptyTextView.setTextColor(getTextColor());*//*

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                                            @Override
                                              public void onScrollStateChanged(AbsListView view, int scrollState) {

                                              }

                                              @Override
                                              public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                                  if (mListView.getCount() != 0
                                                          && mListView.getLastVisiblePosition()== mListView.getCount() - 1 - END_TRIGGER) {
                                                      // Do what you need to get more content.
                                                      loadMore();
                                                  }
                                              }

        });
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String argument;
                if(!mCategory.equals(Constants.CATEGORY_ALL))
                {
                    argument = mCategory;
                }
                else
                {
                    argument = mArgument;
                }
                new MainLoadAsyncTask(getActivity()) {
                    @Override
                    void hideContent() {
                        setRefreshing(true);
                    }

                    @Override
                    void showContent() {
                        updateFragment();
                        setRefreshing(false);
                    }
                }.execute(Constants.ARGUMENT_AUTHORS, argument);

                //Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
              *//*  if (!UserLab.getInstance().isUpdated()) {

                    new MainLoadAsyncTask(getActivity()) {
                        @Override
                        void hideContent() {
                        }

                        @Override
                        void showContent() {
                            updateFragment();
                            setRefreshing(false);
                        }
                    }.execute();
                    return;
                }*//*
                *//*final NewsTeeApiInterface api = FactoryApi.getInstance(getActivity());
                if (mArgument.equals(Constants.ARGUMENT_NEWS_NONE)) {
                    mPage = 0;
                 updateAuthors();
                    Call<DataNews> newsC = api.getNews(MAX_PER_PAGE, mPage);
                    newsC.enqueue(new Callback<DataNews>() {
                        @Override
                        public void onResponse(Call<DataNews> call, Response<DataNews> response) {
                            NewsLab.getInstance().setNews(response.body().getNews());
                            updateFragment();
                            setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Call<DataNews> call, Throwable t) {
                            t.printStackTrace();
                            updateFragment();
                            setRefreshing(false);
                        }
                    });
                } else if (mArgument.equals(Constants.ARGUMENT_NEWS_ADDED)) {
                    updateAuthors();
                 //   Call<DataNews>dataNewsCall = api.
                } else if (mArgument.equals(Constants.ARGUMENT_NEWS_RECOMMENDED)) {
                    updateAuthors();
                    final Call<DataIds> idsC = api.getRecommended();
                    idsC.enqueue(new Callback<DataIds>() {
                        @Override
                        public void onResponse(Call<DataIds> call, Response<DataIds> response) {

                            String ids = response.body().getData();

                            if (ids != null) {
                                if (!ids.equals("")) {

                                    Call<DataNews> newsRecC = api.getNewsByIds(ids);
                                    newsRecC.enqueue(new Callback<DataNews>() {
                                        @Override
                                        public void onResponse(Call<DataNews> call, Response<DataNews> response) {
                                            NewsLab.getInstance().setRecommendedNews(response.body().getNews());
                                            updateFragment();
                                            setRefreshing(false);
                                        }

                                        @Override
                                        public void onFailure(Call<DataNews> call, Throwable t) {
                                            updateFragment();
                                            setRefreshing(false);
                                        }
                                    });

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<DataIds> call, Throwable t) {
                            updateFragment();
                            setRefreshing(false);
                        }
                    });
                } else {
                            updateFragment();
                            setRefreshing(false);
                        }

*//*

            }
        });

        setColorScheme(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        if(mArgument.equals(Constants.ARGUMENT_NEWS_BY_TAG))
        {
            updateFragment();
        }
    }*/
   public class AutoLoadEventDetector extends RecyclerView.OnScrollListener {

       @Override
       public void onScrolled(RecyclerView view, int dx, int dy) {
           RecyclerView.LayoutManager manager = view.getLayoutManager();
           if (manager.getChildCount() > 0) {
               int count = manager.getItemCount();
               int last = ((RecyclerView.LayoutParams) manager
                       .getChildAt(manager.getChildCount() - 1).getLayoutParams()).getViewAdapterPosition();

               if (last == count - 1) {
                  loadMore();
                //   showFooter();
               }
           }
       }
   }
    public void showFooter()
    {
        mNewsFiltered.add(new Footer());
        mNewsRecyclerView.getAdapter().notifyItemInserted(mNewsFiltered.size() - 1);
    }
    public void hideFooter()
    {
        int size = mNewsFiltered.size();
        mNewsFiltered.remove(size - 1); //elimina el footer
        mNewsFiltered.addAll(mNewsFiltered);
        mNewsRecyclerView.getAdapter().notifyItemRangeChanged(size - 1, mNewsFiltered.size() - size);
    }
    private boolean hasFooter() {
        return mNewsFiltered.get(mNewsFiltered.size() - 1) instanceof Footer;
    }

    public void loadMore()
    {
        Log.d(TAG, "@@@@@ LoadMore");
        if(isLoading)
        {
            return;
        }

        int count = mNews.size();
        Log.d(TAG,"@@@@@ count = "+ count);
        Log.d(TAG,"@@@@@ page = "+mPage);
        if(count < (MAX_PER_PAGE*(mPage+1)))
        {
            return;
        }
        mPage++;
        Log.d(TAG, "@@@@@ Loading");
        if (mArgument.equals(Constants.ARGUMENT_NEWS_NONE)) {
            isLoading = true;
            showFooter();
           // String countryValue = new SessionManager(getActivity()).getCountrySettings();
            final NewsTeeApiInterface api = FactoryApi.getInstance(getActivity());
                Call<DataNews> newsC = api.getNewsByType(mCategory,MAX_PER_PAGE, mPage/*,countryValue*/);
                newsC.enqueue(new Callback<DataNews>() {
                    @Override
                    public void onResponse(Call<DataNews> call, Response<DataNews> response) {
                        List<News> loadedNews = response.body().getNews();
                        if (!loadedNews.isEmpty()) {
                            NewsLab newsLab = NewsLab.getInstance();
                            newsLab.addNewses(loadedNews, mCategory);

                            updateFragment();

                        }
                        if (mSwipyRefreshLayout != null) {
                            hideFooter();
                        }
                        isLoading = false;
                    }

                    @Override
                    public void onFailure(Call<DataNews> call, Throwable t) {
                        t.printStackTrace();
                        hideFooter();
                        isLoading = false;
                    }
                });


        } else if (mArgument.equals(Constants.ARGUMENT_NEWS_BY_TAG)) {
            new LoadNewsByTagTask(LoadNewsByTagTask.TYPE_LOAD_MORE).execute();
        } else {
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    abstract String getEmpty();
    abstract int getSecondaryTextColor();
    abstract int getTextColor();

    abstract void setLikeView(TextView likeTextView, ImageView likeImageView, boolean isLiked);
  /*  private void updateAuthors()
    {
        Call<DataAuthor>authorCall = FactoryApi.getInstance(getActivity()).getAuthors();
        authorCall.enqueue(new Callback<DataAuthor>() {
            @Override
            public void onResponse(Call<DataAuthor> call, Response<DataAuthor> response) {
                AuthorLab.getInstance().setAuthors(response.body().getData());
            }

            @Override
            public void onFailure(Call<DataAuthor> call, Throwable t) {

            }
        });
    }*/
  public class HeaderDecoration extends RecyclerView.ItemDecoration {

      private View mLayout;

      public HeaderDecoration(final Context context, RecyclerView parent, @LayoutRes int resId) {
          // inflate and measure the layout
          mLayout = LayoutInflater.from(context).inflate(resId, parent, false);
          mLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                  View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
      }


      @Override
      public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
          super.onDraw(c, parent, state);
          // layout basically just gets drawn on the reserved space on top of the first view
          mLayout.layout(parent.getLeft(), 0, parent.getRight(), mLayout.getMeasuredHeight());
          for (int i = 0; i < parent.getChildCount(); i++) {
              View view = parent.getChildAt(i);
              if (parent.getChildAdapterPosition(view) == 0) {
                  c.save();
                  final int height = mLayout.getMeasuredHeight();
                  final int top = view.getTop() - height;
                  c.translate(0, top);
                  mLayout.draw(c);
                  c.restore();
                  break;
              }
          }
      }

      @Override
      public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
          if (parent.getChildAdapterPosition(view) == 0) {
              outRect.set(0, mLayout.getMeasuredHeight(), 0, 0);
          } else {
              outRect.setEmpty();
          }
      }
  }
  public class LoadNewsByTagTask extends AsyncTask<Boolean,Integer, List<News> >
  {
     // public static final String TYPE_WITH_DIALOG = "with_dialog";
      public static final String TYPE_LOAD_MORE = "load_more";
      public static final String TYPE_UPDATE = "update";
private String loadType;

      ProgressDialog dialog;
      LoadNewsByTagTask()
      {

      }
      LoadNewsByTagTask(String loadType)
      {
          this.loadType = loadType;
        /*  if(loadType != null&&loadType.equals(TYPE_LOAD_MORE))
          {

          }
          else if(loadType.equals(TYPE_LOAD_MORE))
          {

          }
          else if(loadType.equals())
          this.loadType = loadType;
        dialog  = new ProgressDialog(getActivity());

          dialog.setCancelable(false);
          if(mArgumentTitle != null&&!mArgument.isEmpty())
          {
              dialog.setMessage(mArgumentTitle);
          }
*/
      }

      @Override
      protected List<News> doInBackground(Boolean... params) {

          return  loadNewsByTag();
      }



      @Override
      protected void onPostExecute(List<News> newses) {
          super.onPostExecute(newses);

      //
          isLoading = false;
          //   mNews.clear();
          mNews.addAll(newses);
          applyFilter();
         /* if(firstUpdate)
          {
              firstUpdate = false;
              dialog.dismiss();
          }*/
   //       dialog.dismiss();
          if(loadType != null&&loadType.equals(TYPE_LOAD_MORE))
          {
              hideFooter();
          }
          else
          {
              mSwipyRefreshLayout.setRefreshing(false);
          }
      }

      @Override
      protected void onPreExecute() {
          super.onPreExecute();
            if(loadType != null&&loadType.equals(TYPE_LOAD_MORE))
          {
              showFooter();
          }
          else
          {
              mSwipyRefreshLayout.setRefreshing(true);
          }

     /*
          if(firstUpdate)
          {
              dialog.show();
          }*/
          isLoading = true;
          //    dialog.show();
      }


  }

private class LoadNewsByStoryTask extends AsyncTask<Boolean,Integer, List<News> >
{
    ProgressDialog dialog;
    LoadNewsByStoryTask()
    {
        dialog  = new ProgressDialog(getActivity());
        dialog.setMessage("");
    }

    @Override
    protected List<News> doInBackground(Boolean... params) {

        return  loadNewsByStory();
    }



    @Override
    protected void onPostExecute(List<News> newses) {
        super.onPostExecute(newses);
        mNews.clear();
        mNews.addAll(newses);
        applyFilter();
        dialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }


}


/*
    private class LoadNewsRecommendedTask extends AsyncTask<Boolean,Integer, List<News> >
    {
        ProgressDialog dialog;
        LoadNewsRecommendedTask()
        {
            Log.d(TAG,"loadNewsRecommended");
        *//*    dialog  = new ProgressDialog(getActivity());
            dialog.setMessage("");
            dialog.*//*
        }

        @Override
        protected List<News> doInBackground(Boolean... params) {

            return  loadNewsRecommended();
        }



        @Override
        protected void onPostExecute(List<News> newses) {
            super.onPostExecute(newses);
            mNews.clear();
            sortByCategory(newses);
            applyFilter();
            Log.d(TAG, "onPost");
        //    dialog.dismiss();
        }
        //
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPre");

      //      dialog.show();
        }


    }*/
}
