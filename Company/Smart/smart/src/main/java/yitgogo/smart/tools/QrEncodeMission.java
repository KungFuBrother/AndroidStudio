package yitgogo.smart.tools;

import android.graphics.Bitmap;

import com.google.zxing.WriterException;

public class QrEncodeMission extends Mission {

	String content = "";
	int imageWidth = 120;
	OnQrEncodeListener onQrEncodeListener;

	public QrEncodeMission(String content, int imageWidth,
			OnQrEncodeListener onQrEncodeListener) {
		this.content = content;
		this.imageWidth = imageWidth;
		this.onQrEncodeListener = onQrEncodeListener;
	}

	@Override
	public void missionStart() {
		onQrEncodeListener.sendMessage(new QrEncodeMissonMessage(
				OnMissionListener.PROGRESS_START, "任务开始", null));
		encode();
		onQrEncodeListener.sendMessage(new QrEncodeMissonMessage(
				OnMissionListener.PROGRESS_FINISH, "任务结束", null));

	}

	private void encode() {
		showRequestContent();
		try {
			Bitmap bitmap = QrCodeTool.encodeAsBitmap(content, imageWidth);
			onQrEncodeListener.sendMessage(new QrEncodeMissonMessage(
					OnMissionListener.PROGRESS_SUCCESS, "生成二维码成功----------"
							+ toString(), bitmap));
		} catch (WriterException e) {
			onQrEncodeListener.sendMessage(new QrEncodeMissonMessage(
					OnMissionListener.PROGRESS_FAILED, "生成二维码失败----------"
							+ e.getMessage(), null));
			e.printStackTrace();
		}
	}

	/**
	 * 显示网络请求参数
	 * 
	 * @param URL
	 * @param nameValuePairs
	 */
	private void showRequestContent() {
		LogUtil.logInfo("RequestTask---PARAMS", toString());
	}

	@Override
	public String toString() {
		return "QrEncodeMission [content=" + content + ", imageWidth="
				+ imageWidth + ", onQrEncodeListener=" + onQrEncodeListener
				+ "]";
	}

}
