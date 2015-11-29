package yitgogo.smart.tools;

import android.graphics.Bitmap;

public class QrEncodeMissonMessage extends MissionMessage {

	Bitmap bitmap = null;

	public QrEncodeMissonMessage(int progressStatus, String message, Bitmap bitmap) {
		super(progressStatus, message);
		this.bitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	@Override
	public String toString() {
		return "EncodeMessage [progressStatus=" + progressStatus + ", message="
				+ message + ", bitmap=" + bitmap + "]";
	}

}
