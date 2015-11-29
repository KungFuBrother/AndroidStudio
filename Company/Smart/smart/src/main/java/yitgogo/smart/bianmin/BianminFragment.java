package yitgogo.smart.bianmin;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.bianmin.game.ui.GameFilterFragment;
import yitgogo.smart.bianmin.phoneCharge.ui.PhoneChargeFastFragment;
import yitgogo.smart.bianmin.phoneCharge.ui.PhoneChargeSlowFragment;
import yitgogo.smart.bianmin.qq.ui.QQChargeFragment;
import yitgogo.smart.bianmin.telephone.ui.TelePhoneChargeFragment;
import yitgogo.smart.order.model.User;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class BianminFragment extends BaseNormalFragment {

	ListView listView;
	List<ModelBianmin> bianmins;
	BianminAdapter bianminAdapter;
	public static User user = new User();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(BianminFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(BianminFragment.class.getName());
	}

	private void init() {
		bianmins = new ArrayList<BianminFragment.ModelBianmin>();
		bianmins.add(new ModelBianmin(R.drawable.ic_bianmin_phone, "手机充值(快充)",
				new PhoneChargeFastFragment()));
		bianmins.add(new ModelBianmin(R.drawable.ic_bianmin_phone, "手机充值(慢充)",
				new PhoneChargeSlowFragment()));
		bianmins.add(new ModelBianmin(R.drawable.ic_bianmin_kuandai, "固话宽带",
				new TelePhoneChargeFragment()));
		bianmins.add(new ModelBianmin(R.drawable.ic_bianmin_qq, "QQ充值",
				new QQChargeFragment()));
		bianmins.add(new ModelBianmin(R.drawable.ic_bianmin_game, "游戏充值",
				new GameFilterFragment()));
		bianminAdapter = new BianminAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bianmin, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		listView = (ListView) view.findViewById(R.id.bianmin_list);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		select(0);
		listView.setAdapter(bianminAdapter);
	}

	@Override
	protected void registerViews() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				select(arg2);
			}
		});
	}

	private void select(int selection) {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.bianmin_fragment,
						bianmins.get(selection).getFragment()).commit();
	}

	class BianminAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return bianmins.size();
		}

		@Override
		public Object getItem(int position) {
			return bianmins.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.item_bianmin,
						null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.bianmin_icon);
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.bianmin_name);
				LayoutParams layoutParams = new LayoutParams(
						LayoutParams.MATCH_PARENT, 160);
				convertView.setLayoutParams(layoutParams);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.imageView.setImageResource(bianmins.get(position)
					.getIcon());
			viewHolder.textView.setText(bianmins.get(position).getName());
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView textView;
		}

	}

	class ModelBianmin {

		int icon = R.drawable.icon_consumer;
		String name = "";
		Fragment fragment = new Fragment();

		public ModelBianmin(int icon, String name, Fragment fragment) {
			this.fragment = fragment;
			this.icon = icon;
			this.name = name;
		}

		public int getIcon() {
			return icon;
		}

		public String getName() {
			return name;
		}

		public Fragment getFragment() {
			return fragment;
		}

	}

}
