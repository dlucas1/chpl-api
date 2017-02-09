package gov.healthit.chpl.caching;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dao.search.CertifiedProductSearchDAO;
import gov.healthit.chpl.domain.search.BasicSearchResponse;
import gov.healthit.chpl.domain.search.CertifiedProductFlatSearchResult;
import gov.healthit.chpl.manager.CertifiedProductSearchManager;
import net.sf.ehcache.Cache;
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
	private Properties props;
	private String enableCacheInitializationValue;
	private Boolean refreshPreFetchedBasicSearchInProgress;
	
	@Autowired private AsynchronousCacheInitialization asynchronousCacheInitialization;
	@Autowired private CacheEvictor aspectCacheEvictor;
	@Autowired private CertifiedProductSearchDAO certifiedProductSearchDao;
	@Autowired private CertifiedProductSearchManager certifiedProductSearchManager;

	  @PostConstruct
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
			  manager = CacheManager.getInstance();
			  
			  try {
				if(enableCacheInitializationValue != null && enableCacheInitializationValue.equalsIgnoreCase("true")){
					if(isInitializeSearchOptionsDone != null && !isInitializeSearchOptionsDone.isDone()){
						isInitializeSearchOptionsDone.cancel(true);
					}
					isInitializeSearchOptionsDone = asynchronousCacheInitialization.initializeSearchOptions();
					
					if(isInitializeCertificationIdsGetAllDone != null && !isInitializeCertificationIdsGetAllDone.isDone()){
						isInitializeCertificationIdsGetAllDone.cancel(true);
					}
					isInitializeCertificationIdsGetAllDone = asynchronousCacheInitialization.initializeCertificationIdsGetAll();
					
					if(isInitializeCertificationIdsGetAllWithProductsDone != null && !isInitializeCertificationIdsGetAllWithProductsDone.isDone()){
						isInitializeCertificationIdsGetAllWithProductsDone.cancel(true);
					}
					isInitializeCertificationIdsGetAllWithProductsDone = asynchronousCacheInitialization.initializeCertificationIdsGetAllWithProducts();
					
					if(isInitializeDecertifiedDevelopers != null && !isInitializeDecertifiedDevelopers.isDone()){
						isInitializeDecertifiedDevelopers.cancel(true);
					}
					isInitializeDecertifiedDevelopers = asynchronousCacheInitialization.initializeDecertifiedDevelopers();
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
			
			logger.info("Clearing all caches before @ClearAllCaches method execution.");
			manager.clearAll();
		}
		tClearAllEnd = System.currentTimeMillis();
    }
	
//	@Transactional
//	public BasicSearchResponse initializePreFetchedBasicSearch(){
//		//if(refreshPreFetchedBasicSearchInProgress == null || refreshPreFetchedBasicSearchInProgress == false){
//		  refreshPreFetchedBasicSearchInProgress = true;
//		  logger.info("Executing CacheInitializor.initializePreFetchedBasicSearch().");
//		  certifiedProductSearchDao.getAllCertifiedProducts();
//		  refreshPreFetchedBasicSearchInProgress = false;
//		  //CacheManager.getInstance().getCache(CacheNames.PRE_FETCHED_BASIC_SEARCH).;
//		  Cache preFetchedBasicSearch = CacheManager.getInstance().getCache(CacheNames.PRE_FETCHED_BASIC_SEARCH);
//		  CacheManager.getInstance().removeCache(CacheNames.BASIC_SEARCH);
//		  CacheManager.getInstance().addCache(preFetchedBasicSearch);
//		  // replace BASIC_SEARCH with PRE_FETCHED_BASIC_SEARCH
//		  //CacheManager.getInstance().getCache(CacheNames.BASIC_SEARCH).replace(CacheManager.getInstance().getCache(CacheNames.PRE_FETCHED_BASIC_SEARCH).)
//	//  }
//		logger.info("Caching BASIC_SEARCH\nCalling CertifiedProductSearchDAO.getAllCertifiedProducts()");
//		List<CertifiedProductFlatSearchResult> results = certifiedProductSearchDao.getAllCertifiedProducts();
//		BasicSearchResponse response = new BasicSearchResponse();
//		response.setResults(results);
//		return response;
//		
//		
//		
//	}
	
	@Transactional
	@Cacheable(value = CacheNames.PRE_FETCHED_BASIC_SEARCH)
	public BasicSearchResponse initializePreFetchedBasicSearch(){
		logger.debug("Initializing PreFetchedBasicSearch");
		BasicSearchResponse response = new BasicSearchResponse();
		//if(refreshPreFetchedBasicSearchInProgress == null || refreshPreFetchedBasicSearchInProgress == false){
		refreshPreFetchedBasicSearchInProgress = true;
		List<CertifiedProductFlatSearchResult> results = certifiedProductSearchDao.getAllCertifiedProducts();
		refreshPreFetchedBasicSearchInProgress = false;
		response.setResults(results);
		//}
		return response;
	}
}