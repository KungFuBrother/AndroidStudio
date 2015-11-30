package yitgogo.consumer.order.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.product.model.ModelSaleDetailMiaosha;
import yitgogo.consumer.product.model.ModelSaleDetailTejia;
import yitgogo.consumer.product.model.ModelSaleDetailTime;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dtr.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class SaleProductOrderConfirmFragment extends BaseNotifyFragment {

	ImageView imageView;
	TextView nameTextView, priceTextView, countTextView, countAddButton,
			countDeleteButton, additionTextView;

	FrameLayout addressLayout, paymentLayout;
	TextView totalPriceTextView, confirmButton;

	String productId = "";
	int saleType = CaptureActivity.SALE_TYPE_NONE;

	ModelProduct product = new ModelProduct();
	ModelOrderProduct orderProduct = new ModelOrderProduct();

	OrderConfirmPartAddressFragment addressFragment;
	OrderConfirmPartPaymentFragment paymentFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_confirm_order_sale);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(SaleProductOrderConfirmFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(SaleProductOrderConfirmFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new GetProductDetail().execute();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("productId")) {
				productId = bundle.getString("productId");
			}
			if (bundle.containsKey("saleType")) {
				saleType = bundle.getInt("saleType");
			}
		}
		addressFragment = new OrderConfirmPartAddressFragment();
		paymentFragment = new OrderConfirmPartPaymentFragment(true, false);
	}

	protected void findViews() {
		imageView = (ImageView) contentView
				.findViewById(R.id.order_confirm_sale_image);
		nameTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_name);
		priceTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_price);
		countTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_count);
		countDeleteButton = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_count_delete);
		countAddButton = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_count_add);
		additionTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_addition);
		addressLayout = (FrameLayout) contentView
				.findViewById(R.id.order_confirm_sale_address);
		paymentLayout = (FrameLayout) contentView
				.findViewById(R.id.order_confirm_sale_payment);
		totalPriceTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_total_money);
		confirmButton = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_confirm);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		getFragmentManager().beginTransaction()
				.replace(R.id.order_confirm_sale_address, addressFragment)
				.replace(R.id.order_confirm_sale_payment, paymentFragment)
				.commit();
	}

	@Override
	protected void registerViews() {
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addOrder();
			}
		});
		countDeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteCount();
			}
		});
		countAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addCount();
			}
		});
	}

	private void showProductInfo() {
		ImageLoader.getInstance().displayImage(product.getImg(), imageView);
		nameTextView.setText(product.getProductName());
		if (orderProduct.isSale()) {
			priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(orderProduct.getSalePrice()));
		} else {
			priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(product.getPrice()));
		}
		countTextView.setText(orderProduct.getBuyCount() + "");
		additionTextView.setText(orderProduct.getAddition());
		totalPriceTextView.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(countTotalPrice()));
	}

	private void deleteCount() {
		if (orderProduct.getBuyCount() > 1) {
			orderProduct.setBuyCount(orderProduct.getBuyCount() - 1);
		}
		if (orderProduct.getBuyCount() == 1) {
			countDeleteButton.setClickable(false);
		}
		countAddButton.setClickable(true);
		countTextView.setText(orderProduct.getBuyCount() + "");
		totalPriceTextView.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(countTotalPrice()));
	}

	private void addCount() {
		if (orderProduct.isSale()) {
			if (orderProduct.getMaxBuyCount() > 0) {
				if (orderProduct.getMaxBuyCount() > orderProduct.getStock()) {
					if (orderProduct.getBuyCount() < orderProduct.getStock()) {
						orderProduct
								.setBuyCount(orderProduct.getBuyCount() + 1);
					}
					if (orderProduct.getBuyCount() == orderProduct.getStock()) {
						countAddButton.setClickable(false);
					}
				} else {
					if (orderProduct.getBuyCount() < orderProduct
							.getMaxBuyCount()) {
						orderProduct
								.setBuyCount(orderProduct.getBuyCount() + 1);
					}
					if (orderProduct.getBuyCount() == orderProduct
							.getMaxBuyCount()) {
						countAddButton.setClickable(false);
					}
				}
			} else if (orderProduct.getStock() > 0) {
				if (orderProduct.getBuyCount() < orderProduct.getStock()) {
					orderProduct.setBuyCount(orderProduct.getBuyCount() + 1);
				}
				if (orderProduct.getBuyCount() == orderProduct.getStock()) {
					countAddButton.setClickable(false);
				}
			} else {
				if (orderProduct.getBuyCount() < product.getNum()) {
					orderProduct.setBuyCount(orderProduct.getBuyCount() + 1);
				}
				if (orderProduct.getBuyCount() == product.getNum()) {
					countAddButton.setClickable(false);
				}
			}
		} else {
			if (orderProduct.getBuyCount() < product.getNum()) {
				orderProduct.setBuyCount(orderProduct.getBuyCount() + 1);
			}
			if (orderProduct.getBuyCount() == product.getNum()) {
				countAddButton.setClickable(false);
			}
		}
		countDeleteButton.setClickable(true);
		countTextView.setText(orderProduct.getBuyCount() + "");
		totalPriceTextView.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(countTotalPrice()));
	}

	private void getProductSaleInfo() {
		switch (saleType) {
		case CaptureActivity.SALE_TYPE_MIAOSHA:
			new GetMiaoshaSaleDetail().execute();
			break;

		case CaptureActivity.SALE_TYPE_TEJIA:
			new GetTejiaSaleDetail().execute();
			break;

		case CaptureActivity.SALE_TYPE_TIME:
			new GetTimeSaleDetail().execute();
			break;

		default:
			showProductInfo();
			break;
		}
	}

	private double countTotalPrice() {
		double totalPrice = 0;
		if (orderProduct.isSale()) {
			totalPrice = orderProduct.getSalePrice()
					* orderProduct.getBuyCount();
		} else {
			totalPrice = product.getPrice() * orderProduct.getBuyCount();
		}
		return totalPrice;
	}

	private void addOrder() {
		if (addressFragment.getAddress() == null) {
			Notify.show("收货人信息有误");
		} else {
			new AddOrder().execute();
		}
	}

	/**
	 * 促销产品下单
	 * 
	 * @author Tiger
	 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{
	 *         "zhekouhou"
	 *         :"34.0","zongzhekou":"0.0","fuwuZuoji":"028-2356895623"
	 *         ,"zongjine":"34.0","productInfo":
	 *         "[{\"spname\":\"韵思家具 法式田园抽屉储物六斗柜 欧式复古白色五斗柜 4斗柜 KSDG01\",\"price\":\"34.0\",\"Amount\":\"34.0\",\"num\":\"1\"}]"
	 *         ,"ordernumber":"YT5431669380","totalIntegral":"0","fuwushang":
	 *         "测试运营中心一"
	 *         ,"shijian":"2015-07-31","fuwuPhone":"15878978945"}],"totalCount"
	 *         :1,"dataMap":{},"object":null}
	 */
	class AddOrder extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userNumber", User
					.getUser().getUseraccount()));
			nameValuePairs.add(new BasicNameValuePair("customerName",
					addressFragment.getAddress().getPersonName()));
			nameValuePairs.add(new BasicNameValuePair("phone", addressFragment
					.getAddress().getPhone()));
			nameValuePairs
					.add(new BasicNameValuePair("shippingaddress",
							addressFragment.getAddress().getAreaAddress()
									+ addressFragment.getAddress()
											.getDetailedAddress()));
			nameValuePairs.add(new BasicNameValuePair("totalMoney",
					countTotalPrice() + ""));
			nameValuePairs.add(new BasicNameValuePair("sex", User.getUser()
					.getSex()));
			nameValuePairs.add(new BasicNameValuePair("age", User.getUser()
					.getAge()));
			nameValuePairs.add(new BasicNameValuePair("address", Store
					.getStore().getStoreArea()));
			nameValuePairs.add(new BasicNameValuePair("jmdId", Store.getStore()
					.getStoreId()));
			nameValuePairs.add(new BasicNameValuePair("orderType", "0"));
			JSONArray orderArray = new JSONArray();

			try {
				JSONObject object = new JSONObject();
				object.put("productIds", productId);
				object.put("shopNum", orderProduct.getBuyCount());
				if (orderProduct.isSale()) {
					object.put("price", orderProduct.getSalePrice());
				} else {
					object.put("price", product.getPrice());
				}
				switch (saleType) {
				case CaptureActivity.SALE_TYPE_MIAOSHA:
					object.put("isIntegralMall", 2);
					break;
				default:
					object.put("isIntegralMall", 0);
					break;
				}
				orderArray.put(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			nameValuePairs.add(new BasicNameValuePair("data", orderArray
					.toString()));
			return netUtil.postWithoutCookie(API.API_ORDER_ADD_CENTER,
					nameValuePairs, false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						Toast.makeText(getActivity(), "下单成功",
								Toast.LENGTH_SHORT).show();
						if (paymentFragment.getPaymentType() == OrderConfirmPartPaymentFragment.PAY_TYPE_CODE_ONLINE) {
							payMoney(object.optJSONArray("object"));
							getActivity().finish();
							return;
						}
						showOrder(PayFragment.ORDER_TYPE_YY);
						getActivity().finish();
						return;
					} else {
						hideLoading();
						Notify.show(object.optString("message"));
						return;
					}
				} catch (JSONException e) {
					hideLoading();
					Notify.show("下单失败");
					e.printStackTrace();
					return;
				}
			}
			hideLoading();
			Notify.show("下单失败");
		}
	}

	/**
	 * 获取商品详情
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
			nameValuePairs.add(new BasicNameValuePair("jmdId", Store.getStore()
					.getStoreId()));
			nameValuePairs.add(new BasicNameValuePair("productId", productId));
			String result = netUtil.postWithoutCookie(API.API_PRODUCT_DETAIL,
					nameValuePairs, false, false);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				{
					JSONObject object;
					try {
						object = new JSONObject(result);
						if (object.getString("state").equalsIgnoreCase(
								"SUCCESS")) {
							JSONObject detailObject = object
									.optJSONObject("dataMap");
							if (detailObject != null) {
								product = new ModelProduct(detailObject);
								getProductSaleInfo();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 秒杀商品详情
	 * 
	 * @author Tiger
	 * 
	 */
	class GetMiaoshaSaleDetail extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("productId", productId));
			return netUtil.postWithCookie(API.API_SALE_MIAOSHA_DETAIL,
					valuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			try {
				ModelSaleDetailMiaosha saleDetailMiaosha = new ModelSaleDetailMiaosha(
						result);
				if (saleDetailMiaosha != null) {
					orderProduct = new ModelOrderProduct(saleDetailMiaosha);
					showProductInfo();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 限时促销商品详情
	 * 
	 * @author Tiger
	 * 
	 */
	class GetTimeSaleDetail extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("productId", productId));
			return netUtil.postWithCookie(API.API_SALE_TIME_DETAIL, valuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			try {
				ModelSaleDetailTime saleDetailTime = new ModelSaleDetailTime(
						result);
				if (saleDetailTime != null) {
					orderProduct = new ModelOrderProduct(saleDetailTime);
					showProductInfo();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 特价促销商品详情
	 * 
	 * @author Tiger
	 * 
	 */
	class GetTejiaSaleDetail extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("productId", productId));
			return netUtil
					.postWithCookie(API.API_SALE_TEJIA_DETAIL, valuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			try {
				ModelSaleDetailTejia saleDetailTejia = new ModelSaleDetailTejia(
						result);
				if (saleDetailTejia != null) {
					orderProduct = new ModelOrderProduct(saleDetailTejia);
					showProductInfo();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class ModelOrderProduct {

		double salePrice = -1, price = -1;
		boolean isSale = false;
		long buyCount = 1, stock = -1, maxBuyCount = -1;
		String addition = "";

		public ModelOrderProduct(ModelSaleDetailMiaosha saleDetailMiaosha) {
			if (saleDetailMiaosha != null) {
				salePrice = saleDetailMiaosha.getSeckillPrice();
				price = saleDetailMiaosha.getPrice();
				if (saleDetailMiaosha.getSeckillPrice() > 0) {
					// 开始时间<=当前时间，活动已开始
					if (saleDetailMiaosha.getStartTime() <= Calendar
							.getInstance().getTime().getTime()) {
						// 剩余秒杀数量>0，显示秒杀信息
						if (saleDetailMiaosha.getSeckillNUmber() > 0) {
							isSale = true;
							stock = saleDetailMiaosha.getSeckillNUmber();
							maxBuyCount = saleDetailMiaosha.getMemberNumber();
							addition = "剩余" + stock + "件，限购" + maxBuyCount
									+ "件";
						} else {
							isSale = false;
							addition = "秒杀结束，按原价购买";
						}
					} else {
						// 开始时间>当前时间，活动未开始，显示预告
						isSale = false;
						addition = "秒杀未开始，按原价购买";
					}
				}
			}
		}

		public ModelOrderProduct(ModelSaleDetailTejia saleDetailTejia) {
			if (saleDetailTejia != null) {
				salePrice = saleDetailTejia.getSalePrice();
				price = saleDetailTejia.getPrice();
				if (saleDetailTejia.getSalePrice() > 0) {
					if (saleDetailTejia.getNumbers() > 0) {
						isSale = true;
						stock = saleDetailTejia.getNumbers();
					} else {
						isSale = false;
						addition = "活动已结束，按原价购买";
					}
				}
			}
		}

		public ModelOrderProduct(ModelSaleDetailTime saleDetailTime) {
			if (saleDetailTime != null) {
				salePrice = saleDetailTime.getPromotionPrice();
				price = saleDetailTime.getPrice();
				if (saleDetailTime.getPromotionPrice() > 0) {
					// 开始时间>当前时间，未开始，显示活动预告
					if (saleDetailTime.getStartTime() > Calendar.getInstance()
							.getTime().getTime()) {
						isSale = false;
						addition = "活动未开始，按原价购买";
					} else if (saleDetailTime.getEndTime() > Calendar
							.getInstance().getTime().getTime()) {
						// 开始时间<=当前时间，结束时间>当前时间，已开始未结束，活动进行时
						isSale = true;
					} else {
						// 活动结束
						isSale = false;
						addition = "活动已结束，按原价购买";
					}
				}
			}
		}

		public ModelOrderProduct() {
		}

		public double getSalePrice() {
			return salePrice;
		}

		public double getPrice() {
			return price;
		}

		public boolean isSale() {
			return isSale;
		}

		public long getBuyCount() {
			return buyCount;
		}

		public long getStock() {
			return stock;
		}

		public long getMaxBuyCount() {
			return maxBuyCount;
		}

		public String getAddition() {
			return addition;
		}

		public void setBuyCount(long buyCount) {
			this.buyCount = buyCount;
		}

	}

}
