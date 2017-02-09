package gov.healthit.chpl.manager.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.healthit.chpl.caching.CacheNames;
import gov.healthit.chpl.dao.search.CertifiedProductSearchDAO;
import gov.healthit.chpl.domain.search.BasicSearchResponse;
import gov.healthit.chpl.manager.ChplCacheManager;
import gov.healthit.chpl.manager.CertifiedProductSearchManager;

@Service
public class ChplCacheManagerImpl implements ChplCacheManager {
	private static final Logger logger = LogManager.getLogger(ChplCacheManagerImpl.class);
	
	@Autowired CertifiedProductSearchDAO basicCpSearchDao;
	@Autowired CertifiedProductSearchManager cpSearchManager;
	
	@Transactional(readOnly = true)
	@Cacheable(CacheNames.PRE_FETCHED_BASIC_SEARCH)
	public BasicSearchResponse search() {
		logger.info("Caching PRE_FETCHED_BASIC_SEARCH\nExecuting ChplCacheManager.search()");
		return cpSearchManager.search();
	}
}
