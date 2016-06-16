package com.newstee;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newstee.helper.NewsTeeInstructionsDialogFragment;

/**
 * Created by Arnold on 17.02.2016.
 */
public class CatalogFragment extends Fragment {
    private final static String TAG = "CatalogFragment";

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            showDialog();
        }
    }
    public void showDialog() {
        if(!isFirstTime())
        {
            return;
        }

        FragmentManager fm = getActivity().getSupportFragmentManager();
        NewsTeeInstructionsDialogFragment dialog = NewsTeeInstructionsDialogFragment.newInstance(R.drawable.catalogue,getResources().getString(R.string.tab_category),getResources().getString(R.string.instructions_catalog),false);
        dialog.show(fm,NewsTeeInstructionsDialogFragment.DIALOG_INSTRUCTIONS);

    }
    private boolean isFirstTime()
    {
        SharedPreferences preferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBeforeCatalog", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBeforeCatalog", true);
            editor.commit();
        }
        return !ranBefore;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    // private ViewPager mViewPager;


    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "******onResume*****");

      /*  if(musicBound)
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
    public void onStop() {
        super.onStop();
  //      getActivity().unbindService(musicConnection);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);
      //  mediaPlayer = rootView.findViewById(R.id.catalog_media_player);
       // mpBtnPlay = (ImageButton) mediaPlayer.findViewById(R.id.media_player_small_play_button);
        /*mpBtnPlay.setOnClickListener(new View.OnClickListener() {

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
       /* mpTitle = (TextView) mediaPlayer.findViewById(R.id.media_player_small_titlet_TextView);
mediaPlayer.setVisibility(View.GONE);
*/
        new Handler().post(new Runnable() {
            public void run() {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = CatalogListFragment.newInstance(false);
                fm.beginTransaction().replace(R.id.catalog_container, fragment)
                        .commit();
            }
        });

       /* CatalogPagerAdapter mCatalogPagerAdapter = new CatalogPagerAdapter(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.catalog_container);
        mViewPager.setAdapter(mCatalogPagerAdapter);
        mViewPager.setCurrentItem(0);


        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.catalog_tabs);
        tabLayout.setupWithViewPager(mViewPager);*/
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

   /* public class CatalogPagerAdapter extends FragmentPagerAdapter {

        public CatalogPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return CatalogListFragment.newInstance(false);
                case 1:
                    return CatalogListFragment.newInstance(true);
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            *//*Drawable image = ContextCompat.getDrawable(getApplicationContext(), R.drawable.tab_image_thread);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;*//*
           *//* switch (position) {
                case 0:
                    return (getResources().getString(R.string.lenta)).toUpperCase();
                case 1:
                    return (getResources().getString(R.string.catalog)).toUpperCase();
                case 2:
                    return (getResources().getString(R.string.play_list)).toUpperCase();
            }
            return null;*//*

            switch (position) {
                case 0:
                    return getActivity().getResources().getString(R.string.tab_category);
                case 1:
                    return getActivity().getResources().getString(R.string.tab_publish);

            }

            return null;
        }


    }*/



}

