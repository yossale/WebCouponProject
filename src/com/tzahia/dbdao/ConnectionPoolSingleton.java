package com.tzahia.dbdao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.tzahia.exceptions.ConnectionPoolException;

public class ConnectionPoolSingleton {

	private static final int MAX_CONNECTIONS = 10;
	private static ConnectionPoolSingleton instance = null;
	private Collection<Connection> pool;
	private Collection<Connection> inUsedConnections = new ArrayList<Connection>();

	private ConnectionPoolSingleton() {
	}

	public static ConnectionPoolSingleton getInstance() {
		if (instance == null) {
			instance = new ConnectionPoolSingleton();

			try {
				instance.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public synchronized Connection getConnection() {
		while (pool.isEmpty()) {

			try {
				System.out.println("\t Waiting for connection... Connection pool is empty");
				wait();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Connection connection = pool.iterator().next();
		pool.remove(connection);
		inUsedConnections.add(connection);
		System.out.println("\t Got a connection");
		notifyAll();
		return connection;
	}

	public synchronized void returnConnection(Connection connection) {
		while (pool.size() >= MAX_CONNECTIONS) {
			
			try {
				System.out.println("\t Waiting for returning connection... Connection pool is full");
				wait();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pool.add(connection);
		inUsedConnections.remove(connection);
		System.out.println("\t Connection returned");
		notifyAll();
	}

	public void closeAllConnection() throws ConnectionPoolException {
		try {
			
			for (Connection connection : pool) {
				connection.close();
			}

			for (Connection connection : inUsedConnections) {
				connection.close();
			}
			
			instance = null;
		} catch (SQLException e) {
			throw new ConnectionPoolException("A SQL issue at ConnectionPoolSingleton.closeAllConnection");

		}

	}
	
	private void init() throws Exception {
		pool = new ArrayList<Connection>();

		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			pool.add(createConnection());
		}
	}

	private Connection createConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/myproject", "root", "root");
		return connection;
	}

}
