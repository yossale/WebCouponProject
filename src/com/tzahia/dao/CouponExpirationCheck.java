package com.tzahia.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import com.tzahia.dbdao.ConnectionPoolSingleton;
import com.tzahia.exceptions.DbdaoException;

public class CouponExpirationCheck implements Runnable {

	private ConnectionPoolSingleton pool;
	private boolean unStoppAble = true;

	/**
	 * Constructor
	 * @throws DbdaoException
	 */
	public CouponExpirationCheck() throws DbdaoException {
		pool = ConnectionPoolSingleton.getInstance();
	}

	@Override
	public void run() {
		while (unStoppAble) {
			long currentTime = Calendar.getInstance().getTimeInMillis();

			try {
				deleteExpiredCoupon(currentTime);
				Thread.sleep(86400000); // Check every 24 hours
			} catch (InterruptedException | DbdaoException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	/**
	 * calling this function will cause ending this thread
	 * @throws DbdaoException
	 */
	public void endExpirationCheck() throws DbdaoException {
		unStoppAble = false;
	}
	
	private void deleteExpiredCoupon(long currentTime) throws DbdaoException {
		java.sql.Date today = new java.sql.Date(currentTime);
		Connection connection = pool.getConnection();
		String sql = "FROM myproject.coupon WHERE datediff(end_date, '" + today + "') <0;";

		try {
			ResultSet deadCoupon = connection.createStatement().executeQuery("SELECT * " + sql);

			while (deadCoupon.next()) {
				long id = deadCoupon.getLong("id");
				connection.createStatement()
						.execute("DELETE FROM `myproject`.`company_coupon` WHERE `coupon_id`=" + id + ";");
				connection.createStatement()
						.execute("DELETE FROM `myproject`.`customer_coupon` WHERE `coupon_id`=" + id + ";");
			}
			connection.createStatement().execute("DELETE " + sql);

		} catch (SQLException e) {
			throw new DbdaoException("A SQL issue at CouponExpirationCheck.deleteExpiredCoupon");

		} finally {
			pool.returnConnection(connection);
		}

	}

}
