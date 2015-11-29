package yitgogo.smart.local;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.model.ModelLocalSaleMiaoshaDetail;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class LocalSaleMiaoshaDetailFragment extends BaseNotifyFragment
		implements OnClickListener {

	String id = "";
	ModelLocalSaleMiaoshaDetail localSaleMiaoshaDetail;

	ViewPager imagePager;
	TextView nameTextView, priceTextView, unitTextView, stateTextView;
	ImageView lastImageButton, nextImageButton;
	TextView imageIndexText;
	TextView buyButton;
	ImageView qrCodeImageView;

	ImageAdapter imageAdapter;

	WebView webView;
	ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_local_sale_miaosha_detail);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalSaleMiaoshaDetailFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalSaleMiaoshaDetailFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getLocalSaleMiaoshaDetail();
	}

	private void init() {
		measureScreen();
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("id")) {
				id = bundle.getString("id");
			}
		}
		localSaleMiaoshaDetail = new ModelLocalSaleMiaoshaDetail();
		imageAdapter = new ImageAdapter();
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
				.findViewById(R.id.local_sale_miaosha_detail_name);
		priceTextView = (TextView) contentView
				.findViewById(R.id.local_sale_miaosha_detail_price);
		unitTextView = (TextView) contentView
				.findViewById(R.id.local_sale_miaosha_detail_unit);
		stateTextView = (TextView) contentView
				.findViewById(R.id.local_sale_miaosha_detail_state);

		buyButton = (TextView) contentView
				.findViewById(R.id.local_sale_miaosha_detail_buy);

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
	}

	/**
	 * 显示商品详情
	 */
	private void showDetail() {
		if (localSaleMiaoshaDetail != null) {
			qrEncodeProduct(localSaleMiaoshaDetail.getId(),
					localSaleMiaoshaDetail.getNumber(),
					localSaleMiaoshaDetail.getProductName(),
					QrCodeTool.PRODUCT_TYPE_LOCAL_GOODS,
					QrCodeTool.SALE_TYPE_LOCAL_MIAOSHA,
					new OnQrEncodeListener() {
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
			imagePager.setCurrentItem(0);
			nameTextView.setText(localSaleMiaoshaDetail.getProductName());
			priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localSaleMiaoshaDetail
							.getSeckillPrice()));
			unitTextView.setText("原价:" + Parameters.CONSTANT_RMB
					+ decimalFormat.format(localSaleMiaoshaDetail.getPrice()));
			webView.loadData(localSaleMiaoshaDetail.getProductDetais(),
					"text/html; charset=utf-8", "utf-8");
		}
	}

	private void addBrowsHistory() {
		NetworkContent networkContent = new NetworkContent(
				API.API_PRODUCT_BROWSE_HISTORY);
		networkContent.addParameters("productType", "1");
		networkContent.addParameters("productNumber",
				localSaleMiaoshaDetail.getNumber());
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

		case R.id.local_sale_miaosha_detail_buy:
			buy();
			break;

		default:
			break;

		}
	}

	private void buy() {
		if (localSaleMiaoshaDetail != null) {
			Bundle bundle = new Bundle();
			bundle.putString("object", localSaleMiaoshaDetail.getJsonObject()
					.toString());
			openWindow(LocalSaleMiaoshaBuyFragment.class.getName(), "购买商品",
					bundle);
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
			return localSaleMiaoshaDetail.getImages().size();
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
			imageLoader.displayImage(getBigImageUrl(localSaleMiaoshaDetail
					.getImages().get(position).getImgName()), imageView,
					new SimpleImageLoadingListener() {
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

	private void getLocalSaleMiaoshaDetail() {
		NetworkContent networkContent = new NetworkContent(
				API.API_LOCAL_SALE_MIAOSHA_DETAIL);
		networkContent.addParameters("id", id);
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
						if (!TextUtils.isEmpty(message.getResult())) {
							try {
								JSONObject object = new JSONObject(message
										.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									localSaleMiaoshaDetail = new ModelLocalSaleMiaoshaDetail(
											object.optJSONObject("dataMap"));
									showDetail();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

}
