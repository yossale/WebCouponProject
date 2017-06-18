package com.tzahia.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import com.tzahia.beans.Company;
import com.tzahia.beans.Coupon;
import com.tzahia.beans.Customer;
import com.tzahia.dao.CouponType;
import com.tzahia.dbdao.CompanyDBDAO;
import com.tzahia.dbdao.ConnectionPoolSingleton;
import com.tzahia.dbdao.CouponDBDAO;
import com.tzahia.dbdao.CustomerDBDAO;
import com.tzahia.exceptions.ConnectionPoolException;
import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.LoginException;

public class DBDAOTest {

	private static CouponDBDAO couponDBDAO = null;
	private static CompanyDBDAO companyDBDAO = null;
	private static CustomerDBDAO customerDBDAO = null;

	public static void main(String[] args) throws DbdaoException {

		try {
			couponDBDAO = new CouponDBDAO();
//			myCouponTest(couponDBDAO);
			companyDBDAO = new CompanyDBDAO();
//			myCompanyTest(companyDBDAO, couponDBDAO);
			customerDBDAO = new CustomerDBDAO();
			customerTest(customerDBDAO, couponDBDAO);
//			connectionPoolTest(ConnectionPoolSingleton.getInstance());

		} catch (DbdaoException e) {
			System.err.println(e.getMessage());
			throw e;
		}
	}

	private static void myCouponTest(CouponDBDAO couponDBDAO) throws DbdaoException {
		Coupon coupon1 = new Coupon();
		coupon1.setAmount(20);
		coupon1.setMessage("message");
		coupon1.setPrice(39.99);
		java.sql.Date today = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
		java.sql.Date nextWeek = new java.sql.Date(
				Calendar.getInstance().getTimeInMillis() + (7 * 24 * 60 * 60 * 1000));
		coupon1.setStartDate(today);
		coupon1.setEndDate(nextWeek);
		coupon1.setImage("image");
		coupon1.setTitle("my first coupon");
		coupon1.setType(CouponType.FOOD);
		System.out.println("New coupons:");
		couponDBDAO.createCoupon(coupon1);
		System.out.println(coupon1);

		Coupon coupon2 = new Coupon();
		coupon2.setAmount(5);
		coupon2.setMessage("message");
		coupon2.setPrice(20);
		java.sql.Date twoDaysFromNow = new java.sql.Date(
				Calendar.getInstance().getTimeInMillis() + (2 * 24 * 60 * 60 * 1000));
		coupon2.setStartDate(today);
		coupon2.setEndDate(twoDaysFromNow);
		coupon2.setImage("image");
		coupon2.setTitle("my second coupon");
		coupon2.setType(CouponType.ELECTRICITY);
		System.out.println("New coupons:");
		couponDBDAO.createCoupon(coupon2);
		System.out.println(coupon2);

		System.out.println("\nAll coupons:");
		printAllCoupons();

		coupon1.setAmount(67);
		coupon1.setMessage("updated message");
		coupon1.setPrice(4.45);
		coupon1.setImage("updated image");
		coupon1.setTitle("updated first coupon");
		coupon1.setType(CouponType.SPORTS);
		coupon1.setStartDate(today);
		coupon1.setEndDate(nextWeek);
		couponDBDAO.updateCoupon(coupon1);
		System.out.println("\nUpdated coupon 1");
		System.out.println(coupon1);

		printCouponsByType(CouponType.FOOD);
		printCouponsByType(CouponType.ELECTRICITY);
		printCouponsByType(CouponType.CAMPING);

		Coupon newCoupon = couponDBDAO.getCoupon(coupon1.getId());
		System.out.println("\nResult of getCoupon(): \n" + newCoupon);

//		couponDBDAO.removeCoupon(coupon1);
//		couponDBDAO.removeCoupon(coupon2);
//		System.out.println("All coupons after remove:");
//		printAllCoupons();
	}

