package com.newstee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newstee.helper.InternetHelper;
import com.newstee.helper.NewsTeeInstructionsDialogFragment;
import com.newstee.helper.SQLiteHandler;
import com.newstee.helper.SessionManager;
import com.newstee.model.data.User;
import com.newstee.model.data.UserLab;
import com.newstee.utils.DisplayImageLoaderOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

/**
 * Created by Arnold on 17.02.2016.
 */
public class ProfileFragment extends Fragment  {
    private static final String TAG = "ProfileFragment";
    private CatalogListFragment2 mCatalogListFragment2;
   // private Button editButton;
    private FloatingActionButton editButton;

    private SessionManager session;
    private SQLiteHandler db;
    private boolean update = false;


ImageLoader imageLoader = ImageLoader.getInstance();
 //   LinearLayout profileInfo;
    ImageView backgroundImgView;
    ImageView avatarImgView;
    TextView usernameTitle;
  //  TextView name;
  //  TextView likes;
 //   TextView subscribes;
    ViewPager mViewPager;

    TabLayout mTabLayout;


    private ProfilePagerAdapter mProfilePagerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCatalogListFragment2 = CatalogListFragment2.newInstance(Constants.ARGUMENT_ADDED_TAGS);
        session = new SessionManager(getActivity());
        db = new SQLiteHandler(getActivity());
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    // private ViewPager mViewPager;
public void update()
{
    if(mViewPager == null)
    {
        return;
    }
    mProfilePagerAdapter = new ProfilePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mProfilePagerAdapter);
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);
        if(mProfilePagerAdapter != null)
        {
            mProfilePagerAdapter.notifyDataSetChanged();

        }
    Bitmap back = EditProfileActivity.getImageFromStorage(getActivity());
    if(back != null)
    {
        backgroundImgView.setImageBitmap(back);
    }

    if (session.isLoggedIn()) {
     //   profileInfo.setVisibility(View.GONE);
        //    name.setText(UserLab.getInstance().getUser().getUserLogin());
        User u = UserLab.getInstance().getUser();
        String name = u.getUserLogin();
        if (name == null) {
            HashMap<String, String> userData = db.getUserDetails();
            String username = userData.get(SQLiteHandler.KEY_NAME);
            if (username != null) {
                name = username;
            } else {
                name = getString(R.string.tab_profile);
            }
        }
        usernameTitle.setText(name);
      //  likes.setText("" + UserLab.getInstance().getLikedNews().size());
      //  subscribes.setText("" + UserLab.getInstance().getAddedTags().size());

        String avatar = UserLab.getInstance().getUser().getAvatar();
        if (avatar != null) {
            avatar = InternetHelper.toCorrectLink(avatar);
            imageLoader.displayImage(avatar, avatarImgView, DisplayImageLoaderOptions.getInstance()/*, new ImageLoadingListener() {
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
                    loadedImage = NewsListFragment.centerCrop(loadedImage);
                    loadedImage = new CircleTransform().transform(loadedImage);
                    ((ImageView) view).setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });*/
            );
       //     imageLoader.displayImage(avatar, backgroundImgView, DisplayImageLoaderOptions.getInstance());
        }
        Log.d(TAG, "@@@@@@ avatar "+ avatar);
    }
    else
    {
       usernameTitle.setText(R.string.tab_profile);
    //    name.setVisibility(View.GONE);
    }
}


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
                showDialog();
            update();



        }
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
        if(editButton !=null)
        {
            editButton.setVisibility(View.INVISIBLE);
            editButton.show();
        }
    }

    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_coordinator2, container, false);
   //     profileInfo = (LinearLayout)rootView.findViewById(R.id.profile_info_layout);
   //     likes = (TextView)rootView.findViewById(R.id.profile_like_count);
   //     subscribes = (TextView)rootView.findViewById(R.id.profile_subscribes_count);
       usernameTitle = (TextView)rootView.findViewById(R.id.profile_username_title);
      //  name = (TextView)rootView.findViewById(R.id.profile_name_TextView);
        backgroundImgView= (ImageView)rootView.findViewById(R.id.profile_background_imageView);
        avatarImgView = (ImageView)rootView.findViewById(R.id.profile_avatar_imageView);
      //  mProfilePagerAdapter = new ProfilePagerAdapter(getChildFragmentManager());
      mViewPager = (ViewPager)rootView.findViewById(R.id.profile_container);
     //   mViewPager.setAdapter(mProfilePagerAdapter);
      //  mViewPager.setCurrentItem(0);
       mTabLayout = (TabLayout)rootView.findViewById(R.id.profile_tabs);
      //  mTabLayout.setupWithViewPager(mViewPager);

       /* BitmapFactory.Options o=new BitmapFactory.Options();
                o.inSampleSize = 4;
                o.inDither=false;                     //Disable Dithering mode
                o.inPurgeable=true;
                avatarImgView.setImageBitmap(new CircleTransform().transform(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_test, o)));
               */
  //      editButton = (Button) rootView.findViewById(R.id.profile_edit_btn);
        editButton = (FloatingActionButton) rootView.findViewById(R.id.profile_edit_btn);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isLoggedIn())
                {
                    getActivity().startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
else
                {
                    Snackbar.make(mViewPager,R.string.msg_auth_pls,Snackbar.LENGTH_SHORT).show();
                }
                    }
        });
       // mProfilePagerAdapter = new ProfilePagerAdapter(getChildFragmentManager());
       update();

        // Set up the ViewPager with the sections adapter.


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
    public Bitmap getRoundedShape(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

     int   x = (source.getWidth() - size) / 2;
      int  y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap.Config config = source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(size, size, config);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size/2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    public void showDialog() {
        if(!isFirstTime())
        {
            return;
        }

        FragmentManager fm = getActivity().getSupportFragmentManager();
        NewsTeeInstructionsDialogFragment dialog = NewsTeeInstructionsDialogFragment.newInstance(R.drawable.profile,getResources().getString(R.string.tab_profile),getResources().getString(R.string.instructions_profile),false);
        dialog.show(fm,NewsTeeInstructionsDialogFragment.DIALOG_INSTRUCTIONS);

    }
    private boolean isFirstTime()
    {
        if(getActivity() == null)
        {
            Log.e(TAG,"@@@@ getActivity = null");
            return false;
        }
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBeforeProfile", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBeforeProfile", true);
            editor.apply();
        }
        return !ranBefore;
    }

    public void updatePager() throws IllegalStateException {
        mProfilePagerAdapter = new ProfilePagerAdapter(getChildFragmentManager());
        if(mViewPager == null)
        {
            return;
        }
        mViewPager.setAdapter(mProfilePagerAdapter);
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    /*
      int targetHeight= source.getHeight();
        int targetWidth =  source.getWidth();
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = source;


        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }*/
public class ProfilePagerAdapter extends FragmentPagerAdapter {

    public ProfilePagerAdapter(FragmentManager fm) {
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
                return mCatalogListFragment2;
            case 1:
                return MyPlaylistListFragment.newInstance(Constants.ARGUMENT_NEWS_LIKED,Constants.CATEGORY_ALL);
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
                return getActivity().getResources().getString(R.string.my_subscriptions) + " "+UserLab.getInstance().getAddedTags().size();
            case 1:
                return "     "+getActivity().getResources().getString(R.string.my_likes)+ " "+UserLab.getInstance().getLikedNews().size();

        }

        return null;
    }
}
public static class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
}
}
