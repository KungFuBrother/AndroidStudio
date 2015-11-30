package yitgogo.consumer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.smartown.yitian.gogo.R;

public class PasswordView extends View {

	int code = -1;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			invalidate();
		};
	};

	public PasswordView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (haveCode()) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(getResources().getColor(
					R.color.material_blue_grey_800));
			paint.setStyle(Paint.Style.FILL);
			RectF rectF = new RectF(0, 0, getWidth(), getHeight());
			canvas.drawArc(rectF, 0, 360.0f, true, paint);
		} else {
			canvas.drawColor(Color.TRANSPARENT);
		}
	}

	public void setCode(int code) {
		this.code = code;
		handler.sendEmptyMessage(0);
	}

	public int getCode() {
		return code;
	}

	public boolean haveCode() {
		return code >= 0 & code <= 9;
	}

	public void deleteCode() {
		setCode(-1);
	}
}
