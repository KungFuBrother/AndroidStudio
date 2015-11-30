package yitgogo.consumer.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.EncodeQRTask;
import yitgogo.consumer.user.model.User;

public class UserShareFragment extends BaseNotifyFragment {

    TextView codeTextView;
    ImageView codeImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_share);
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserShareFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserShareFragment.class.getName());
    }

    @Override
    protected void findViews() {
        codeTextView = (TextView) contentView.findViewById(R.id.share_code);
        codeImageView = (ImageView) contentView
                .findViewById(R.id.share_code_image);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        if (User.getUser().isLogin()) {
            if (!TextUtils.isEmpty(User.getUser().getMyRecommend())) {
                codeTextView.setText(User.getUser().getMyRecommend());
                try {
                    new EncodeQRTask() {
                        protected void onPostExecute(
                                android.graphics.Bitmap result) {
                            if (result != null) {
                                codeImageView.setImageBitmap(result);
                            }
                        }

                        ;
                    }.execute(getShareCodeContent(User.getUser()
                            .getUseraccount(), User.getUser().getMyRecommend()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void registerViews() {
        addImageButton(R.drawable.ic_share_black_24dp, "share",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        shareMsg("易田购购", "网购上易田，省心又省钱，快来下载吧！"
                                + API.URL_DOWNLOAD + " ，我的推荐码是"
                                + User.getUser().getMyRecommend());
                    }
                });
    }

    protected void shareMsg(String msgTitle, String msgText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain"); // 纯文本
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享易田购购APP"));
    }

}
