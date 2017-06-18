package com.tzahia.dbdao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.mysql.jdbc.Statement;
import com.tzahia.beans.Coupon;
import com.tzahia.dao.CouponDAO;
import com.tzahia.dao.CouponType;
import com.tzahia.exceptions.DbdaoException;

public class CouponDBDAO implements CouponDAO {

	// object members
	ConnectionPoolSingleton connectionPoolSingleton = ConnectionPoolSingleton.getInstance();

	// Constractor
	public CouponDBDAO() {
	}

	// Create
	@Override
	public Coupon createCoupon(Coupon coupon) throws DbdaoException {
		Connection connection = null;
		couponValidCheck(coupon);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "INSERT INTO `myproject`.`coupon` (`title`, `start_date`, `end_date`, `amount`, `type`, `message`, `price`, `image`) "
					+ "VALUES ('" + coupon.getTitle() + "','" + sdf.format(coupon.getStartDate()) + "','"
					+ sdf.format(coupon.getEndDate()) + "','" + coupon.getAmount() + "','" + coupon.getType() + "','"
					+ coupon.getMessage() + "','" + coupon.getPrice() + "','" + coupon.getImage() + "')";
			java.sql.PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.executeUpdate();
			ResultSet generatedkeys = statement.getGeneratedKeys();
			generatedkeys.next();
			long newId = generatedkeys.getLong(1);
			coupon.setId(newId);
			return coupon;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Coupon.createCoupon:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Coupon.createCoupon did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Coupon.createCoupon did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Remove
	@Override
	public void removeCoupon(Coupon coupon) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			// deleting the coupon from the company_coupon table
			connection.createStatement()
					.execute("DELETE FROM myproject.company_coupon WHERE coupon_id=" + coupon.getId());
			// deleting the coupon from the customer_coupon table
			connection.createStatement()
					.execute("DELETE FROM myproject.customer_coupon WHERE coupon_id=" + coupon.getId());
			// finally delete the coupon from the coupon table and from Database
			connection.createStatement().execute("DELETE FROM myproject.coupon WHERE id=" + coupon.getId());
			System.out.println("The coupon was removed successfully");

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Coupon.removeCoupon:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Coupon.removeCoupon did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Coupon.removeCoupon did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Update
	@Override
	public void updateCoupon(Coupon coupon) throws DbdaoException {
		Connection connection = null;
		couponValidCheck(coupon);

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "UPDATE myproject.coupon SET title= '" + coupon.getTitle() + "',start_date='"
					+ coupon.getStartDate() + "',end_date='" + coupon.getEndDate() + "',amount=" + coupon.getAmount()
					+ ",type='" + coupon.getType() + "',message='" + coupon.getMessage() + "',price="
					+ coupon.getPrice() + ",image='" + coupon.getImage() + "' WHERE id=" + coupon.getId();
			System.out.println(sql);
			final boolean results = connection.createStatement().execute(sql);

			if (results) {
				System.out.println("Update coupon's details was issued successfully");
			}

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Coupon.updateCoupon:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Coupon.updateCoupon did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Coupon.updateCoupon did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Getters
	@Override
	public Coupon getCoupon(long id) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "SELECT * FROM myproject.coupon WHERE id=" + id;
			final ResultSet results = connection.createStatement().executeQuery(sql);

			if (results.next()) {
				final Coupon coupon = getCouponByResultSet(results);
				return coupon;
			} else {
				throw new DbdaoException("A coupon with this id doesn't exist");
			}

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Coupon.getCoupon:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Coupon.getCoupon did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException("Return connection at Coupon.getCoupon did not succeed:\n" + e.getMessage());
			}
		}
	}

	public Coupon getCouponByResultSet(ResultSet results) throws DbdaoException {
		Coupon coupon = new Coupon();

		try {
			if (results.getRow() == 0) {
				results.next();
			}
			coupon.setId(results.getLong("id"));
			coupon.setTitle(results.getString("title"));
			coupon.setStartDate(results.getDate("start_date"));
			coupon.setEndDate(results.getDate("end_date"));
			coupon.setAmount(results.getInt("amount"));
			coupon.setType(CouponType.valueOf(results.getString("type").toUpperCase()));
			coupon.setMessage(results.getString("message"));
			coupon.setPrice(results.getDouble("price"));
			coupon.setImage(results.getString("image"));

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Coupon.createNewCoupon:\n" + e.getMessage());
		}
		return coupon;
	}

	@Override
	public Collection<Coupon> getAllCoupon() throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "SELECT * FROM myproject.coupon";
			final ResultSet results = connection.createStatement().executeQuery(sql);
			return createCouponCollection(results);

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Coupon.getAllCoupon:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Coupon.getAllCoupon did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Coupon.getAllCoupon did not succeed:\n" + e.getMessage());
			}
		}
	}

	@Override
	public Collection<Coupon> getCouponByType(CouponType couponType) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			final String sql = "SELECT * FROM myproject.coupon WHERE type='" + couponType + "'";
			final ResultSet results = connection.createStatement().executeQuery(sql);
			return createCouponCollection(results);

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Coupon.getCouponByType:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Coupon.getCouponByType did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException(
						"Return connection at Coupon.getCouponByType did not succeed:\n" + e.getMessage());
			}
		}
	}

	// Create
	public Collection<Coupon> createCouponCollection(ResultSet results) throws DbdaoException {
		final Collection<Coupon> couponCollection = new ArrayList<Coupon>();

		try {

			while (results.next()) {
				final Coupon coupon = getCouponByResultSet(results);
				couponCollection.add(coupon);
			}

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Coupon.createCouponCollection:\n" + e.getMessage());
		}
		return couponCollection;
	}

	public boolean uniqueTitle(Coupon coupon) throws DbdaoException {
		Connection connection = null;

		try {
			connection = connectionPoolSingleton.getConnection();
			String sql = "SELECT * FROM myproject.coupon WHERE title='" + coupon.getTitle() + "';";
			ResultSet results = connection.createStatement().executeQuery(sql);
			if (results.next()) {
				return false;
			}
			return true;

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at Coupon.uniqueTitle:\n" + e.getMessage());
		} catch (Exception e) {
			throw new DbdaoException("Connection at Coupon.uniqueTitle did not succeed:\n" + e.getMessage());
		} finally {

			try {
				connectionPoolSingleton.returnConnection(connection);
			} catch (Exception e) {
				throw new DbdaoException("Return connection at Coupon.uniqueTitle did not succeed:\n" + e.getMessage());
			}
		}
	}

	private void couponValidCheck(Coupon coupon) throws DbdaoException {
		if (coupon.getStartDate() == null || coupon.getEndDate() == null || coupon.getMessage() == null
				|| coupon.getTitle() == null || coupon.getType() == null) {
			throw new DbdaoException("Not all filld are completed");
		}
		if (coupon.getAmount() < 0 || coupon.getPrice() < 0) {
			throw new DbdaoException(
					"Negative numbers at CouponDBDAO.couponValidCheck at coupon id =" + coupon.getId());
		}
		if (coupon.getImage() == null) {
			coupon.setImage("");
		}
	}

}
