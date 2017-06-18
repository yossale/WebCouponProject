package com.tzahia.dbdao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.mysql.jdbc.Statement;
import com.tzahia.beans.Coupon;
import com.tzahia.beans.Customer;
import com.tzahia.dao.CouponType;
import com.tzahia.dao.CustomerDAO;
import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.LoginException;

public class CustomerDBDAO implements CustomerDAO {

	private final ConnectionPoolSingleton connectionPoolSingleton = ConnectionPoolSingleton.getInstance();
	private CouponDBDAO couponDBDAO;

	// Constractors
	public CustomerDBDAO() throws DbdaoException {
		couponDBDAO = new CouponDBDAO();
	}

	public CustomerDBDAO(CouponDBDAO couponDBDAO) throws DbdaoException {
		this.couponDBDAO = couponDBDAO;
	}

	// Create
	@Override
	public Customer createCustomer(Customer customer) throws DbdaoException {
		Connection connection = null;
		customerValidCheck(customer);

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "INSERT INTO myproject.customer (cust_name, password) VALUES ('" + customer.getCustName()
					+ "','" + customer.getPassword() + "')";
			java.sql.PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.executeUpdate();
			ResultSet generatedKeys = statement.getGeneratedKeys();
			generatedKeys.next();
			long newId = generatedKeys.getLong(1);
			customer.setId(newId);
			for (Coupon coupon : customer.getCoupons()) {
				connection.createStatement()
						.execute("INSERT INTO myproject.customer_coupon (cust_id, coupon_id) VALUES ('"
								+ customer.getId() + "', '" + coupon.getId() + "');");
			}
			return customer;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.createCustomer:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Customer.createCustomer did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Customer.createCustomer did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Remove
	@Override
	public void removeCustomer(Customer customer) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			// deleting the customer from customer_coupon table
			connection.createStatement()
					.execute("DELETE FROM myproject.customer_coupon WHERE cust_id=" + customer.getId());

			// deleting the customer from customer table and the Database
			final boolean results = connection.createStatement()
					.execute("DELETE FROM myproject.customer WHERE id=" + customer.getId());

			if (results) {
				System.out.println("The customer was removed successfully");
			} else {
				System.out.println("Customer does not exist");
			}

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.removeCustomer:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Customer.removeCustomer did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Customer.removeCustomer did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Update
	@Override
	public void updateCustomer(Customer customer) throws DbdaoException {
		Connection connection = null;
		customerValidCheck(customer);

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "UPDATE `myproject`.`customer` SET `cust_name`='" + customer.getCustName()
					+ "',`password`='" + customer.getPassword() + "'WHERE `id`=" + customer.getId();
			final boolean results = connection.createStatement().execute(sql);

			if (results) {
				System.out.println("Update customer's details was issued successfully");
			} else {
				System.out.println("Customer does not exist");
			}

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.updateCustomer:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Customer.updateCustomer did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Customer.updateCustomer did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Getters
	@Override
	public Customer getCustomer(long id) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "SELECT * FROM `myproject`.`customer` WHERE `id`=" + id;
			final ResultSet results = connection.createStatement().executeQuery(sql);

			if (results.next()) {
				final Customer customer = getCustomerByResultSet(results);
				return customer;
			} else {
				throw new DbdaoException("Customer does not exist");
			}

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.getCustomer:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Customer.getCustomer did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Customer.getCustomer did not succeed:\n" + e.getMessage());
			}
		}
	}

	@Override
	public Collection<Customer> getAllCustomer() throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "SELECT * FROM `myproject`.`customer`";
			final ResultSet results = connection.createStatement().executeQuery(sql);
			return createCustomerCollection(results);

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.getAllCustomer:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Customer.getAllCustomer did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Customer.getAllCustomer did not succeed:\n" + e.getMessage());
			}
		}
	}

	@Override
	public Collection<Coupon> getCoupons(Customer customer) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "select myproject.coupon. * from myproject.customer_coupon "
					+ "inner join myproject.coupon on customer_coupon.coupon_id = coupon.id where customer_coupon.cust_id = "
					+ customer.getId();
			final ResultSet results = connection.createStatement().executeQuery(sql);
			return couponDBDAO.createCouponCollection(results);

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.getCoupons:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Customer.getCoupons did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Customer.getCoupons did not succeed:\n" + e.getMessage());
			}
		}
	}

	@Override
	public Customer login(String custName, String password) throws DbdaoException, LoginException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "select * from myproject.customer WHERE cust_name='" + custName + "'";
			final ResultSet results = connection.createStatement().executeQuery(sql);

			if (results.next()) {

				if (results.getString("cust_name").equals(custName) && results.getString("password").equals(password)) {
					return getCustomerByResultSet(results);
				}
			}
			throw new LoginException();
		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.login:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException("Return connection at Customer.login did not succeed:\n" + e.getMessage());
			}
		}
	}

	public boolean uniqueName(Customer customer) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			String sql = "SELECT * FROM `myproject`.`customer` WHERE `cust_name`='" + customer.getCustName() + "';";
			ResultSet results = connection.createStatement().executeQuery(sql);

			if (results.next()) {
				return false;
			}
			return true;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.getCoupons:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Customer.getCoupons did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Customer.getCoupons did not succeed:\n" + e.getMessage());
			}
		}
	}

	public Collection<Coupon> getAllPurchasedCouponByType(Customer customer, CouponType type) throws DbdaoException {
		Connection connection = null;
		ArrayList<Coupon> coupons = new ArrayList<>();

		try {
			connection = connectionPoolSingleton.getConnection();
			String sql = "select * from myproject.customer_coupon"
					+ " inner join myproject.coupon Where (customer_coupon.coupon_id = coupon.id and customer_coupon.cust_id="
					+ customer.getId() + " AND coupon.type='" + type + "');";
			ResultSet results = connection.createStatement().executeQuery(sql);

			while (results.next()) {
				coupons.add(couponDBDAO.getCouponByResultSet(results));
			}
			return coupons;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.getAllPurchasedCouponByType:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException(
					"Connection at Customer.getAllPurchasedCouponByType did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException("Return connection at Customer.getAllPurchasedCouponByType did not succeed:\n"
						+ e.getMessage());
			}
		}
	}

	public Collection<Coupon> getAllPurchasedCouponByPrice(Customer customer, double price) throws DbdaoException {
		Connection connection = null;
		ArrayList<Coupon> coupons = new ArrayList<>();

		try {
			connection = connectionPoolSingleton.getConnection();
			String sql = "select * from myproject.customer_coupon "
					+ " inner join myproject.coupon Where (customer_coupon.coupon_id = coupon.id and customer_coupon.cust_id="
					+ customer.getId() + " AND coupon.price <= " + price + ");";
			ResultSet results = connection.createStatement().executeQuery(sql);

			while (results.next()) {
				coupons.add(couponDBDAO.getCouponByResultSet(results));
			}
			return coupons;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.getAllPurchasedCouponByPrice:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException(
					"Connection at Customer.getAllPurchasedCouponByPrice did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException("Return connection at Customer.getAllPurchasedCouponByPrice did not succeed:\n"
						+ e.getMessage());
			}
		}
	}

	private void customerValidCheck(Customer customer) throws DbdaoException {
		if (customer.getCustName() == null || customer.getPassword() == null) {
			throw new DbdaoException("Not all filld are completed");
		}
		if (customer.getCoupons() == null) {
			customer.setCoupons(new ArrayList<Coupon>());
		}
	}

	private Customer getCustomerByResultSet(ResultSet results) throws DbdaoException {
		final Customer customer = new Customer();

		try {
			if (results.getRow() == 0) {
				results.next();
			}
			customer.setId(results.getLong("id"));
			customer.setCustName(results.getString("cust_name"));
			customer.setPassword(results.getString("password"));
			customer.setCoupons(this.getCoupons(customer));

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.createNewCustomer:\n" + e.getMessage());
		}
		return customer;
	}

	private Collection<Customer> createCustomerCollection(ResultSet results) throws DbdaoException {
		Collection<Customer> customerCollection = new ArrayList<Customer>();

		try {

			while (results.next()) {
				final Customer customer = getCustomerByResultSet(results);
				customerCollection.add(customer);
			}
			return customerCollection;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Customer.createCustomerCollecetion:\n" + e.getMessage());
		}
	}

}
