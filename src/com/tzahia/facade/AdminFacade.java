package com.tzahia.facade;

import java.util.Collection;

import com.tzahia.beans.Company;
import com.tzahia.beans.Customer;
import com.tzahia.dbdao.CompanyDBDAO;
import com.tzahia.dbdao.CustomerDBDAO;
import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.FacadeException;

public class AdminFacade implements CouponClientFacade {
	
	private CompanyDBDAO companyDBDAO;
	private CustomerDBDAO customerDBDAO;
	
	//Constructor
	public AdminFacade(CompanyDBDAO companyDBDAO, CustomerDBDAO customerDBDAO) throws DbdaoException{
		this.companyDBDAO = companyDBDAO;
		this.customerDBDAO = customerDBDAO;
	}
	
	public Company createCompany(Company company) throws DbdaoException, FacadeException{
		if (companyDBDAO.uniqueName(company)){
			return companyDBDAO.createCompany(company);
			
		}else{
			throw new FacadeException("Company name already exist");
		}
	}
	
	public void removeCompany(Company company) throws DbdaoException{
		companyDBDAO.removeCompany(company);
	}
	
	public void updateCompany(Company updatedCompany) throws DbdaoException{
		Company original = companyDBDAO.getCompany(updatedCompany.getId());
		original.setEmail(updatedCompany.getEmail());
		original.setPassword(updatedCompany.getPassword());
		original.setCoupons(updatedCompany.getCoupons());
		companyDBDAO.updateCompany(original);
	}
		
	public Collection<Company> getAllCompanies() throws DbdaoException{
		return companyDBDAO.getAllCompanies();
	}
	
	public Company getCompany(long id) throws DbdaoException{
		return companyDBDAO.getCompany(id);
	}
	
	public Customer createCustomer(Customer customer) throws DbdaoException, FacadeException{
		if (customerDBDAO.uniqueName(customer)){
			return customerDBDAO.createCustomer(customer);
			
		}else{
			throw new FacadeException("Customer name already exist");
		}
	}
	
	public void removeCustomer(Customer customer) throws DbdaoException{
		customerDBDAO.removeCustomer(customer);
	}
	
	public void updateCustomer (Customer updatedCustomer) throws DbdaoException{
		Customer original = customerDBDAO.getCustomer(updatedCustomer.getId());
		original.setPassword(updatedCustomer.getPassword());
		original.setCoupons(updatedCustomer.getCoupons());
		customerDBDAO.updateCustomer(original);
	}
		
	public Collection<Customer> getAllCustomers() throws DbdaoException{
		return customerDBDAO.getAllCustomer();
	}
	
	public Customer getCustomer(long id) throws DbdaoException{
		return customerDBDAO.getCustomer(id);
	}

	@Override
	public boolean login(String user, String password) {
		if (user.equals("admin") && password.equals("1234")){
			return true;
		}
		return false;
	}

}
