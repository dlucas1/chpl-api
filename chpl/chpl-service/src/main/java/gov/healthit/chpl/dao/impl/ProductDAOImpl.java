package gov.healthit.chpl.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dao.ProductDAO;
import gov.healthit.chpl.dto.ProductDTO;
import gov.healthit.chpl.dto.ProductOwnerDTO;
import gov.healthit.chpl.entity.ProductActiveOwnerEntity;
import gov.healthit.chpl.entity.ProductEntity;
import gov.healthit.chpl.entity.ProductInsertableOwnerEntity;

@Repository("productDAO")
public class ProductDAOImpl extends BaseDAOImpl implements ProductDAO {
	private static final Logger logger = LogManager.getLogger(DeveloperDAOImpl.class);
	
	@Override
	public ProductDTO create(ProductDTO dto) throws EntityCreationException,
			EntityRetrievalException {
		
		ProductEntity entity = null;
		try {
			if (dto.getId() != null){
				entity = this.getEntityById(dto.getId());
			}
		} catch (EntityRetrievalException e) {
			throw new EntityCreationException(e);
		}
		
		if (entity != null) {
			throw new EntityCreationException("An entity with this ID already exists.");
		} else {
			
			entity = new ProductEntity();
			entity.setName(dto.getName());
			entity.setReportFileLocation(dto.getReportFileLocation());
			entity.setDeveloperId(dto.getDeveloperId());
			entity.setDeleted(false);
			entity.setLastModifiedUser(Util.getCurrentUser().getId());
			create(entity);	

			ProductDTO result = new ProductDTO(entity);
			if(dto.getOwnerHistory() != null && dto.getOwnerHistory().size() > 0) {
				for(ProductOwnerDTO prevOwner : dto.getOwnerHistory()) {
					prevOwner.setProductId(entity.getId());
					ProductOwnerDTO prevOwnerDto = addOwnershipHistory(prevOwner);
					result.getOwnerHistory().add(prevOwnerDto);
				}
			}
			return result;
		}
		
	}
	
	@Override
	public ProductDTO update(ProductDTO dto) throws EntityRetrievalException {
		ProductEntity entity = this.getEntityById(dto.getId());
		//update product data
		entity.setReportFileLocation(dto.getReportFileLocation());
		entity.setName(dto.getName());
		entity.setDeveloperId(dto.getDeveloperId());
		entity.setDeleted(dto.getDeleted() == null ? false : dto.getDeleted());
		entity.setLastModifiedUser(Util.getCurrentUser().getId());
		update(entity);

		//update ownership history
		
		//there used to be owners but aren't anymore so delete the existing ones
		if(dto.getOwnerHistory() == null || dto.getOwnerHistory().size() == 0) {
			if(entity.getOwnerHistory() != null && entity.getOwnerHistory().size() > 0) {
				for(ProductActiveOwnerEntity existingPrevOwner : entity.getOwnerHistory()) {
					existingPrevOwner.setDeleted(true);
					existingPrevOwner.setLastModifiedDate(new Date());
					existingPrevOwner.setLastModifiedUser(Util.getCurrentUser().getId());
					entityManager.merge(existingPrevOwner);
					entityManager.flush();
				}
			}
		} else {
			//Look for new entries in ownership history that aren't already
			//in the list of previous owners.
			for(ProductOwnerDTO updatedProductPrevOwner : dto.getOwnerHistory()) {
				boolean alreadyExists = false;
				for(int i = 0; i < entity.getOwnerHistory().size() && !alreadyExists; i++) {
					ProductActiveOwnerEntity existingProductPreviousOwner = entity.getOwnerHistory().get(i);
					if(existingProductPreviousOwner.getDeveloper() != null && 
						updatedProductPrevOwner.getDeveloper() != null && 
							existingProductPreviousOwner.getDeveloper().getId().longValue() == 
							updatedProductPrevOwner.getDeveloper().getId().longValue()) {
						alreadyExists = true;
					}
				}
				
				if(!alreadyExists) {
					addOwnershipHistory(updatedProductPrevOwner);
				}
			}
			
			//Look for entries in the existing ownership history that are 
			//not in the passed-in history for the updated product
			for(ProductActiveOwnerEntity existingPrevOwner : entity.getOwnerHistory()) {
				boolean isInUpdate = false;
				for(int i = 0; i < dto.getOwnerHistory().size() && !isInUpdate; i++) {
					ProductOwnerDTO updatedProductPreviousOwner = dto.getOwnerHistory().get(i);
					if(existingPrevOwner.getDeveloper() != null && 
						updatedProductPreviousOwner.getDeveloper() != null && 
						existingPrevOwner.getDeveloper().getId().longValue() == 
						updatedProductPreviousOwner.getDeveloper().getId().longValue()) {
						isInUpdate = true;
					}
				}
				if(!isInUpdate) {
					existingPrevOwner.setDeleted(true);
					existingPrevOwner.setLastModifiedDate(new Date());
					existingPrevOwner.setLastModifiedUser(Util.getCurrentUser().getId());
					entityManager.merge(existingPrevOwner);
					entityManager.flush();
				}
			}
		}
		
		entityManager.clear();
		return getById(dto.getId());
	}

