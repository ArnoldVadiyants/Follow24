package com.newstee;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newstee.helper.InternetHelper;
import com.newstee.helper.SessionManager;
import com.newstee.model.data.Author;
import com.newstee.model.data.AuthorLab;
import com.newstee.model.data.DataPost;
import com.newstee.model.data.Tag;
import com.newstee.model.data.TagLab;
import com.newstee.model.data.UserLab;
import com.newstee.network.FactoryApi;
import com.newstee.network.interfaces.NewsTeeApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CatalogListFragment extends SwipeRefreshListFragment
{
	private final static String TAG = "CatalogListFragment";
	private SessionManager session;
	private  List<Item> items = new ArrayList<Item>();
private CatalogListAdapter adapter;
	private static final String ARG_IS_CANAL = "is_canal";
	private boolean mIsCanal;

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static CatalogListFragment newInstance(boolean isCanal) {
		CatalogListFragment fragment = new CatalogListFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_IS_CANAL, isCanal);
		fragment.setArguments(args);
		return fragment;
	}

	public CatalogListFragment() {
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
		mIsCanal = getArguments().getBoolean(ARG_IS_CANAL);
		session = new SessionManager(getActivity());





	}





	private static class Item
	{
		public final String id;
		public final String icon;
		public final String title;
		public final boolean isAdded;


		public Item(String id, String icon, String title, boolean isAdded) {
			this.id = id;
			this.icon = icon;
			this.title = title;
			this.isAdded = isAdded;
		}
	}
	
	public static class ViewHolder 
	{
		public final ImageView imageView;
		public final TextView title;
		public final ImageButton addBtn;
		public final RelativeLayout catalogFeed;

		public ViewHolder(ImageView imageView, TextView title, ImageButton addBtn, RelativeLayout catalogFeed) {
			this.imageView = imageView;
			this.title = title;
			this.addBtn = addBtn;
			this.catalogFeed = catalogFeed;
		}
	}
	
	private class CatalogListAdapter extends ArrayAdapter<Item>
	{

		public CatalogListAdapter(Context context)
		{
			super(context, R.layout.catalog_item_title, items);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			ViewHolder holder = null;
			if(view == null)
			{
				view = LayoutInflater.from(getContext()).inflate(R.layout.catalog_item_title, parent, false);
				TextView titleTextView = (TextView)view.findViewById(R.id.title_TextView);
				ImageView imageView = (ImageView)view.findViewById(R.id.canal_icon_ImageVew);
				RelativeLayout catalogFeed = (RelativeLayout)view.findViewById(R.id.catalog_feed);
				ImageButton imageButton =(ImageButton)view.findViewById(R.id.catalog_item_add_ImageButton);



				view.setTag(new ViewHolder(imageView, titleTextView, imageButton, catalogFeed));
			}
			if(holder == null && view != null)
			{
				Object tag = view.getTag();
				if(tag instanceof ViewHolder)
				{
					holder = (ViewHolder)tag;
				}
			}
			final Item item = getItem(position);
			if(item != null && holder != null)
			{

				if(mIsCanal)
				{
				//	holder.imageView.set

					holder.catalogFeed.setTag(position);
					holder.catalogFeed.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(getContext(), CanalFragmentActivity.class));

							Log.d(TAG, "List_item onCLick" + " " + (v.getTag()));
						}
					});
					holder.addBtn.setVisibility(View.GONE);
				}
				else
				{
					final String id = item.id.trim();
					holder.catalogFeed.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent i = new Intent(getContext(), NewsByArgumentActivity.class);
							i.putExtra(NewsByArgumentActivity.ARG_ID, item.id);
							i.putExtra(NewsByArgumentActivity.ARG_TITLE, item.title);
							i.putExtra(NewsByArgumentActivity.ARG_SORT_BY_ARGUMENT, Constants.ARGUMENT_NEWS_BY_TAG);
							startActivity(i);
						}
					});
					holder.imageView.setVisibility(View.GONE);
					holder.title.setTypeface(Typeface.DEFAULT_BOLD);
					holder.addBtn.setOnClickListener(new View.OnClickListener() {

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
							UserLab.getInstance().addTag(TagLab.getInstance().getTag(id));
							NewsTeeApiInterface nApi = FactoryApi.getInstance(getActivity());
							Call<DataPost> call;
							if (UserLab.getInstance().isAddedTag(id)) {
								call = nApi.subscribe(id);
								((ImageButton) v).setImageResource(R.drawable.ic_is_added);
							} else {
								call = nApi.unsubscribe(id);
								((ImageButton) v).setImageResource(R.drawable.news_to_add_button);
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

					});
				//	holder
					if (UserLab.getInstance().isAddedTag(id)) {
						holder.addBtn.setImageResource(R.drawable.ic_is_added);
					}
					else
					{
						holder.addBtn.setImageResource(R.drawable.news_to_add_button);
					}
				}

				holder.title.setText(item.title);
			//	holder
			}
			return view;
		}
	}
private void update()
{
	items.clear();
	if(mIsCanal)
	{
		List<Author>authors =AuthorLab.getInstance().getAuthors();
		for(Author a : authors)
		{
			items.add(new Item(a.getId(), a.getAvatar(), a.getName(), false));
		}

	}
	else {
		List<Tag> tags = TagLab.getInstance().getTags(getActivity());
		for (Tag t : tags) {
			items.add(new Item(t.getId(), null, t.getNameTag(), true));
		}
	}
}


	@Override
	public void onResume() {
		super.onResume();
		if(adapter ==null) {
			return;
		}
		update();
		adapter.notifyDataSetChanged();


	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new CatalogListAdapter(getActivity());
		setListAdapter(adapter);
		setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if(!UserLab.getInstance().isUpdated())
				{

					new MainLoadAsyncTask(getActivity()) {
						@Override
						void hideContent() {
						}
						@Override
						void showContent() {
							update();
							adapter.notifyDataSetChanged();
							setRefreshing(false);
						}
					}.execute();
					return;
				}
				else
				{
					new MainLoadAsyncTask(getActivity()) {
						@Override
						void hideContent() {
						}
						@Override
						void showContent() {
							update();
							adapter.notifyDataSetChanged();
							setRefreshing(false);
						}
					}.execute(Constants.ARGUMENT_TAGS);
				}


			/*	Call<DataTag> tagC = FactoryApi.getInstance(getActivity()).getTags();
				tagC.enqueue(new Callback<DataTag>() {
					@Override
					public void onResponse(Call<DataTag> call, Response<DataTag> response) {
						TagLab.getInstance().setTags(response.body().getData());
						update();
						adapter.notifyDataSetChanged();
						setRefreshing(false);
					}

					@Override
					public void onFailure(Call<DataTag> call, Throwable t) {

						update();
						adapter.notifyDataSetChanged();
						setRefreshing(false);
					}
				});
*/
			}
		});
		setColorScheme(R.color.colorAccent,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
		//return inflater.inflate(R.layout.catalog_listview, container, false);
	}
/*@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
{
	return inflater.inflate(R.layout.catalog_listview, container, false);
}*/

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
}
