/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dtr.zxing.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.dtr.zxing.camera.CameraManager;
import com.dtr.zxing.decode.DecodeThread;
import com.dtr.zxing.model.ModelQRCodeOrder;
import com.dtr.zxing.model.ModelQRCodeProduct;
import com.dtr.zxing.model.ModelQRCodeShake;
import com.dtr.zxing.model.ModelQRCodeShare;
import com.dtr.zxing.utils.CaptureActivityHandler;
import com.dtr.zxing.utils.InactivityTimer;
import com.google.zxing.Result;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import yitgogo.consumer.ContainerActivity;
import yitgogo.consumer.local.ui.LocalGoodsDetailFragment;
import yitgogo.consumer.local.ui.LocalSaleMiaoshaDetailFragment;
import yitgogo.consumer.local.ui.LocalSaleTejiaDetailFragment;
import yitgogo.consumer.local.ui.LocalServiceDetailFragment;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.product.ui.ProductDetailFragment;
import yitgogo.consumer.product.ui.ScoreProductDetailFragment;
import yitgogo.consumer.tools.ScreenUtil;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements
        SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    public static final int CODE_TYPE_PRODUCT = 1;
    public static final int CODE_TYPE_ORDER = 2;
    public static final int CODE_TYPE_SHAKE = 3;
    public static final int CODE_TYPE_SHARE = 4;

    public static final int PRODUCT_TYPE_PLATFORM = 1;
    public static final int PRODUCT_TYPE_SCORE = 2;
    public static final int PRODUCT_TYPE_LOCAL_GOODS = 3;
    public static final int PRODUCT_TYPE_LOCAL_SERVICE = 4;

    public final static int SALE_TYPE_NONE = 0;
    public final static int SALE_TYPE_TIME = 1;
    public final static int SALE_TYPE_MIAOSHA = 2;
    public final static int SALE_TYPE_TEJIA = 3;
    public final static int SALE_TYPE_LOCAL_MIAOSHA = 4;
    public final static int SALE_TYPE_LOCAL_TEJIA = 5;

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;

    private SurfaceView scanPreview = null;
    private LinearLayout scanContainer;
    private FrameLayout scanCropView;
    private View scanLine;
    private LinearLayout titleLayout;

    private Rect mCropRect = null;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    private boolean isHasSurface = false;

    Intent intent;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        intent = getIntent();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);

        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (LinearLayout) findViewById(R.id.capture_container);
        scanCropView = (FrameLayout) findViewById(R.id.capture_crop_view);
        scanLine = findViewById(R.id.capture_scan_line);
        titleLayout = (LinearLayout) findViewById(R.id.capture_title);

        titleLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                finish();
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ScreenUtil.getScreenWidth() / 3 * 2,
                ScreenUtil.getScreenWidth() / 3 * 2);
        scanCropView.setLayoutParams(layoutParams);

        inactivityTimer = new InactivityTimer(this);

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f);
        animation.setDuration(5000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(CaptureActivity.class.getName());
        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        handler = null;

        if (isHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(scanPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(CaptureActivity.class.getName());
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG,
                    "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result result, Bundle resultBundle) {
        inactivityTimer.onActivity();
        if (result != null) {
            if (!TextUtils.isEmpty(result.getText())) {
                String dataString = new String(Base64.decode(result.getText()
                        .getBytes(), Base64.DEFAULT));
                try {
                    JSONObject object = new JSONObject(dataString);
                    Bundle bundle = new Bundle();
                    switch (object.optInt("codeType")) {

                        case CODE_TYPE_PRODUCT:
                            ModelQRCodeProduct product = new ModelQRCodeProduct(
                                    object.optJSONObject("data"));
                            switch (product.getProductType()) {
                                case PRODUCT_TYPE_PLATFORM:

                                    bundle.putString("productId",
                                            product.getProductId());
                                    bundle.putInt("saleType", product.getSaleType());
                                    jump(ProductDetailFragment.class.getName(),
                                            product.getProductName(), bundle);

                                    break;

                                case PRODUCT_TYPE_SCORE:

                                    bundle.putString("productId",
                                            product.getProductId());
                                    jump(ScoreProductDetailFragment.class.getName(),
                                            product.getProductName(), bundle);

                                    break;
                                case PRODUCT_TYPE_LOCAL_GOODS:

                                    bundle.putString("id", product.getProductId());
                                    switch (product.getSaleType()) {
                                        case SALE_TYPE_LOCAL_MIAOSHA:
                                            jump(LocalSaleMiaoshaDetailFragment.class
                                                            .getName(),
                                                    product.getProductName(), bundle);
                                            break;

                                        case SALE_TYPE_LOCAL_TEJIA:
                                            jump(LocalSaleTejiaDetailFragment.class
                                                            .getName(),
                                                    product.getProductName(), bundle);
                                            break;

                                        default:
                                            jump(LocalGoodsDetailFragment.class.getName(),
                                                    product.getProductName(), bundle);
                                            break;
                                    }

                                    break;
                                case PRODUCT_TYPE_LOCAL_SERVICE:

                                    bundle.putString("productId",
                                            product.getProductId());
                                    jump(LocalServiceDetailFragment.class.getName(),
                                            product.getProductName(), bundle);

                                    break;
                                default:
                                    break;
                            }
                            break;

                        case CODE_TYPE_ORDER:

                            ModelQRCodeOrder order = new ModelQRCodeOrder(
                                    object.optJSONObject("data"));
                            if (order.getTotalMoney() > 0) {
                                payMoney(order);
                            }

                            break;

                        case CODE_TYPE_SHAKE:

                            ModelQRCodeShake shake = new ModelQRCodeShake(
                                    object.optJSONObject("data"));
                            if (intent != null) {
                                bundle.putString("activityCode",
                                        shake.getActivityCode());
                                bundle.putString("activityName",
                                        shake.getActivityName());
                                intent.putExtras(bundle);
                                setResult(3, intent);
                                finish();
                            }

                            break;
                        case CODE_TYPE_SHARE:

                            ModelQRCodeShare share = new ModelQRCodeShare(
                                    object.optJSONObject("data"));
                            if (intent != null) {
                                bundle.putString("userCode", share.getUserCode());
                                bundle.putString("userAccount",
                                        share.getUserAccount());
                                intent.putExtras(bundle);
                                setResult(4, intent);
                                finish();
                            }

                            break;

                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG,
                    "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager,
                        DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("相机打开出错，请稍后重试");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 带参数的fragment跳转
     *
     * @param fragmentName
     * @param fragmentTitle
     * @param bundle
     */
    protected void jump(String fragmentName, String fragmentTitle,
                        Bundle parameters) {
        Intent intent = new Intent(CaptureActivity.this,
                ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("fragmentTitle", fragmentTitle);
        bundle.putBundle("parameters", parameters);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void payMoney(ModelQRCodeOrder order) {
        if (order.getTotalMoney() > 0) {
            ArrayList<String> orderNumbers = new ArrayList<String>();
            String[] orders = order.getOrderNumbers().trim().split(",");
            for (int i = 0; i < orders.length; i++) {
                orderNumbers.add(orders[i]);
            }
            payMoney(orderNumbers, order.getTotalMoney(), order.getOrderType());
        }
    }

    protected void payMoney(ArrayList<String> orderNumbers, double totalMoney,
                            int orderType) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("orderNumbers", orderNumbers);
        bundle.putDouble("totalMoney", totalMoney);
        bundle.putInt("orderType", orderType);
        jump(PayFragment.class.getName(), "订单支付", bundle);
    }

}