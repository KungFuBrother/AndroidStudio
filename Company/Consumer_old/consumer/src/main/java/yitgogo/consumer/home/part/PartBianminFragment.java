package yitgogo.consumer.home.part;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.activity.ActivityFragment;
import yitgogo.consumer.bianmin.phoneCharge.ui.PhoneChargeFragment;
import yitgogo.consumer.bianmin.qq.ui.QQChargeFragment;
import yitgogo.consumer.bianmin.telephone.ui.TelePhoneChargeFragment;
import yitgogo.consumer.money.ui.MoneyHomeFragment;
import yitgogo.consumer.order.ui.OrderFragment;
import yitgogo.consumer.product.ui.ProductScoreFragment;
import yitgogo.consumer.product.ui.ShoppingCarFragment;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;

public class PartBianminFragment extends BaseNormalFragment {

    static PartBianminFragment bianminFragment;
    List<ModelBianmin> bianmins;
    BianminAdapter bianminAdapter;
    GridView bianminGridView;

    class ModelBianmin {

        String name = "";
        int image = R.drawable.loading_default;
        String fragmentName = "";
        boolean needLogin = false;

        public ModelBianmin(String name, int image, String fragmentName,
                            boolean needLogin) {
            this.fragmentName = fragmentName;
            this.image = image;
            this.name = name;
            this.needLogin = needLogin;
        }

        public String getName() {
            return name;
        }

        public int getImage() {
            return image;
        }

        public String getFragmentName() {
            return fragmentName;
        }

        public boolean isNeedLogin() {
            return needLogin;
        }

    }

    public static PartBianminFragment getBianminFragment() {
        if (bianminFragment == null) {
            bianminFragment = new PartBianminFragment();
        }
        return bianminFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        measureScreen();
        bianmins = new ArrayList<PartBianminFragment.ModelBianmin>();
        bianmins.add(new ModelBianmin("手机充值", R.drawable.ic_bianmin_phone,
                PhoneChargeFragment.class.getName(), true));
        bianmins.add(new ModelBianmin("固话宽带", R.drawable.ic_bianmin_kuandai,
                TelePhoneChargeFragment.class.getName(), true));
        bianmins.add(new ModelBianmin("QQ充值", R.drawable.ic_bianmin_qq,
                QQChargeFragment.class.getName(), true));
        bianmins.add(new ModelBianmin("摇一摇", R.drawable.ic_home_shake,
                ActivityFragment.class.getName(), false));
//		bianmins.add(new ModelBianmin("游戏充值", R.drawable.ic_bianmin_game,
//				GameFilterFragment.class.getName(), true));
        bianmins.add(new ModelBianmin("钱袋子", R.drawable.ic_money_pag,
                MoneyHomeFragment.class.getName(), true));
        bianmins.add(new ModelBianmin("我的订单", R.drawable.icon_home_order,
                OrderFragment.class.getName(), true));
        bianmins.add(new ModelBianmin("积分购", R.drawable.icon_home_score,
                ProductScoreFragment.class.getName(), false));
        bianminAdapter = new BianminAdapter();
        bianmins.add(new ModelBianmin("购物车", R.drawable.icon_car,
                ShoppingCarFragment.class.getName(), false));
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_part_bianmin, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        bianminGridView = (GridView) view.findViewById(R.id.part_bianmin_items);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        bianminGridView.setAdapter(bianminAdapter);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                screenWidth / 5 * 2);
        bianminGridView.setLayoutParams(layoutParams);
    }

    @Override
    protected void registerViews() {
        bianminGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (bianmins.get(arg2).isNeedLogin()) {
                    if (!User.getUser().isLogin()) {
                        jump(UserLoginFragment.class.getName(), "会员登录");
                        return;
                    }
                }
                jump(bianmins.get(arg2).getFragmentName(), bianmins.get(arg2)
                        .getName());
            }
        });
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
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(
                        R.layout.list_item_bianmin, null);
                holder.brandLogoImage = (ImageView) convertView
                        .findViewById(R.id.list_bianmin_image);
                holder.brandNameText = (TextView) convertView
                        .findViewById(R.id.list_bianmin_name);
                android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
                        android.widget.AbsListView.LayoutParams.MATCH_PARENT,
                        screenWidth / 5);
                // holder.brandNameText.setVisibility(View.GONE);
                convertView.setLayoutParams(params);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.brandNameText.setText(bianmins.get(position).getName());
            holder.brandLogoImage.setImageResource(bianmins.get(position)
                    .getImage());
            return convertView;
        }

        class ViewHolder {
            TextView brandNameText;
            ImageView brandLogoImage;
        }

    }
}
