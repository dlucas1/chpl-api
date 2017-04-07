package gov.healthit.chpl.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import gov.healthit.chpl.dao.StatisticsDAO;
import gov.healthit.chpl.domain.DateRange;
import gov.healthit.chpl.domain.Statistics;

@Repository("statisticsDAO")
public class StatisticsDAOImpl extends BaseDAOImpl implements StatisticsDAO {

	@Override
	public Long getTotalDevelopers(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM DeveloperEntity "
				+ "WHERE (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ "OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalDevelopersWith2014Listings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(DISTINCT developerCode) FROM CertifiedProductDetailsEntity "
				+ " WHERE year = '2014' AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate)"
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalDevelopersWith2015Listings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(DISTINCT developerCode) FROM CertifiedProductDetailsEntity "
				+ " WHERE year = '2015' AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate)"
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalCertifiedProducts(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(DISTINCT productVendorName) FROM CertifiedProductDetailsEntity "
				+ " WHERE (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalCPsActive2014Listings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(DISTINCT productVendorName) FROM CertifiedProductDetailsEntity "
				+ " WHERE year = '2014' AND UPPER(certificationStatusName) IN ('ACTIVE', 'SUSPENDED BY ONC-ACB', 'SUSPENDED BY ONC') "
				+ " AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalCPsActive2015Listings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(DISTINCT productVendorName) FROM CertifiedProductDetailsEntity "
				+ " WHERE year = '2015' AND UPPER(certificationStatusName) IN ('ACTIVE', 'SUSPENDED BY ONC-ACB', 'SUSPENDED BY ONC') "
				+ " AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalCPsActiveListings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(DISTINCT productVendorName) FROM CertifiedProductDetailsEntity "
				+ " WHERE UPPER(certificationStatusName) IN ('ACTIVE', 'SUSPENDED BY ONC-ACB', 'SUSPENDED BY ONC') "
				+ " AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalListings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM CertifiedProductDetailsEntity "
				+ " WHERE (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalActive2014Listings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM CertifiedProductDetailsEntity "
				+ " WHERE year = '2014' AND UPPER(certificationStatusName) IN ('ACTIVE', 'SUSPENDED BY ONC-ACB', 'SUSPENDED BY ONC') "
				+ " AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalActive2015Listings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM CertifiedProductDetailsEntity "
				+ " WHERE year = '2015' AND UPPER(certificationStatusName) IN ('ACTIVE', 'SUSPENDED BY ONC-ACB', 'SUSPENDED BY ONC') "
				+ " AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotal2014Listings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM CertifiedProductDetailsEntity "
				+ " WHERE year = '2014' AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotal2015Listings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM CertifiedProductDetailsEntity "
				+ " WHERE year = '2015' AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotal2011Listings(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM CertifiedProductDetailsEntity "
				+ " WHERE year = '2011' AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalSurveillanceActivities(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM SurveillanceEntity "
				+ " WHERE (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalOpenSurveillanceActivities(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM SurveillanceEntity "
				+ " WHERE startDate <= now() AND (endDate IS NULL OR endDate >= now()) AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalClosedSurveillanceActivities(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM SurveillanceEntity "
				+ " WHERE startDate <= now() AND (endDate IS NOT NULL AND endDate <= now()) AND (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalNonConformities(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM SurveillanceNonconformityEntity "
				+ " WHERE (deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate)) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalOpenNonconformities(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM SurveillanceNonconformityEntity "
				+ " WHERE nonconformityStatusId = 1 AND ((deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate))) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Long getTotalClosedNonconformities(DateRange dateRange) {
		Query query = entityManager.createQuery("SELECT count(*) FROM SurveillanceNonconformityEntity "
				+ " WHERE nonconformityStatusId = 2 AND ((deleted = false AND creationDate BETWEEN :creationStartDate AND :creationEndDate) "
				+ " OR (deleted = true AND creationDate BETWEEN :creationStartDate AND :creationEndDate AND lastModifiedDate > :creationEndDate))) ");
		query.setParameter("creationStartDate", dateRange.getStartDate());
		query.setParameter("creationEndDate", dateRange.getEndDate());
		return (Long) query.getSingleResult();
	}

	@Override
	public Statistics calculateStatistics(DateRange dateRange) {
		return new Statistics(dateRange, getTotalDevelopers(dateRange), getTotalDevelopersWith2014Listings(dateRange), getTotalDevelopersWith2015Listings(dateRange),
				getTotalCertifiedProducts(dateRange), getTotalCPsActive2014Listings(dateRange), getTotalCPsActive2015Listings(dateRange),
				getTotalCPsActiveListings(dateRange), getTotalListings(dateRange), getTotalActive2014Listings(dateRange), getTotalActive2015Listings(dateRange),
				getTotal2014Listings(dateRange), getTotal2015Listings(dateRange), getTotal2011Listings(dateRange), getTotalSurveillanceActivities(dateRange),
				getTotalOpenSurveillanceActivities(dateRange), getTotalClosedSurveillanceActivities(dateRange), getTotalNonConformities(dateRange),
				getTotalOpenNonconformities(dateRange), getTotalClosedNonconformities(dateRange));
	}

}
