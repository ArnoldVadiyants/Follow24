package com.newstee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Arnold on 25.03.2016.
 */
public class CarModeFragment extends Fragment {
    private final static String TAG = "CarModeFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.car_mode_layout, container, false);
       /* View header = view.findViewById(R.id.header);
        ImageView headerTitle =(ImageView) header.findViewById(R.id.car_mode_header_title);
        LinearLayout headerPlayer = (LinearLayout)header.findViewById(R.id.car_mode_header_player);
        TextView headerTitleCanal = (TextView)header.findViewById(R.id.car_mode_header_title_canal);
        TextView headerTitleNews = (TextView)header.findViewById(R.id.car_mode_header_title_news);
        ImageButton headerPlayButton = (ImageButton)header.findViewById(R.id.car_mode_header_play);
        FrameLayout streamTab = (FrameLayout)view.findViewById(R.id.car_mode_tab_stream);
        FrameLayout playlistTab = (FrameLayout)view.findViewById(R.id.car_mode_tab_playlist);
        FrameLayout recentTab = (FrameLayout)view.findViewById(R.id.car_mode_tab_recent);
        FrameLayout recommendTab = (FrameLayout)view.findViewById(R.id.car_mode_tab_recommend);
        Button exitTab = ( Button)view.findViewById(R.id.car_mode_tab_exit);
        headerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        streamTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        playlistTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        recentTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        recommendTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        exitTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CarModeFragmentActivity.class));
                getActivity().finish();
            }
        });
*/
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            Log.i(TAG, "Visible");
            startActivity(new Intent(getContext(), CarModeFragmentActivity.class));
            getActivity().finish();
        }
    }
}
