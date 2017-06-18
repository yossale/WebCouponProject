package com.tzahia.dao;

import java.util.Collection;

import com.tzahia.beans.Company;
import com.tzahia.beans.Coupon;
import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.LoginException;


public interface CompanyDAO {
	/**
	 * Set a new Company to Company table in the Database
	 * @param company
	 * @return
	 * @throws DbdaoException
	 */
	public Company createCompany(Company company) throws DbdaoException;
	/**
	 * Remove a Company from Database, also remove all the company's coupons
	 * @param company
	 * @throws DbdaoException
	 */
	public void removeCompany(Company company) throws DbdaoException;
	/**
	 * Update a Company in the Company table in the Database
	 * @param company
	 * @throws DbdaoException
	 */
	public void updateCompany(Company company) throws DbdaoException;
	/**
	 * Get a Company by its id from the Company table in the Database
	 * @param id
	 * @return
	 * @throws DbdaoException
	 */
	public Company getCompany(long id) throws DbdaoException;
	/**
	 * Get a collection of all the Companies from the Company table in the Database
	 * @return
	 * @throws DbdaoException
	 */
	public Collection<Company> getAllCompanies() throws DbdaoException;
	/**
	 * Get a collection of all the Company's Coupons from the company_coupon join table in the Database
	 * @param company
	 * @return
	 * @throws DbdaoException
	 */
	public Collection<Coupon> getCoupons(Company company) throws DbdaoException;
	/**
	 * Trying to login: check the name and the password of the company and compare it to the company table in the Database 
	 * @param compName
	 * @param password
	 * @return
	 * @throws DbdaoException
	 * @throws LoginException
	 */
	public Company login(String compName, String password) throws DbdaoException, LoginException;

}
