package gov.healthit.chpl.dao.impl;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.dao.CertifiedProductDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dto.CertifiedProductDTO;
import gov.healthit.chpl.entity.CertifiedProductEntity;
import gov.healthit.chpl.entity.ProductVersionEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository(value="certifiedProductDAO")
public class CertifiedProductDAOImpl extends BaseDAOImpl implements CertifiedProductDAO {
	
	
	public CertifiedProductDTO create(CertifiedProductDTO dto) throws EntityCreationException{
		
		CertifiedProductEntity entity = null;
		try {
			if (dto.getId() != null){
				entity = this.getEntityById(dto.getId());
			}
		} catch (EntityRetrievalException e) {
			throw new EntityCreationException(e);
		}
		
		if (entity != null) {
			throw new EntityCreationException("A product with this ID already exists.");
		} else {
			
			entity = new CertifiedProductEntity();
			
			entity.setAcbCertificationId(dto.getAcbCertificationId());
			entity.setChplProductNumber(dto.getChplProductNumber());
			entity.setPracticeTypeId(dto.getPracticeTypeId());
			entity.setProductClassificationTypeId(dto.getProductClassificationTypeId());
			entity.setQualityManagementSystemAtt(dto.getQualityManagementSystemAtt());
			entity.setReportFileLocation(dto.getReportFileLocation());
			entity.setTestingLabId(dto.getTestingLabId());
			entity.setOtherAcb(dto.getOtherAcb());
			
			if(dto.getCertificationBodyId() != null) {
				entity.setCertificationBodyId(dto.getCertificationBodyId());
			}
			
			if(dto.getCertificationEditionId() != null) {
				entity.setCertificationEditionId(dto.getCertificationEditionId());
			}
			
			if(dto.getCertificationStatusId() != null) {
				entity.setCertificationStatusId(dto.getCertificationStatusId());
			}
			
			if(dto.getProductVersionId() != null) {
				entity.setProductVersionId(dto.getProductVersionId());
			}
			
			if(dto.getCreationDate() != null) {
				entity.setCreationDate(dto.getCreationDate());
			} else {
				entity.setCreationDate(new Date());
			}
			
			if(dto.getDeleted() != null) {
				entity.setDeleted(dto.getDeleted());
			} else {
				entity.setDeleted(false);
			}
			
			if(dto.getLastModifiedDate() != null) {
				entity.setLastModifiedDate(dto.getLastModifiedDate());
			} else {
				entity.setLastModifiedDate(new Date());
			}
			
			if(dto.getLastModifiedUser() != null) {
				entity.setLastModifiedUser(dto.getLastModifiedUser());
			} else {
				entity.setLastModifiedUser(Util.getCurrentUser().getId());
			}
			
			create(entity);
			return new CertifiedProductDTO(entity);
		}
	}

	public CertifiedProductDTO update(CertifiedProductDTO dto) throws EntityRetrievalException{
		
		CertifiedProductEntity entity = getEntityById(dto.getId());		
		
		entity.setAcbCertificationId(dto.getAcbCertificationId());
		entity.setChplProductNumber(dto.getChplProductNumber());
		entity.setPracticeTypeId(dto.getPracticeTypeId());
		entity.setProductClassificationTypeId(dto.getProductClassificationTypeId());
		entity.setQualityManagementSystemAtt(dto.getQualityManagementSystemAtt());
		entity.setReportFileLocation(dto.getReportFileLocation());
		entity.setTestingLabId(dto.getTestingLabId());
		entity.setOtherAcb(dto.getOtherAcb());
		
		if(dto.getCertificationBodyId() != null) {
			entity.setCertificationBodyId(dto.getCertificationBodyId());
		}
		
		if(dto.getCertificationEditionId() != null) {
			entity.setCertificationEditionId(dto.getCertificationEditionId());
		}
		
		if(dto.getCertificationStatusId() != null) {
			entity.setCertificationStatusId(dto.getCertificationStatusId());
		}
		
		if(dto.getProductVersionId() != null) {
			entity.setProductVersionId(dto.getProductVersionId());
		}
		
		if(dto.getCreationDate() != null) {
			entity.setCreationDate(dto.getCreationDate());
		} else {
			entity.setCreationDate(new Date());
		}
		
		if(dto.getDeleted() != null) {
			entity.setDeleted(dto.getDeleted());
		} else {
			entity.setDeleted(false);
		}
		
		if(dto.getLastModifiedDate() != null) {
			entity.setLastModifiedDate(dto.getLastModifiedDate());
		} else {
			entity.setLastModifiedDate(new Date());
		}
		
		if(dto.getLastModifiedUser() != null) {
			entity.setLastModifiedUser(dto.getLastModifiedUser());
		} else {
			entity.setLastModifiedUser(Util.getCurrentUser().getId());
		}
		
		update(entity);
		return new CertifiedProductDTO(entity);
	}
	
