package com.tzahia.server;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.tzahia.beans.Company;
import com.tzahia.dbdao.CompanyDBDAO;
import com.tzahia.exceptions.DbdaoException;

@Path("company")
public class GetAllCompany {

	@GET
	  @Path("text")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String sayPlainTextHello() {
	    return "Hello Company";
	  }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll() throws DbdaoException {
		Collection<Company> companies = null;
		CompanyDBDAO companyDBDAO = new CompanyDBDAO();

		companies = companyDBDAO.getAllCompanies();

		return companies.toString();
	}
}
