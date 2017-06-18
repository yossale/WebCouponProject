package com.tzahia.dao;

import java.util.Collection;

import com.tzahia.beans.Coupon;
import com.tzahia.beans.Customer;
import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.LoginException;

public interface CustomerDAO {
		/**
		 * Set a new Customer to Customer table in the Database
		 * @param customer
		 * @return
		 * @throws DbdaoException
		 */
		public Customer createCustomer (Customer customer) throws DbdaoException;
		/**
		 * Remove a Customer from Database, also remove Customer from inner join tables
		 * @param customer
		 * @throws DbdaoException
		 */
		public void removeCustomer (Customer customer) throws DbdaoException;
		/**
		 * Update a Customer in the Customer table in the Database
		 * @param customer
		 * @throws DbdaoException
		 */
		public void updateCustomer (Customer customer) throws DbdaoException;
		/**
		 * Get a Customer by its id from the Customer table in the Database
		 * @param id
		 * @return
		 * @throws DbdaoException
		 */
		public Customer getCustomer (long id) throws DbdaoException;
		/**
		 * Get a collection of all the Customers from the Customer table in the Database
		 * @return
		 * @throws DbdaoException
		 */
		public Collection<Customer> getAllCustomer() throws DbdaoException;
		/**
		 * Get a collection of all the Coupons of the same Customer from the inner join table in the Database
		 * @param customer
		 * @return
		 * @throws DbdaoException
		 */
		public Collection<Coupon> getCoupons(Customer customer) throws DbdaoException;
		/**
		 * Trying to login: check the name and the password of the Customer and compare it to the Customer table in the Database
		 * @param custName
		 * @param password
		 * @return
		 * @throws DbdaoException
		 * @throws LoginException
		 */
		public Customer login (String custName, String password)throws DbdaoException, LoginException;
}
