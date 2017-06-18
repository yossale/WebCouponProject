package com.tzahia.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.tzahia.dbdao.ConnectionPoolSingleton;
import com.tzahia.exceptions.DbdaoException;

public class ReturnConnectionThread extends Thread {
	private ConnectionPoolSingleton pool;
	private Connection connection;

	public ReturnConnectionThread() throws DbdaoException {
		pool = ConnectionPoolSingleton.getInstance();
	}

	@Override
	public void run() {
		for (int i = 0; i < 12; i++) {

			System.out.println("ReturnConnectionThread --> run");
			try {
				connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/myproject", "root", "root");
				pool.returnConnection(connection);
				Thread.sleep(2000);

			} catch (SQLException | InterruptedException e) {
				System.out.println("Exception in ReturnCollectionThread: " + e.getMessage());
			}
		}

		System.out.println("Test failed");
	}
}
