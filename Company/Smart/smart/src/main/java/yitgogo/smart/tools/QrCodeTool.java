package yitgogo.smart.tools;

import java.util.EnumMap;
import java.util.Map;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QrCodeTool {

	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	public static final int CODE_TYPE_PRODUCT = 1;
	public static final int CODE_TYPE_ORDER = 2;

	public static final int PRODUCT_TYPE_PLATFORM = 1;
	public static final int PRODUCT_TYPE_SCORE = 2;
	public static final int PRODUCT_TYPE_LOCAL_GOODS = 3;
	public static final int PRODUCT_TYPE_LOCAL_SERVICE = 4;

	public final static int SALE_TYPE_NONE = 0;
	public final static int SALE_TYPE_TIME = 1;
	public final static int SALE_TYPE_MIAOSHA = 2;
	public final static int SALE_TYPE_TEJIA = 3;
	public final static int SALE_TYPE_LOCAL_MIAOSHA = 4;
	public final static int SALE_TYPE_LOCAL_TEJIA = 5;

	/**
	 * 生成图像
	 */
	// public static Bitmap createQRCode(String content, int imageWidth) {
	// Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType,
	// Object>();
	// hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	// BitMatrix matrix;
	// try {
	// matrix = new MultiFormatWriter().encode(content,
	// BarcodeFormat.QR_CODE, imageWidth, imageWidth, hints);
	// int[] pixels = new int[imageWidth * imageWidth];
	// for (int y = 0; y < imageWidth; y++) {
	// for (int x = 0; x < imageWidth; x++) {
	// if (matrix.get(x, y)) {
	// pixels[y * imageWidth + x] = BLACK;
	// }
	// }
	// }
	// Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageWidth,
	// Bitmap.Config.ARGB_8888);
	// bitmap.setPixels(pixels, 0, imageWidth, 0, 0, imageWidth,
	// imageWidth);
	// return bitmap;
	// } catch (WriterException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	public static Bitmap encodeAsBitmap(String content, int imageWidth)
			throws WriterException {
		String contentsToEncode = content;
		if (contentsToEncode == null) {
			return null;
		}
		Map<EncodeHintType, Object> hints = null;
		String encoding = guessAppropriateEncoding(contentsToEncode);
		if (encoding != null) {
			hints = new EnumMap<>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		BitMatrix result;
		try {
			result = new MultiFormatWriter().encode(contentsToEncode,
					BarcodeFormat.QR_CODE, imageWidth, imageWidth, hints);
		} catch (IllegalArgumentException iae) {
			// Unsupported format
			return null;
		}
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

}
