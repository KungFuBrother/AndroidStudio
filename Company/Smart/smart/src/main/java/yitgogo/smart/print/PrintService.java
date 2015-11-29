package yitgogo.smart.print;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.LogUtil;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;

public class PrintService extends Service {

	boolean isPrinting = false, isDestroy = false;
	Client serverClient;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				PrintDialog printDialog = new PrintDialog(PrintService.this);
				printDialog.getWindow().setType(
						(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
				printDialog.show();
				break;

			default:
				break;
			}
		};
	};
	List<PrintData> printDatas = new ArrayList<PrintData>();
	PrintData printData = new PrintData();

	@Override
	public IBinder onBind(Intent intent) {
		LogUtil.logInfo("PrintService", "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.logInfo("PrintService", "onCreate");
		// 启动服务时连接打印服务器；
		connectPrintServer();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		LogUtil.logInfo("PrintService", "onStart");
	}

	@Override
	public void onDestroy() {
		isDestroy = true;
		serverClient.stop();
		super.onDestroy();
		LogUtil.logInfo("PrintService", "onDestroy");
	}

	private void connectPrintServer() {
		serverClient = new Client("print.yitos.net", 9988,
				new ChannelInboundHandlerAdapter() {

					@Override
					public void channelInactive(ChannelHandlerContext ctx)
							throws Exception {
						LogUtil.logInfo("Socket", "channelInactive");
					}

					@Override
					public void channelReadComplete(ChannelHandlerContext ctx)
							throws Exception {
						LogUtil.logInfo("Socket", "channelReadComplete");
					}

					@Override
					public void channelWritabilityChanged(
							ChannelHandlerContext ctx) throws Exception {
						LogUtil.logInfo("Socket", "channelRegistered");
					}

					// 收到消息时调用此方法
					public void channelRead(ChannelHandlerContext ctx,
							Object msg) throws Exception {
						LogUtil.logInfo("Socket", "channelRead");
						// 如果收到打印消息时，没有在打印，则进行打印操作
						if (!isPrinting) {
							String message = new String(Base64.decode(
									(String) msg, Base64.DEFAULT), "UTF-8");
							LogUtil.logInfo("Socket", message);
							printData = new PrintData(new JSONObject(message));
							handler.sendEmptyMessage(0);
						}
					}

					// 成功连接到打印服务器，注册设备
					public void channelActive(ChannelHandlerContext ctx)
							throws Exception {
						LogUtil.logInfo("Socket", "channelActive");
						String message = "MachineCode:"
								+ Device.getDeviceCode();
						LogUtil.logInfo("Socket", message);
						ctx.writeAndFlush(new String(message.getBytes(),
								"UTF-8"));
					}

					// 建立连接时调用此方法
					public void channelRegistered(ChannelHandlerContext ctx)
							throws Exception {
						LogUtil.logInfo("Socket", "channelRegistered");
					}

					// 断开连接
					public void channelUnregistered(ChannelHandlerContext ctx)
							throws Exception {
						super.channelUnregistered(ctx);
						LogUtil.logInfo("Socket", "channelUnregistered");
						// 如果服务没有停止，重连打印服务器
						if (!isDestroy) {
							connectPrintServer();
						}
					}

				});
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					serverClient.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	class PrintDialog extends Dialog {

		TextView messageTextView;
		ImageView closeImageView;

		Handler dialogHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {

				case 1:
					isPrinting = false;
					dismiss();
					break;

				default:
					break;
				}
			};
		};

		public PrintDialog(Context context) {
			super(context);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			isPrinting = true;
			setCancelable(false);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawableResource(R.color.divider);
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_print);
			closeImageView = (ImageView) findViewById(R.id.print_close);
			messageTextView = (TextView) findViewById(R.id.print_message);
			closeImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					isPrinting = false;
					dismiss();
				}
			});
		}

		@Override
		public void onAttachedToWindow() {
			super.onAttachedToWindow();
			new PrintTask().execute();
		}

		class PrintTask extends AsyncTask<Void, Void, Boolean> {

			@Override
			protected void onPreExecute() {
				messageTextView.setText("正在打印订单...");
			}

			@Override
			protected Boolean doInBackground(Void... paramVarArgs) {
				Printer printer = new Printer(printData);
				try {
					printer.connect();
					printer.print();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					messageTextView.setText("打印成功");
				} else {
					messageTextView.setText("打印失败");
				}
				dialogHandler.sendEmptyMessageDelayed(1, 2000);
			}

		}
	}

}
