package com.tzahia.facade;

import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.LoginException;

public interface CouponClientFacade {
	
	public boolean login(String user, String password)throws DbdaoException, LoginException;

}
