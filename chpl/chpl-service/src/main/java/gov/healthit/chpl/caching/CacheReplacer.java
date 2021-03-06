package gov.healthit.chpl.caching;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

@Component
public class CacheReplacer {
	private static final Logger logger = LogManager.getLogger(CacheReplacer.class);
	
	/** Removes all keys from the oldCache and puts all keys from the newCache into the oldCache
	 * 
	 * @param oldCache - the cache whose values will be replaced
	 * @param newCache - the cache whose keys will replace the values in the old cache
	 */
	public static void replaceCache(Cache oldCache, Cache newCache){
		logger.info("Replacing " + oldCache.getName() + " with " + newCache.getName());
		List<Integer> keys = newCache.getKeys();
		Map<Object, Element> objects = newCache.getAll(keys);
		if(objects.size() > 0){
			oldCache.removeAll();
			oldCache.putAll(objects.values());
		} else{
			logger.info("Attempted to replace cache " + oldCache.getName() + " with an empty cache.");
		}
	}
}
