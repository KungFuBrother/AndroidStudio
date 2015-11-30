package yitgogo.consumer.view;

import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.user.model.VersionInfo;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

public class NormalAskDialog extends DialogFragment {

	LayoutInflater layoutInflater;
	View dialogView;

	TextView okButton, cancelButton, contentTextView;

	public boolean makeSure = false;

	private boolean cancelable = true;
	private String content = "", okButtonLable = "", cancelButtonLable = "";

	/**
	 * 版本信息 检查更新
	 */
	private VersionInfo versionInfo = new VersionInfo();

	boolean isUpdateDialog = false;

	public NormalAskDialog(String content, String okButtonLable,
			String cancelButtonLable) {
		this.cancelButtonLable = cancelButtonLable;
		this.content = content;
		this.okButtonLable = okButtonLable;
	}

	public NormalAskDialog(String content, String okButtonLable,
			String cancelButtonLable, boolean cancelable) {
		this.cancelButtonLable = cancelButtonLable;
		this.content = content;
		this.okButtonLable = okButtonLable;
		this.cancelable = cancelable;
	}

	public NormalAskDialog(VersionInfo versionInfo) {
		this.versionInfo = versionInfo;
		isUpdateDialog = true;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(cancelable);
		init();
		findViews();
	}

	private void init() {
		layoutInflater = LayoutInflater.from(getActivity());
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView,
				new LayoutParams((ScreenUtil.getScreenWidth() / 5 * 4),
						android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
		return dialog;
	}

	private void findViews() {
		dialogView = layoutInflater.inflate(R.layout.dialog_ask, null);
		contentTextView = (TextView) dialogView
				.findViewById(R.id.dialog_content);
		okButton = (TextView) dialogView.findViewById(R.id.dialog_button_ok);
		cancelButton = (TextView) dialogView
				.findViewById(R.id.dialog_button_cancel);
		initViews();
		registerViews();
	}

	private void initViews() {
		if (isUpdateDialog) {
			contentTextView.setText("发现新版本：\n" + versionInfo.getVerName()
					+ "\n\n" + "更新日志：" + "\n" + versionInfo.getMessage());
			okButton.setText("立即更新");
			if (versionInfo.getGrade() == 2) {
				setCancelable(false);
				cancelButton.setVisibility(View.GONE);
			} else {
				setCancelable(true);
				cancelButton.setText("暂不更新");
			}
		} else {
			contentTextView.setText(content);
			okButton.setText(okButtonLable);
			cancelButton.setText(cancelButtonLable);
		}
	}

	private void registerViews() {
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				makeSure = false;
				dismiss();
			}
		});
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				makeSure = true;
				dismiss();
			}
		});
	}

}