	@Override
	@Transactional
	public void delete(Long id) throws EntityRetrievalException {
		ProductEntity toDelete = getEntityById(id);
		if(toDelete == null) {
			throw new EntityRetrievalException("Could not find product with id " + id + " for deletion.");
		}
		//delete owner history
		if(toDelete.getOwnerHistory() != null) {
			for(ProductActiveOwnerEntity prevOwner : toDelete.getOwnerHistory()) {
				prevOwner.setDeleted(true);
				prevOwner.setLastModifiedDate(new Date());
				prevOwner.setLastModifiedUser(Util.getCurrentUser().getId());
				entityManager.merge(prevOwner);
				entityManager.flush();
			}
		}
		toDelete.setDeleted(true);
		toDelete.setLastModifiedDate(new Date());
		toDelete.setLastModifiedUser(Util.getCurrentUser().getId());
		update(toDelete);
	}
	
	@Override
	public ProductOwnerDTO addOwnershipHistory(ProductOwnerDTO toAdd) {
		ProductInsertableOwnerEntity entityToAdd = new ProductInsertableOwnerEntity();
		entityToAdd.setProductId(toAdd.getProductId());
		entityToAdd.setCreationDate(new Date());
		entityToAdd.setLastModifiedDate(new Date());
		entityToAdd.setDeleted(false);
		entityToAdd.setLastModifiedUser(Util.getCurrentUser().getId());
		if(toAdd.getDeveloper() != null) {
			entityToAdd.setDeveloperId(toAdd.getDeveloper().getId());
		}
		entityToAdd.setTransferDate(new Date(toAdd.getTransferDate()));
		entityManager.persist(entityToAdd);
		entityManager.flush();
		
		return new ProductOwnerDTO(entityToAdd);
	}
	
