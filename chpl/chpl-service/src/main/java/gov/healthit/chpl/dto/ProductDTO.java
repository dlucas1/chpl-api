package gov.healthit.chpl.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.healthit.chpl.domain.Statuses;
import gov.healthit.chpl.entity.ProductActiveOwnerEntity;
import gov.healthit.chpl.entity.ProductEntity;
import gov.healthit.chpl.entity.ProductVersionEntity;

public class ProductDTO implements Serializable {
	private static final long serialVersionUID = -5440560685496661764L;
	private Long id;
	private Date creationDate;
	private Boolean deleted;
	private Date lastModifiedDate;
	private Long lastModifiedUser;
	private String name;
	private ContactDTO contact;
	private Set<ProductVersionDTO> productVersions = new HashSet<ProductVersionDTO>();
	private String reportFileLocation;
	private Long developerId;
	private String developerName;
	private String developerCode;
	private Statuses statuses;
	private List<ProductOwnerDTO> ownerHistory;
	
	public ProductDTO(){
		this.ownerHistory = new ArrayList<ProductOwnerDTO>();
	}
	public ProductDTO(ProductEntity entity){
		this();
		
		this.id = entity.getId();
		this.creationDate = entity.getCreationDate();
		this.deleted = entity.isDeleted();
		this.lastModifiedDate = entity.getLastModifiedDate();
		this.lastModifiedUser = entity.getLastModifiedUser();
		this.name = entity.getName();
		this.reportFileLocation = entity.getReportFileLocation();
		if(entity.getContact() != null) {
			this.contact = new ContactDTO(entity.getContact());
		}
		this.developerId = entity.getDeveloperId();
		if(entity.getDeveloper() != null) {
			this.developerName = entity.getDeveloper().getName();
			this.developerCode = entity.getDeveloper().getDeveloperCode();
		}
		if(entity.getOwnerHistory() != null) {
			for(ProductActiveOwnerEntity ownerEntity : entity.getOwnerHistory()) {
				ProductOwnerDTO ownerDto = new ProductOwnerDTO(ownerEntity);
				this.ownerHistory.add(ownerDto);
			}
		}
		if(entity.getProductVersions() != null) {
			for(ProductVersionEntity version : entity.getProductVersions()) {
				ProductVersionDTO versionDto = new ProductVersionDTO(version);
				this.productVersions.add(versionDto);
			}
		}
		
		if(entity.getProductCertificationStatusesEntity() != null){
			this.statuses = new Statuses(entity.getProductCertificationStatusesEntity().getActive(), 
					entity.getProductCertificationStatusesEntity().getRetired(), 
					entity.getProductCertificationStatusesEntity().getWithdrawnByDeveloper(), 
					entity.getProductCertificationStatusesEntity().getWithdrawnByAcb(), 
					entity.getProductCertificationStatusesEntity().getSuspendedByAcb(),
					entity.getProductCertificationStatusesEntity().getSuspendedByOnc(),
					entity.getProductCertificationStatusesEntity().getTerminatedByOnc());
		}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<ProductVersionDTO> getProductVersions() {
		return productVersions;
	}
	public void setProductVersions(Set<ProductVersionDTO> productVersions) {
		this.productVersions = productVersions;
	}
	public String getReportFileLocation() {
		return reportFileLocation;
	}
	public void setReportFileLocation(String reportFileLocation) {
		this.reportFileLocation = reportFileLocation;
	}
	public Long getDeveloperId() {
		return developerId;
	}
	public void setDeveloperId(Long developerId) {
		this.developerId = developerId;
	}
	public String getDeveloperName() {
		return developerName;
	}
	public void setDeveloperName(String developerName) {
		this.developerName = developerName;
	}
	public Statuses getStatuses(){
		return statuses;
	}
	public void setStatuses(Statuses statuses) {
		this.statuses = statuses;
	}
	public List<ProductOwnerDTO> getOwnerHistory() {
		return ownerHistory;
	}
	public void setOwnerHistory(List<ProductOwnerDTO> ownerHistory) {
		this.ownerHistory = ownerHistory;
	}
	public String getDeveloperCode() {
		return developerCode;
	}
	public void setDeveloperCode(String developerCode) {
		this.developerCode = developerCode;
	}
	public ContactDTO getContact() {
		return contact;
	}
	public void setContact(ContactDTO contact) {
		this.contact = contact;
	}
	
}
