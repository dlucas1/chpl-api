package gov.healthit.chpl.caching;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.dao.EntityRetrievalException;
import net.sf.ehcache.CacheManager;

@Component
@Aspect
public class CacheInitializor {
	private static final Logger logger = LogManager.getLogger(CacheInitializor.class);
	private static final String DEFAULT_PROPERTIES_FILE = "environment.properties";
	private static Integer initializeTimeoutSecs;
	private static Integer clearAllCachesTimeoutSecs;
	private CacheManager manager;
	private Long tInitStart;
	private Long tInitEnd;
	private Double tInitElapsedSecs;
	private Long tClearAllStart;
	private Long tClearAllEnd;
	private Double tClearAllElapsedSecs;
	private Future<Boolean> isInitializeSearchOptionsDone;
	private Future<Boolean> isInitializeCertificationIdsGetAllDone;
	private Future<Boolean> isInitializeCertificationIdsGetAllWithProductsDone;
	private Future<Boolean> isInitializeDecertifiedDevelopers;
	private Future<Boolean> isInitializeBasicSearch;
	private Properties props;
	private String enableCacheInitializationValue;
	
	@Autowired private CacheUtil cacheUtil;
	@Autowired private AsynchronousCacheInitialization asynchronousCacheInitialization;

	  @EventListener(ContextRefreshedEvent.class)
	  @Async
	  public void initialize() throws IOException, EntityRetrievalException, InterruptedException {
		  if(props == null){
			  InputStream in = CacheInitializor.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE);
			  
			  if (in == null) {
					props = null;
					throw new FileNotFoundException("Environment Properties File not found in class path.");
				} else {
					props = new Properties();
					props.load(in);
					in.close();
				}
			  
			  enableCacheInitializationValue = props.getProperty("enableCacheInitialization").toString();
			  initializeTimeoutSecs = Integer.parseInt(props.getProperty("cacheInitializeTimeoutSecs").toString());
			  clearAllCachesTimeoutSecs = Integer.parseInt(props.getProperty("cacheClearTimeoutSecs").toString());
		  }
		  
		  tInitStart = System.currentTimeMillis();
		  if(tInitEnd != null) {
			  tInitElapsedSecs = (tInitStart - tInitEnd) / 1000.0;
		  }
		  
		  if(tInitEnd == null || tInitElapsedSecs > initializeTimeoutSecs) {
			  manager = cacheUtil.getMyCacheManager();
			  
			  try {
				if(enableCacheInitializationValue != null && enableCacheInitializationValue.equalsIgnoreCase("true")){
					isInitializeSearchOptionsDone = asynchronousCacheInitialization.initializeSearchOptions();
					isInitializeCertificationIdsGetAllDone = asynchronousCacheInitialization.initializeCertificationIdsGetAll();
					isInitializeCertificationIdsGetAllWithProductsDone = asynchronousCacheInitialization.initializeCertificationIdsGetAllWithProducts();
					isInitializeDecertifiedDevelopers = asynchronousCacheInitialization.initializeDecertifiedDevelopers();
<<<<<<< HEAD
=======
					
					if(isInitializeBasicSearch != null && !isInitializeBasicSearch.isDone()){
						isInitializeBasicSearch.cancel(true);
					}
>>>>>>> upstream/development
					isInitializeBasicSearch = asynchronousCacheInitialization.initializeBasicSearch();
				  }
			} catch (Exception e) {
				System.out.println("Caching failed to initialize");
				e.printStackTrace();
			}
		  }
			  tInitEnd = System.currentTimeMillis();
	  }
	  
	@Before("@annotation(ClearAllCaches)")
    public void beforeClearAllCachesMethod() {
		tClearAllStart = System.currentTimeMillis();
		if(tClearAllEnd != null){
			  tClearAllElapsedSecs = (tClearAllStart - tClearAllEnd) / 1000.0;
		  }
		
		if(tClearAllEnd == null || tClearAllElapsedSecs > clearAllCachesTimeoutSecs) {
			// Stop initializing caches if running
			if(isInitializeSearchOptionsDone != null && !isInitializeSearchOptionsDone.isDone()){
				isInitializeSearchOptionsDone.cancel(true);
			}
			
			if(isInitializeCertificationIdsGetAllDone != null && !isInitializeCertificationIdsGetAllDone.isDone()){
				isInitializeCertificationIdsGetAllDone.cancel(true);
			}
			
			if(isInitializeCertificationIdsGetAllWithProductsDone != null && !isInitializeCertificationIdsGetAllWithProductsDone.isDone()){
				isInitializeCertificationIdsGetAllWithProductsDone.cancel(true);
			}
			
			if(isInitializeDecertifiedDevelopers != null && !isInitializeDecertifiedDevelopers.isDone()){
				isInitializeDecertifiedDevelopers.cancel(true);
			}
			
			if(isInitializeBasicSearch != null && !isInitializeBasicSearch.isDone()){
<<<<<<< HEAD
				isInitializeBasicSearch.cancel(true);
=======
								isInitializeBasicSearch.cancel(true);
>>>>>>> upstream/development
			}
			
			logger.info("Clearing all caches before @ClearAllCaches method execution.");
			manager.clearAll();
		}
		tClearAllEnd = System.currentTimeMillis();
    }
}