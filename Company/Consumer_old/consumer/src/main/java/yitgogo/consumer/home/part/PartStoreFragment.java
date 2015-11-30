package yitgogo.consumer.home.part;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.home.model.ModelLocalStore;
import yitgogo.consumer.local.ui.LocalStoreDetailFragment;
import yitgogo.consumer.local.ui.LocalStoreFragment;

public class PartStoreFragment extends BaseNormalFragment {

    static PartStoreFragment storeFragment;
    LinearLayout moreStoreButton;
    HorizontalScrollView horizontalScrollView;
    GridView storeGridView;
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
        View view = inflater.inflate(R.layout.home_part_store, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        moreStoreButton = (LinearLayout) view
                .findViewById(R.id.part_store_more);
        horizontalScrollView = (HorizontalScrollView) view
                .findViewById(R.id.part_store_horizontal_scroll);
        storeGridView = (GridView) view.findViewById(R.id.part_store_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        storeGridView.setAdapter(storeAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, screenWidth / 5 * 2);
        storeGridView.setLayoutParams(params);
    }

    @Override
    protected void registerViews() {
        moreStoreButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(LocalStoreFragment.class.getName(), "店铺街");
            }
        });
        storeGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Bundle bundle = new Bundle();
                bundle.putString("storeId", localStores.get(arg2).getId());
                bundle.putString("storeName", localStores.get(arg2)
                        .getShopname());
                jump(LocalStoreDetailFragment.class.getName(), "店铺街", bundle,
                        true);
            }
        });
    }

    public void refresh(String result) {
        localStores.clear();
        storeAdapter.notifyDataSetChanged();
        if (result.length() > 0) {
            JSONObject object;
            try {
                object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                    JSONArray array = object.optJSONArray("dataList");
                    if (array != null) {
                        if (array.length() > 4) {
                            for (int i = 0; i < 4; i++) {
                                localStores.add(new ModelLocalStore(array
                                        .optJSONObject(i)));
                            }
                        } else {
                            for (int i = 0; i < array.length(); i++) {
                                localStores.add(new ModelLocalStore(array
                                        .optJSONObject(i)));
                            }
                        }
                        if (localStores.size() > 0) {
                            int colums = localStores.size();
                            storeGridView
                                    .setLayoutParams(new LinearLayout.LayoutParams(
                                            colums * (screenWidth / 3),
                                            LinearLayout.LayoutParams.MATCH_PARENT));
                            storeGridView.setColumnWidth(screenWidth / 3);
                            storeGridView.setStretchMode(GridView.NO_STRETCH);
                            storeGridView.setNumColumns(colums);
                            storeAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                        R.layout.list_local_store_home, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.local_store_image);
                viewHolder.textView = (TextView) convertView
                        .findViewById(R.id.local_store_name);
                convertView.setTag(viewHolder);
                AbsListView.LayoutParams storeParams = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        screenWidth / 5 * 2);
                convertView.setLayoutParams(storeParams);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView
                    .setText(localStores.get(position).getShopname());
            ImageLoader.getInstance().displayImage(
                    getSmallImageUrl(localStores.get(position).getImg()),
                    viewHolder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }

    }

}
