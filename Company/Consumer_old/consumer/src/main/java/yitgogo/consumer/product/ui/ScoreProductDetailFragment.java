package yitgogo.consumer.product.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.home.model.ModelScoreProductDetail;
import yitgogo.consumer.order.ui.ScoreProductOrderConfirmFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.Notify;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class ScoreProductDetailFragment extends BaseNotifyFragment implements
		OnClickListener {

	ViewPager imagePager;
	LinearLayout htmlButton;
	TextView nameTextView, descriptionTextView, attrTextView, priceTextView,
			scoreTextView, stateTextView;
	ImageView lastImageButton, nextImageButton;
	TextView imageIndexText;
	TextView buyButton;

	ImageAdapter imageAdapter;
	String productId = "";
	ModelScoreProductDetail productDetail;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_product_score_detail);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ScoreProductDetailFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ScoreProductDetailFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new GetProductDetail().execute();
	}

	private void init() {
		measureScreen();
		Bundle bundle = getArguments();
		if (bundle.containsKey("productId")) {
			productId = bundle.getString("productId");
		}
		productDetail = new ModelScoreProductDetail();
		imageAdapter = new ImageAdapter();
	}

	protected void findViews() {
		imagePager = (ViewPager) contentView
				.findViewById(R.id.score_product_detail_images);
		htmlButton = (LinearLayout) contentView
				.findViewById(R.id.score_product_detail_html);
		nameTextView = (TextView) contentView
				.findViewById(R.id.score_product_detail_name);
		descriptionTextView = (TextView) contentView
				.findViewById(R.id.score_product_detail_description);
		attrTextView = (TextView) contentView
				.findViewById(R.id.score_product_detail_attr_name);
		priceTextView = (TextView) contentView
				.findViewById(R.id.score_product_detail_price);
		scoreTextView = (TextView) contentView
				.findViewById(R.id.score_product_detail_score);
		stateTextView = (TextView) contentView
				.findViewById(R.id.score_product_detail_state);
		lastImageButton = (ImageView) contentView
				.findViewById(R.id.score_product_detail_image_last);
		nextImageButton = (ImageView) contentView
				.findViewById(R.id.score_product_detail_image_next);
		imageIndexText = (TextView) contentView
				.findViewById(R.id.score_product_detail_image_index);
		buyButton = (TextView) contentView
				.findViewById(R.id.score_product_detail_buy);
		initViews();
		registerViews();
	}

	protected void initViews() {
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				screenWidth, screenWidth);
		imagePager.setLayoutParams(layoutParams);
		imagePager.setAdapter(imageAdapter);
	}

	@Override
	protected void registerViews() {
		htmlButton.setOnClickListener(this);
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
		imageAdapter.notifyDataSetChanged();
		nameTextView.setText(productDetail.getName());
		descriptionTextView.setText(productDetail.getNeirong());
		attrTextView.setText(productDetail.getAttr());
		priceTextView.setText("¥"
				+ decimalFormat.format(productDetail.getJifenjia()));
		scoreTextView.setText("+" + productDetail.getJifen() + "积分");
		if (productDetail.getNo() <= 0) {
			stateTextView.setText("无货");
		} else {
			stateTextView.setText("有货");
		}
		if (imageAdapter.getCount() > 0) {
			imageIndexText.setText(1 + "/" + imageAdapter.getCount());
		}
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
		if (User.getUser().isLogin()) {
			if (productDetail.getNo() > 0) {
				Bundle bundle = new Bundle();
				bundle.putString("productId", productDetail.getId());
				jump(ScoreProductOrderConfirmFragment.class.getName(), "确认订单",
						bundle);
			} else {
				Notify.show("此商品无货，暂不能购买");
			}
		} else {
			Notify.show("请先登录");
			jump(UserLoginFragment.class.getName(), "会员登录");
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

	/**
	 * 获取积分商品详情
	 * 
	 * @author Tiger
	 * 
	 */
	class GetProductDetail extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", productId));
			return netUtil.postWithoutCookie(API.API_SCORE_PRODUCT_DETAIL,
					nameValuePairs, false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() == 0) {
				htmlButton.setClickable(false);
				buyButton.setClickable(false);
			} else {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						productDetail = new ModelScoreProductDetail(
								object.optJSONObject("dataMap"));
						showDetail();
					} else {
						htmlButton.setClickable(false);
						buyButton.setClickable(false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.score_product_detail_html:
			Bundle bundle = new Bundle();
			bundle.putString("html", productDetail.getXiangqing());
			bundle.putInt("type", WebFragment.TYPE_HTML);
			jump(WebFragment.class.getName(), productDetail.getName(), bundle);
			break;

		case R.id.score_product_detail_image_last:
			if (imageAdapter.getCount() > 0) {
				if (imagePager.getCurrentItem() == 0) {
					setImagePosition(imageAdapter.getCount() - 1);
				} else {
					setImagePosition(imagePager.getCurrentItem() - 1);
				}
			}
			break;

		case R.id.score_product_detail_image_next:
			if (imageAdapter.getCount() > 0) {
				if (imagePager.getCurrentItem() == imageAdapter.getCount() - 1) {
					setImagePosition(0);
				} else {
					setImagePosition(imagePager.getCurrentItem() + 1);
				}
			}
			break;

		case R.id.score_product_detail_buy:
			buy();
			break;

		default:
			break;

		}
	}

}
