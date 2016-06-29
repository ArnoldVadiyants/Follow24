package com.newstee;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newstee.helper.NewsTeeInstructionsDialogFragment;
import com.newstee.model.data.News;
import com.newstee.model.data.NewsLab;
import com.newstee.model.data.Tag;
import com.newstee.model.data.TagLab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnold on 20.02.2016.
 */
public class NewsThreadFragment extends Fragment {

private NewsThreadListFragment newsFragment;
    private NewsThreadListFragment articleFragment;
    private NewsThreadListFragment storyFragment;
    private LinearLayout startButton;
    private ArrayList<String> mFilterTagIds = new ArrayList<>();
    private int isVisibleCount = 0;
    private ViewPager mViewPager;
    private PlayListPager mPlayListPager;
    private ImageButton filterButton;
    private final static String TAG = "NewsThreadFragment";
   // View mediaPlayer;
    private int newSongValue = 0;
    private boolean playingValue = false;
    private Handler mHandler = new Handler();
    ImageButton mpBtnPlay;
    TextView mpTitle;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsFragment =  (NewsThreadListFragment)NewsThreadListFragment.newInstance(Constants.ARGUMENT_NONE,Constants.CATEGORY_NEWS);
        articleFragment =  (NewsThreadListFragment) NewsThreadListFragment.newInstance(Constants.ARGUMENT_NONE,Constants.CATEGORY_ARTICLE);
        storyFragment = (NewsThreadListFragment) NewsThreadListFragment.newInstance(Constants.ARGUMENT_NONE, Constants.CATEGORY_STORY);

