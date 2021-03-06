package gov.healthit.chpl.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "vendor_status")
public class DeveloperStatusEntity implements Cloneable, Serializable {
	private static final long serialVersionUID = 1730728043307135377L;

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic( optional = false )
	@Column( name = "vendor_status_id", nullable = false  )
	private Long id;
	
	@Column(name = "name")
	@Type(type = "gov.healthit.chpl.entity.PostgresDeveloperStatusType" , parameters ={@org.hibernate.annotations.Parameter(name = "enumClassName",value = "gov.healthit.chpl.entity.DeveloperStatusType")} )
	private DeveloperStatusType name;

	@Basic( optional = false )
	@Column( name = "creation_date", nullable = false  )
	private Date creationDate;
	
	@Basic( optional = false )
	@NotNull
	@Column( nullable = false  )
	private Boolean deleted;
	
	@Basic( optional = false )
	@Column( name = "last_modified_date", nullable = false  )
	private Date lastModifiedDate;
	
	@Basic( optional = false )
	@NotNull
	@Column( name = "last_modified_user", nullable = false  )
	private Long lastModifiedUser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DeveloperStatusType getName() {
		return name;
	}

	public void setName(DeveloperStatusType name) {
		this.name = name;
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