	public void delete(Long productId){
		
		// TODO: How to delete this without leaving orphans
		Query query = entityManager.createQuery("UPDATE CertifiedProductEntity SET deleted = true WHERE certified_product_id = :productid");
		query.setParameter("productid", productId);
		query.executeUpdate();
		
	}
	
	public List<CertifiedProductDTO> findAll(){
		
		List<CertifiedProductEntity> entities = getAllEntities();
		List<CertifiedProductDTO> products = new ArrayList<>();
		
		for (CertifiedProductEntity entity : entities) {
			CertifiedProductDTO product = new CertifiedProductDTO(entity);
			products.add(product);
		}
		return products;
		
	}
	
	public CertifiedProductDTO getById(Long productId) throws EntityRetrievalException{
		
		CertifiedProductEntity entity = getEntityById(productId);
		CertifiedProductDTO dto = new CertifiedProductDTO(entity);
		return dto;
		
	}
	
	public List<CertifiedProductDTO> getByVersionId(Long versionId) {
		Query query = entityManager.createQuery( "from CertifiedProductEntity where (NOT deleted = true) and product_version_id = :versionId)", CertifiedProductEntity.class );
		query.setParameter("versionId", versionId);
		List<CertifiedProductEntity> results = query.getResultList();
		
		List<CertifiedProductDTO> dtoResults = new ArrayList<CertifiedProductDTO>();
		for(CertifiedProductEntity result : results) {
			dtoResults.add(new CertifiedProductDTO(result));
		}
		return dtoResults;
	}
	
	public List<CertifiedProductDTO> getByVersionIds(List<Long> versionIds) {
		Query query = entityManager.createQuery( "from CertifiedProductEntity where (NOT deleted = true) and product_version_id IN :idList", CertifiedProductEntity.class );
		query.setParameter("idList", versionIds);
		List<CertifiedProductEntity> results = query.getResultList();
		
		List<CertifiedProductDTO> dtoResults = new ArrayList<CertifiedProductDTO>(results.size());
		for(CertifiedProductEntity result : results) {
			dtoResults.add(new CertifiedProductDTO(result));
		}
		return dtoResults;
	}
	
	private void create(CertifiedProductEntity product) {
		
		entityManager.persist(product);
		
	}
	
	private void update(CertifiedProductEntity product) {
		
		entityManager.merge(product);	
	
	}
	
	private List<CertifiedProductEntity> getAllEntities() {
		
		List<CertifiedProductEntity> result = entityManager.createQuery( "from CertifiedProductEntity where (NOT deleted = true) ", CertifiedProductEntity.class).getResultList();
		return result;
		
	}
	
	private CertifiedProductEntity getEntityById(Long entityId) throws EntityRetrievalException {
		
		CertifiedProductEntity entity = null;
		
		Query query = entityManager.createQuery( "from CertifiedProductEntity where (certified_product_id = :entityid) ", CertifiedProductEntity.class );
		query.setParameter("entityid", entityId);
		List<CertifiedProductEntity> result = query.getResultList();
		
		if (result.size() > 1){
			throw new EntityRetrievalException("Data error. Duplicate Certified Product id in database.");
		}
		
		if (result.size() > 0){
			entity = result.get(0);
		}
		
		return entity;
	}
	
}