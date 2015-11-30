package yitgogo.consumer.order.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelPayData implements Parcelable {

	ArrayList<ModelPayOrder> orders = new ArrayList<ModelPayOrder>();

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(orders);
	}

	public static final Parcelable.Creator<ModelPayData> CREATOR = new Parcelable.Creator<ModelPayData>() {

		public ModelPayData createFromParcel(Parcel in) {
			return new ModelPayData(in);
		}

		public ModelPayData[] newArray(int size) {
			return new ModelPayData[size];
		}
	};

	private ModelPayData(Parcel in) {
		orders = in.readArrayList(ClassLoader.getSystemClassLoader());
	}

}
