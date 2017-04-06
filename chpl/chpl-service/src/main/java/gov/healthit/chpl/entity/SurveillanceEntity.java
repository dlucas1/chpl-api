package gov.healthit.chpl.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;


@Entity
@Table(name = "surveillance")
public class SurveillanceEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "friendly_id", insertable = false, updatable = false)
	private String friendlyId;
	
	@Column(name = "certified_product_id", insertable = false, updatable = false)
	private Long certifiedProductId;
	
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "certified_product_id", insertable = false, updatable = false)
	private CertifiedProductEntity certifiedProduct;
	
	@Column(name = "start_date")
	private Date startDate;
	
	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "type_id")
	private Long surveillanceTypeId;
	
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id", insertable = false, updatable = false)
	private SurveillanceTypeEntity surveillanceType;
	
	@Column(name = "randomized_sites_used")
	private Integer numRandomizedSites;
	
	@Column( name = "deleted")
	private Boolean deleted;
	
	@Column( name = "last_modified_user")
	private Long lastModifiedUser;
	
	@Column( name = "creation_date", insertable = false, updatable = false  )
	private Date creationDate;
	
	@Column( name = "last_modified_date", insertable = false, updatable = false )
	private Date lastModifiedDate;
	
	@Column(name = "user_permission_id")
	private Long userPermissionId;
	
 	@OneToMany( fetch = FetchType.LAZY, mappedBy = "surveillanceId"  )
	@Basic( optional = false )
	@Column( name = "surveillance_id", nullable = false  )
 	@Where(clause="deleted <> 'true'")
	private Set<SurveillanceRequirementEntity> surveilledRequirements = new HashSet<SurveillanceRequirementEntity>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getNumRandomizedSites() {
		return numRandomizedSites;
	}

	public void setNumRandomizedSites(Integer numRandomizedSites) {
		this.numRandomizedSites = numRandomizedSites;
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

	public CertifiedProductEntity getCertifiedProduct() {
		return certifiedProduct;
	}

	public void setCertifiedProduct(CertifiedProductEntity certifiedProduct) {
		this.certifiedProduct = certifiedProduct;
	}

	public Long getCertifiedProductId() {
		return certifiedProductId;
	}

	public void setCertifiedProductId(Long certifiedProductId) {
		this.certifiedProductId = certifiedProductId;
	}

	public Long getSurveillanceTypeId() {
		return surveillanceTypeId;
	}

	public void setSurveillanceTypeId(Long surveillanceTypeId) {
		this.surveillanceTypeId = surveillanceTypeId;
	}

	public SurveillanceTypeEntity getSurveillanceType() {
		return surveillanceType;
	}

	public void setSurveillanceType(SurveillanceTypeEntity surveillanceType) {
		this.surveillanceType = surveillanceType;
	}

	public Set<SurveillanceRequirementEntity> getSurveilledRequirements() {
		return surveilledRequirements;
	}

	public void setSurveilledRequirements(Set<SurveillanceRequirementEntity> surveilledRequirements) {
		this.surveilledRequirements = surveilledRequirements;
	}

	public String getFriendlyId() {
		return friendlyId;
	}

	public void setFriendlyId(String friendlyId) {
		this.friendlyId = friendlyId;
	}

	public Long getUserPermissionId() {
		return userPermissionId;
	}

	public void setUserPermissionId(Long userPermissionId) {
		this.userPermissionId = userPermissionId;
	}
}
