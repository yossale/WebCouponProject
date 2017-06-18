package com.tzahia.facade;

import java.util.Collection;
import java.util.Date;

import com.tzahia.beans.Coupon;
import com.tzahia.beans.Customer;
import com.tzahia.dao.CouponType;
import com.tzahia.dbdao.CouponDBDAO;
import com.tzahia.dbdao.CustomerDBDAO;
import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.FacadeException;
import com.tzahia.exceptions.LoginException;

public class CustomerFacade implements CouponClientFacade {

	private CustomerDBDAO customerDBDAO;
	private CouponDBDAO couponDBDAO;
	private Customer customer;
	
	public CustomerFacade(CustomerDBDAO customerDBDAO, CouponDBDAO couponDBDAO) throws DbdaoException {
		this.customerDBDAO = customerDBDAO;
		this.couponDBDAO = couponDBDAO;
	}
	
	public void purchaseCoupon(Coupon coupon) throws FacadeException, DbdaoException{
		boolean purchaseBefore = false;
		Date currentDate = new Date();
		
		for (Coupon c: customer.getCoupons()){
	
			if (c.equals(coupon)){
				purchaseBefore = true;
				break;
			}
		}
		if (purchaseBefore){
			throw new FacadeException("This Coupon has already been purchased by this customer");
		}
		if (coupon.getAmount() <= 0){
			throw new FacadeException("This coupon is out of stock");
		}
		if (currentDate.after(coupon.getEndDate())){
			throw new FacadeException("This coupon has expired");
		}
		couponDBDAO.updateCoupon(coupon);
		customer.getCoupons().add(coupon);
		customerDBDAO.updateCustomer(customer);
		coupon.setAmount(coupon.getAmount()-1);
	}
	
	public Collection<Coupon> getAllPurchasedCoupon() throws DbdaoException{
		customer = customerDBDAO.getCustomer(customer.getId());
		return customer.getCoupons();
	}
	
	public Collection<Coupon> getAllPurchasedCouponByType(CouponType type) throws DbdaoException{
		return customerDBDAO.getAllPurchasedCouponByType(customer, type);
	}
	
	public Collection<Coupon> getAllPurchasedCouponByPrice(double price) throws DbdaoException{
		return customerDBDAO.getAllPurchasedCouponByPrice(customer, price);
	}
	
	public Customer getCustomerInfo() throws DbdaoException {
		return customerDBDAO.getCustomer(customer.getId());	
	}

	@Override
	public boolean login(String user, String password) throws DbdaoException, LoginException {
		try {
			customer = customerDBDAO.login(user, password);
			return true;

		} catch (LoginException e) {
			return false;
		}
	}

}
	
	
	
	


