package yitgogo.consumer.home.model;

public class ModelHomeEntrance {

	String name = "", value = "";
	int image = 0, type = 0;
	public static int TYPE_CLASS = 1;
	public static int TYPE_NONE = 2;

	public ModelHomeEntrance() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
