package yitgogo.smart.print;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

public class Printer {

	Socket printSocket;
	PrintWriter printWriter;

	PrintData printData;
	DecimalFormat decimalFormat = new DecimalFormat("0.00");

	public Printer(PrintData printData) {
		this.printData = printData;
	}

	public void connect() throws UnknownHostException, IOException {
		printSocket = new Socket();
		SocketAddress socAddress = new InetSocketAddress("192.168.0.123", 9100);
		printSocket.connect(socAddress, 5000);
		printWriter = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(printSocket.getOutputStream(), "GBK")));
	}

	public void connect(int timeout) throws UnknownHostException, IOException {
		printSocket = new Socket();
		SocketAddress socAddress = new InetSocketAddress("192.168.0.123", 9100);
		printSocket.connect(socAddress, timeout);
	}

	public void stop() {
		printWriter.close();
		try {
			printSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void print() {
		printInit();
		printTitle();
		printContent(printData.getShopname() + "(销售单)", 1);
		printLine();
		printContent("时间:" + printData.getDate(), 0);
		printContent("订单:" + printData.getId(), 0);
		printLine();
		printContent("姓名:" + printData.getConsignee(), 0);
		printContent("电话:" + printData.getTelphone(), 0);
		printContent("地址:" + printData.getAddress(), 0);
		printLine();
		printproduct("单价", "数量", "小计");
		printLine();
		for (int i = 0; i < printData.getProducts().size(); i++) {
			if (i > 0) {
				printEmpty();
			}
			printContent(printData.getProducts().get(i).getSpname(), 0);
			printproduct(
					"￥"
							+ decimalFormat.format(printData.getProducts()
									.get(i).getPrice()),
					printData.getProducts().get(i).getNum() + "",
					"￥"
							+ decimalFormat.format(printData.getProducts()
									.get(i).getAmount()));
		}
		printLine();
		printPrice("总计:", "￥" + decimalFormat.format(printData.getTotal()));
		printPrice("折扣:", "￥" + decimalFormat.format(printData.getZhekou()));
		printPrice("实付:", "￥" + decimalFormat.format(printData.getShifu()));
		printLine();
		printContent(
				"服务电话:" + printData.getServicephone1() + "/"
						+ printData.getServicephone2(), 0);
		printContent("加盟/投诉:400-666-3867", 0);
		printContent("谢谢惠顾，欢迎下次光临！", 1);
		printEmpty();
		printEmpty();
		printEmpty();
		stop();
	}

	// 初始化
	private void printInit() {
		printWriter.write(27);
		printWriter.write(64);
	}

	// 选择字库(0/1 24/16)
	private void setWordType(int i) {
		printWriter.write(27);
		printWriter.write(77);
		printWriter.write(i);
	}

	// 对齐(0/1/2 左/中/右)
	private void setPosition(int i) {
		printWriter.write(27);
		printWriter.write(97);
		printWriter.write(i);
	}

	// 粗体(1:设置 0:取消)
	private void setStyleBold(int i) {
		printWriter.write(27);
		printWriter.write(69);
		printWriter.write(i);
	}

	// 粗体(1:设置 0:取消)
	private void setTextScale(int i) {
		printWriter.write(29);
		printWriter.write(33);
		printWriter.write(i);
	}

	private void printTitle() {
		setPosition(1);
		setStyleBold(1);
		printWriter.println("易田电商");
	}

	private void printContent(String content, int position) {
		setPosition(position);
		setStyleBold(0);
		printWriter.println(content);
	}

	private void printEmpty() {
		printWriter.println();
	}

	private void printLine() {
		printContent(" - - - - - - - - - - - - - - - -", 0);
	}

	private void printproduct(String price, String count, String countPrice) {
		printContent(
				getFormatedString(price, 14, 0)
						+ getFormatedString(count, 4, 1)
						+ getFormatedString(countPrice, 14, 2), 0);
	}

	public String getFormatedString(String string, int maxLength, int type) {
		StringBuilder stringBuilder = new StringBuilder();
		int length = getStringLength(string);
		if (length < maxLength) {
			int i = maxLength - length;
			switch (type) {
			case 0:
				stringBuilder.append(string);
				for (int j = 0; j < i; j++) {
					stringBuilder.append(" ");
				}
				break;

			case 1:
				for (int j = 0; j < i / 2; j++) {
					stringBuilder.append(" ");
				}
				stringBuilder.append(string);
				for (int j = 0; j < i - i / 2; j++) {
					stringBuilder.append(" ");
				}
				break;

			case 2:
				for (int j = 0; j < i; j++) {
					stringBuilder.append(" ");
				}
				stringBuilder.append(string);
				break;

			default:
				break;
			}
		} else {
			stringBuilder.append(string.substring(0, maxLength / 2));
		}
		return stringBuilder.toString();
	}

	private int getStringLength(String string) {
		int length = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.substring(i, i + 1).getBytes().length == 1) {
				length += 1;
			} else {
				length += 2;
			}
		}
		return length;
	}

	private void printPrice(String key, String value) {
		StringBuilder stringBuilder = new StringBuilder();
		int length = getStringLength(key) + getStringLength(value);
		if (length < 32) {
			int i = 32 - length;
			stringBuilder.append(key);
			for (int j = 0; j < i; j++) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(value);
		}
		printContent(stringBuilder.toString(), 0);
	}

}
