package yitgogo.smart.local;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.model.LocalCarController;
import yitgogo.smart.local.model.ModelLocalGoodsDetail;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.OnQrEncodeListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.tools.QrCodeTool;
import yitgogo.smart.tools.QrEncodeMissonMessage;
import yitgogo.smart.tools.ScreenUtil;
import yitgogo.smart.view.InnerListView;
import yitgogo.smart.view.Notify;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class LocalGoodsDetailFragment extends BaseNotifyFragment implements
		OnClickListener {

	ViewPager imagePager;
	TextView nameTextView, priceTextView, unitTextView, stateTextView;
	ImageView lastImageButton, nextImageButton;
	TextView imageIndexText;
	TextView buyButton, addCarButton;
	ImageView qrCodeImageView;
	InnerListView relationList;

	WebView webView;
	ProgressBar progressBar;
	String goodsId = "";

	ModelLocalGoodsDetail goodsDetail;

	ImageAdapter imageAdapter;
	RelationAdapter relationAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_fragment_local_goods_detail);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalGoodsDetailFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalGoodsDetailFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getGoodsDetail();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("goodsId")) {
				goodsId = bundle.getString("goodsId");
			}
		}
		goodsDetail = new ModelLocalGoodsDetail();
		imageAdapter = new ImageAdapter();
		relationAdapter = new RelationAdapter();
	}

	protected void findViews() {
		imagePager = (ViewPager) contentView
				.findViewById(R.id.product_detail_images);
		lastImageButton = (ImageView) contentView
				.findViewById(R.id.product_detail_image_last);
		nextImageButton = (ImageView) contentView
				.findViewById(R.id.product_detail_image_next);
		imageIndexText = (TextView) contentView
				.findViewById(R.id.product_detail_image_index);

		qrCodeImageView = (ImageView) contentView
				.findViewById(R.id.product_detail_qrcode);
		nameTextView = (TextView) contentView
				.findViewById(R.id.local_goods_detail_name);
		priceTextView = (TextView) contentView
				.findViewById(R.id.local_goods_detail_price);
		unitTextView = (TextView) contentView
				.findViewById(R.id.local_goods_detail_unit);
		stateTextView = (TextView) contentView
				.findViewById(R.id.local_goods_detail_state);
		relationList = (InnerListView) contentView
				.findViewById(R.id.local_goods_detail_relation_list);

		buyButton = (TextView) contentView
				.findViewById(R.id.local_goods_detail_buy);
		addCarButton = (TextView) contentView
				.findViewById(R.id.local_goods_detail_car);

		webView = (WebView) contentView.findViewById(R.id.web_webview);
		progressBar = (ProgressBar) contentView.findViewById(R.id.web_progress);

		initViews();
		registerViews();
	}

	protected void initViews() {
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				ScreenUtil.getScreenWidth() / 3);
		imagePager.setLayoutParams(layoutParams);
		imagePager.setAdapter(imageAdapter);
		relationList.setAdapter(relationAdapter);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressBar.setVisibility(View.GONE);
				} else {
					if (progressBar.getVisibility() == View.GONE)
						progressBar.setVisibility(View.VISIBLE);
					progressBar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		WebSettings settings = webView.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setUseWideViewPort(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setLoadWithOverviewMode(true);

		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setAppCachePath(getActivity().getCacheDir().getPath());
		settings.setAppCacheEnabled(true);
	}

	@Override
	protected void registerViews() {
		lastImageButton.setOnClickListener(this);
		nextImageButton.setOnClickListener(this);
		buyButton.setOnClickListener(this);
		addCarButton.setOnClickListener(this);
		imagePager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				imageIndexText.setText((imagePager.getCurrentItem() + 1) + "/"
						+ imageAdapter.getCount());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		relationList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (!goodsDetail.getProductRelations().get(arg2).getId()
						.equals(goodsDetail.getLocalGoods().getId())) {
					goodsId = goodsDetail.getProductRelations().get(arg2)
							.getId();
					getGoodsDetail();
				}
			}
		});
	}

	/**
	 * 显示商品详情
	 */
	private void showDetail() {
		if (goodsDetail != null) {
			qrEncodeProduct(goodsDetail.getLocalGoods().getId(), goodsDetail
					.getLocalGoods().getRetailProdManagerNumber(), goodsDetail
					.getLocalGoods().getRetailProdManagerName(),
					QrCodeTool.PRODUCT_TYPE_LOCAL_GOODS,
					QrCodeTool.SALE_TYPE_NONE, new OnQrEncodeListener() {
						@Override
						public void onSuccess(QrEncodeMissonMessage message) {
							super.onSuccess(message);
							if (message.getBitmap() != null) {
								qrCodeImageView.setImageBitmap(message
										.getBitmap());
							}
						}
					});
			addBrowsHistory();
			imageAdapter.notifyDataSetChanged();
			relationAdapter.notifyDataSetChanged();
			imageIndexText.setText((imagePager.getCurrentItem() + 1) + "/"
					+ imageAdapter.getCount());
			nameTextView.setText(goodsDetail.getLocalGoods()
					.getRetailProdManagerName());
			priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(goodsDetail.getLocalGoods()
							.getRetailPrice()));
			unitTextView.setText("/" + goodsDetail.getLocalGoods().getUnit());
			webView.loadData(goodsDetail.getLocalGoods()
					.getRetailProdDescribe(), "text/html; charset=utf-8",
					"utf-8");
		}
	}

	private void addBrowsHistory() {
		NetworkContent networkContent = new NetworkContent(
				API.API_PRODUCT_BROWSE_HISTORY);
		networkContent.addParameters("productType", "1");
		networkContent.addParameters("productNumber", goodsDetail
				.getLocalGoods().getRetailProdManagerNumber());
		networkContent.addParameters("equipNo", Device.getDeviceCode());
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener());
	}

	/**
	 * 点击左右导航按钮切换图片
	 * 
	 * @param imagePosition
	 */
	private void setImagePosition(int imagePosition) {
		imagePager.setCurrentItem(imagePosition, true);
		imageIndexText.setText((imagePosition + 1) + "/"
				+ imageAdapter.getCount());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.product_detail_image_last:
			if (imageAdapter.getCount() > 0) {
				if (imagePager.getCurrentItem() == 0) {
					setImagePosition(imageAdapter.getCount() - 1);
				} else {
					setImagePosition(imagePager.getCurrentItem() - 1);
				}
			}
			break;

		case R.id.product_detail_image_next:
			if (imageAdapter.getCount() > 0) {
				if (imagePager.getCurrentItem() == imageAdapter.getCount() - 1) {
					setImagePosition(0);
				} else {
					setImagePosition(imagePager.getCurrentItem() + 1);
				}
			}
			break;

		case R.id.local_goods_detail_buy:
			buy();
			break;

		case R.id.local_goods_detail_car:
			addToCar();
			break;

		default:
			break;

		}
	}

	private void buy() {
		if (goodsDetail != null) {
			Bundle bundle = new Bundle();
			bundle.putString("object", goodsDetail.getLocalGoods()
					.getJsonObject().toString());
			openWindow(LocalGoodsBuyFragment.class.getName(), "购买商品", bundle);
		}
	}

	/**
	 * 添加到购物车
	 */
	private void addToCar() {
		switch (LocalCarController.addGoods(goodsDetail.getLocalGoods())) {
		case 0:
			Notify.show("已添加到购物车");
			break;

		case 1:
			Notify.show("已添加过此商品");
			break;

		case 2:
			Notify.show("添加到购物车失败");
			break;

		default:
			break;
		}
	}

	/**
	 * viewpager适配器
	 */
	private class ImageAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return goodsDetail.getLocalGoods().getImages().size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = layoutInflater.inflate(
					R.layout.adapter_viewpager, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.view_pager_img);
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.view_pager_loading);
			imageLoader.displayImage(getBigImageUrl(goodsDetail.getLocalGoods()
					.getImages().get(position).getRetailProductImgUrl()),
					imageView, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});
			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	private void getGoodsDetail() {
		NetworkContent networkContent = new NetworkContent(
				API.API_LOCAL_BUSINESS_GOODS_DETAIL);
		networkContent.addParameters("retailProductManagerID", goodsId);
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {

					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						resolveData(message);
					}

				});
	}

	private void resolveData(NetworkMissionMessage message) {
		if (!TextUtils.isEmpty(message.getResult())) {
			JSONObject object;
			try {
				object = new JSONObject(message.getResult());
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONObject dataMap = object.optJSONObject("dataMap");
					if (dataMap != null) {
						goodsDetail = new ModelLocalGoodsDetail(dataMap);
						showDetail();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class RelationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return goodsDetail.getProductRelations().size();
		}

		@Override
		public Object getItem(int position) {
			return goodsDetail.getProductRelations().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.list_diliver_payment, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.diliver_payment_check);
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.diliver_payment_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (goodsDetail.getProductRelations().get(position).getId()
					.equals(goodsDetail.getLocalGoods().getId())) {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_normal);
			}
			viewHolder.textView.setText(goodsDetail.getProductRelations()
					.get(position).getAttName());
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView textView;
		}

	}

}
