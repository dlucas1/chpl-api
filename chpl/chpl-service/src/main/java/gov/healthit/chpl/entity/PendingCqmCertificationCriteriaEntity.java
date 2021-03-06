package gov.healthit.chpl.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="pending_cqm_certification_criteria")
public class PendingCqmCertificationCriteriaEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name = "pending_cqm_certification_criteria_id", nullable = false  )
	private Long id;
	
	@Basic( optional = false )
	@Column( name = "pending_cqm_criterion_id", nullable = false  )
	private Long pendingCqmId;
	
	@Basic( optional = false )
	@Column( name = "certification_criterion_id", nullable = false  )
	private Long certificationId;
	
	@Basic( optional = true )
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "certification_criterion_id", unique=true, nullable = true, insertable=false, updatable= false)
	private CertificationCriterionEntity certificationCriteria;

	@Basic( optional = false )
	@Column( name = "last_modified_date", nullable = false  )
	private Date lastModifiedDate;
	
	@Basic( optional = false )
	@Column( name = "last_modified_user", nullable = false  )
	private Long lastModifiedUser;
	
	@Basic( optional = false )
	@Column( name = "creation_date", nullable = false  )
	private Date creationDate;
	
	@Basic( optional = false )
	@Column( name = "deleted", nullable = false  )
	private Boolean deleted;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getPendingCqmId() {
		return pendingCqmId;
	}

	public void setPendingCqmId(Long pendingCqmId) {
		this.pendingCqmId = pendingCqmId;
	}

	public Long getCertificationId() {
		return certificationId;
	}

	public void setCertificationId(Long certificationId) {
		this.certificationId = certificationId;
	}

	public CertificationCriterionEntity getCertificationCriteria() {
		return certificationCriteria;
	}

	public void setCertificationCriteria(CertificationCriterionEntity certificationCriteria) {
		this.certificationCriteria = certificationCriteria;
	}
}
