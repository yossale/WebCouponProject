package com.tzahia.beans;

import java.util.Collection;

public class Customer {

	// Variables Declarations
	private long id;
	private String custName;
	private String password;
	private Collection<Coupon> coupons;

	/**
	 * Empty constructor 
	 */
	public Customer() {
	}
	/**
	 * Constructor with all the variables
	 * @param custName
	 * @param password
	 * @param coupons
	 */
	public Customer(String custName, String password, Collection<Coupon> coupons) {
		this.custName = custName;
		this.password = password;
		this.coupons = coupons;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}
	/**
	 * Checking if this customer equals to the one the function got
	 * @param c
	 * @return
	 */
	public boolean equals(Customer c){
		
		if (this.getId() == c.getId()){
			return true;
		}else {
			return false;
		}
	}
	/**
	 * Make a copy of this customer
	 * @return
	 */
	public Customer cloneCustomer() {
		Customer newCustomer = new Customer();
		newCustomer.custName = this.custName;
		newCustomer.password = this.password;
		return newCustomer;
	}

	// toString
	@Override
	public String toString() {
		String s = "Customer Id: " + id + ", Name: " + custName + ", Password: " + password + "\nCoupons: ";
		
		for (Coupon coupon : coupons){
			s += "\n\t" + coupon.toString(); 
		}
		return s;
	}

}
