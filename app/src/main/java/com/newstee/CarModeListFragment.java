package com.newstee;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.newstee.helper.InternetHelper;
import com.newstee.model.data.News;
import com.newstee.model.data.NewsLab;
import com.newstee.model.data.UserLab;
import com.newstee.utils.DisplayImageLoaderOptions;
import com.newstee.utils.MPUtilities;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnold on 17.02.2016.
 */
public class CarModeListFragment extends Fragment implements  SeekBar.OnSeekBarChangeListener{
    private final static String TAG = "CarModeListFragment";
    private View mediaPlayer;
    private LinearLayout startButton;
    private    ViewPager mViewPager;
    private int newSongValue = -1;
    private ImageButton mpBtnPlay;
    private TextView mpTitle;
    private ImageView mpPicture;
    private SeekBar mpDuring;
    private boolean mpPlayingValue = false;
    private MPUtilities utils;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Handler mHandler = new Handler();
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    private int mSection = 0;
    private String mArgument = Constants.ARGUMENT_NEWS_NONE;
ImageView icon;
    TextView title;
    private PlayListPager mPlayListPager;
    public static CarModeListFragment newInstance(int sectionNumber) {
        CarModeListFragment fragment = new CarModeListFragment();
        Bundle args = new Bundle();
        args.putInt(CarModeListActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public CarModeListFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new MPUtilities();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPlayListPager = new PlayListPager(getChildFragmentManager());
        View rootView = inflater.inflate(R.layout.fragment_car_mode, container, false);
        mediaPlayer = rootView.findViewById(R.id.car_mode_list_media_player);
        mpBtnPlay = (ImageButton) mediaPlayer.findViewById(R.id.media_player_small_play_button);
        mpTitle = (TextView) mediaPlayer.findViewById(R.id.media_player_small_title_TextView);
        mpTitle.setOnClickListener(mediaPlayerClickListener);
        mpPicture =  (ImageView) mediaPlayer.findViewById(R.id.media_player_small_picture_ImageView);
        mpPicture.setOnClickListener(mediaPlayerClickListener);
        mpDuring = (SeekBar)mediaPlayer.findViewById(R.id.media_player_small_during_seekBar);
        mpBtnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (musicBound) {
                    if (musicSrv != null) {
                        if (musicSrv.isPlaying()) {

                            musicSrv.pausePlayer();
                            mpBtnPlay.setImageResource(R.drawable.ic_media_play_car_mode);
                        } else {
                            // Resume song
                            musicSrv.go();
                            // Changing button image to pause button
                            mpBtnPlay.setImageResource(R.drawable.ic_media_pause_car_mode);

                        }
                    }
                }


            }
        });

        icon =(ImageView) rootView.findViewById(R.id.car_mode_list_icon);
        title =(TextView) rootView.findViewById(R.id.car_mode_list_title);
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = getResources().getColor(android.R.color.white, null);
        }
        else {
            color = getResources().getColor(android.R.color.white);
        }
        mpDuring.getProgressDrawable().setColorFilter(
                color, PorterDuff.Mode.SRC_IN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mpDuring.getThumb().mutate().setAlpha(0);
        }
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mpDuring.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } */

        mpDuring.setOnSeekBarChangeListener(this);
        mediaPlayer.setVisibility(View.GONE);
        mSection= getArguments().getInt(CarModeListActivity.ARG_SECTION_NUMBER);

        switch (mSection)
        {
            case CarModeListActivity.TAB_STREAM:
            {
                mArgument = Constants.ARGUMENT_NEWS_NONE;
                icon.setImageResource(R.drawable.stream_clicked);
                title.setText(getResources().getText(R.string.tab_stream));
                break;
            }
            case CarModeListActivity.TAB_MY_PLAYLIST:
            {
                mArgument = Constants.ARGUMENT_NEWS_ADDED;
                icon.setImageResource(R.drawable.play_clicked);
                title.setText(getResources().getText(R.string.tab_play_list));
                break;
            }
            case CarModeListActivity.TAB_RECENT:
            {
                mArgument = Constants.ARGUMENT_NEWS_RECENT;
                icon.setImageResource(R.drawable.ic_clock);
                title.setText(getResources().getText(R.string.car_mode_tab_recent));
                break;
            }
            case CarModeListActivity.TAB_RECOMMEND:
            {
                mArgument = Constants.ARGUMENT_NEWS_RECOMMENDED;
                icon.setImageResource(R.drawable.ic_star);
                title.setText(getResources().getText(R.string.car_mode_tab_recommend));
                break;
            }
        }

 startButton = (LinearLayout) rootView.findViewById(R.id.car_mode_list_start_button);
        ImageButton exitBtn = (ImageButton)rootView.findViewById(R.id.car_mode_list_exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mViewPager = (ViewPager)rootView.findViewById(R.id.car_mode_list_container);
        mViewPager.setAdapter(mPlayListPager);
        mViewPager.setCurrentItem(0);

        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.car_mode_list_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        startButton.setOnClickListener(new View.OnClickListener() {
            String argumentTitle = "";
            @Override
            public void onClick(View v) {

                String category = null;
                if (mViewPager.getCurrentItem() == 0) {
                    category = Constants.CATEGORY_NEWS;
                } else if (mViewPager.getCurrentItem() == 1) {
                    category = Constants.CATEGORY_ARTICLE;
                }
                if (category == null) {
                    return;
                }
                List<News> news = new ArrayList<News>();
                switch (mArgument) {
                    case Constants.ARGUMENT_NEWS_NONE: {
                        argumentTitle = getString(R.string.tab_stream);
                        news.addAll(NewsLab.getInstance().getNewsAndArticles());
                        break;
                    }
                    case Constants.ARGUMENT_NEWS_ADDED: {
                        argumentTitle = getString(R.string.tab_play_list);
                        news.addAll(UserLab.getInstance().getAddedNews());
                        break;
                    }
                    case Constants.ARGUMENT_NEWS_RECENT: {
                        argumentTitle = getString(R.string.car_mode_tab_recent);
                        List<News> newsInverse = UserLab.getInstance().getRecentNews();
                        for (int i = newsInverse.size() - 1; i >= 0; i--) {
                            news.add(newsInverse.get(i));
                        }
                        break;
                    }
                    case Constants.ARGUMENT_NEWS_RECOMMENDED:
                    {
                        argumentTitle = getString(R.string.car_mode_tab_recommend);
                        news.addAll(NewsLab.getInstance().getRecommendedNews());
                        break;
                    }
                }
                List<News> newsList = new ArrayList<News>();
                for (News n : news) {
                    if (n.getCategory().equals(category)) {
                        newsList.add(n);
                    }
                }
                if (newsList.isEmpty()) {
                    return;
                }
                PlayList playList = PlayList.getInstance();
                playList.setNewsList(newsList, argumentTitle);
                playList.setArgument(mArgument);
                Intent i = new Intent(getActivity(), MediaPlayerFragmentActivity.class);
                i.putExtra(MediaPlayerFragmentActivity.ARG_AUDIO_ID, newsList.get(0).getId());
                startActivity(i);

            }

        });
/*

        FragmentManager fm = getChildFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.car_mode_list_content);
            if (fragment == null) {
                fragment = new CarModeNewsListFragment();
                fm.beginTransaction().add(R.id.car_mode_list_content, fragment)
                        .commit();
            }
*/



        return rootView;
    }
    private View.OnClickListener mediaPlayerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), MediaPlayerFragmentActivity.class);
            startActivity(i);
        }
    };
    @Override
    public void onStart() {
        super.onStart();
        connectService();
    }


    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "******onServiceConnected*****");
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass catalogue
            musicBound = true;
            if(musicSrv == null)
            {
                return;
            }
            if(musicSrv.isPlaying())
            {
                mediaPlayer.setVisibility(View.VISIBLE);
            }
            //   updateMPNews();
        }

        /*
            @Override
            protected void onStart() {
                super.onSt
                connectService();
            }*/
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    public void connectService() {
        if (playIntent == null) {
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }

    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = musicSrv.getDur();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        musicSrv.seek(currentPosition);

        // updateFragment timer progress again
        updateMediaPlayer();
    }
    public void updateMediaPlayer() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if(musicSrv != null) {
                if (!musicSrv.isNullPlayer()) {
                    if (!musicSrv.isPaused()) {

                        long totalDuration = musicSrv.getDur();
                        long currentDuration = musicSrv.getPosn();
                        if (newSongValue != musicSrv.getNewSongValue()) {
                            mpTitle.setText(musicSrv.getSongTitle());
                            String pictureUrl = musicSrv.getNewsPictureUrl();
                            pictureUrl = InternetHelper.toCorrectLink(pictureUrl);
                            imageLoader.displayImage(pictureUrl,mpPicture, DisplayImageLoaderOptions.getInstance());
                            newSongValue = musicSrv.getNewSongValue();
                        }
                        if (mpPlayingValue != musicSrv.isPlaying()) {
                            if (mpPlayingValue) {
                                mpBtnPlay.setImageResource(R.drawable.ic_media_play_car_mode);
                            } else {
                                mpBtnPlay.setImageResource(R.drawable.ic_media_pause_car_mode);

                            }
                            mpPlayingValue = !mpPlayingValue;
                        }

                        // Updating progress bar
                        int progress = (utils.getProgressPercentage(currentDuration, totalDuration));
                        //Log.d("Progress", ""+progress);
                        Log.d(TAG, "Progress " + progress + " bufferedProgress "+musicSrv.getBufferPosition());
                        mpDuring.setProgress(progress);
                        mpDuring.setSecondaryProgress(musicSrv.getBufferPosition());

                    }
                }
            }

            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(musicBound)
        {
            if(musicSrv != null)
            {
                if(musicSrv.isPlaying())
                {
                    mediaPlayer.setVisibility(View.VISIBLE);
                }
            }
        }
        updateMediaPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }



    public class PlayListPager extends FragmentPagerAdapter {

        public PlayListPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return CarModeNewsListFragment.newInstance(mArgument,Constants.CATEGORY_NEWS);
                case 1:
                    return CarModeNewsListFragment.newInstance(mArgument,Constants.CATEGORY_ARTICLE);
                case 2:
                    return  CarModeNewsListFragment.newInstance(mArgument,Constants.CATEGORY_STORY);
            }

            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            /*Drawable image = ContextCompat.getDrawable(getApplicationContext(), R.drawable.tab_image_thread);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;*/
           /* switch (position) {
                case 0:
                    return (getResources().getString(R.string.lenta)).toUpperCase();
                case 1:
                    return (getResources().getString(R.string.catalog)).toUpperCase();
                case 2:
                    return (getResources().getString(R.string.play_list)).toUpperCase();
            }
            return null;*/

            switch (position) {
                case 0:
                    return getActivity().getResources().getString(R.string.news);
                case 1:
                    return getActivity().getResources().getString(R.string.articles);
                case 2:
                    return getActivity().getResources().getString(R.string.story);

            }

            return null;
        }
    }
}

