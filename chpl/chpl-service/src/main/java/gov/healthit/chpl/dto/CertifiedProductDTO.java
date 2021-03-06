package gov.healthit.chpl.dto;


import java.io.Serializable;
import java.util.Date;

import org.springframework.util.StringUtils;

import gov.healthit.chpl.entity.listing.CertifiedProductEntity;

public class CertifiedProductDTO implements Serializable {
	private static final long serialVersionUID = 7918387302717979598L;
	public static final int CHPL_PRODUCT_ID_PARTS = 9;
	public static final int EDITION_CODE_INDEX = 0;
	public static final int ATL_CODE_INDEX = 1;
	public static final int ACB_CODE_INDEX = 2;
	public static final int DEVELOPER_CODE_INDEX = 3;
	public static final int PRODUCT_CODE_INDEX = 4;
	public static final int VERSION_CODE_INDEX = 5;
	public static final int ICS_CODE_INDEX = 6;
	public static final int ADDITIONAL_SOFTWARE_CODE_INDEX = 7;
	public static final int CERTIFIED_DATE_CODE_INDEX = 8;
	
	private Long id;
	private String productCode;
	private String versionCode;
	private Integer icsCode;
	private String additionalSoftwareCode;
	private String certifiedDateCode;
	private String acbCertificationId;
	private Long certificationBodyId;
	private Long certificationEditionId;
	private String chplProductNumber;
	private Date creationDate;
	private Boolean deleted;
	private Date lastModifiedDate;
	private Long lastModifiedUser;
	private Long practiceTypeId;
	private Long productClassificationTypeId;
	private Long productVersionId;
	private Long meaningfulUseUsers;
	private String reportFileLocation;
	private String sedReportFileLocation;
	private String sedIntendedUserDescription;
    private Date sedTestingEnd;
	private Long testingLabId;
	private Long certificationStatusId;
	private String otherAcb;
	private String transparencyAttestationUrl;
	private Boolean ics;
	private Boolean sedTesting;
	private Boolean qmsTesting;
	private Boolean accessibilityCertified;
	private String productAdditionalSoftware;
	private Boolean transparencyAttestation = null;
	
	public CertifiedProductDTO(){}
	
	public CertifiedProductDTO(CertifiedProductEntity entity){
		this.id = entity.getId();
		this.productCode = entity.getProductCode();
		this.versionCode = entity.getVersionCode();
		this.icsCode = entity.getIcsCode();
		this.additionalSoftwareCode = entity.getAdditionalSoftwareCode();
		this.certifiedDateCode = entity.getCertifiedDateCode();
		this.acbCertificationId = entity.getAcbCertificationId();
		this.certificationBodyId = entity.getCertificationBodyId();
		this.certificationEditionId = entity.getCertificationEditionId();
		this.chplProductNumber = entity.getChplProductNumber();
		this.creationDate = entity.getCreationDate();
		this.deleted = entity.getDeleted();
		this.lastModifiedDate = entity.getLastModifiedDate();
		this.lastModifiedUser = entity.getLastModifiedUser();
		this.meaningfulUseUsers = entity.getMeaningfulUseUsers();
		this.practiceTypeId = entity.getPracticeTypeId();
		this.productClassificationTypeId = entity.getProductClassificationTypeId();
		this.productVersionId = entity.getProductVersionId();
		this.reportFileLocation = entity.getReportFileLocation();
		this.sedReportFileLocation = entity.getSedReportFileLocation();
		this.sedIntendedUserDescription = entity.getSedIntendedUserDescription();
		this.sedTestingEnd = entity.getSedTestingEnd();
		this.transparencyAttestationUrl = entity.getTransparencyAttestationUrl();
		this.testingLabId = entity.getTestingLabId();		
		this.certificationStatusId = entity.getCertificationStatusId();
		this.otherAcb = entity.getOtherAcb();
		this.setIcs(entity.getIcs());
		this.setSedTesting(entity.getSedTesting());
		this.setQmsTesting(entity.getQmsTesting());
		this.setAccessibilityCertified(entity.getAccessibilityCertified());
		this.setProductAdditionalSoftware(entity.getProductAdditionalSoftware());
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
	public String getChplProductNumberForActivity() {
		if(StringUtils.isEmpty(this.chplProductNumber)) {
			return "a certified product";
		}
		return this.chplProductNumber;
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
	public Long getMeaningfulUseUsers() {
		return meaningfulUseUsers;
	}
	public void setMeaningfulUseUsers(Long meaningfulUseUsers) {
		this.meaningfulUseUsers = meaningfulUseUsers;
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

	public Long getCertificationStatusId() {
		return certificationStatusId;
	}

	public void setCertificationStatusId(Long certificationStatusId) {
		this.certificationStatusId = certificationStatusId;
	}

	public String getOtherAcb() {
		return otherAcb;
	}

	public void setOtherAcb(String otherAcb) {
		this.otherAcb = otherAcb;
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

	public Integer getIcsCode() {
		return icsCode;
	}

	public void setIcsCode(Integer icsCode) {
		this.icsCode = icsCode;
	}

	public Boolean getTransparencyAttestation() {
		return transparencyAttestation;
	}

	public void setTransparencyAttestation(Boolean transparencyAttestation) {
		this.transparencyAttestation = transparencyAttestation;
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

	public String getSedReportFileLocation() {
		return sedReportFileLocation;
	}

	public void setSedReportFileLocation(String sedReportFileLocation) {
		this.sedReportFileLocation = sedReportFileLocation;
	}

	public Boolean getQmsTesting() {
		return qmsTesting;
	}

	public void setQmsTesting(Boolean qmsTesting) {
		this.qmsTesting = qmsTesting;
	}

	public String getProductAdditionalSoftware() {
		return productAdditionalSoftware;
	}

	public void setProductAdditionalSoftware(String productAdditionalSoftware) {
		this.productAdditionalSoftware = productAdditionalSoftware;
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
}
