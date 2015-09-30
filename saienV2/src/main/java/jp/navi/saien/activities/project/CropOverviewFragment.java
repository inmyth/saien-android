package jp.navi.saien.activities.project;

import jp.navi.saien.R;
import jp.navi.saien.activities.splash.LoginActivity;
import jp.navi.saien.json.CropItem;
import jp.navi.saien.json.CropListResponse;
import jp.navi.saien.utils.DateUtil;
import jp.navi.saien.utils.GsonUtils;
import jp.navi.saien.utils.PreferenceUtils;
import jp.navi.saien.utils.UrlUtils;
import jp.navi.saien.utils.VolleyUtils;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.androidquery.AQuery;

public class CropOverviewFragment extends SherlockFragment{
	
	private Adapter adapter;
	private AQuery a;
	private CropOverviewListener listener;
    private RequestQueue queue = VolleyUtils.getRequestQueue();
    private String userName;

	
	
	public static SherlockFragment newFragment(String initLoad){
		CropOverviewFragment cropOverviewFragment = new CropOverviewFragment();		
	    Bundle args = new Bundle();
	    args.putString(LoginActivity.LOAD_INITIAL_LOAD, initLoad);
	    cropOverviewFragment.setArguments(args);
	    return cropOverviewFragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof CropOverviewListener){
			listener = (CropOverviewListener) activity;			
		}else{
        	throw new ClassCastException("must implement CropOverviewListener");
		}		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new Adapter();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crop_overview, container, false);
		a = new AQuery(v);
		View verticalPadding = inflater.inflate(R.layout.footer_transparent, null);
		a.id(R.id.list).getListView().addFooterView(verticalPadding);
		a.id(R.id.list).getListView().addHeaderView(verticalPadding);	
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		String initLoad = getArguments().getString(LoginActivity.LOAD_INITIAL_LOAD);
		if (initLoad != null){
			buildList(initLoad);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		a.id(R.id.list).adapter(adapter);

	}
	
	
	public void buildList(String response){
		adapter.clear();
		CropListResponse cropListResponse = GsonUtils.toBean(response, CropListResponse.class);
		userName = cropListResponse.getResult().getUserName();
		for (CropItem cropItem : cropListResponse.getResult().getCropItems()){
			adapter.add(cropItem);		
		}		
		adapter.add(new CropItem());
	}
	
	private class Adapter extends ArrayAdapter<CropItem> {
		private ImageLoader imageLoader = VolleyUtils.getImageLoader();


		public Adapter() {
			super(getSherlockActivity(), R.layout.row_crop_overview);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Holder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_crop_overview, parent, false);
				holder = new Holder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			final AQuery ar = holder.ar;
			final CropItem cropItem = getItem(position);
			
			if (position != getCount() - 1){
				ar.id(holder.row1).visible();
				ar.id(holder.row2).gone();
				SpannableStringBuilder sb = new SpannableStringBuilder();		
				sb.append(getString(R.string.name) + ": ");
				int start = sb.length();
				sb.append(cropItem.getTitle());
				sb.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.green_dk)), start, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);	
				ar.id(holder.line1).text(sb);
				ar.id(holder.line2).text(cropItem.getPlan());
				ar.id(holder.line3).text(DateUtil.getDayCues(cropItem.getDatemin(), "[/]"));
				
				ar.id(holder.row1).clicked(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						listener.onItemClick(cropItem);					
					}
				});				
				
				ar.id(holder.line4).clicked(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String pageUrl = UrlUtils.getUrlPage(getItem(position).getGuid());
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl));
						startActivity(browserIntent);						
					}
				});
			}else{
				ar.id(holder.row1).gone();
				ar.id(holder.row2).visible().clicked(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String toGo = userName != null ? UrlUtils.getUrlOwner(userName) : UrlUtils.URL;
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(toGo));
						startActivity(browserIntent);
					}
				});
				
			}
			return convertView;
		}
		
		class Holder {
			AQuery ar;
			TextView line1, line2, line3, line4;
			LinearLayout row1, row2;
			
			public Holder(View v) {
				ar = new AQuery(v);
				line1 = ar.id(R.id.line1).getTextView();
				line2 = ar.id(R.id.line2).getTextView();
				line3 = ar.id(R.id.line3).getTextView();
				line4 = ar.id(R.id.line4).getTextView();		
				row1 = (LinearLayout) ar.id(R.id.row1).getView();
				row2 = (LinearLayout) ar.id(R.id.row2).getView();
			}
			
		}
	}
	
	public interface CropOverviewListener{
		public void onItemClick(CropItem cropItem);
		
		
		
	}

}
