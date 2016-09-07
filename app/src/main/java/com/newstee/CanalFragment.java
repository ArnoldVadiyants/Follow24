package com.newstee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Arnold on 17.02.2016.
 */
public class CanalFragment extends Fragment {
    TextView publish_titleTextView;
    TextView article_titleTextView;
    TextView mixes_titleTextView;
    RelativeLayout publish_relative;
    RelativeLayout article_relative;
    RelativeLayout mixes_relative;
    ImageView publish_icon;
    ImageView article_icon;
    ImageView mixes_icon;
    ImageButton filter_button;


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
    public View getView() {
        return super.getView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_canal, container, false);
        View smallMp = rootView.findViewById(R.id.canal_media_player);
        smallMp.setVisibility(View.GONE);
        TextView canalTitle =  (TextView)rootView.findViewById(R.id.canal_canal_titleTextView);
        ImageButton exitButton = (ImageButton)rootView.findViewById(R.id.canal_exit_imageButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        TextView subscribersSum =  (TextView)rootView.findViewById(R.id.canal_subscriptions_sum_textView);
       Button to_subscriptButton = (Button)rootView.findViewById(R.id.canal_to_subscript_button);
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.canal_news_container);
        if (fragment == null) {
            fragment = NewsThreadListFragment.newInstance(Constants.ARGUMENT_NEWS_NONE,Constants.CATEGORY_ALL);
            fm.beginTransaction().replace(R.id.canal_news_container, fragment)
                    .commit();
        }
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


}
