package com.tzahia.dao;

import java.util.Collection;

import com.tzahia.beans.Coupon;
import com.tzahia.exceptions.DbdaoException;

public interface CouponDAO {
	/**
	 * Set a new Coupon to Coupon table in the Database
	 * @param coupon
	 * @return
	 * @throws DbdaoException
	 */
	public Coupon createCoupon (Coupon coupon) throws DbdaoException;
	/**
	 * Remove a Coupon from Database, also remove this coupon from all the inner join tables
	 * @param coupon
	 * @throws DbdaoException
	 */
	public void removeCoupon (Coupon coupon) throws DbdaoException;
	/**
	 * Update a Coupon in the Coupon table in the Database
	 * @param coupon
	 * @throws DbdaoException
	 */
	public void updateCoupon (Coupon coupon) throws DbdaoException;
	/**
	 * Get a Coupon by its id from the Coupon table in the Database
	 * @param id
	 * @return
	 * @throws DbdaoException
	 */
	public Coupon getCoupon (long id) throws DbdaoException;
	/**
	 * Get a collection of all the Coupons from the Coupon table in the Database
	 * @return
	 * @throws DbdaoException
	 */
	public Collection<Coupon> getAllCoupon() throws DbdaoException;
	/**
	 * Get a collection of all the Coupons by its type from the Coupon table in the Database
	 * @param couponType
	 * @return
	 * @throws DbdaoException
	 */
	public Collection<Coupon> getCouponByType(CouponType couponType)throws DbdaoException;



}
