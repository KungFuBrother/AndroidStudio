package yitgogo.smart.home.part;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelLocalStore;
import yitgogo.smart.local.LocalStoreFragment;

public class PartStoreFragment extends BaseNormalFragment {

    static PartStoreFragment storeFragment;
    HorizontalScrollView horizontalScrollView;
    GridView gridView;
    List<ModelLocalStore> localStores;
    StoreAdapter storeAdapter;

    public static PartStoreFragment getStoreFragment() {
        if (storeFragment == null) {
            storeFragment = new PartStoreFragment();
        }
        return storeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        measureScreen();
        localStores = new ArrayList<ModelLocalStore>();
        storeAdapter = new StoreAdapter();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.part_home_store, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        gridView = (GridView) view.findViewById(R.id.store_grid);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        gridView.setAdapter(storeAdapter);
    }

    @Override
    protected void registerViews() {
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                HomeData.getInstance().setStoreSelection(arg2);
                openWindow(LocalStoreFragment.class.getName(), "店铺街");
            }
        });
    }

    public void refresh() {
        localStores = HomeData.getInstance().getLocalStores();
        if (localStores.size() > 0) {
            int colums = localStores.size();
            gridView.setLayoutParams(new LinearLayout.LayoutParams(colums
                    * HomeData.columWidth,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            gridView.setColumnWidth(HomeData.columWidth);
            gridView.setStretchMode(GridView.NO_STRETCH);
            gridView.setNumColumns(colums);
        }
        storeAdapter.notifyDataSetChanged();
        if (localStores.isEmpty()) {
            getView().setVisibility(View.GONE);
        } else {
            getView().setVisibility(View.VISIBLE);
        }
    }

    class StoreAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localStores.size();
        }

        @Override
        public Object getItem(int position) {
            if (localStores.size() > 4) {
                return 4;
            }
            return localStores.get(position);
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
                        R.layout.list_product_grid, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.list_product_image);
                viewHolder.priceTextView = (TextView) convertView
                        .findViewById(R.id.list_product_price);
                viewHolder.nameTextView = (TextView) convertView
                        .findViewById(R.id.list_product_name);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        HomeData.imageHeight);
                viewHolder.imageView.setLayoutParams(layoutParams);
                viewHolder.priceTextView.setVisibility(View.GONE);
                viewHolder.nameTextView.setSingleLine();
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.nameTextView.setText(localStores.get(position)
                    .getShopname());
            imageLoader.displayImage(getSmallImageUrl(localStores.get(position)
                    .getImg()), viewHolder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView priceTextView, nameTextView;
        }
    }

}
