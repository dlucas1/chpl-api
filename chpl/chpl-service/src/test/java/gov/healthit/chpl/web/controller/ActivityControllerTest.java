package gov.healthit.chpl.web.controller;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.healthit.chpl.auth.permission.GrantedPermission;
import gov.healthit.chpl.auth.user.JWTAuthenticatedUser;
import gov.healthit.chpl.caching.UnitTestRules;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.ActivityEvent;
import gov.healthit.chpl.web.controller.exception.ValidationException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { gov.healthit.chpl.CHPLTestConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:data/testData.xml") 
public class ActivityControllerTest {
	private static JWTAuthenticatedUser adminUser;
	
	@Autowired Environment env;
	
	@Autowired ActivityController activityController;
	
	@Rule
    @Autowired
    public UnitTestRules cacheInvalidationRule;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		adminUser = new JWTAuthenticatedUser();
		adminUser.setFirstName("Administrator");
		adminUser.setId(-2L);
		adminUser.setLastName("Administrator");
		adminUser.setSubjectName("admin");
		adminUser.getPermissions().add(new GrantedPermission("ROLE_ADMIN"));
	}
	
	/** 
	 * Tests that listActivity returns results for a Certified Product
	 * @throws IOException 
	 * @throws ValidationException 
	 */
	@Transactional
	@Test
	public void test_listActivity() throws EntityRetrievalException, EntityCreationException, IOException, ValidationException{
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		// Note: certification_criterion_id="59" has testTool="true", number 170.315 (h)(1) and title "Direct Project"
		Long cpId = 1L; // this CP has ics_code = "1" & associated certification_result_id = 8 with certification_criterion_id="59"
		Long cpId2 = 10L; // this CP has ics_code = "0" & associated certification_result_id = 9 with certification_criterion_id="59"
		
		List<ActivityEvent> cpActivityEvents = activityController.activityForCertifiedProductById(cpId, null, null);
		assertTrue(cpActivityEvents.size() == 4);
		
		List<ActivityEvent> cp2ActivityEvents = activityController.activityForCertifiedProductById(cpId2, null, null);
		assertTrue(cp2ActivityEvents.size() == 0);
	}
	
	/** 
	 * GIVEN a user is looking at activity
	 * WHEN they try to search for more than configurable days (set to 60)
	 * THEN they should not be able to make the call
	 * @throws IOException 
	 * @throws ValidationException 
	 */
	@Transactional
	@Test(expected=ValidationException.class)
	public void test_dateValidation_outOfRangeThrowsException() throws EntityRetrievalException, EntityCreationException, IOException, ValidationException{
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		activityController.activityForACBs(0L, cal.getTimeInMillis(), false);
	}
	
	/** 
	 * GIVEN a user is looking at activity
	 * WHEN they try to search for a date range within the configurable days (set to 60)
	 * THEN they should be able to make the call
	 * @throws IOException 
	 * @throws ValidationException 
	 */
	@Transactional
	@Test
	public void test_dateValidation_insideRangeDoesNotThrowException() throws EntityRetrievalException, EntityCreationException, IOException, ValidationException{
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		Calendar calEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Integer maxActivityRangeInDays = Integer.getInteger(env.getProperty("maxActivityRangeInDays"), 60);
		calStart.add(Calendar.DATE, -maxActivityRangeInDays + 1);
		activityController.activityForACBs(calStart.getTimeInMillis(), calEnd.getTimeInMillis(), false);
	}
	
	/** 
	 * GIVEN a user is looking at activity
	 * WHEN they try to search for a date range equal to the max number of days (set to 60)
	 * THEN they should be able to make the call
	 * @throws IOException 
	 * @throws ValidationException 
	 */
	@Transactional
	@Test
	public void test_dateValidation_allowsSixtyDays() throws EntityRetrievalException, EntityCreationException, IOException, ValidationException{
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		Calendar calEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Integer maxActivityRangeInDays = Integer.getInteger(env.getProperty("maxActivityRangeInDays"), 60);
		calStart.add(Calendar.DATE, -maxActivityRangeInDays);
		activityController.activityForACBs(calStart.getTimeInMillis(), calEnd.getTimeInMillis(), false);
	}
	
	/** 
	 * GIVEN a user is looking at activity
	 * WHEN they try to search for a date range greater than the max number of days (set to 60)
	 * THEN they should not be able to make the call
	 * @throws IOException 
	 * @throws ValidationException 
	 */
	@Transactional
	@Test(expected=ValidationException.class)
	public void test_dateValidation_ExceptionForSixtyOneDays() throws EntityRetrievalException, EntityCreationException, IOException, ValidationException{
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		Calendar calEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calStart.add(Calendar.DATE, -61);
		activityController.activityForACBs(calStart.getTimeInMillis(), calEnd.getTimeInMillis(), false);
	}
	
	/** 
	 * GIVEN a user is looking at activity
	 * WHEN they try to search for a date range with the start date > end date
	 * THEN they should not be able to make the call
	 * @throws IOException 
	 * @throws ValidationException 
	 */
	@Transactional
	@Test(expected=ValidationException.class)
	public void test_dateValidation_startDateAfterEndDate() throws EntityRetrievalException, EntityCreationException, IOException, ValidationException{
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		Calendar calEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calStart.add(Calendar.DATE, 20);
		activityController.activityForACBs(calStart.getTimeInMillis(), calEnd.getTimeInMillis(), false);
	}
}
