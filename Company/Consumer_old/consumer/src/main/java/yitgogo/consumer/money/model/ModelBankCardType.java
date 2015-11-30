package yitgogo.consumer.money.model;

/**
 * 银行卡类型
 * 
 * @author Tiger
 * 
 */
public class ModelBankCardType {

	String id = "", name = "";

	public ModelBankCardType() {
	}

	public ModelBankCardType(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
