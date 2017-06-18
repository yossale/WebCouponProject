package com.tzahia.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.tzahia.beans.Company;
import com.tzahia.beans.Coupon;
import com.tzahia.dao.CouponType;
import com.tzahia.dbdao.CompanyDBDAO;
import com.tzahia.dbdao.CouponDBDAO;
import com.tzahia.exceptions.DbdaoException;
import com.tzahia.exceptions.LoginException;

public class CompanyFacade implements CouponClientFacade {

	private CompanyDBDAO companyDBDAO;
	private CouponDBDAO couponDBDAO;
	private Company company;

	public CompanyFacade(CompanyDBDAO companyDBDAO, CouponDBDAO couponDBDAO) throws DbdaoException {
		this.companyDBDAO = companyDBDAO;
		this.couponDBDAO = couponDBDAO;
	}

	public Coupon createCoupon(Coupon coupon) throws DbdaoException {
		coupon = couponDBDAO.createCoupon(coupon);
		company.getCoupons().add(coupon);
		companyDBDAO.updateCompany(company);
		return coupon;
	}

	public void removeCoupon(Coupon coupon) throws DbdaoException {
		couponDBDAO.removeCoupon(coupon);
		company = companyDBDAO.getCompany(company.getId());
	}

	public void updateCoupon(Coupon updatedCoupon) throws DbdaoException {
		company = companyDBDAO.getCompany(company.getId());
		ArrayList<Coupon> coupons = (ArrayList<Coupon>) company.getCoupons();
		Coupon original = null;
		
		for (int i = 0; i < coupons.size(); i++) {
			
			if (coupons.get(i).getId() == updatedCoupon.getId()) {
				original = coupons.remove(i);
			}
		}
		
		if (original == null){
			System.out.println("Coupon not found");
			return;
		}
		original.setEndDate(updatedCoupon.getEndDate());
		original.setPrice(updatedCoupon.getPrice());
		coupons.add(original);
		couponDBDAO.updateCoupon(original);
	}

	public Coupon getCoupon(long id) throws DbdaoException {
		return couponDBDAO.getCoupon(id);
	}

	public Collection<Coupon> getAllCoupons() throws DbdaoException {
		return companyDBDAO.getCoupons(company);
	}

	public Collection<Coupon> getCouponByType(CouponType type) throws DbdaoException {
		company = companyDBDAO.getCompany(company.getId());
		ArrayList<Coupon> coupons = new ArrayList<>();
		
		for (Coupon coupon : company.getCoupons()) {
			
			if (coupon.getType().equals(type)) {
				coupons.add(coupon);
			}
		}
		return coupons;
	}

	public Company getCompanyInfo() throws DbdaoException {
		return companyDBDAO.getCompany(company.getId());
	}

	public Collection<Coupon> getCouponsByMaxPrice(double maxPrice) throws DbdaoException {
		company = companyDBDAO.getCompany(company.getId());
		double couponPrice;
		ArrayList<Coupon> coupons = new ArrayList<>();
		
		for (Coupon coupon : company.getCoupons()) {
			couponPrice = coupon.getPrice();
			
			if (couponPrice <= maxPrice) {
				coupons.add(coupon);
			}
		}
		return coupons;
	}

	public Collection<Coupon> getCouponsByEndDate(Date lastDate) throws DbdaoException {
		company = companyDBDAO.getCompany(company.getId());
		Date endDate;
		ArrayList<Coupon> coupons = new ArrayList<>();
		for (Coupon coupon : company.getCoupons()) {
			endDate = coupon.getEndDate();
			if (endDate.before(lastDate)) {
				coupons.add(coupon);
			}
		}
		return coupons;
	}

	@Override
	public boolean login(String user, String password) throws DbdaoException, LoginException {
		try {
				company = companyDBDAO.login(user, password);
				return true;
				
		}catch (LoginException e){
			return false;
		}
	}			

}
