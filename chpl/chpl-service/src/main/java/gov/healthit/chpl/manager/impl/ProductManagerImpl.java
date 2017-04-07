package gov.healthit.chpl.manager.impl;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.auth.SendMailUtil;
import gov.healthit.chpl.caching.CacheNames;
import gov.healthit.chpl.caching.ClearBasicSearch;
import gov.healthit.chpl.dao.DeveloperDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dao.ProductDAO;
import gov.healthit.chpl.dao.ProductVersionDAO;
import gov.healthit.chpl.domain.ActivityConcept;
import gov.healthit.chpl.dto.DeveloperDTO;
import gov.healthit.chpl.dto.DeveloperStatusEventDTO;
import gov.healthit.chpl.dto.ProductDTO;
import gov.healthit.chpl.dto.ProductOwnerDTO;
import gov.healthit.chpl.dto.ProductVersionDTO;
import gov.healthit.chpl.entity.DeveloperStatusType;
import gov.healthit.chpl.manager.ActivityManager;
import gov.healthit.chpl.manager.ProductManager;

@Service
public class ProductManagerImpl implements ProductManager {
	private static final Logger logger = LogManager.getLogger(ProductManagerImpl.class);
	
	@Autowired private SendMailUtil sendMailService;
	@Autowired private Environment env;
	
	@Autowired ProductDAO productDao;
	@Autowired ProductVersionDAO versionDao;
	@Autowired DeveloperDAO devDao;

	@Autowired
	ActivityManager activityManager;
	
	@Override
	@Transactional(readOnly = true)
	public ProductDTO getById(Long id) throws EntityRetrievalException {
		return productDao.getById(id);
	}

