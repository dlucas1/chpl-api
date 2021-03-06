package gov.healthit.chpl.entity.listing;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import gov.healthit.chpl.entity.CertificationCriterionEntity;

/** 
 * Object mapping for hibernate-handled table: certification_result.
 * 
 *
 * @author autogenerated / cwatson
 */

@Entity
@Table(name = "certification_result")
public class CertificationResultEntity  implements Serializable {

	/** Serial Version UID. */
	private static final long serialVersionUID = -9050374846030066967L;

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
    @Column(name = "certification_result_id", nullable = false)
	private Long id;
	
	@Basic(optional = false)
	@Column(name = "certification_criterion_id", nullable = false)
	private Long certificationCriterionId;
	
	@Basic(optional = false)
	@Column(name = "certified_product_id", nullable = false)
	private Long certifiedProductId;
	
	@Column(name = "gap")
	private Boolean gap;
    
	@Column(name = "sed")
	private Boolean sed;
	
	@Column(name = "g1_success")
	private Boolean g1Success;
	
	@Column(name = "g2_success")
	private Boolean g2Success;
	
	@Basic(optional = false)
	@Column(name = "success", nullable = false)
	private Boolean success;
	
	@Column(name = "api_documentation")
	private String apiDocumentation;
	
	@Column(name = "privacy_security_framework")
	private String privacySecurityFramework;
	
	@Basic(optional = true)
	@OneToMany(fetch = FetchType.LAZY, mappedBy="certificationResult")
	private List<CertificationResultTestToolEntity> certificationResultTestTool;
	
	@Basic(optional = true)
	@ManyToOne
	@JoinColumn(name = "certified_product_id", nullable = false, insertable=false, updatable=false)
	private CertifiedProductEntity certifiedProduct;
	
	@Basic(optional = false)
	@ManyToOne(targetEntity = CertificationCriterionEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "certification_criterion_id", nullable = false, insertable = false, updatable = false)
	private CertificationCriterionEntity certificationCriterion;

	/**
	 * Default constructor, mainly for hibernate use.
	 */
	public CertificationResultEntity() {
		// Default constructor
	} 

	/** Constructor taking a given ID.
	 * @param id to set
	 */
	public CertificationResultEntity(Long id) {
		this.id = id;
	}
	
 	/** Return the type of this class. Useful for when dealing with proxies.
	* @return Defining class.
	*/
	@Transient
	public Class<?> getClassType() {
		return CertificationResultEntity.class;
	}
 
	 /**
	 * Return the value associated with the column: certificationCriterion.
	 * @return A CertificationCriterion object (this.certificationCriterion)
	 */
	public Long getCertificationCriterionId() {
		return this.certificationCriterionId;
	}
	
	 /**  
	 * Set the value related to the column: certificationCriterion.
	 * @param certificationCriterion the certificationCriterion value you wish to set
	 */
	public void setCertificationCriterionId(final Long certificationCriterionId) {
		this.certificationCriterionId = certificationCriterionId;
	}
	
	public void setCertificationCriterion(CertificationCriterionEntity certificationCriterion) {
		this.certificationCriterion = certificationCriterion;
	}
	
	/**
	 * Return the value associated with the column: certifiedProduct.
	 * @return A CertifiedProduct object (this.certifiedProduct)
	 */
	public Long getCertifiedProductId() {
		return this.certifiedProductId;
	}
	
	 /**  
	 * Set the value related to the column: certifiedProduct.
	 * @param certifiedProduct the certifiedProduct value you wish to set
	 */
	public void setCertifiedProductId(final Long certifiedProductId) {
		this.certifiedProductId = certifiedProductId;
	}

	 /**
	 * Return the value associated with the column: gap.
	 * @return A Boolean object (this.gap)
	 */
	public Boolean isGap() {
		return this.gap;	
	}
  
	 /**  
	 * Set the value related to the column: gap.
	 * @param gap the gap value you wish to set
	 */
	public void setGap(final Boolean gap) {
		this.gap = gap;
	}

	 /**
	 * Return the value associated with the column: id.
	 * @return A Long object (this.id)
	 */
	public Long getId() {
		return this.id;
	}
	
	 /**  
	 * Set the value related to the column: id.
	 * @param id the id value you wish to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	 /**
	 * Return the value associated with the column: successful.
	 * @return A Boolean object (this.successful)
	 */
	public Boolean isSuccess() {
		return this.success;		
	}
	

  
	 /**  
	 * Set the value related to the column: successful.
	 * @param successful the successful value you wish to set
	 */
	public void setSuccess(final Boolean success) {
		this.success = success;
	}

	public Boolean getSed() {
		return sed;
	}

	public void setSed(Boolean sed) {
		this.sed = sed;
	}

	public Boolean getG1Success() {
		return g1Success;
	}

	public void setG1Success(Boolean g1Success) {
		this.g1Success = g1Success;
	}

	public Boolean getG2Success() {
		return g2Success;
	}

	public void setG2Success(Boolean g2Success) {
		this.g2Success = g2Success;
	}

	public Boolean getGap() {
		return gap;
	}

	public Boolean getSuccess() {
		return success;
	}
	
	@Basic( optional = false )
	@Column( name = "creation_date", nullable = false  )
	protected Date creationDate;
	
	@Basic( optional = false )
	@Column( nullable = false  )
	protected Boolean deleted;
	
	@Basic( optional = false )
	@Column( name = "last_modified_date", nullable = false  )
	protected Date lastModifiedDate;
	
	@Basic( optional = false )
	@Column( name = "last_modified_user", nullable = false  )
	protected Long lastModifiedUser;
	
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

	public String getApiDocumentation() {
		return apiDocumentation;
	}

	public void setApiDocumentation(String apiDocumentation) {
		this.apiDocumentation = apiDocumentation;
	}

	public String getPrivacySecurityFramework() {
		return privacySecurityFramework;
	}

	public void setPrivacySecurityFramework(String privacySecurityFramework) {
		this.privacySecurityFramework = privacySecurityFramework;
	}
	
	public List<CertificationResultTestToolEntity> getCertificationResultTestTool() {
		return certificationResultTestTool;
	}

	public void setCertificationResultTestTool(List<CertificationResultTestToolEntity> certificationResultTestTool) {
		this.certificationResultTestTool = certificationResultTestTool;
	}

	public CertifiedProductEntity getCertifiedProduct() {
		return certifiedProduct;
	}

	public void setCertifiedProduct(CertifiedProductEntity certifiedProduct) {
		this.certifiedProduct = certifiedProduct;
	}
}
