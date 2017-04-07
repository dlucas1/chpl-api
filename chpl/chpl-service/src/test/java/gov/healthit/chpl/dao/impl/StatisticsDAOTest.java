package gov.healthit.chpl.dao.impl;

import java.util.Calendar;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.healthit.chpl.caching.UnitTestRules;
import gov.healthit.chpl.dao.StatisticsDAO;
import gov.healthit.chpl.domain.DateRange;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { gov.healthit.chpl.CHPLTestConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:data/testData.xml")
public class StatisticsDAOTest extends TestCase {
	@Autowired
	private StatisticsDAO statisticsDao;
	
	@Rule
    @Autowired
    public UnitTestRules cacheInvalidationRule;
	
	/**
	 * Given that getTotalDevelopers(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all non-deleted developers are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalDevelopers_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalDevelopers = statisticsDao.getTotalDevelopers(dateRange);
		assertNotNull(totalDevelopers);
		assertEquals(9L, totalDevelopers.longValue()); 
	}
	
	/**
	 * Given that getTotalDevelopers(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all non-deleted developers are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalDevelopers_filterDateRange_endDateFiltersResults() {
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 0, 1);
		DateRange dateRange = new DateRange(new Date(0), new Date(cal.getTimeInMillis()));
		Long totalDevelopers = statisticsDao.getTotalDevelopers(dateRange);
		assertNotNull(totalDevelopers);
		assertEquals(6L, totalDevelopers.longValue()); 
	}
	
	/**
	 * Given that getTotalDevelopers(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all non-deleted developers are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalDevelopers_filterDateRange_startDateFiltersResults() {
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 0, 1);
		DateRange dateRange = new DateRange(new Date(cal.getTimeInMillis()), new Date());
		Long totalDevelopers = statisticsDao.getTotalDevelopers(dateRange);
		assertNotNull(totalDevelopers);
		assertEquals(4L, totalDevelopers.longValue()); 
	}
	
	/**
	 * Given that getTotalDevelopers(DateRange) is called 
	 * Given that a deleted developer exists with a lastModifiedDate in 2017
	 * When the start date is set to the beginning of time and endDate is set to 12/31/2016
	 * Then the deleted developer with lastModifiedDate in 2017 is included in the count
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalDevelopers_filterDateRange_lastModifiedDateFiltersResults() {
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 11, 31);
		DateRange dateRange = new DateRange(new Date(0), new Date(cal.getTimeInMillis()));
		Long totalDevelopers = statisticsDao.getTotalDevelopers(dateRange);
		assertNotNull(totalDevelopers);
		assertEquals(7L, totalDevelopers.longValue()); 
	}
	
	/**
	 * Given that getTotalDevelopersWith2014Listings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all non-deleted developers with 2014 listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalDevelopersWith2014Listings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalDevelopers2014Listings = statisticsDao.getTotalDevelopersWith2014Listings(dateRange);
		assertNotNull(totalDevelopers2014Listings);
		assertEquals(1L, totalDevelopers2014Listings.longValue());
	}
	
	/**
	 * Given that getTotalDevelopersWith2015Listings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all non-deleted developers with 2015 listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalDevelopersWith2015Listings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalDevelopers2015Listings = statisticsDao.getTotalDevelopersWith2015Listings(dateRange);
		assertNotNull(totalDevelopers2015Listings);
		assertEquals(3L, totalDevelopers2015Listings.longValue()); 
	}
	
	
	/**
	 * Given that getTotalCertifiedProducts(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all non-deleted CPs are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalCertifiedProducts_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalCertifiedProducts = statisticsDao.getTotalCertifiedProducts(dateRange);
		assertNotNull(totalCertifiedProducts);
		assertEquals(8L, totalCertifiedProducts.longValue()); 
	}
	
	/**
	 * Given that getTotalCPsActive2014Listings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all non-deleted CPs with active 2014 listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalCPsActive2014Listings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalActive2014Listings = statisticsDao.getTotalCPsActive2014Listings(dateRange);
		assertNotNull(totalActive2014Listings);
		assertEquals(1L, totalActive2014Listings.longValue()); 
	}
	
	/**
	 * Given that getTotalCPsActive2015Listings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all non-deleted CPs with active 2015 listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalCPsActive2015Listings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalActive2015Listings = statisticsDao.getTotalCPsActive2015Listings(dateRange);
		assertNotNull(totalActive2015Listings);
		assertEquals(2L, totalActive2015Listings.longValue()); 
	}
	
	
	/**
	 * Given that getTotalCPsActiveListings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all non-deleted CPs with active listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalCPsActiveListings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalActiveCPs = statisticsDao.getTotalCPsActiveListings(dateRange);
		assertNotNull(totalActiveCPs);
		assertEquals(4L, totalActiveCPs.longValue()); 
	}
	
	/**
	 * Given that getTotalListings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalListings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalListings = statisticsDao.getTotalListings(dateRange);
		assertNotNull(totalListings);
		assertEquals(16L, totalListings.longValue()); 
	}
	
	
	/**
	 * Given that getTotalActive2014Listings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all 2014 active listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalActive2014Listings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalActive2014Listings = statisticsDao.getTotalActive2014Listings(dateRange);
		assertNotNull(totalActive2014Listings);
		assertEquals(2L, totalActive2014Listings.longValue()); 
	}
	
	/**
	 * Given that getTotalActive2015Listings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all 2015 active listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalActive2015Listings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalActive2015Listings = statisticsDao.getTotalActive2015Listings(dateRange);
		assertNotNull(totalActive2015Listings);
		assertEquals(3L, totalActive2015Listings.longValue()); 
	}
	
	/**
	 * Given that getTotal2014Listings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all 2014 listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotal2014Listings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long total2014Listings = statisticsDao.getTotal2014Listings(dateRange);
		assertNotNull(total2014Listings);
		assertEquals(3L, total2014Listings.longValue()); 
	}
	
	/**
	 * Given that getTotal2015Listings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all 2015 listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotal2015Listings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long total2015Listings = statisticsDao.getTotal2015Listings(dateRange);
		assertNotNull(total2015Listings);
		assertEquals(10L, total2015Listings.longValue()); 
	}
	
	/**
	 * Given that getTotal2011Listings(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all 2011 listings are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotal2011Listings_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long total2011Listings = statisticsDao.getTotal2011Listings(dateRange);
		assertNotNull(total2011Listings);
		assertEquals(3L, total2011Listings.longValue()); 
	}
	
	/**
	 * Given that getTotalSurveillanceActivities(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all surveillance activities are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalSurveillanceActivities_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalSurveillanceActivities = statisticsDao.getTotalSurveillanceActivities(dateRange);
		assertNotNull(totalSurveillanceActivities);
		assertEquals(4L, totalSurveillanceActivities.longValue()); 
	}
	
	/**
	 * Given that getTotalOpenSurveillanceActivities(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all open surveillance activities are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalOpenSurveillanceActivities_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalOpenSurveillanceActivities = statisticsDao.getTotalOpenSurveillanceActivities(dateRange);
		assertNotNull(totalOpenSurveillanceActivities);
		assertEquals(1L, totalOpenSurveillanceActivities.longValue()); 
	}
	
	/**
	 * Given that getTotalClosedSurveillanceActivities(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all closed surveillance activities are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalClosedSurveillanceActivities_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalClosedSurveillanceActivities = statisticsDao.getTotalClosedSurveillanceActivities(dateRange);
		assertNotNull(totalClosedSurveillanceActivities);
		assertEquals(3L, totalClosedSurveillanceActivities.longValue()); 
	}
	
	/**
	 * Given that getTotalOpenNonconformities(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all open surveillance nonconformities are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalOpenNonConformities_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalOpenSurveillanceNonconformities = statisticsDao.getTotalOpenNonconformities(dateRange);
		assertNotNull(totalOpenSurveillanceNonconformities);
		assertEquals(3L, totalOpenSurveillanceNonconformities.longValue()); 
	}
	
	/**
	 * Given that getTotalClosedNonconformities(DateRange) is called 
	 * When the start date is set to the beginning of time and endDate is set to now()
	 * Then all closed surveillance nonconformities are returned
	 */
	@Test
	@Transactional(readOnly = true)
	public void getTotalClosedNonconformities_filterDateRange_allDates() {
		DateRange dateRange = new DateRange(new Date(0), new Date());
		Long totalClosedSurveillanceNonconformities = statisticsDao.getTotalClosedNonconformities(dateRange);
		assertNotNull(totalClosedSurveillanceNonconformities);
		assertEquals(1L, totalClosedSurveillanceNonconformities.longValue()); 
	}
	
}
