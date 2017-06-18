package com.tzahia.beans;

import java.util.Date;

import com.tzahia.dao.CouponType;

public class Coupon {

	// Variables Declarations
	private long id;
	private String title;
	private Date startDate;
	private Date endDate;
	private int amount;
	private CouponType type;
	private String message;
	private double price;
	private String image;
	
	/**
	 * Empty Constructor
	 */
	public Coupon() {
	}

	/**
	 * Constructor with all the variables
	 * @param title
	 * @param startDate
	 * @param endDate
	 * @param amount
	 * @param type
	 * @param message
	 * @param price
	 * @param image
	 */
	public Coupon(String title, Date startDate, Date endDate, int amount, CouponType type, String message, double price,
			String image) {
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.type = type;
		this.message = message;
		this.price = price;
		this.image = image;
	}
	/**
	 * Another constructor with less variables
	 * @param title
	 * @param startDate
	 * @param endDate
	 * @param type
	 * @param message
	 * @param price
	 */
	public Coupon(String title, Date startDate, Date endDate, CouponType type, String message, double price) {
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.type = type;
		this.message = message;
		this.price = price;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public CouponType getType() {
		return type;
	}

	public void setType(CouponType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * Checking if this coupon has already been purchased by this customer in the past
	 * @param c
	 * @return
	 */
	public boolean equals(Coupon c){
		
		if (this.getId() == c.getId()){
			return true;
		}else {
			return false;
		}
	}
	/**
	 * Make a copy of this coupon
	 * @return
	 */
	public Coupon cloneCoupon(){
		Coupon newCoupon = new Coupon();
		newCoupon.title = this.title;
		newCoupon.startDate = this.startDate;
		newCoupon.endDate = this.endDate;
		newCoupon.amount = this.amount;
		newCoupon.type = this.type;
		newCoupon.message = this.message;
		newCoupon.price = this.price;
		newCoupon.image = this.image;
		return newCoupon;
	}

	// toString
	@Override
	public String toString() {
		return "Coupon id: " + id + ", title: " + title + ", valid from: " + startDate + ", till: " + endDate + 
				".\nThere are: " + amount + " in stock, CouponType: " 
				+ type + ". message: " + message + ". Price in NIS: " + price + ", image: " + image + "\n";  
	}

}
