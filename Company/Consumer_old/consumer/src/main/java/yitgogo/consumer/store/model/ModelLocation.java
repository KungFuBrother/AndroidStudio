package yitgogo.consumer.store.model;

import org.json.JSONArray;

public class ModelLocation {
	double longitude = 0, latitude = 0;

	public ModelLocation() {
	}

	public ModelLocation(JSONArray location) {
		longitude = location.optDouble(0, 0);
		latitude = location.optDouble(1, 0);
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	@Override
	public String toString() {
		return "ModelLocation [longitude=" + longitude + ", latitude="
				+ latitude + "]";
	}

}
