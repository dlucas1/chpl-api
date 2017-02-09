package gov.healthit.chpl.caching;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

@Component
@Aspect
public class CacheEvictor {
	private static final Logger logger = LogManager.getLogger(CacheEvictor.class);
	@Autowired private CacheInitializor cacheInitializor;
	
	@CacheEvict(value=CacheNames.BASIC_SEARCH, allEntries = true)
	public void evictBasicSearch(){
		logger.debug("Evicting cache " + CacheNames.BASIC_SEARCH);
	}
	
	/**
	 * Keep the BASIC_SEARCH cache active; 
	 * Evict the PRE_FETCHED_BASIC_SEARCH; 
	 * Refresh the PRE_FETCHED_BASIC_SEARCH (CacheInitializor.initializePreFetchedBasicSearch());
	 * Replace the newly refreshed PRE_FETCHED_BASIC_SEARCH 
	 */
	@After("@annotation(ClearBasicSearch)")
	@CacheEvict(value=CacheNames.PRE_FETCHED_BASIC_SEARCH, beforeInvocation=true, allEntries=true)
	public void evictPreFetchedBasicSearch(){
		CacheManager manager = CacheManager.getInstance();
		logger.debug("Evicted " + CacheNames.PRE_FETCHED_BASIC_SEARCH);
		cacheInitializor.initializePreFetchedBasicSearch();
		CacheReplacor.replaceCache(manager.getCache(CacheNames.BASIC_SEARCH), manager.getCache(CacheNames.PRE_FETCHED_BASIC_SEARCH));
	}
}
