package yitgogo.smart.sale.ui;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelSaleTheme;
import yitgogo.smart.tools.QrCodeTool;

public class SaleThemeFragment extends BaseNotifyFragment {

    FrameLayout imageLayout;
    HorizontalScrollView horizontalScrollView;
    GridView imageGridView;

    WebView webView;
    ProgressBar progressBar;

    List<ModelSaleTheme> saleThemes;
    SaleThemeAdapter saleThemeAdapter;
    ModelSaleTheme saleTheme = new ModelSaleTheme();
    File htmlFile;

    int columWidth = 340;
    int columHeight = 140;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_sale_theme);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SaleThemeFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SaleThemeFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectTheme(saleThemes.get(HomeData.getInstance()
                .getSaleThemeSelection()));
    }

    private void init() {
        htmlFile = new File(getActivity().getCacheDir().getPath(),
                "saleTheme.html");
        saleThemes = HomeData.getInstance().getSaleThemes();
        saleThemeAdapter = new SaleThemeAdapter();
    }

    protected void findViews() {
        imageLayout = (FrameLayout) contentView.findViewById(R.id.image_layout);
        horizontalScrollView = (HorizontalScrollView) contentView
                .findViewById(R.id.image_scroll);
        imageGridView = (GridView) contentView.findViewById(R.id.image_grid);
        webView = (WebView) contentView.findViewById(R.id.sale_theme_web);
        progressBar = (ProgressBar) contentView
                .findViewById(R.id.sale_theme_web_progress);
        initViews();
        registerViews();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    protected void initViews() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, columHeight);
        imageLayout.setLayoutParams(layoutParams);
        if (saleThemes.size() > 0) {
            int colums = saleThemes.size();
            imageGridView.setLayoutParams(new LinearLayout.LayoutParams(colums
                    * columWidth, LinearLayout.LayoutParams.MATCH_PARENT));
            imageGridView.setColumnWidth(columWidth);
            imageGridView.setStretchMode(GridView.NO_STRETCH);
            imageGridView.setNumColumns(colums);
        }
        imageGridView.setAdapter(saleThemeAdapter);
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
        webView.addJavascriptInterface(new JsInterface(), "yitgogo");
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

    private void selectTheme(ModelSaleTheme modelSaleTheme) {

        if (!saleThemes.isEmpty()) {
            if (modelSaleTheme != null) {
                saleTheme = modelSaleTheme;
            } else {
                saleTheme = saleThemes.get(0);
            }
            saleThemeAdapter.notifyDataSetChanged();
            new DownLoad().execute();
        }
    }

    protected void registerViews() {
        imageGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                selectTheme(saleThemes.get(arg2));
            }
        });
    }

    class SaleThemeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return saleThemes.size();
        }

        @Override
        public Object getItem(int position) {
            return saleThemes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(
                        R.layout.list_image_scroll, null);
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.scroll_image);
                viewHolder.selectionImageView = (ImageView) convertView
                        .findViewById(R.id.scroll_image_selection);
                LayoutParams layoutParams = new LayoutParams(columWidth,
                        columHeight);
                convertView.setLayoutParams(layoutParams);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (saleTheme.getId().equals(saleThemes.get(position).getId())) {
                viewHolder.selectionImageView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.selectionImageView.setVisibility(View.GONE);
            }
            imageLoader.displayImage(getSmallImageUrl(saleThemes.get(position)
                    .getMoblieImg()), viewHolder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView, selectionImageView;
        }

    }

    class DownLoad extends AsyncTask<Void, Float, Boolean> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(saleTheme.getMoblieUrl());
            HttpResponse response;
            try {
                response = client.execute(get);
                HttpEntity entity = response.getEntity();
                long length = entity.getContentLength();
                InputStream is = entity.getContent();
                FileOutputStream fileOutputStream = null;
                if (is != null) {
                    fileOutputStream = new FileOutputStream(htmlFile);
                    byte[] b = new byte[512];
                    int charb = -1;
                    int count = 0;
                    while ((charb = is.read(b)) != -1) {
                        count += charb;
                        publishProgress((float) count / (float) length);
                        fileOutputStream.write(b, 0, charb);
                    }
                }
                fileOutputStream.flush();
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            hideLoading();
            if (result) {
                webView.loadUrl("file://" + htmlFile.getPath());
            } else {
                getActivity().finish();
            }
        }
    }

    class JsInterface {

        @JavascriptInterface
        public boolean showProductInfo(String productId) {
            showProductDetail(productId, QrCodeTool.SALE_TYPE_NONE);
            return true;
        }
    }
}
