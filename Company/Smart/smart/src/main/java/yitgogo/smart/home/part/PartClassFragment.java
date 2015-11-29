package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelClass;
import yitgogo.smart.product.ui.ProductClassesFragment;
import yitgogo.smart.view.InnerGridView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
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

import com.smartown.yitgogo.smart.R;

public class PartClassFragment extends BaseNormalFragment {

    static PartClassFragment classFragment;
    LinearLayout showMoreButton;
    InnerGridView classGridView;
    List<ModelClass> allClasses, showingClasses;
    ClassAdapter classAdapter;

    int columHeight = 120;

    boolean showing = false;

    public static PartClassFragment getClassFragment() {
        if (classFragment == null) {
            classFragment = new PartClassFragment();
        }
        return classFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        allClasses = new ArrayList<>();
        showingClasses = new ArrayList<>();
        classAdapter = new ClassAdapter();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.part_home_classes, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        classGridView = (InnerGridView) view.findViewById(R.id.part_class_list);
        showMoreButton = (LinearLayout) view.findViewById(R.id.part_class_more);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        classGridView.setAdapter(classAdapter);
    }

    @Override
    protected void registerViews() {
        classGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                HomeData.getInstance().setClassSelection(arg2);
                openWindow(ProductClassesFragment.class.getName(), "商品分类");

            }
        });
        showMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showing = !showing;
                if (showing) {
                    showingClasses = allClasses;
                    classAdapter.notifyDataSetChanged();
                } else {
                    if (allClasses.size() > 9) {
                        showingClasses = allClasses.subList(0, 9);
                    } else {
                        showingClasses = allClasses;
                    }
                    classAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void refresh() {
        showingClasses = new ArrayList<>();
        allClasses = HomeData.getInstance().getClasses();
        if (allClasses.size() > 9) {
            showingClasses = allClasses.subList(0, 9);
        } else {
            showingClasses = allClasses;
        }
        classAdapter.notifyDataSetChanged();
        if (allClasses.isEmpty()) {
            getView().setVisibility(View.GONE);
        } else {
            getView().setVisibility(View.VISIBLE);
        }
    }

    class ClassAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return showingClasses.size();
        }

        @Override
        public Object getItem(int position) {
            return showingClasses.get(position);
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
                convertView = layoutInflater.inflate(R.layout.list_home_class,
                        null);
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.list_home_class_image);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.list_home_class_name);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, columHeight);
                convertView.setLayoutParams(layoutParams);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            imageLoader.displayImage(showingClasses.get(position).getImg(),
                    holder.imageView);
            holder.textView.setText(showingClasses.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

}