	@Override
	public void deletePreviousOwner(Long previousOwnershipId) throws EntityRetrievalException {
		ProductActiveOwnerEntity toDelete = getProductPreviousOwner(previousOwnershipId);
		if(toDelete == null) {
			throw new EntityRetrievalException("Could not find previous ownership with id " + previousOwnershipId);
		}
		toDelete.setDeleted(true);
		toDelete.setLastModifiedDate(new Date());
		toDelete.setLastModifiedUser(Util.getCurrentUser().getId());
		entityManager.merge(toDelete);
		entityManager.flush();
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<ProductDTO> findAll() {
		
		List<ProductEntity> entities = getAllEntities();
		List<ProductDTO> dtos = new ArrayList<>();
		
		for (ProductEntity entity : entities) {
			ProductDTO dto = new ProductDTO(entity);
			dtos.add(dto);
		}
		return dtos;
	}
	
	@Transactional(readOnly=true)
	public List<ProductDTO> findAllIncludingDeleted() {
		List<ProductEntity> entities = getAllEntitiesIncludingDeleted();
		List<ProductDTO> dtos = new ArrayList<>();
		
		for (ProductEntity entity : entities) {
			ProductDTO dto = new ProductDTO(entity);
			dtos.add(dto);
		}
		return dtos;
	}

	@Override
	@Transactional(readOnly=true)
	public ProductDTO getById(Long id) throws EntityRetrievalException {
		
		ProductEntity entity = getEntityById(id);
		if(entity == null) { 
			return null;
		}
		ProductDTO dto = new ProductDTO(entity);
		return dto;
		
	}
	
	@Transactional(readOnly=true)
	public List<ProductDTO> getByDeveloper(Long developerId) {		
		Query query = entityManager.createQuery( "SELECT distinct pe "
				+ "FROM ProductEntity pe "
				+ " LEFT OUTER JOIN FETCH pe.developer "
				+ "LEFT OUTER JOIN FETCH pe.ownerHistory "
				+ "WHERE (pe.developerId = :entityid) "
				+ "AND (NOT pe.deleted = true)", ProductEntity.class );
		query.setParameter("entityid", developerId);
		List<ProductEntity> results = query.getResultList();
		
		List<ProductDTO> dtoResults = new ArrayList<ProductDTO>();
		for(ProductEntity result : results) {
			dtoResults.add(new ProductDTO(result));
		}
		return dtoResults;
	}
	
	@Transactional(readOnly=true)
	public List<ProductDTO> getByDevelopers(List<Long> developerIds) {
		Query query = entityManager.createQuery( "SELECT distinct pe "
				+ "FROM ProductEntity pe "
				+ " LEFT OUTER JOIN FETCH pe.developer "
				+ "LEFT OUTER JOIN FETCH pe.ownerHistory "
				+ "where (NOT pe.deleted = true) "
				+ "AND pe.developerId IN (:idList) ", ProductEntity.class );
		query.setParameter("idList", developerIds);
		List<ProductEntity> results = query.getResultList();

		List<ProductDTO> dtoResults = new ArrayList<ProductDTO>();
		for(ProductEntity result : results) {
			dtoResults.add(new ProductDTO(result));
		}
		return dtoResults;
	}
	
	@Transactional(readOnly=true)
	public ProductDTO getByDeveloperAndName(Long developerId, String name) {
		Query query = entityManager.createQuery( "SELECT distinct pe "
				+ "FROM ProductEntity pe "
				+ " LEFT OUTER JOIN FETCH pe.developer "
				+ "LEFT OUTER JOIN FETCH pe.ownerHistory "
				+ "where (NOT pe.deleted = true) "
				+ "AND (pe.developerId = :developerId) and "
				+ "(pe.name = :name)", ProductEntity.class );
		query.setParameter("developerId", developerId);
		query.setParameter("name", name);
		List<ProductEntity> results = query.getResultList();
		
		ProductDTO result = null;
		if(results != null && results.size() > 0) {
			result = new ProductDTO(results.get(0));
		}
		return result;
	}
	
	private void create(ProductEntity entity) {
		
		entityManager.persist(entity);
		entityManager.flush();
	}
	
	private void update(ProductEntity entity) {
		
		entityManager.merge(entity);	
		entityManager.flush();
	}
	
	private List<ProductEntity> getAllEntities() {
		
		List<ProductEntity> result = entityManager.createQuery( "SELECT distinct pe "
				+ "FROM ProductEntity pe "
				+ "LEFT OUTER JOIN FETCH pe.developer "
				+ "LEFT OUTER JOIN FETCH pe.ownerHistory "
				+ "LEFT OUTER JOIN FETCH pe.productCertificationStatuses "
				+ "where (NOT pe.deleted = true) ", 
				ProductEntity.class).getResultList();
		
		logger.debug("SQL call: List<ProductEntity> getAllEntities()");
		return result;
		
	}

	private ProductActiveOwnerEntity getProductPreviousOwner(Long ppoId) {
		ProductActiveOwnerEntity result = null;
		Query query = entityManager.createQuery("SELECT po "
				+ "FROM ProductActiveOwnerEntity po " 
				+ "LEFT OUTER JOIN FETCH po.developer "
				+ "WHERE (po.id = :ppoId)", ProductActiveOwnerEntity.class);
		query.setParameter("ppoId", ppoId);
		List<ProductActiveOwnerEntity> results = query.getResultList();
		if(results != null && results.size() > 0) {
			result = results.get(0);
		}
		return result;
		
	}
	
	private List<ProductEntity> getAllEntitiesIncludingDeleted() {
		List<ProductEntity> result = entityManager.createQuery( "from ProductEntity ", ProductEntity.class).getResultList();
		return result;
	}
	
	private ProductEntity getEntityById(Long id) throws EntityRetrievalException {
		ProductEntity entity = null;

		Query query = entityManager.createQuery( "SELECT distinct pe "
				+ "FROM ProductEntity pe "
				+ "LEFT OUTER JOIN FETCH pe.developer "
				+ "LEFT OUTER JOIN FETCH pe.ownerHistory "
				+ "WHERE (NOT pe.deleted = true) "
				+ "AND (pe.id = :entityid) ", ProductEntity.class );
		query.setParameter("entityid", id);
		List<ProductEntity> result = query.getResultList();
		
		if (result.size() > 1){
			throw new EntityRetrievalException("Data error. Duplicate product id in database.");
		} else if(result.size() == 1) {
			entity = result.get(0);
		}
		
		return entity;
	}
}
