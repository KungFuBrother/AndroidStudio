package yitgogo.consumer.order.ui;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.view.Notify;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

public class OrderConfirmPartDeliverFragment extends BaseNormalFragment {

	RadioGroup sendTypeGroup;
	LinearLayout selfAddressLayout;
	TextView selfAddressTextView, tipTextView;
	int deliverNum = 0, buyCount = 0;

	public final static int DELIVER_TYPE_SELF = 0;
	public final static int DELIVER_TYPE_TOHOME = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_confirm_order_deliver, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		sendTypeGroup = (RadioGroup) view.findViewById(R.id.order_deliver_type);
		selfAddressLayout = (LinearLayout) view
				.findViewById(R.id.order_deliver_address_layout);
		selfAddressTextView = (TextView) view
				.findViewById(R.id.order_deliver_address);
		tipTextView = (TextView) view.findViewById(R.id.order_deliver_tip);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		selfAddressTextView.setText(Store.getStore().getStoreAddess()
				+ "\n联系人：" + Store.getStore().getStoreContactor() + "\n"
				+ Store.getStore().getStoreName());
		sendTypeGroup.check(R.id.order_deliver_self);
		selectDeliverType();
	}

	private void selectDeliverType() {
		if (sendTypeGroup.getCheckedRadioButtonId() == R.id.order_deliver_self) {
			selfAddressLayout.setVisibility(View.VISIBLE);
			tipTextView.setVisibility(View.GONE);
		} else {
			selfAddressLayout.setVisibility(View.GONE);
			tipTextView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void registerViews() {
		sendTypeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.order_deliver_tohome) {
					if (buyCount < deliverNum) {
						Notify.show("购满" + deliverNum + "件才可享受送货上门服务");
						sendTypeGroup.check(R.id.order_deliver_self);
					}
				}
				selectDeliverType();
			}
		});
	}

	/**
	 * 初始化配送方式
	 * 
	 * @param deliverYN
	 *            是否支持送货上门
	 * @param deliverNum
	 *            满多少送货
	 */
	public void initDeliverType(boolean deliverYN, int deliverNum) {
		this.deliverNum = deliverNum;
		if (!deliverYN) {
			sendTypeGroup.removeViewAt(1);
			this.deliverNum = 10000;
		}
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	public int getDeliverType() {
		switch (sendTypeGroup.getCheckedRadioButtonId()) {
		case R.id.order_deliver_self:
			return DELIVER_TYPE_SELF;

		case R.id.order_deliver_tohome:
			return DELIVER_TYPE_TOHOME;

		default:
			return DELIVER_TYPE_SELF;
		}
	}

	public String getDeliverTypeName() {

		switch (sendTypeGroup.getCheckedRadioButtonId()) {
		case R.id.order_deliver_self:
			return "自取";

		case R.id.order_deliver_tohome:
			return "送货上门";

		default:
			return "自取";
		}
	}
}
