package com.newstee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.newstee.helper.InternetHelper;
import com.newstee.helper.SessionManager;
import com.newstee.model.data.DataPost;
import com.newstee.model.data.Tag;
import com.newstee.model.data.TagItem;
import com.newstee.model.data.TagLab;
import com.newstee.model.data.UserLab;
import com.newstee.network.FactoryApi;
import com.newstee.network.interfaces.NewsTeeApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiki.noadapter.LayoutSelector;
import vn.tiki.noadapter.OnItemClickListener;
import vn.tiki.noadapter.OnlyAdapter;
import vn.tiki.noadapter.TypeDeterminer;


public class CatalogListFragment2 extends Fragment {
	private RecyclerRefreshLayout mSwipyRefreshLayout;
	private RecyclerView mTagsRecyclerView;
	private OnlyAdapter mOnlyAdapter;
	private UpdateDataInterface mUpdateDataInterface;
	private final static String TAG = "CatalogListFragment";
	private SessionManager session;
	private List<TagItem> items = new ArrayList<>();
	private String mTagsType = Constants.ARGUMENT_TAGS;
	private static final String ARG_TAGS_TYPE = "tags_type";

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static CatalogListFragment2 newInstance(String argumentTagsType) {
		CatalogListFragment2 fragment = new CatalogListFragment2();
		Bundle args = new Bundle();
		args.putString(ARG_TAGS_TYPE, argumentTagsType);
		fragment.setArguments(args);
		return fragment;
	}

	public CatalogListFragment2() {
	}
	/*@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser)
		{
			if(adapter ==null) {
				return;
			}
			updateFragment();
			adapter.notifyDataSetChanged();


		}
	}*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		if(b != null)
		{
			mTagsType = b.getString(ARG_TAGS_TYPE,Constants.ARGUMENT_TAGS);
		}
		session = new SessionManager(getActivity());





	}






	private void feedClick(TagItem item)
	{
		Intent i = new Intent(getContext(), NewsByArgumentActivity.class);
		i.putExtra(NewsByArgumentActivity.ARG_ID, item.id);
		i.putExtra(NewsByArgumentActivity.ARG_TITLE, item.title);
		i.putExtra(NewsByArgumentActivity.ARG_SORT_BY_ARGUMENT, Constants.ARGUMENT_NEWS_BY_TAG);
		startActivity(i);
	}
	private void addClick(View v,String id)
	{
		if	(!InternetHelper.getInstance(getActivity()).isOnline()) {
			Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
			return;
		}
		if(!session.isLoggedIn())
		{
			Toast.makeText(getContext(),"Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
			return;

		}
		UserLab.getInstance().addTag(TagLab.getInstance().getTag(id));
		NewsTeeApiInterface nApi = FactoryApi.getInstance(getActivity());
		Call<DataPost> call;
		if (UserLab.getInstance().isAddedTag(id)) {
			call = nApi.subscribe(id);
					((ImageButton) v).setImageResource(R.drawable.	ic_is_added);
		} else {
			call = nApi.unsubscribe(id);
			((ImageButton) v).setImageResource(R.drawable.news_to_add_button);
		}
		if(mTagsType.equals(Constants.ARGUMENT_ADDED_TAGS))
		{
			if(mUpdateDataInterface !=null)
			{
				mUpdateDataInterface.updateData();
			}
		}
		//	NewsTeeApiInterface nApi = FactoryApi.getInstance(getActivity());
		//	Call<DataPost> call = nApi.addTags(id);
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
				Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}


private void update()
{
	items.clear();
	List<Tag> tags = TagLab.getInstance().getTags(getActivity());
	if(mTagsType.equals(Constants.ARGUMENT_TAGS))
	{
		for (Tag t : tags) {
			boolean isAdded = false;
			if (UserLab.getInstance().isAddedTag(t.getId())) {
				isAdded = true;
			}
			items.add(new TagItem(getActivity(),t.getId(), t.getNameTag(), isAdded));
		}
	}
	else if(mTagsType.equals(Constants.ARGUMENT_ADDED_TAGS))
	{
		for (Tag t : tags) {
			if (UserLab.getInstance().isAddedTag(t.getId())) {
				items.add(new TagItem(getActivity(),t.getId(), t.getNameTag(), true));
			}

		}


	}


}


	@Override
	public void onResume() {
		super.onResume();
		if(mOnlyAdapter ==null) {
			return;
		}
		update();
		mOnlyAdapter.notifyDataSetChanged();


	}



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
		mTagsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recycler_view);
		mTagsRecyclerView.setHasFixedSize(true);
		// use a linear layout manager
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
		mTagsRecyclerView.setLayoutManager(mLayoutManager);
		mOnlyAdapter = new OnlyAdapter.Builder()
				.layoutSelector(new LayoutSelector() {
					@Override public int layoutForType(int type) {
						return R.layout.catalog_item_title;
					}
				})
				.typeDeterminer(new TypeDeterminer() {
					@Override
					public int typeOf(Object item) {
						if(item instanceof TagItem)
						{
							return 1;
						}
						return 0;
					}
				})
				.onItemClickListener(new OnItemClickListener() {
					@Override public void onItemClick(View view, Object item, int position) {
						TagItem item1 = (TagItem) item;
						switch(view.getId()) {
							case R.id.title_TextView:
								feedClick(item1);
								break;
							case R.id.catalog_item_add_ImageButton:
								addClick(view,item1.id);
								break;
						}
					}
				})
				.build();
		mOnlyAdapter.setItems(items);
		mTagsRecyclerView.setAdapter(mOnlyAdapter);
		//  mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
		//mSwipyRefreshLayout.
		mSwipyRefreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {

					new MainLoadAsyncTask(getActivity()) {
						@Override
						void hideContent() {
						}
						@Override
						void showContent() {
							update();
							mOnlyAdapter.notifyDataSetChanged();
							if(mTagsType.equals(Constants.ARGUMENT_ADDED_TAGS))
							{
								if(mUpdateDataInterface !=null)
								{
									mUpdateDataInterface.updateData();
								}
							}
							mSwipyRefreshLayout.setRefreshing(false);
						}
					}.execute(Constants.ARGUMENT_TAGS);

			}
		});
return view;	}

/*@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
{
	return inflater.inflate(R.layout.catalog_listview, container, false);
}*/
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		Log.d(TAG, "onAttach");
		Activity a;
		if(context instanceof Activity)
		{
			a = (Activity)context;
			onAttach(a);
		}
     /*   if (context instanceof  com.materialdesign.FeedConsumer){
        // // Activity  activity=(Activity) context;
         //   if (activity instanceof com.materialdesign.FeedConsumer) {
            //    feedConsumer = (com.materialdesign.FeedConsumer) context;
            onAttach(hostActivity);
           }
       // }*/
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof UpdateDataInterface) {
			Log.d(TAG, "onAttachAct");
			mUpdateDataInterface = (UpdateDataInterface) activity;
		}
	}


	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "onDetach");
		mUpdateDataInterface = null;
	}
}
