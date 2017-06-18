package com.tzahia.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class Company {

	// Variables Declarations
	private long id;
	private String compName;
	private String password;
	private String email;
	private Collection<Coupon> coupons;

	/**
	 * Constructor with all the variables
	 * @param compName
	 * @param password
	 * @param email
	 * @param coupons
	 */
	public Company(String compName, String password, String email, Collection<Coupon> coupons) {
		this.compName = compName;
		this.password = password;
		this.email = email;
		this.coupons = coupons;
	}
	
	public Company(String compName, String password, String email) {
		this.compName = compName;
		this.password = password;
		this.email = email;
		this.coupons = new LinkedList<Coupon>();
	}
	
	/**
	 * Empty Constructor
	 */
	public Company(){
		coupons = new ArrayList<>(); 
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}
	/**
	 * Checking if this company equals to the one the function got
	 * @param c
	 * @return
	 */
	public boolean equals(Company c){
		if (this.getId() == c.getId()){
			return true;
		}else {
			return false;
		}
	}
	/**
	 * Make a copy of this company
	 * @return
	 */
	public Company cloneCompany() {
		Company newCompany = new Company();
		newCompany.compName = this.compName;
		newCompany.password = this.password;
		newCompany.email = this.email;
		return newCompany;
	}

	// toString
	@Override
	public String toString() {
		String s = id + " " + compName + "\nEmail - " + email + "\nPassword - " + password + "\nCoupons: ";
		
		for (Coupon coupon : coupons){
			s += "\n\t" + coupon.toString(); 
		}
		return s;
	}

}
