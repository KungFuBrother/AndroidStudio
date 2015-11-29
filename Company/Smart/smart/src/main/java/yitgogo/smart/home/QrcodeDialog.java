package yitgogo.smart.home;

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
import android.widget.ImageView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class QrcodeDialog extends DialogFragment {

	View dialogView;
	ImageView closeButton;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(QrcodeDialog.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(QrcodeDialog.class.getName());
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		return dialog;
	}

	private void findViews() {
		dialogView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_qrcode, null);
		closeButton = (ImageView) dialogView.findViewById(R.id.dialog_close);
		initViews();
	}

	private void initViews() {
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

}
