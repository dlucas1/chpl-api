package gov.healthit.chpl.caching;

import java.util.List;

import org.springframework.stereotype.Component;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Component
public class CacheReplacor {
	
	public static void replaceCache(Cache oldCache, Cache newCache){
		CacheManager manager = CacheManager.getInstance();
		List<Element> keys = manager.getCache(CacheNames.PRE_FETCHED_BASIC_SEARCH).getKeys();
		manager.getCache(CacheNames.BASIC_SEARCH).removeAll();
		manager.getCache(CacheNames.BASIC_SEARCH).putAll(keys);
	}
}
