package gov.healthit.chpl.dto;


import java.io.Serializable;
import java.util.Date;

import gov.healthit.chpl.entity.CertificationStatusEventEntity;

public class CertificationStatusEventDTO implements Serializable {
	private static final long serialVersionUID = 1171613630377844762L;
	private Long id;
	private Long certifiedProductId;
	private Date eventDate;
	private CertificationStatusDTO status;
	private Date creationDate;
	private Boolean deleted;
	private Date lastModifiedDate;
	private Long lastModifiedUser;
	
	public CertificationStatusEventDTO(){}
	
	public CertificationStatusEventDTO(CertificationStatusEventEntity entity){
		
		this.id = entity.getId();
		this.certifiedProductId = entity.getCertifiedProductId();
		this.eventDate = entity.getEventDate();
		if(entity.getCertificationStatus() != null) {
			this.status = new CertificationStatusDTO(entity.getCertificationStatus());
		} else if(entity.getCertificationStatusId() != null){
			this.status = new CertificationStatusDTO();
			this.status.setId(entity.getCertificationStatusId());
		}
		this.creationDate = entity.getCreationDate();
		this.deleted = entity.getDeleted();
		this.lastModifiedDate = entity.getLastModifiedDate();
		this.lastModifiedUser = entity.getLastModifiedUser();
		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Long getLastModifiedUser() {
		return lastModifiedUser;
	}
	public void setLastModifiedUser(Long lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}
	public Long getCertifiedProductId() {
		return certifiedProductId;
	}

	public void setCertifiedProductId(Long certifiedProductId) {
		this.certifiedProductId = certifiedProductId;
	}

	public CertificationStatusDTO getStatus() {
		return status;
	}

	public void setStatus(CertificationStatusDTO status) {
		this.status = status;
	}
	
}