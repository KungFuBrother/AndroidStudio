package yitgogo.consumer.order.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelPayOrder implements Parcelable {

	String orderNumber = "", orderInfo = "";
	double orderMoney = -1;
	int productCount = 0;

	public ModelPayOrder(String orderNumber, String orderInfo,
			double orderMoney, int productCount) {
		this.orderInfo = orderInfo;
		this.orderNumber = orderNumber;
		this.orderMoney = orderMoney;
		this.productCount = productCount;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(orderNumber);
		dest.writeString(orderInfo);
		dest.writeDouble(orderMoney);
		dest.writeInt(productCount);
	}

	public static final Parcelable.Creator<ModelPayOrder> CREATOR = new Parcelable.Creator<ModelPayOrder>() {

		public ModelPayOrder createFromParcel(Parcel in) {
			return new ModelPayOrder(in);
		}

		public ModelPayOrder[] newArray(int size) {
			return new ModelPayOrder[size];
		}
	};

	private ModelPayOrder(Parcel in) {
		orderNumber = in.readString();
		orderInfo = in.readString();
		orderMoney = in.readDouble();
		productCount = in.readInt();
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public String getOrderInfo() {
		return orderInfo;
	}

	public double getOrderMoney() {
		return orderMoney;
	}

	public int getProductCount() {
		return productCount;
	}
}