	private static void myCompanyTest(CompanyDBDAO companyDBDAO, CouponDBDAO couponDBDAO) throws DbdaoException {
		Company company1 = new Company();
		company1.setCompName("Nike");
		company1.setPassword("NikePassword");
		company1.setEmail("Nike@gmail.com");

		Coupon compCoup1 = couponDBDAO.getCoupon(206);
		Coupon compCoup2 = couponDBDAO.getCoupon(207);
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		coupons.add(compCoup1);
		coupons.add(compCoup2);
		company1.setCoupons(coupons);

		companyDBDAO.createCompany(company1);
		System.out.println("\nNew Company: ");
		System.out.println(company1);
		System.out.println("\nNew Company with getCompany():");
		System.out.println(companyDBDAO.getCompany(company1.getId()));

		Company company2 = new Company("second company", "newpassword", "second email", new ArrayList<Coupon>());
		companyDBDAO.createCompany(company2);
		System.out.println("\nNew Company: ");
		System.out.println(company2);
		System.out.println("\nNew Company with getCompany():");
		System.out.println(companyDBDAO.getCompany(company2.getId()));

		try {
			Company company = companyDBDAO.login(company1.getCompName(), company1.getPassword());
			System.out.println("\nValid login of " + company.getCompName());
			company = companyDBDAO.login(company1.getCompName(), company2.getPassword());
			System.out.println("\nLogin error");

		} catch (LoginException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("\nAll companies: ");
		printAllCompanies();

		company1.setCompName("Updated name");
		company1.setPassword("new password");
		company1.setEmail("new email");
		companyDBDAO.updateCompany(company1);
		System.out.println("\nAll companies after update");
		printAllCompanies();

//		companyDBDAO.removeCompany(company2);
//		companyDBDAO.removeCompany(company1);
//		System.out.println("\nAll companies after remove");
//		printAllCompanies();
	}

	private static void customerTest(CustomerDBDAO customerDBDAO, CouponDBDAO couponDBDAO) throws DbdaoException {
		Customer customer1 = new Customer();
		customer1.setCustName("My Customer");
		customer1.setPassword("easy password");

		Coupon coupon11 = couponDBDAO.getCoupon(206);
		Coupon coupon12 = couponDBDAO.getCoupon(207);
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		coupons.add(coupon11);
		coupons.add(coupon12);
		customer1.setCoupons(coupons);

		customerDBDAO.createCustomer(customer1);
		System.out.println("New Customer: ");
		System.out.println(customer1);
		System.out.println("\nNew Customer with getCustomer():");
		System.out.println(customerDBDAO.getCustomer(customer1.getId()));

		Customer customer2 = new Customer("second customer", "second email", new ArrayList<Coupon>());
		customerDBDAO.createCustomer(customer2);
		System.out.println("\nNew Customer: ");
		System.out.println(customer2);
		System.out.println("\nNew Customer with getCustomer():");
		System.out.println(customerDBDAO.getCustomer(customer2.getId()));

		try {
			customerDBDAO.login(customer1.getCustName(), customer1.getPassword());
			System.out.println("\nValid login");
			customerDBDAO.login(customer1.getCustName(), customer2.getPassword());
			System.out.println("\nLogin error");

		} catch (LoginException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("\nAll customers: ");
		printAllCustomers();

		customer1.setCustName("Updated name");
		customer1.setPassword("new password");
		customerDBDAO.updateCustomer(customer1);
		System.out.println("\nAll companies after update");
		printAllCustomers();

//		customerDBDAO.removeCustomer(customer2);
//		customerDBDAO.removeCustomer(customer1);
//		System.out.println("\nAll companies after remove");
//		printAllCustomers();
	}

	private static void connectionPoolTest(ConnectionPoolSingleton pool) throws DbdaoException {
		testCloseAllConnections(pool);
		System.out.println("/nTesting synchronization of getConnection and returnConnection functions:");
		testPoolSynchronization(pool);
	}

	private static void printAllCoupons() throws DbdaoException {
		ArrayList<Coupon> coupons = (ArrayList<Coupon>) couponDBDAO.getAllCoupon();
		System.out.println();
		for (Coupon coupon : coupons)
			System.out.println(coupon);
	}

	private static void printCouponsByType(CouponType type) throws DbdaoException {
		ArrayList<Coupon> coupons = (ArrayList<Coupon>) couponDBDAO.getCouponByType(type);
		System.out.println("\nCoupons of type " + type + ":\n");
		for (Coupon coupon : coupons)
			System.out.println(coupon);
	}

	private static void printAllCompanies() throws DbdaoException {
		ArrayList<Company> companies = (ArrayList<Company>) companyDBDAO.getAllCompanies();
		for (Company company : companies)
			System.out.println("\n" + company);
	}

	private static void printAllCustomers() throws DbdaoException {
		ArrayList<Customer> customers = (ArrayList<Customer>) customerDBDAO.getAllCustomer();
		for (Customer customer : customers)
			System.out.println("\n" + customer);
	}

	private static void testPoolSynchronization(ConnectionPoolSingleton pool) throws DbdaoException {
		GetConnectionThread getThread = new GetConnectionThread();
		ReturnConnectionThread returnThread = new ReturnConnectionThread();
		getThread.start(); // tries to get 15 connections in 500 mls gaps
		returnThread.start(); // tries to get 15 connections in 2500 mls gaps
	}

	private static void testCloseAllConnections(ConnectionPoolSingleton pool) throws DbdaoException {
		System.out.println("\nClosing all connections:");

		try {
			pool.closeAllConnection();
		} catch (ConnectionPoolException e1) {
			e1.printStackTrace();
		}
		Connection connection = pool.getConnection();

		try {
			if (connection.isClosed()) {
				System.out.println("Connections closed");

			} else {
				System.out.println("Connections not closed");
			}

		} catch (SQLException e) {
			throw new DbdaoException("Connection pool error");
		}

		ConnectionPoolSingleton.getInstance();
	}
}
