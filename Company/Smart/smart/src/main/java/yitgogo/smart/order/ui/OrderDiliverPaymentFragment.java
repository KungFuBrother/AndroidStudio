package yitgogo.smart.order.ui;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.order.model.ModelDiliver;
import yitgogo.smart.order.model.ModelPayment;
import yitgogo.smart.view.InnerListView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;

public class OrderDiliverPaymentFragment extends BaseNormalFragment {

	InnerListView diliverListView, paymentListView;
	List<ModelDiliver> dilivers = new ArrayList<ModelDiliver>();
	ModelDiliver selectedDiliver = new ModelDiliver();
	DiliverAdapter diliverAdapter;

	List<ModelPayment> payments = new ArrayList<ModelPayment>();
	ModelPayment selectedPayment = new ModelPayment();
	PaymentAdapter paymentAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		diliverAdapter = new DiliverAdapter();
		paymentAdapter = new PaymentAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_diliver_payment, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		diliverListView = (InnerListView) view.findViewById(R.id.diliver_type);
		paymentListView = (InnerListView) view.findViewById(R.id.payment_type);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		diliverListView.setAdapter(diliverAdapter);
		paymentListView.setAdapter(paymentAdapter);
	}

	@Override
	protected void registerViews() {
		diliverListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedDiliver = dilivers.get(arg2);
				diliverAdapter.notifyDataSetChanged();
				onDiliverChange();
			}
		});
		paymentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedPayment = payments.get(arg2);
				paymentAdapter.notifyDataSetChanged();
			}
		});
	}

	public void addDiliverType(int type, String name) {
		dilivers.add(new ModelDiliver(type, name));
	}

	public void addPaymentType(int type, String name) {
		payments.add(new ModelPayment(type, name));
	}

	class DiliverAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dilivers.size();
		}

		@Override
		public Object getItem(int position) {
			return dilivers.get(position);
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
			if (selectedDiliver.getType() == dilivers.get(position).getType()) {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_normal);
			}
			viewHolder.textView.setText(dilivers.get(position).getName());
			return convertView;
		}

	}

	class PaymentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return payments.size();
		}

		@Override
		public Object getItem(int position) {
			return payments.get(position);
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
			if (selectedPayment.getType() == payments.get(position).getType()) {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_normal);
			}
			viewHolder.textView.setText(payments.get(position).getName());
			return convertView;
		}

	}

	class ViewHolder {
		ImageView imageView;
		TextView textView;
	}

	public ModelDiliver getSelectedDiliver() {
		return selectedDiliver;
	}

	public ModelPayment getSelectedPayment() {
		return selectedPayment;
	}

	public void onDiliverChange() {
	}
}
