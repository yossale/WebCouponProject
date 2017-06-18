package com.tzahia.dbdao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.mysql.jdbc.Statement;
import com.tzahia.beans.Company;
import com.tzahia.beans.Coupon;
import com.tzahia.dao.CompanyDAO;
import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.LoginException;

public class CompanyDBDAO implements CompanyDAO {

	private final ConnectionPoolSingleton connectionPoolSingleton = ConnectionPoolSingleton.getInstance();
	private CouponDBDAO couponDBDAO;

	// Constructors
	public CompanyDBDAO() throws DbdaoException {
		couponDBDAO = new CouponDBDAO();
	}

	public CompanyDBDAO(CouponDBDAO couponDBDAO) throws DbdaoException {
		this.couponDBDAO = couponDBDAO;
	}

	// Create
	@Override
	public Company createCompany(Company company) throws DbdaoException {
		Connection connection = null;
		companyValidCheck(company);

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "INSERT INTO myproject.company (comp_name, password, email) VALUES (" + "'"
					+ company.getCompName() + "','" + company.getPassword() + "','" + company.getEmail() + "')";
			java.sql.PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.executeUpdate();
			ResultSet generatedkeys = statement.getGeneratedKeys();
			generatedkeys.next();
			long newId = generatedkeys.getLong(1);
			company.setId(newId);
			for (Coupon coupon : company.getCoupons()) {
				String query = "INSERT INTO myproject.company_coupon (comp_id, coupon_id) VALUES ("
						+ company.getId() + ", " + coupon.getId() + ");";
				connection.createStatement().execute(query);
			}
			return company;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.createCompany:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Company.createCompany did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Remove
	@Override
	public void removeCompany(Company company) throws DbdaoException {
		Connection connection = null;
		connection = connectionPoolSingleton.getConnection();

		try {
			for (Coupon coupon : company.getCoupons()) {

				try {
					couponDBDAO.removeCoupon(coupon);
				} catch (Exception e) {
					if (e.getMessage() != "Coupon does not exist") {
						throw new DbdaoException("Company: " + company.getCompName() + "Coupon: " + coupon.getId()
								+ " = Coupon not removed:\n" + e.getMessage());
					}
				}
			}
			final String sql = "DELETE FROM myproject.company WHERE id=" + company.getId();
			final boolean results = connection.createStatement().execute(sql);

			if (results) {
				System.out.println("The company was removed successfully");
			}

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.removeCompany:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Company.removeCompany did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Company.removeCompany did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Update
	@Override
	public void updateCompany(Company company) throws DbdaoException {
		Connection connection = null;
		companyValidCheck(company);

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "UPDATE `myproject`.`company` SET `comp_name`=" + "'" + company.getCompName()
					+ "',`password`=" + "'" + company.getPassword() + "',`email`=" + "'" + company.getEmail()
					+ "'WHERE `id`=" + company.getId();
			final boolean results = connection.createStatement().execute(sql);

			if (results) {
				System.out.println("Update company's details was issued successfully");
			}

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.updateCompany:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Company.updateCompany did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Company.updateCompany did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Getters
	@Override
	public Company getCompany(long id) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "SELECT * FROM `myproject`.`company` WHERE `id`=" + id;
			final ResultSet results = connection.createStatement().executeQuery(sql);
			final Company company = getCompanyByResultSet(results);
			return company;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.getCompany:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Company.getCompany did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException("Return connection at Company.getCompany did not succeed:\n" + e.getMessage());
			}
		}
	}

	@Override
	public Collection<Company> getAllCompanies() throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "SELECT * FROM `myproject`.`company`";
			final ResultSet results = connection.createStatement().executeQuery(sql);
			return createCompanyCollection(results);

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.getAllCompanies:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Company.getAllCompanies did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Company.getAllCompanies did not succeed:\n" + e.getMessage());
			}
		}
	}

	@Override
	public Collection<Coupon> getCoupons(Company company) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "select `myproject`.`coupon`.* from `myproject`.`company_coupon` "
					+ "inner join `myproject`.`coupon` on company_coupon.Coupon_ID = coupon.id where company_coupon.Comp_ID = "
					+ company.getId();
			final ResultSet results = connection.createStatement().executeQuery(sql);
			return couponDBDAO.createCouponCollection(results);

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.getCoupons:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Company.getCoupons did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException("Return connection at Company.getCoupons did not succeed:\n" + e.getMessage());
			}
		}
	}

	@Override
	public Company login(String compName, String password) throws DbdaoException, LoginException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "SELECT * FROM myproject.company WHERE comp_name='" + compName + "'";
			final ResultSet results = connection.createStatement().executeQuery(sql);

			if (results.next()) {

				if (results.getString("comp_name").equals(compName) && results.getString("password").equals(password)) {
					return getCompanyByResultSet(results);
				}
			}
			throw new LoginException();

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.login:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException("Return connection at Company.login did not succeed:\n" + e.getMessage());
			}
		}
	}

	public boolean uniqueName(Company company) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			String sql = "SELECT * FROM `myproject`.`company` WHERE `comp_name`='" + company.getCompName() + "';";
			ResultSet results = connection.createStatement().executeQuery(sql);

			if (results.next()) {
				return false;
			}
			return true;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.getCoupons:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Company.getCoupons did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException("Return connection at Company.getCoupons did not succeed:\n" + e.getMessage());
			}
		}
	}

	private void companyValidCheck(Company company) throws DbdaoException {
		if (company.getCompName() == null || company.getPassword() == null || company.getEmail() == null) {
			throw new DbdaoException("Not all filld are completed");
		}

		if (company.getCoupons() == null) {
			company.setCoupons(new ArrayList<Coupon>());
		}
	}

	private Company getCompanyByResultSet(ResultSet results) throws DbdaoException {
		final Company company = new Company();

		try {

			if (results.getRow() == 0) {
				results.next();
			}
			company.setId(results.getLong("id"));
			company.setCompName(results.getString("comp_name"));
			company.setPassword(results.getString("password"));
			company.setEmail(results.getString("email"));
			company.setCoupons(this.getCoupons(company));

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.createNewCompany:\n" + e.getMessage());
		}
		return company;
	}

	private Collection<Company> createCompanyCollection(ResultSet results) throws DbdaoException {
		Collection<Company> companyCollection = new ArrayList<Company>();

		try {

			while (results.next()) {
				final Company company = getCompanyByResultSet(results);
				companyCollection.add(company);
			}
			return companyCollection;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Company.createCompanyCollection:\n" + e.getMessage());
		}
	}

}
