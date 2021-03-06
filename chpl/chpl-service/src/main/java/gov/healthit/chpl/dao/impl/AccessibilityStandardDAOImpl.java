package gov.healthit.chpl.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.dao.AccessibilityStandardDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dto.AccessibilityStandardDTO;
import gov.healthit.chpl.entity.AccessibilityStandardEntity;

@Repository("accessibilityStandardDAO")
public class AccessibilityStandardDAOImpl extends BaseDAOImpl implements AccessibilityStandardDAO {
	
	@Override
	public AccessibilityStandardDTO create(AccessibilityStandardDTO dto)
			throws EntityCreationException, EntityRetrievalException {
		
		AccessibilityStandardEntity entity = null;
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
			entity = new AccessibilityStandardEntity();
			entity.setCreationDate(new Date());
			entity.setDeleted(false);
			entity.setLastModifiedDate(new Date());
			entity.setLastModifiedUser(Util.getCurrentUser().getId());
			entity.setName(dto.getName());
			create(entity);
			return new AccessibilityStandardDTO(entity);
		}		
	}

	@Override
	public AccessibilityStandardDTO update(AccessibilityStandardDTO dto)
			throws EntityRetrievalException {
		AccessibilityStandardEntity entity = this.getEntityById(dto.getId());
		
		if(entity == null) {
			throw new EntityRetrievalException("Entity with id " + dto.getId() + " does not exist");
		}
		
		entity.setName(dto.getName());
		
		update(entity);
		return new AccessibilityStandardDTO(entity);
	}

	@Override
	public void delete(Long id) throws EntityRetrievalException {
		
		AccessibilityStandardEntity toDelete = getEntityById(id);
		
		if(toDelete != null) {
			toDelete.setDeleted(true);
			toDelete.setLastModifiedDate(new Date());
			toDelete.setLastModifiedUser(Util.getCurrentUser().getId());
			update(toDelete);
		}
	}

	@Override
	public AccessibilityStandardDTO getById(Long id)
			throws EntityRetrievalException {
		
		AccessibilityStandardDTO dto = null;
		AccessibilityStandardEntity entity = getEntityById(id);
		
		if (entity != null){
			dto = new AccessibilityStandardDTO(entity);
		}
		return dto;
	}
	
	@Override
	public AccessibilityStandardDTO getByName(String name) {
		
		AccessibilityStandardDTO dto = null;
		List<AccessibilityStandardEntity> entities = getEntitiesByName(name);
		
		if (entities != null && entities.size() > 0){
			dto = new AccessibilityStandardDTO(entities.get(0));
		}
		return dto;
	}
	
	@Override
	public List<AccessibilityStandardDTO> findAll() {
		
		List<AccessibilityStandardEntity> entities = getAllEntities();
		List<AccessibilityStandardDTO> dtos = new ArrayList<AccessibilityStandardDTO>();
		
		for (AccessibilityStandardEntity entity : entities) {
			AccessibilityStandardDTO dto = new AccessibilityStandardDTO(entity);
			dtos.add(dto);
		}
		return dtos;
		
	}

	private void create(AccessibilityStandardEntity entity) {
		
		entityManager.persist(entity);
		entityManager.flush();
		
	}
	
	private void update(AccessibilityStandardEntity entity) {
		
		entityManager.merge(entity);	
		entityManager.flush();
	}
	
	private List<AccessibilityStandardEntity> getAllEntities() {
		return entityManager.createQuery( "from AccessibilityStandardEntity where (NOT deleted = true) ", AccessibilityStandardEntity.class).getResultList();
	}
	
	private AccessibilityStandardEntity getEntityById(Long id) throws EntityRetrievalException {
		
		AccessibilityStandardEntity entity = null;
			
		Query query = entityManager.createQuery( "from AccessibilityStandardEntity where (NOT deleted = true) AND (id = :entityid) ", AccessibilityStandardEntity.class );
		query.setParameter("entityid", id);
		List<AccessibilityStandardEntity> result = query.getResultList();
		
		if (result.size() > 1){
			throw new EntityRetrievalException("Data error. Duplicate id in database.");
		}
		
		if (result.size() > 0){
			entity = result.get(0);
		}
		
		return entity;
	}
	
	
	private List<AccessibilityStandardEntity> getEntitiesByName(String name) {
		
		Query query = entityManager.createQuery( "from AccessibilityStandardEntity where (NOT deleted = true) AND (name = :name) ", AccessibilityStandardEntity.class );
		query.setParameter("name", name);
		List<AccessibilityStandardEntity> result = query.getResultList();
		
		return result;
	}
	
	
}