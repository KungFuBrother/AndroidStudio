package yitgogo.smart.order.model;

public class UserInfo {

	User user = new User();
	String phone = "", name = "", address = "", area = "";

	public UserInfo() {
	}

	public UserInfo(User user, String phone, String name, String area,
			String address) {
		this.user = user;
		this.name = name;
		this.phone = phone;
		this.area = area;
		this.address = address;
	}

	public UserInfo(User user, ModelAddress modelAddress) {
		this.user = user;
		name = modelAddress.getPersonName();
		phone = modelAddress.getPhone();
		area = modelAddress.getAreaAddress();
		address = modelAddress.getDetailedAddress();
	}

	public User getUser() {
		return user;
	}

	public String getPhone() {
		return phone;
	}

	public String getName() {
		return name;
	}

	public String getArea() {
		return area;
	}

	public String getAddress() {
		return address;
	}

}