       List<Tag>tags = TagLab.getInstance().getTags();
       for(Tag t: tags) {
           mFilterTagIds.add(t.getId());
       }

    }
    public void showDialog() {
        if(!isFirstTime())
        {
           return;
        }

        FragmentManager fm = getActivity().getSupportFragmentManager();
        NewsTeeInstructionsDialogFragment dialog = NewsTeeInstructionsDialogFragment.newInstance(R.drawable.stream,getResources().getString(R.string.tab_stream),getResources().getString(R.string.instructions_thread),true);
     //  dialog.get

        dialog.show(fm,NewsTeeInstructionsDialogFragment.DIALOG_INSTRUCTIONS);

    }
    private boolean isFirstTime()
    {
        SharedPreferences preferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBeforeThread", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBeforeThread", true);
            editor.commit();
        }
        return !ranBefore;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView  = inflater.inflate(R.layout.fragment_thread, container, false);
        startButton = (LinearLayout)rootView.findViewById(R.id.thread_start_button);
        mViewPager = (ViewPager)rootView.findViewById(R.id.thread_container);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String category = null;
                if(mViewPager.getCurrentItem() == 0)
                {
                    category = Constants.CATEGORY_NEWS;
                }
                else if(mViewPager.getCurrentItem() == 1) {
                category = Constants.CATEGORY_ARTICLE;
                }
                if(category==null)
                {
                 return;
                }
                    List<News>news = NewsLab.getInstance().getNews();
                    List<News>newsList= new ArrayList<News>();
                    for(News n:news)
                    {
                        if(n.getCategory().equals(category))
                        {
                            newsList.add(n);
                        }
                    }
                if(newsList.isEmpty())
                {
                    return;
                }
                    PlayList.getInstance().setNewsList(newsList,getString(R.string.tab_stream));
                Intent i = new Intent(getActivity(), MediaPlayerFragmentActivity.class);
                i.putExtra(MediaPlayerFragmentActivity.ARG_AUDIO_ID, newsList.get(0).getId());
                startActivity(i);

                }

        });
        filterButton= (ImageButton)rootView.findViewById(R.id.filter_imageButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ArrayList<String>ids = new ArrayList<String>();
                ids.addAll(mFilterTagIds);
                final List<Tag>tags = TagLab.getInstance().getTags();
                boolean[]checkedCategories = new boolean[tags.size()];
                List<CharSequence>titles = new ArrayList<>();
                for(int i =0; i<tags.size(); i++) {
                    titles.add(tags.get(i).getNameTag());
                    for(int j = 0; j< mFilterTagIds.size(); j++)
                    {
                        if(tags.get(i).getId().equals(mFilterTagIds.get(j))) {
                            checkedCategories[i] = true;
                            break;
                        }
                    }

                }
                AlertDialog.Builder choicesBuilder = new AlertDialog.Builder(getActivity());
                choicesBuilder.setTitle(R.string.filter);
                choicesBuilder.setMultiChoiceItems(titles.toArray(new CharSequence[titles.size()]), checkedCategories, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        String item = tags.get(which).getId();
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            ids.add(item);
                        } else if (ids.contains(item)) {
                            // Else, if the item is already in the array, remove it
                            ids.remove(item);
                        }
                    }
                });
                choicesBuilder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        mFilterTagIds.clear();
                        mFilterTagIds.addAll(ids);
                        newsFragment.setFilterTagIds(mFilterTagIds);
                        newsFragment.applyFilter();
                        articleFragment.setFilterTagIds(mFilterTagIds);
                        articleFragment.applyFilter();
                        storyFragment.setFilterTagIds(mFilterTagIds);
                        storyFragment.applyFilter();
                        setFilterButton((ImageButton)v);


                    }
                });
                choicesBuilder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


              /*  choicesBuilder.setItems(R.array.flags_list,new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item == 7)
                        {
                            //	FragmentManager fm = getActivity().getSupportFragmentManager();
                            //	DatePickerFragment dialog = DatePickerFragment

                            //	dialog.setTargetFragment(ReminderFragment.this, 0);
                            //	dialog.show(fm,DIALOG_DATE);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            ChoiceReplyFragment choiceDialog =ChoiceReplyFragment.newInstance(mReminder.getReplyTime());
                            choiceDialog.setTargetFragment(ReminderFragment.this, REQUEST_DATE);
                            choiceDialog.show(fm, DIALOG_REPLY);
                        }
                        mReminder.setFlag(item);
                        updateFlag();

                    }// onClick
                }// choicesBuilder.setItems*/

                AlertDialog choicesDialog = choicesBuilder.create();
                choicesDialog.show();

        }});
     //   mediaPlayer = rootView.findViewById(R.id.thread_media_player);
     //   mpBtnPlay = (ImageButton) mediaPlayer.findViewById(R.id.media_player_small_play_button);
      /*  mpBtnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (musicSrv.isPlaying()) {
                    if (musicBound) {
                        musicSrv.pausePlayer();
                        mpBtnPlay.setImageResource(R.drawable.play);
                    }
                } else {
                    // Resume song
                    if (musicBound) {
                        musicSrv.go();
                        // Changing button image to pause button
                        mpBtnPlay.setImageResource(R.drawable.ic_media_pause);
                    }
                }

            }
        });*/
    //    mpTitle = (TextView) mediaPlayer.findViewById(R.id.media_player_small_titlet_TextView);
     //   mediaPlayer.setVisibility(View.GONE);
        mPlayListPager = new PlayListPager(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.

        mViewPager.setAdapter(mPlayListPager);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    startButton.setEnabled(false);
                } else {
                    startButton.setEnabled(true);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.thread_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        return rootView;
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


         //   updateMPNews();
        }

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

    public void  updateMPNews()
    {

        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable stream
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if(musicSrv != null) {
                if(!musicSrv.isNullPlayer())
                {

                    if (!musicSrv.isPaused()) {
                        if (newSongValue != musicSrv.getNewSongValue()) {
                            mpTitle.setText(musicSrv.getSongTitle());
                            newSongValue = musicSrv.getNewSongValue();
                        }
                        if (playingValue != musicSrv.isPlaying()) {
                            if (playingValue) {
                                mpBtnPlay.setImageResource(R.drawable.ic_media_play);
                            } else {
                                mpBtnPlay.setImageResource(R.drawable.ic_media_pause);

                            }
                            playingValue = !playingValue;
                        }

                    }
                }
            }
            // Running this stream after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    //start and bind the service when the activity starts
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "******onStart*****");
        connectService();
    }
    private boolean isFiltered()
    {
        if(mFilterTagIds.size() == TagLab.getInstance().getTags().size())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    private void setFilterButton(ImageButton filterButton)
    {
        if(filterButton == null)
        {
            return;
        }
        if(isFiltered())
        {
            filterButton.setImageResource(R.drawable.filter_button_is_active_back);
        }
        else
        {
            filterButton.setImageResource(R.drawable.filter_button_back);
        }
    }
    private void setFilterButton()
    {
        if(filterButton == null)
        {
            return;
        }
       if(isFiltered())
        {
            filterButton.setImageResource(R.drawable.filter_button_is_active_back);
        }
        else
        {
            filterButton.setImageResource(R.drawable.filter_button_back);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "******onResume*****");
        setFilterButton();
/*
        if(musicBound)
        {
            if(musicSrv!=null)
            {

                if(musicSrv.isPlaying())
                {
                    mpBtnPlay.setImageResource(R.drawable.ic_media_pause);
                    mediaPlayer.setVisibility(View.VISIBLE);

                }
                else
                {
                    mediaPlayer.setVisibility(View.GONE);
                    mpBtnPlay.setImageResource(R.drawable.ic_media_play);
                }
                mpTitle.setText(musicSrv.getSongTitle());
            }
        }*/

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {

                showDialog();

            Log.d("@@@@ " + TAG, isVisibleToUser+ "");
           /* newsFragment.setUserVisibleHint(true);
            newsFragment.setUserVisibleHint(true);
            newsFragment.setUserVisibleHint(true);*/
        }
    }

    public class PlayListPager extends FragmentPagerAdapter {
        public int current_position = 0;
        public PlayListPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return newsFragment;
                case 1:
                    return articleFragment;
                case 2:
                    return storyFragment;
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

