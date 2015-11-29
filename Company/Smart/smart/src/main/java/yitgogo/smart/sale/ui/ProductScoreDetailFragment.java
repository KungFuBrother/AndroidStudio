package yitgogo.smart.sale.ui;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.ModelScoreProductDetail;
import yitgogo.smart.order.ui.ProductScoreBuyFragment;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.OnQrEncodeListener;
import yitgogo.smart.tools.QrCodeTool;
import yitgogo.smart.tools.QrEncodeMissonMessage;
import yitgogo.smart.tools.ScreenUtil;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class ProductScoreDetailFragment extends BaseNotifyFragment implements
		OnClickListener {

	ViewPager imagePager;
	TextView nameTextView, descriptionTextView, attrTextView, priceTextView,
			scorePriceTextView, stateTextView;
	ImageView lastImageButton, nextImageButton;
	TextView imageIndexText;
	TextView buyButton;
	ImageView qrCodeImageView;

	ImageAdapter imageAdapter;
	String productId = "";
	ModelScoreProductDetail productDetail;

	WebView webView;
	ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_fragment_product_score_detail);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ProductScoreDetailFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ProductScoreDetailFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getProductDetail();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("productId")) {
				productId = bundle.getString("productId");
			}
		}
		productDetail = new ModelScoreProductDetail();
		imageAdapter = new ImageAdapter();
	}

	protected void findViews() {
		imagePager = (ViewPager) contentView
				.findViewById(R.id.product_detail_images);
		nameTextView = (TextView) contentView
				.findViewById(R.id.product_detail_name);
		descriptionTextView = (TextView) contentView
				.findViewById(R.id.product_detail_description);
		attrTextView = (TextView) contentView
				.findViewById(R.id.product_detail_attr);

		qrCodeImageView = (ImageView) contentView
				.findViewById(R.id.product_detail_qrcode);
		priceTextView = (TextView) contentView
				.findViewById(R.id.product_detail_price);
		scorePriceTextView = (TextView) contentView
				.findViewById(R.id.product_detail_price_score);
		stateTextView = (TextView) contentView
				.findViewById(R.id.product_detail_state);
		lastImageButton = (ImageView) contentView
				.findViewById(R.id.product_detail_image_last);
		nextImageButton = (ImageView) contentView
				.findViewById(R.id.product_detail_image_next);
		imageIndexText = (TextView) contentView
				.findViewById(R.id.product_detail_image_index);
		buyButton = (TextView) contentView
				.findViewById(R.id.product_detail_buy);

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
		qrEncodeProduct(productDetail.getId(), productDetail.getNumber(),
				productDetail.getName(), QrCodeTool.PRODUCT_TYPE_SCORE,
				QrCodeTool.SALE_TYPE_NONE, new OnQrEncodeListener() {
					@Override
					public void onSuccess(QrEncodeMissonMessage message) {
						super.onSuccess(message);
						if (message.getBitmap() != null) {
							qrCodeImageView.setImageBitmap(message.getBitmap());
						}
					}
				});
		addBrowsHistory();
		imageAdapter.notifyDataSetChanged();
		nameTextView.setText(productDetail.getName());
		descriptionTextView.setText(productDetail.getNeirong());
		attrTextView.setText(productDetail.getAttr());
		priceTextView.setText("¥"
				+ decimalFormat.format(productDetail.getJifenjia()));
		scorePriceTextView.setText("+" + productDetail.getJifen() + "积分");
		if (productDetail.getNo() <= 0) {
			stateTextView.setText("无货");
		} else {
			stateTextView.setText("有货");
		}
		if (imageAdapter.getCount() > 0) {
			imageIndexText.setText(1 + "/" + imageAdapter.getCount());
		}
		webView.loadData(productDetail.getXiangqing(),
				"text/html; charset=utf-8", "utf-8");
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

	/**
	 * 添加到购物车
	 */
	private void buy() {
		if (productDetail.getNo() > 0) {
			Bundle bundle = new Bundle();
			bundle.putString("object", productDetail.getJsonObject().toString());
			openWindow(ProductScoreBuyFragment.class.getName(), "确认订单", bundle);
		} else {
			Notify.show("此商品无货，暂不能购买");
		}
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

		case R.id.product_detail_buy:
			buy();
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
			return productDetail.getImgs().size();
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
			ImageLoader.getInstance().displayImage(
					getBigImageUrl(productDetail.getImgs().get(position)),
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

	private void addBrowsHistory() {
		NetworkContent networkContent = new NetworkContent(
				API.API_PRODUCT_BROWSE_HISTORY);
		networkContent.addParameters("productType", "0");
		networkContent
				.addParameters("productNumber", productDetail.getNumber());
		networkContent.addParameters("equipNo", Device.getDeviceCode());
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener());
	}

	/**
	 * 获取积分商品详情
	 * 
	 * @author Tiger
	 * 
	 */
	private void getProductDetail() {
		NetworkContent networkContent = new NetworkContent(
				API.API_SCORE_PRODUCT_DETAIL);
		networkContent.addParameters("id", productId);
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
								if (object.getString("state").equalsIgnoreCase(
										"SUCCESS")) {
									productDetail = new ModelScoreProductDetail(
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