	@Override
	@Transactional(readOnly = true) 
	public List<ProductDTO> getAll() {
		return productDao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProductDTO> getByDeveloper(Long developerId) {
		return productDao.getByDeveloper(developerId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDTO> getByDevelopers(List<Long> developerIds) {
		return productDao.getByDevelopers(developerIds);
	}
	
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ACB_ADMIN') or hasRole('ROLE_ACB_STAFF')")
	@CacheEvict(value = {CacheNames.PRODUCT_NAMES, CacheNames.SEARCH, CacheNames.COUNT_MULTI_FILTER_SEARCH_RESULTS}, allEntries=true)
	@ClearBasicSearch
	public ProductDTO create(ProductDTO dto) throws EntityRetrievalException, EntityCreationException, JsonProcessingException {
		//check that the developer of this product is Active
		if(dto.getDeveloperId() == null) {
			throw new EntityCreationException("Cannot create a product without a developer ID.");
		}
			
		DeveloperDTO dev = devDao.getById(dto.getDeveloperId());
		if(dev == null) {
			throw new EntityRetrievalException("Cannot find developer with id " + dto.getDeveloperId());
		}
		DeveloperStatusEventDTO currDevStatus = dev.getStatus();
		if(currDevStatus == null || currDevStatus.getStatus() == null) {
			String msg = "The product " + dto.getName()+ " cannot be created since the status of developer " + dev.getName() + " cannot be determined.";
			logger.error(msg);
			throw new EntityCreationException(msg);
		} else if(!currDevStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString())) {
			String msg = "The product " + dto.getName()+ " cannot be created since the developer " + dev.getName() + " has a status of " + currDevStatus.getStatus().getStatusName();
			logger.error(msg);
			throw new EntityCreationException(msg);
		}
			
		ProductDTO result = productDao.create(dto);
		String activityMsg = "Product "+dto.getName()+" was created.";
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_PRODUCT, result.getId(), activityMsg, null, result);
		return result;
	}

	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ACB_ADMIN') or hasRole('ROLE_ACB_STAFF')")
	@CacheEvict(value = {CacheNames.PRODUCT_NAMES, CacheNames.SEARCH, CacheNames.COUNT_MULTI_FILTER_SEARCH_RESULTS}, allEntries=true)
	@ClearBasicSearch
	public ProductDTO update(ProductDTO dto, boolean lookForSuspiciousActivity) throws EntityRetrievalException, EntityCreationException, JsonProcessingException {
		
		ProductDTO beforeDTO = productDao.getById(dto.getId());
		
		//check that the developer of this product is Active
		if(beforeDTO.getDeveloperId() == null) {
			throw new EntityCreationException("Cannot update a product without a developer ID.");
		}
			
		DeveloperDTO dev = devDao.getById(beforeDTO.getDeveloperId());
		if(dev == null) {
			throw new EntityRetrievalException("Cannot find developer with id " + beforeDTO.getDeveloperId());
		}
		DeveloperStatusEventDTO currDevStatus = dev.getStatus();
		if(currDevStatus == null || currDevStatus.getStatus() == null) {
			String msg = "The product " + dto.getName()+ " cannot be updated since the status of developer " + dev.getName() + " cannot be determined.";
			logger.error(msg);
			throw new EntityCreationException(msg);
		} else if(!currDevStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString())) {
			String msg = "The product " + dto.getName()+ " cannot be updated since the developer " + dev.getName() + " has a status of " + currDevStatus.getStatus().getStatusName();
			logger.error(msg);
			throw new EntityCreationException(msg);
		}
		
		ProductDTO result = productDao.update(dto);
		//the developer name is not updated at this point until after transaction commit so we have to set it
		DeveloperDTO devDto = devDao.getById(result.getDeveloperId());
		result.setDeveloperName(devDto.getName());
		
		String activityMsg = "Product "+dto.getName()+" was updated.";
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_PRODUCT, result.getId(), activityMsg, beforeDTO, result);
		if(lookForSuspiciousActivity) {
			checkSuspiciousActivity(beforeDTO, result);
		}
		return result;
		
	}
	
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@CacheEvict(value = {CacheNames.PRODUCT_NAMES, CacheNames.SEARCH, CacheNames.COUNT_MULTI_FILTER_SEARCH_RESULTS}, allEntries=true)
	@ClearBasicSearch
	public ProductDTO merge(List<Long> productIdsToMerge, ProductDTO toCreate) throws EntityRetrievalException, EntityCreationException, JsonProcessingException {
		
		List<ProductDTO> beforeProducts = new ArrayList<ProductDTO>();
		for(Long productId : productIdsToMerge) {
			beforeProducts.add(productDao.getById(productId));
		}
		
		for(ProductDTO beforeProduct : beforeProducts) {
			Long devId = beforeProduct.getDeveloperId();
			DeveloperDTO dev = devDao.getById(devId);
			DeveloperStatusEventDTO currDevStatus = dev.getStatus();
			if(currDevStatus == null || currDevStatus.getStatus() == null) {
				String msg = "The product " + beforeProduct.getName()+ " cannot be merged since the status of developer " + dev.getName() + " cannot be determined.";
				logger.error(msg);
				throw new EntityCreationException(msg);
			} else if(!currDevStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString())) {
				String msg = "The product " + beforeProduct.getName()+ " cannot be merged since the developer " + dev.getName() + " has a status of " + currDevStatus.getStatus().getStatusName();
				logger.error(msg);
				throw new EntityCreationException(msg);
			}
		}
		
		ProductDTO createdProduct = productDao.create(toCreate);

		//search for any versions assigned to the list of products passed in
		List<ProductVersionDTO> assignedVersions = versionDao.getByProductIds(productIdsToMerge);
		//reassign those versions to the new product
		for(ProductVersionDTO version : assignedVersions) {
			version.setProductId(createdProduct.getId());
			versionDao.update(version);
		}
		
		// - mark the passed in products as deleted
		for(Long productId : productIdsToMerge) {
			productDao.delete(productId);
		}

		String activityMsg = "Merged "+ productIdsToMerge.size() + " products into new product '" + createdProduct.getName() + "'.";
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_PRODUCT, createdProduct.getId(), activityMsg, beforeProducts , createdProduct);
		
		return createdProduct;
	}
	
	@Override
	public void checkSuspiciousActivity(ProductDTO original, ProductDTO changed) {
		String subject = "CHPL Questionable Activity";
		String htmlMessage = "<p>Activity was detected on product " + original.getName() + ".</p>" 
				+ "<p>To view the details of this activity go to: " + 
				env.getProperty("chplUrlBegin") + "/#/admin/reports</p>";
		
		boolean sendMsg = false;
		
		//check name change
		if( (original.getName() != null && changed.getName() == null) ||
			(original.getName() == null && changed.getName() != null) ||
			!original.getName().equals(changed.getName()) ) {
			sendMsg = true;
		}
		
		//if there was a different amount of owner history
		if( (original.getOwnerHistory() != null && changed.getOwnerHistory() == null) ||
			(original.getOwnerHistory() == null && changed.getOwnerHistory() != null) ||
			(original.getOwnerHistory().size() != changed.getOwnerHistory().size()) ) {
			sendMsg = true;
		} else {
			//the same counts of owner history are there but we should check the contents
			for(ProductOwnerDTO originalOwner : original.getOwnerHistory()) {
				boolean foundOriginalOwner = false;
				for(ProductOwnerDTO changedOwner : changed.getOwnerHistory()) {
					if(originalOwner.getDeveloper().getId().longValue() == changedOwner.getDeveloper().getId().longValue()) {
						foundOriginalOwner = true;
					}
				}
				if(!foundOriginalOwner) {
					sendMsg = true;
				}
			}
		}
		
		if(sendMsg) {
			String emailAddr = env.getProperty("questionableActivityEmail");
			String[] emailAddrs = emailAddr.split(";");
			try {
				sendMailService.sendEmail(emailAddrs, subject, htmlMessage);
			} catch(MessagingException me) {
				logger.error("Could not send questionable activity email", me);
			}
		}	
	}
	
}
