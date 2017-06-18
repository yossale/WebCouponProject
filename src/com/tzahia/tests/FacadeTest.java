package com.tzahia.tests;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

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
import com.tzahia.exceptions.FacadeException;
import com.tzahia.exceptions.LoginException;
import com.tzahia.facade.AdminFacade;
import com.tzahia.facade.ClientType;
import com.tzahia.facade.CompanyFacade;
import com.tzahia.facade.CouponClientFacade;
import com.tzahia.facade.CouponSystemSingleton;
import com.tzahia.facade.CustomerFacade;

public class FacadeTest {

	private static CouponDBDAO couponDBDAO;
	private static CompanyDBDAO companyDBDAO;
	private static CustomerDBDAO customerDBDAO;
	private static Company company1, company2, company3 = null;
	private static Customer customer1, customer2, customer3 = null;
	private static Coupon coupon1, coupon2, coupon3, coupon4 = null;
	private static java.sql.Date date1, date2, date3, date4 = null;
	private static ConnectionPoolSingleton pool;

	public static void main(String[] args) {

		try {
			setNewFields();
			SingletonLoginAndExpirationTest();
			removeSomeFields();
			shutdownTest();
			testPoolSynchronization(pool);

		} catch (DbdaoException | FacadeException e) {
			System.err.println(e.getMessage());

		} catch (InterruptedException e) {
			System.err.println("Error in Thread.sleep method");
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (ConnectionPoolException e) {
			e.printStackTrace();
		}
	}

	public static void custFacadeTest(CouponClientFacade facade) throws FacadeException, DbdaoException {
		CustomerFacade custFacade = (CustomerFacade) facade;
		System.out.println("\nCustomer facade test\n\nCustomer data:");
		System.out.println(custFacade.getCustomerInfo());

		try {
			custFacade.purchaseCoupon(coupon1);
			System.out.println("Successful coupon purchase");
			custFacade.purchaseCoupon(coupon1);
			System.out.println("\nWrongly successful duplicate purchase");

		} catch (FacadeException e) {
			System.err.println("Failed coupon purchase: " + e.getMessage());
		}

		try {
			coupon2.setAmount(0);
			couponDBDAO.updateCoupon(coupon2);
			custFacade.purchaseCoupon(coupon2);
			System.out.println("\nWrongly successful out-of-stock purchase");

		} catch (FacadeException e) {
			System.err.println("Failed coupon purchase: " + e.getMessage());
		}

		coupon2.setAmount(18);
		custFacade.purchaseCoupon(coupon2);
		System.out.println("Another successful coupon purchase");
		System.out.println("\nAll purchased coupons:");
		printAllPurchasedCoupons(custFacade);
		System.out.println("\nAll purchased CAMPING coupons:");
		printAllPurchasedCouponsByType(custFacade, CouponType.CAMPING);
		System.out.println("\nAll purchased coupon up to 5 NIS:");
		printAllPurchasedCouponsByPrice(custFacade, 5);
	}

	private static void setNewFields() throws DbdaoException {
		couponDBDAO = new CouponDBDAO();
		companyDBDAO = new CompanyDBDAO();
		customerDBDAO = new CustomerDBDAO();

		try {
			DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
			java.util.Date d1 = format.parse("01-01-2017");
			java.util.Date d2 = format.parse("12-02-2017");
			java.util.Date d3 = format.parse("20-08-2017");
			java.util.Date d4 = format.parse("11-05-2018");

			date1 = new java.sql.Date(d1.getTime());
			date2 = new java.sql.Date(d2.getTime());
			date3 = new java.sql.Date(d3.getTime());
			date4 = new java.sql.Date(d4.getTime());

		} catch (ParseException e) {
			System.err.println(e.toString());
		}

		coupon1 = new Coupon("a RESTURANTS coupon", date1, date3, 7, CouponType.RESTURANTS, "Resturamt message", 4.7,
				"image");
		coupon2 = new Coupon("a HEALTH coupon", date2, date3, 33, CouponType.HEALTH, "Health message", 2.4, "image");
		coupon3 = new Coupon("a ELECTRICITY coupon", date2, date4, 11, CouponType.ELECTRICITY, "Electricity message", 3,
				"image");
		coupon4 = new Coupon("a CAMPING coupon", date1, date2, 3, CouponType.CAMPING, "No message", 11, "image");
		coupon1 = couponDBDAO.createCoupon(coupon1);
		coupon2 = couponDBDAO.createCoupon(coupon2);
		coupon3 = couponDBDAO.createCoupon(coupon3);
		coupon4 = couponDBDAO.createCoupon(coupon4);

		company1 = new Company("LG", "LGPass", "LG@gmail.com", new ArrayList<Coupon>(Arrays.asList(coupon1)));
		company2 = new Company("KIA", "KIAPass", "KIA@gmail.com",
				new ArrayList<Coupon>(Arrays.asList(coupon2, coupon3)));
		company3 = new Company("CASTRO", "CASTROPass", "CASTRO@gmail.com", new ArrayList<Coupon>());
		company1 = companyDBDAO.createCompany(company1);
		company2 = companyDBDAO.createCompany(company2);
		company3 = companyDBDAO.createCompany(company3);

		customer1 = new Customer("Tzahi", "TzahiPass", new ArrayList<Coupon>());
		customer2 = new Customer("Moshe", "MoshePass", new ArrayList<Coupon>());
		customer3 = new Customer("Ronit", "RonitPass", new ArrayList<Coupon>());
		customer1 = customerDBDAO.createCustomer(customer1);
		customer2 = customerDBDAO.createCustomer(customer2);
		customer3 = customerDBDAO.createCustomer(customer3);
	}

	private static void SingletonLoginAndExpirationTest()
			throws DbdaoException, FacadeException, InterruptedException, LoginException {

		// initialize system singleton and test daily expiration task
		System.out.println("\nTesting daily coupon expiration task:");
		System.out.println("\nAll coupons:");
		printAllCoupons();
		CouponSystemSingleton couponSystemsSingleton = CouponSystemSingleton.getInstance();
		Thread.sleep(1500);
		System.out.println("\nAll coupons after expiration thread started:");
		printAllCoupons();

		try {
			// successful login's examples
			CouponClientFacade custFacade = couponSystemsSingleton.login("Moshe", "MoshePass", ClientType.CUSTOMER);
			System.out.println("\nFacade type: " + custFacade.getClass().toString());
			custFacadeTest(custFacade);

			CouponClientFacade compFacade = couponSystemsSingleton.login("LG", "LGPass", ClientType.COMPANY);
			System.out.println("\nFacade type: " + compFacade.getClass().toString());
			compFacadeTest(compFacade);

			CouponClientFacade adminFacade = couponSystemsSingleton.login("admin", "1234", ClientType.ADMINISTRATOR);
			System.out.println("\nFacade type: " + adminFacade.getClass().toString());
			adminFacadeTest(adminFacade);

		} catch (LoginException e) {
			System.out.println(e);
		}

		System.out.println("\nExpect three invalid logins:");
		try {
			couponSystemsSingleton.login("admin", "2222", ClientType.ADMINISTRATOR);
			System.out.println("Wrongly successful login");

		} catch (LoginException e) {
			System.err.println("Invalid login");
		}

		try {
			couponSystemsSingleton.login("LG", "hhh", ClientType.COMPANY);
			System.out.println("Wrongly successful login");

		} catch (LoginException e) {
			System.err.println("Invalid login");
		}

		try {
			couponSystemsSingleton.login("Moshe", "aaa", ClientType.CUSTOMER);
			System.out.println("Wrongly successful login");

		} catch (LoginException e) {
			System.err.println("Invalid login");
		}
	}

	private static void compFacadeTest(CouponClientFacade facade) throws DbdaoException {
		CompanyFacade compFacade = (CompanyFacade) facade;
		System.out.println("\nCompany data:");
		System.out.println(compFacade.getCompanyInfo());

		System.out.println("\nTesting createCoupon where title is already in use: ");
		java.sql.Date today = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
		java.sql.Date tomorrow = new java.sql.Date(Calendar.getInstance().getTimeInMillis() + (24 * 60 * 60 * 1000));
		Coupon coupon = new Coupon("qqq", today, tomorrow, CouponType.HEALTH, "new coupon", 3);
		System.out.println(coupon);

		try {
			coupon = compFacade.createCoupon(coupon);

		} catch (DbdaoException e) {
			System.err.println(e.getMessage());
		}
		coupon.setTitle("New title");

		try {
			coupon = compFacade.createCoupon(coupon);

		} catch (DbdaoException e) {
			System.err.println(e.getMessage());
		}

		System.out.println("\ngetCoupon():");
		System.out.println(compFacade.getCoupon(coupon.getId()));

		System.out.println("\nUpdate coupon price to 7 NIS:");
		coupon.setPrice(7);
		compFacade.updateCoupon(coupon);
		System.out.println(coupon);

		System.out.println("\nAll coupons:");
		printAllCompanyCoupons(compFacade);
		System.out.println("\nCompany data: ");
		System.out.println(compFacade.getCompanyInfo());

		System.out.println("\nAll coupons between 2.5 and 3.5 NIS");
		System.out.println(compFacade.getCouponsByMaxPrice(20));

		System.out.println("\nAll sports coupons:");
		System.out.println(compFacade.getCouponByType(CouponType.FOOD));

		System.out.println("\nAll coupons endDate from " + date1.toString() + " to " + date3.toString());
		System.out.println(compFacade.getCouponsByEndDate(date3));

		compFacade.removeCoupon(coupon);
		System.out.println("\nCoupon removed.");
		System.out.println("\nAll coupons:");
		printAllCompanyCoupons(compFacade);
	}

	private static void adminFacadeTest(CouponClientFacade facade) throws DbdaoException, FacadeException {
		AdminFacade adminFacade = (AdminFacade) facade;
		Company company11 = new Company();
		company11.setCompName("My Company");
		company11.setPassword("easy password");
		company11.setEmail("new email");

		// testind add coupons to company
		java.sql.Date today = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
		java.sql.Date tomorrow = new java.sql.Date(Calendar.getInstance().getTimeInMillis() + (24 * 60 * 60 * 1000));
		Coupon coupon11 = new Coupon("first coupon", today, tomorrow, 4, CouponType.CAMPING, "this is my message", 4,
				"my image");
		coupon11 = couponDBDAO.createCoupon(coupon11);
		Coupon coupon12 = new Coupon("second coupon", today, tomorrow, 2, CouponType.FOOD, "another coupon", 6,
				"my image");
		coupon12 = couponDBDAO.createCoupon(coupon12);
		ArrayList<Coupon> coupons = new ArrayList<Coupon>();
		coupons.add(coupon11);
		coupons.add(coupon12);
		company11.setCoupons(coupons);

		company11 = adminFacade.createCompany(company11);
		System.out.println("\nNew Company: ");
		System.out.println(company11);
		System.out.println("\nNew Company with getCompany():");
		System.out.println(adminFacade.getCompany(company11.getId()));

		Company company12 = new Company("second company", "password2", "second email", new ArrayList<Coupon>());
		company12 = adminFacade.createCompany(company12);
		System.out.println("\nNew Company: ");
		System.out.println(company12);
		System.out.println("\nNew Company with getCompany():");
		System.out.println(adminFacade.getCompany(company12.getId()));

		System.out.println("\nAll companies: ");
		printAllCompanies(adminFacade);

		company11.setCompName("Updated name");
		company11.setPassword("new password");
		company11.setEmail("new email");
		coupons.removeAll(coupons);
		coupons.add(coupon11);
		company11.setCoupons(coupons);
		adminFacade.updateCompany(company11);
		System.out.println("\nCompany after update:");
		Company company = adminFacade.getCompany(company11.getId());
		System.out.println(company);
		System.out.println("\nAll companies after update");
		printAllCompanies(adminFacade);

		adminFacade.removeCompany(company12);
		adminFacade.removeCompany(company11);
		System.out.println("\nAll companies after remove");
		printAllCompanies(adminFacade);

		Customer customer = customer3.cloneCustomer();
		System.out.println("\nTry to add customer with duplicate name:");

		try {
			customer = adminFacade.createCustomer(customer);

		} catch (FacadeException e) {
			System.err.println(e.getMessage());
		}

		customer.setCustName("new name");
		System.out.println("\nWith different name:");

		try {
			customer = adminFacade.createCustomer(customer);

		} catch (FacadeException e) {
			System.err.println(e.getMessage());
		}

		System.out.println("\nAll customers:");
		printAllCustomers(adminFacade);

		System.out.println("\nOld customer password:");
		System.out.println(customer.getPassword());
		customer.setCustName("newnew name");
		customer.setPassword("newpassword");
		System.out.println("\nUpdate customer (only password change should be effective):");
		adminFacade.updateCustomer(customer);
		System.out.println("Getting updated customer with getCustomer:");
		customer = adminFacade.getCustomer(customer.getId());
		System.out.println(customer);
		System.out.println("password: " + customer.getPassword());

		System.out.println("\nRemoving customer:");
		adminFacade.removeCustomer(customer);
		printAllCustomers(adminFacade);
		couponDBDAO.removeCoupon(coupon12);
	}

	private static void removeSomeFields() throws DbdaoException {
		couponDBDAO.removeCoupon(coupon1);
		couponDBDAO.removeCoupon(coupon2);
		couponDBDAO.removeCoupon(coupon3);
		customerDBDAO.removeCustomer(customer1);
		customerDBDAO.removeCustomer(customer2);
		customerDBDAO.removeCustomer(customer3);
		companyDBDAO.removeCompany(company1);
		companyDBDAO.removeCompany(company2);
		companyDBDAO.removeCompany(company3);
	}

	private static void shutdownTest() throws DbdaoException, ConnectionPoolException {
		CouponSystemSingleton cSystem = CouponSystemSingleton.getInstance();

		System.out.println("\nTesting shutdown");
		System.out.println("Expect failed getConnection:");

		try {
			ConnectionPoolSingleton pool = ConnectionPoolSingleton.getInstance();
			cSystem.shutdown();
			pool.getConnection().createStatement();
			System.err.println("shutdown failed");

		} catch (DbdaoException | SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void printAllCompanyCoupons(CompanyFacade facade) throws DbdaoException {
		ArrayList<Coupon> coupons = (ArrayList<Coupon>) facade.getAllCoupons();
		System.out.println();
		for (Coupon coupon : coupons)
			System.out.println(coupon);
	}

	private static void printAllCoupons() throws DbdaoException {
		ArrayList<Coupon> coupons = (ArrayList<Coupon>) couponDBDAO.getAllCoupon();
		System.out.println();
		for (Coupon coupon : coupons)
			System.out.println(coupon);
	}

	private static void printAllPurchasedCoupons(CustomerFacade facade) throws DbdaoException {
		ArrayList<Coupon> coupons = (ArrayList<Coupon>) facade.getAllPurchasedCoupon();
		System.out.println();
		for (Coupon coupon : coupons)
			System.out.println(coupon);
	}

	private static void printAllPurchasedCouponsByType(CustomerFacade facade, CouponType type) throws DbdaoException {
		ArrayList<Coupon> coupons = (ArrayList<Coupon>) facade.getAllPurchasedCouponByType(type);
		System.out.println();
		for (Coupon coupon : coupons)
			System.out.println(coupon);
	}

	private static void printAllPurchasedCouponsByPrice(CustomerFacade facade, double price) throws DbdaoException {

		ArrayList<Coupon> coupons = (ArrayList<Coupon>) facade.getAllPurchasedCouponByPrice(price);
		System.out.println();
		for (Coupon coupon : coupons)
			System.out.println(coupon);
	}

	private static void printAllCustomers(AdminFacade adminFacade) throws DbdaoException {
		ArrayList<Customer> customers = (ArrayList<Customer>) adminFacade.getAllCustomers();
		for (Customer customer : customers)
			System.out.println("\n" + customer);
	}

	private static void printAllCompanies(AdminFacade facade) throws DbdaoException {
		ArrayList<Company> companies = (ArrayList<Company>) facade.getAllCompanies();
		for (Company company : companies)
			System.out.println("\n" + company);
	}

	private static void testPoolSynchronization(ConnectionPoolSingleton pool) throws DbdaoException {
		GetConnectionThread getThread = new GetConnectionThread();
		ReturnConnectionThread returnThread = new ReturnConnectionThread();
		// Thread that get 12 connections in 700 mls gaps
		getThread.start();
		// Thread that return 12 connections in 2000 mls gaps
		returnThread.start();
	}
}
