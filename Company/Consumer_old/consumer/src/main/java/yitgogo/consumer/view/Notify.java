package yitgogo.consumer.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smartown.yitian.gogo.R;

public class Notify {

	static Toast toast;
	static TextView textView;

	public static void init(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.layout_toast,
				null);
		textView = (TextView) view.findViewById(R.id.toast_text);
		toast = new Toast(context);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
	}

	public static void show(String text) {
		textView.setText(text);
		toast.show();
	}

	public static void show(String text, int duration) {
		textView.setText(text);
		toast.setDuration(duration);
		toast.show();
	}
}
