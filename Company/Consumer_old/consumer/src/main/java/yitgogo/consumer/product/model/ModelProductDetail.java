package yitgogo.consumer.product.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.home.model.ModelListPrice;

public class ModelProductDetail {
	/*
	 * {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount"
	 * :1,"dataMap":{"imgs":[
	 * "http://images.yitos.net/images/public/20150523/48741432375113331.jpg"
	 * ],"cuxiaodate"
	 * :"nullnnull","state":"1","number":"YT84024815267","brandName"
	 * :"长虹","id":"33479"
	 * ,"unit":"2222","num":2133,"price":2312.0,"yuanjia":null,
	 * "pmname":null,"xiangqing"
	 * :null,"addDate":"2015-05-23","listAtt":[{"id":490
	 * ,"attributeName":"尺寸","attributeValueSet"
	 * :[{"id":423,"attName":"32英寸","attriuteBeanMap"
	 * :{"id":490,"name":"尺寸"},"sort"
	 * :0},{"id":444,"attName":"60英寸","attriuteBeanMap"
	 * :{"id":490,"name":"尺寸"},"sort"
	 * :0}],"pcvSe":[],"sort":0}],"place":"1232","productName"
	 * :"本店商品电视"},"object":null}
	 */

	String cuxiaodate = "", number = "", brandName = "", id = "", unit = "",
			pmname = "", xiangqing = "", addDate = "", place = "",
			productName = "", stateString = "", image = "";
	int state = 2;
	long num = 0;
	double price = 0, yuanjia = 0;
	List<String> images = new ArrayList<String>();
	List<ModelAttrType> attrTypes = new ArrayList<ModelAttrType>();

	public ModelProductDetail() {
	}

	public ModelProductDetail(JSONObject object) throws JSONException {
		// TODO Auto-generated constructor stub
		if (object.has("cuxiaodate")) {
			if (!object.getString("cuxiaodate").equalsIgnoreCase("null")) {
				cuxiaodate = object.optString("cuxiaodate");
			}
		}
		if (object.has("number")) {
			if (!object.getString("number").equalsIgnoreCase("null")) {
				number = object.optString("number");
			}
		}
		if (object.has("brandName")) {
			if (!object.getString("brandName").equalsIgnoreCase("null")) {
				brandName = object.optString("brandName");
			}
		}
		if (object.has("id")) {
			if (!object.getString("id").equalsIgnoreCase("null")) {
				id = object.optString("id");
			}
		}
		if (object.has("unit")) {
			if (!object.getString("unit").equalsIgnoreCase("null")) {
				unit = object.optString("unit");
			}
		}
		if (object.has("pmname")) {
			if (!object.getString("pmname").equalsIgnoreCase("null")) {
				pmname = object.optString("pmname");
			}
		}
		if (object.has("xiangqing")) {
			if (!object.getString("xiangqing").equalsIgnoreCase("null")) {
				xiangqing = object.optString("xiangqing");
			}
		}
		if (object.has("addDate")) {
			if (!object.getString("addDate").equalsIgnoreCase("null")) {
				addDate = object.optString("addDate");
			}
		}
		if (object.has("place")) {
			if (!object.getString("place").equalsIgnoreCase("null")) {
				place = object.optString("place");
			}
		}
		if (object.has("productName")) {
			if (!object.getString("productName").equalsIgnoreCase("null")) {
				productName = object.optString("productName");
			}
		}
		if (object.has("state")) {
			if (!object.getString("state").equalsIgnoreCase("null")) {
				state = object.optInt("state");
				switch (state) {
				case 1:
					stateString = "有货";
					break;

				case 2:
					stateString = "无货";
					break;
				case 3:
					stateString = "无货,可预订";
					break;
				default:
					stateString = "无货";
					break;
				}
			}
		}
		if (object.has("num")) {
			if (!object.getString("num").equalsIgnoreCase("null")) {
				num = object.optLong("num");
			}
		}
		if (object.has("price")) {
			if (!object.getString("price").equalsIgnoreCase("null")) {
				price = object.optDouble("price");
			}
		}
		if (object.has("yuanjia")) {
			if (!object.getString("yuanjia").equalsIgnoreCase("null")) {
				yuanjia = object.optDouble("yuanjia");
			}
		}
		if (object.has("imgs")) {
			if (!object.getString("imgs").equalsIgnoreCase("null")) {
				JSONArray imageArray = object.getJSONArray("imgs");
				for (int i = 0; i < imageArray.length(); i++) {
					images.add(imageArray.getString(i));
				}
			}
		}
		if (object.has("listAtt")) {
			if (!object.getString("listAtt").equalsIgnoreCase("null")) {
				JSONArray attrArray = object.getJSONArray("listAtt");
				for (int i = 0; i < attrArray.length(); i++) {
					attrTypes.add(new ModelAttrType(attrArray.getJSONObject(i),
							null, ModelAttrType.TYPE_ATTR));
				}
			}
		}
	}

	public void setListPrice(ModelListPrice listPrice) {
		// TODO Auto-generated method stub
		setPrice(listPrice.getPrice());
	}

	public void setAttrPrice(ModelPriceListByAttr attrPrice) {
		// TODO Auto-generated method stub
		setPrice(attrPrice.getPrice());
		setYuanjia(attrPrice.getYuanjia());
		setNum(attrPrice.getNum());
		setState(attrPrice.getState());
		setProductName(attrPrice.getPname());
		setStateString(attrPrice.getStateString());
		setImage(attrPrice.getImgName());
	}

	public String getCuxiaodate() {
		return cuxiaodate;
	}

	public String getNumber() {
		return number;
	}

	public String getBrandName() {
		return brandName;
	}

	public String getId() {
		return id;
	}

	public String getUnit() {
		return unit;
	}

	public String getPmname() {
		return pmname;
	}

	public String getXiangqing() {
		return xiangqing;
	}

	public String getAddDate() {
		return addDate;
	}

	public String getPlace() {
		return place;
	}

	public String getProductName() {
		return productName;
	}

	public String getStateString() {
		return stateString;
	}

	public List<String> getImages() {
		return images;
	}

	public List<ModelAttrType> getAttrTypes() {
		return attrTypes;
	}

	public int getState() {
		return state;
	}

	public long getNum() {
		return num;
	}

	public double getPrice() {
		return price;
	}

	public double getYuanjia() {
		return yuanjia;
	}

	public void setCuxiaodate(String cuxiaodate) {
		this.cuxiaodate = cuxiaodate;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setPmname(String pmname) {
		this.pmname = pmname;
	}

	public void setXiangqing(String xiangqing) {
		this.xiangqing = xiangqing;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setStateString(String stateString) {
		this.stateString = stateString;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setYuanjia(double yuanjia) {
		this.yuanjia = yuanjia;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return image;
	}

}
