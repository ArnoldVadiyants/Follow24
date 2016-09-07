package com.newstee;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.newstee.helper.NewsTeeInstructionsDialogFragment;
import com.newstee.model.data.News;
import com.newstee.model.data.UserLab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnold on 23.02.2016.
 */
public class MyPlaylistFragment extends Fragment{

    public static boolean ifShowMediaPlayer = false;
    private LinearLayout startButton;
    private PlayListPager mPlayListPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            showDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ifShowMediaPlayer)
        {

        }


    }
    @Override
    public View getView() {
        return super.getView();
    }

    public void showDialog() {
        if(!isFirstTime())
        {
            return;
        }

        FragmentManager fm = getActivity().getSupportFragmentManager();
        NewsTeeInstructionsDialogFragment dialog = NewsTeeInstructionsDialogFragment.newInstance(R.drawable.play,getResources().getString(R.string.tab_play_list),getResources().getString(R.string.instructions_lenta),false);
        dialog.show(fm,NewsTeeInstructionsDialogFragment.DIALOG_INSTRUCTIONS);

    }
    private boolean isFirstTime()
    {
        SharedPreferences preferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBeforePlayList", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBeforePlayList", true);
            editor.commit();
        }
        return !ranBefore;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_playlist, container, false);
        mPlayListPager = new PlayListPager(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)rootView.findViewById(R.id.my_playlist_container);
        mViewPager.setAdapter(mPlayListPager);
        mViewPager.setCurrentItem(0);
        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.my_playlist_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        startButton = (LinearLayout)rootView.findViewById(R.id.my_playlist_start_button);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 2)
                {
                    startButton.setEnabled(false);
                }
                else
                {
                    startButton.setEnabled(true);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
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
                List<News> news = UserLab.getInstance().getAddedNews();
                List<News> newsList = new ArrayList<News>();
                for (News n : news) {
                    if (n.getCategory().equals(category)) {
                        newsList.add(n);
                    }
                }
                if(newsList.isEmpty())
                {
                    return;
                }
                PlayList playList =   PlayList.getInstance();
                playList.setNewsList(newsList,getString(R.string.tab_play_list));
                playList.setArgument("start_button");
                Intent i = new Intent(getActivity(), MediaPlayerFragmentActivity.class);
                i.putExtra(MediaPlayerFragmentActivity.ARG_AUDIO_ID, newsList.get(0).getId());
                startActivity(i);

            }

        });



       /* View article_view =  rootView.findViewById(R.id.theme_article_item);
            article_icon = (ImageView) article_view.findViewById(R.id.item_icon);
            article_titleTextView = (TextView) article_view.findViewById(R.id.item_title);
        article_titleTextView.setText(R.string.theme_article);
            article_relative = (RelativeLayout) article_view.findViewById(R.id.item_action);

        View publish_view =  rootView.findViewById(R.id.publish);
            publish_icon = (ImageView) publish_view.findViewById(R.id.item_icon);
            publish_titleTextView = (TextView) publish_view.findViewById(R.id.item_title);
        publish_titleTextView.setText(R.string.publish);
            publish_relative = (RelativeLayout) publish_view.findViewById(R.id.item_action);
        View mixes_view =  rootView.findViewById(R.id.new_mixes);
            mixes_icon = (ImageView) mixes_view.findViewById(R.id.item_icon);
            mixes_titleTextView = (TextView)mixes_view.findViewById(R.id.item_title);
        mixes_titleTextView.setText(R.string.publish);
            mixes_relative = (RelativeLayout)mixes_view.findViewById(R.id.item_action);
            filter_button = (ImageButton)mixes_view.findViewById(R.id.item_filter_button);

*/
        return rootView;
    }
    public class PlayListPager extends FragmentPagerAdapter {

        public PlayListPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MyPlaylistListFragment.newInstance(Constants.ARGUMENT_NEWS_ADDED,Constants.CATEGORY_NEWS);
                case 1:
                    return MyPlaylistListFragment.newInstance(Constants.ARGUMENT_NEWS_ADDED,Constants.CATEGORY_ARTICLE);
                case 2:
                    return  MyPlaylistListFragment.newInstance(Constants.ARGUMENT_NEWS_ADDED,Constants.CATEGORY_STORY);
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


