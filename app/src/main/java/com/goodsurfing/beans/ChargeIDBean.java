package com.goodsurfing.beans;

public class ChargeIDBean {

	private String id;
	private String name;
	private String Money;
	private String type;
	private boolean checked;

	public ChargeIDBean() {
		super();
	}

	public ChargeIDBean(String id, String name, String price) {
		super();
		this.id = id;
		this.name = name;
		this.Money = price;
	}

	public ChargeIDBean(String id, String name, String price, String type) {
		super();
		this.id = id;
		this.name = name;
		this.Money = price;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoney() {
		return Money;
	}

	public void setMoney(String price) {
		this.Money = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
