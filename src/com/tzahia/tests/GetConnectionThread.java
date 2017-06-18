package com.tzahia.tests;

import com.tzahia.dbdao.ConnectionPoolSingleton;
import com.tzahia.exceptions.DbdaoException;

public class GetConnectionThread extends Thread {

	private ConnectionPoolSingleton pool;

	public GetConnectionThread() throws DbdaoException {
		pool = ConnectionPoolSingleton.getInstance();
	}

	@Override
	public void run() {
		for (int i = 0; i < 12; i++) {

			System.out.println("    GetConnectionThread --> run");
			try {
				pool.getConnection();
				Thread.sleep(700);

			} catch (InterruptedException e) {
				System.out.println("InterruptedException in GetConnectionThread: " + e.getMessage());

			} catch (Exception e) {
				System.out.println("Connection Pool Exception in GetConnectionThread: " + e.getMessage());
			}
		}
	}
}
