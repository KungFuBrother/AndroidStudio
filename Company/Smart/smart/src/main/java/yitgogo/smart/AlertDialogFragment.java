package yitgogo.smart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;

public class AlertDialogFragment extends BaseNotifyFragment {

	TextView alertTextView;
	String alertString = "";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_alert_dialog);
		init();
		findViews();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("alertString")) {
				alertString = bundle.getString("alertString");
			}
		}
	}

	protected void findViews() {
		alertTextView = (TextView) contentView
				.findViewById(R.id.alert_dialog_text);
		initViews();
	}

	@Override
	protected void initViews() {
		alertTextView.setText(alertString);
	}

}
