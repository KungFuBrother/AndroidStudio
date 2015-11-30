package yitgogo.consumer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.smartown.yitian.gogo.R;

public class CircleProgressView extends View {

	private float progress = 0;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			invalidate();
		};
	};

	public CircleProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(getResources().getColor(R.color.blue));
		paint.setStyle(Paint.Style.FILL);
		RectF rectF = new RectF(0, 0, getWidth(), getHeight());
		canvas.drawArc(rectF, -90.0f, progress * 360.0f, true, paint);
	}

	public void setProgress(float progress) {
		this.progress = progress;
		handler.sendEmptyMessage(0);
	}

}
