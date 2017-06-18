package com.tzahia.facade;

import com.tzahia.dao.CouponExpirationCheck;
import com.tzahia.dbdao.CompanyDBDAO;
import com.tzahia.dbdao.ConnectionPoolSingleton;
import com.tzahia.dbdao.CouponDBDAO;
import com.tzahia.dbdao.CustomerDBDAO;
import com.tzahia.exceptions.ConnectionPoolException;
import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.LoginException;

public class CouponSystemSingleton {

	private static CouponSystemSingleton instance = null;
	private CompanyDBDAO companyDBDAO;
	private CustomerDBDAO customerDBDAO;
	private CouponDBDAO couponDBDAO;
	private CouponExpirationCheck couponeExCh;

	public CouponSystemSingleton() throws DbdaoException {
		couponDBDAO = new CouponDBDAO();
		companyDBDAO = new CompanyDBDAO(couponDBDAO);
		customerDBDAO = new CustomerDBDAO(couponDBDAO);
		couponeExCh = new CouponExpirationCheck();
		Thread couponeExChThread = new Thread(couponeExCh);
		couponeExChThread.start();
	}

	public static CouponSystemSingleton getInstance() throws DbdaoException {
		if (instance == null) {
			instance = new CouponSystemSingleton();
		}
		return instance;
	}

	public CouponClientFacade login(String user, String password, ClientType customer)
			throws DbdaoException, LoginException {
		
		if (ClientType.COMPANY.equals(customer)) {		
			CompanyFacade companyFacade = new CompanyFacade(companyDBDAO, couponDBDAO);
			
			if (companyFacade.login(user, password)) {
				return companyFacade;
			} else {
				throw new LoginException();
			}
		}
		if (ClientType.CUSTOMER.equals(customer)) {		
			CustomerFacade customerFacade = new CustomerFacade(customerDBDAO, couponDBDAO);
		
			if (customerFacade.login(user, password)) {
				return customerFacade;
			} else {
				throw new LoginException();
			}
		}
		if (ClientType.ADMINISTRATOR.equals(customer)) {		
			AdminFacade adminFacade = new AdminFacade(companyDBDAO, customerDBDAO);
			
			if (adminFacade.login(user, password)) {
				return adminFacade;
			} else {
				throw new LoginException();
			}
		}
		System.out.println("clientType doesn't match!");
		return null;
	}

	public void shutdown() throws DbdaoException, ConnectionPoolException {

		couponeExCh.endExpirationCheck();
		ConnectionPoolSingleton.getInstance().closeAllConnection();
	}

}
