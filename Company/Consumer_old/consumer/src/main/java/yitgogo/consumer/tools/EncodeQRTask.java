package yitgogo.consumer.tools;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class EncodeQRTask extends AsyncTask<ContentValues, Void, Bitmap> {

	ContentValues contentValues = new ContentValues();
	String content = "";
	int imageWidth = 0;

	@Override
	protected Bitmap doInBackground(ContentValues... params) {
		ContentValues contentValues = params[0];
		content = contentValues.getAsString("content");
		imageWidth = contentValues.getAsInteger("imageWidth");
		return QrCodeTool.createQRCode(content, imageWidth);
	}

}
