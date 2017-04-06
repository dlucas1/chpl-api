package gov.healthit.chpl.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "certified_product")
public class CertifiedProductStatisticsEntity {
	private static final long serialVersionUID = -2928065796560477879L;
	
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "certified_product_id", nullable = false)
	private Long id;
    
    @Column(name = "product_code")
    private String productCode;
    
    @Column(name = "version_code")
    private String versionCode;
    
    @Column(name = "ics_code")
    private String icsCode;
    
    @Column(name = "additional_software_code")
    private String additionalSoftwareCode;
    
    @Column(name = "certified_date_code")
    private String certifiedDateCode;
    
	@Basic( optional = true )
	@Column( name = "acb_certification_id", length = 250  )
	private String acbCertificationId;
	
	@Basic( optional = false )
	@Column(name = "certification_body_id", nullable = false )
	private Long certificationBodyId;
	
	@Basic( optional = false )
	@Column(name = "certification_edition_id", nullable = false )
	private Long certificationEditionId;
	
	@Basic( optional = true )
	@Column( name = "chpl_product_number", length = 250  )
	private String chplProductNumber;
	
	@Basic( optional = true )
	@Column(name = "practice_type_id", nullable = true )
	private Long practiceTypeId;
	
	@Basic( optional = true )
	@Column(name = "product_classification_type_id", nullable = true )
	private Long productClassificationTypeId;
	
	@Basic( optional = false )
	@Column(name = "product_version_id", nullable = false )
	private Long productVersionId;
	
	@Basic( optional = true )
	@Column( name = "report_file_location", length = 255  )
	private String reportFileLocation;
	
	@Basic(optional = true) 
	@Column(name = "sed_report_file_location")
	private String sedReportFileLocation;
	
	@Basic(optional = true) 
    @Column(name = "sed_intended_user_description")
    private String sedIntendedUserDescription;

	@Basic(optional = true) 
    @Column(name = "sed_testing_end")
    private Date sedTestingEnd;
    
	@Basic( optional = true )
	@Column(name = "testing_lab_id", nullable = true )
	private Long testingLabId;
	
	@Basic(optional = true) 
	@Column(name = "other_acb", length=64)
	private String otherAcb;
	
	@Basic(optional = false)
	@Column(name ="certification_status_id", nullable = false)
	private Long certificationStatusId;
	
	@Basic(optional = true)
	@Column(name ="meaningful_use_users", nullable = true)
	private Long meaningfulUseUsers;
	
	@Column(name = "transparency_attestation_url")
	private String transparencyAttestationUrl;
	
	@Column(name = "ics")
	private Boolean ics;
	
	@Column(name = "sed")
	private Boolean sedTesting;
	
	@Column(name = "qms")
	private Boolean qmsTesting;
	
	@Column(name = "accessibility_certified")
	private Boolean accessibilityCertified;
	
	@Column(name = "product_additional_software")
	private String productAdditionalSoftware;
	
	@Basic(optional = true)
	@OneToMany(targetEntity = CertificationResultEntity.class, mappedBy = "certifiedProduct", fetch = FetchType.LAZY)
	private List<CertificationResultEntity> certificationResult;
	
	@Basic(optional = true)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "certified_product_id", nullable = false, insertable=false, updatable=false)
	private CertifiedProductStatisticsEntity certifiedProduct;
	
	@Basic(optional = true)
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "certified_product_id", nullable=false, insertable=false, updatable=false)
	private Set<SurveillanceEntity> surveillanceEntity;
	
	@Basic(optional = true)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "certification_edition_id", nullable=false, insertable=false, updatable=false)
	private CertificationEditionEntity certificationEditionEntity;

	/**
	 * Default constructor, mainly for hibernate use.
	 */
	public CertifiedProductStatisticsEntity() {
		// Default constructor
	} 

	/** Constructor taking a given ID.
	 * @param id to set
	 */
	public CertifiedProductStatisticsEntity(Long id) {
		this.id = id;
	}
	
	/** Return the type of this class. Useful for when dealing with proxies.
	* @return Defining class.
	*/
	@Transient
	public Class<?> getClassType() {
		return CertifiedProductEntity.class;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAcbCertificationId() {
		return acbCertificationId;
	}

	public void setAcbCertificationId(String acbCertificationId) {
		this.acbCertificationId = acbCertificationId;
	}

	public Long getCertificationBodyId() {
		return certificationBodyId;
	}

	public void setCertificationBodyId(Long certificationBodyId) {
		this.certificationBodyId = certificationBodyId;
	}

	public Long getCertificationEditionId() {
		return certificationEditionId;
	}

	public void setCertificationEditionId(Long certificationEditionId) {
		this.certificationEditionId = certificationEditionId;
	}

	public String getChplProductNumber() {
		return chplProductNumber;
	}

	public void setChplProductNumber(String chplProductNumber) {
		this.chplProductNumber = chplProductNumber;
	}

	public Long getPracticeTypeId() {
		return practiceTypeId;
	}

	public void setPracticeTypeId(Long practiceTypeId) {
		this.practiceTypeId = practiceTypeId;
	}

	public Long getProductClassificationTypeId() {
		return productClassificationTypeId;
	}

	public void setProductClassificationTypeId(Long productClassificationTypeId) {
		this.productClassificationTypeId = productClassificationTypeId;
	}

	public Long getProductVersionId() {
		return productVersionId;
	}

	public void setProductVersionId(Long productVersionId) {
		this.productVersionId = productVersionId;
	}

	public String getReportFileLocation() {
		return reportFileLocation;
	}

	public void setReportFileLocation(String reportFileLocation) {
		this.reportFileLocation = reportFileLocation;
	}

	public Long getTestingLabId() {
		return testingLabId;
	}

	public void setTestingLabId(Long testingLabId) {
		this.testingLabId = testingLabId;
	}

	public String getOtherAcb() {
		return otherAcb;
	}

	public void setOtherAcb(String otherAcb) {
		this.otherAcb = otherAcb;
	}

	public Long getCertificationStatusId() {
		return certificationStatusId;
	}

	public void setCertificationStatusId(Long certificationStatusId) {
		this.certificationStatusId = certificationStatusId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getAdditionalSoftwareCode() {
		return additionalSoftwareCode;
	}

	public void setAdditionalSoftwareCode(String additionalSoftwareCode) {
		this.additionalSoftwareCode = additionalSoftwareCode;
	}

	public String getCertifiedDateCode() {
		return certifiedDateCode;
	}

	public void setCertifiedDateCode(String certifiedDateCode) {
		this.certifiedDateCode = certifiedDateCode;
	}

	public String getIcsCode() {
		return icsCode;
	}

	public void setIcsCode(String icsCode) {
		this.icsCode = icsCode;
	}

	public Boolean getIcs() {
		return ics;
	}

	public void setIcs(Boolean ics) {
		this.ics = ics;
	}

	public Boolean getSedTesting() {
		return sedTesting;
	}

	public void setSedTesting(Boolean sedTesting) {
		this.sedTesting = sedTesting;
	}

	public Boolean getQmsTesting() {
		return qmsTesting;
	}

	public void setQmsTesting(Boolean qmsTesting) {
		this.qmsTesting = qmsTesting;
	}

	public String getSedReportFileLocation() {
		return sedReportFileLocation;
	}

	public void setSedReportFileLocation(String sedReportFileLocation) {
		this.sedReportFileLocation = sedReportFileLocation;
	}

	public String getProductAdditionalSoftware() {
		return productAdditionalSoftware;
	}

	public void setProductAdditionalSoftware(String productAdditionalSoftware) {
		this.productAdditionalSoftware = productAdditionalSoftware;
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

	public Long getMeaningfulUseUsers(){
		return meaningfulUseUsers;
	}
	public void setMeaningfulUseUsers(Long meaningfulUseUsers){
		this.meaningfulUseUsers = meaningfulUseUsers;
	}
	
	public String getTransparencyAttestationUrl() {
		return transparencyAttestationUrl;
	}

	public void setTransparencyAttestationUrl(String transparencyAttestationUrl) {
		this.transparencyAttestationUrl = transparencyAttestationUrl;
	}

	public Boolean getAccessibilityCertified() {
		return accessibilityCertified;
	}

	public void setAccessibilityCertified(Boolean accessibilityCertified) {
		this.accessibilityCertified = accessibilityCertified;
	}

	public String getSedIntendedUserDescription() {
		return sedIntendedUserDescription;
	}

	public void setSedIntendedUserDescription(String sedIntendedUserDescription) {
		this.sedIntendedUserDescription = sedIntendedUserDescription;
	}

	public Date getSedTestingEnd() {
		return sedTestingEnd;
	}

	public void setSedTestingEnd(Date sedTestingEnd) {
		this.sedTestingEnd = sedTestingEnd;
	}
	
	public CertifiedProductStatisticsEntity getCertifiedProduct(){
		return this;
	}
	
	public Set<SurveillanceEntity> getSurveillanceEntity() {
		return surveillanceEntity;
	}

	public void setSurveillanceEntity(Set<SurveillanceEntity> surveillanceEntity) {
		this.surveillanceEntity = surveillanceEntity;
	}

	public CertificationEditionEntity getCertificationEditionEntity() {
		return certificationEditionEntity;
	}

	public void setCertificationEditionEntity(CertificationEditionEntity certificationEditionEntity) {
		this.certificationEditionEntity = certificationEditionEntity;
	}
}
