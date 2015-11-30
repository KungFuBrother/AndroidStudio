package yitgogo.consumer.view;

import yitgogo.consumer.tools.ScreenUtil;
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
import android.widget.EditText;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

public class CodeEditDialog extends DialogFragment {

	LayoutInflater layoutInflater;
	View dialogView;

	TextView titleTextView, okButton, cancelButton;
	EditText codeEditText;

	private boolean cancelable = true;
	private String titleString = "";

	public String code = "";
	public boolean ok = false;

	public CodeEditDialog(String titleString, boolean cancelable) {
		this.titleString = titleString;
		this.cancelable = cancelable;
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
		dialogView = layoutInflater.inflate(R.layout.dialog_edit_code, null);
		titleTextView = (TextView) dialogView
				.findViewById(R.id.dialog_edit_code_title);
		okButton = (TextView) dialogView.findViewById(R.id.dialog_edit_code_ok);
		cancelButton = (TextView) dialogView
				.findViewById(R.id.dialog_edit_code_cancel);
		codeEditText = (EditText) dialogView
				.findViewById(R.id.dialog_edit_code_edit);
		initViews();
		registerViews();
	}

	private void initViews() {
		titleTextView.setText(titleString);
	}

	private void registerViews() {
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ok = false;
				dismiss();
			}
		});
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (codeEditText.length() == 6) {
					code = codeEditText.getText().toString().trim();
					ok = true;
					dismiss();
				} else {
					Notify.show("请输入正确的验证码");
				}
			}
		});
	}

}
