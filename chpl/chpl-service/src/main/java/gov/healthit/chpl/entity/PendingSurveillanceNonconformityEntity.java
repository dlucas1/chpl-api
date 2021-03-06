package gov.healthit.chpl.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "pending_surveillance_nonconformity")
public class PendingSurveillanceNonconformityEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "pending_surveillance_requirement_id")
	private Long pendingSurveillanceRequirementId;
	
	@Column(name = "nonconformity_type")
	private String type;
	
	@Column(name = "nonconformity_status")
	private String status;
	
	@Column(name = "date_of_determination")
	private Date dateOfDetermination;
	
	@Column(name = "corrective_action_plan_approval_date")
	private Date capApproval;
	
	@Column(name = "corrective_action_start_date")
	private Date capStart;
	
	@Column(name = "corrective_action_must_complete_date")
	private Date capMustCompleteDate;
	
	@Column(name = "corrective_action_end_date")
	private Date capEndDate;
	
	@Column(name = "summary")
	private String summary;
	
	@Column(name = "findings")
	private String findings;
	
	@Column(name = "sites_passed")
	private Integer sitesPassed;
	
	@Column(name = "total_sites")
	private Integer totalSites;
	
	@Column(name = "developer_explanation")
	private String developerExplanation;
	
	@Column(name = "resolution")
	private String resolution;
	
	@Column( name = "deleted")
	private Boolean deleted;
	
	@Column( name = "last_modified_user")
	private Long lastModifiedUser;
	
	@Column( name = "creation_date", insertable = false, updatable = false  )
	private Date creationDate;
	
	@Column( name = "last_modified_date", insertable = false, updatable = false )
	private Date lastModifiedDate;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPendingSurveillanceRequirementId() {
		return pendingSurveillanceRequirementId;
	}

	public void setPendingSurveillanceRequirementId(Long pendingSurveillanceRequirementId) {
		this.pendingSurveillanceRequirementId = pendingSurveillanceRequirementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateOfDetermination() {
		return dateOfDetermination;
	}

	public void setDateOfDetermination(Date dateOfDetermination) {
		this.dateOfDetermination = dateOfDetermination;
	}

	public Date getCapApproval() {
		return capApproval;
	}

	public void setCapApproval(Date capApproval) {
		this.capApproval = capApproval;
	}

	public Date getCapStart() {
		return capStart;
	}

	public void setCapStart(Date capStart) {
		this.capStart = capStart;
	}

	public Date getCapMustCompleteDate() {
		return capMustCompleteDate;
	}

	public void setCapMustCompleteDate(Date capMustCompleteDate) {
		this.capMustCompleteDate = capMustCompleteDate;
	}

	public Date getCapEndDate() {
		return capEndDate;
	}

	public void setCapEndDate(Date capEndDate) {
		this.capEndDate = capEndDate;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getFindings() {
		return findings;
	}

	public void setFindings(String findings) {
		this.findings = findings;
	}

	public Integer getSitesPassed() {
		return sitesPassed;
	}

	public void setSitesPassed(Integer sitesPassed) {
		this.sitesPassed = sitesPassed;
	}

	public Integer getTotalSites() {
		return totalSites;
	}

	public void setTotalSites(Integer totalSites) {
		this.totalSites = totalSites;
	}

	public String getDeveloperExplanation() {
		return developerExplanation;
	}

	public void setDeveloperExplanation(String developerExplanation) {
		this.developerExplanation = developerExplanation;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
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
}
