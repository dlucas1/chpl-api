package gov.healthit.chpl.entity;

import gov.healthit.chpl.activity.ActivityConcept;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name="activity")
public class ActivityEntity {
    
	@Id 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_activity_idGenerator")
	@Basic( optional = false )
	@Column( name = "activity_id", nullable = false )
	@SequenceGenerator(name = "activity_activity_idGenerator", sequenceName = "activity_activity_id_seq")
	private Long id;
	
	@Basic( optional = true )
	@Column( name = "description", nullable = true )
	private String description;
	
	@Basic( optional = false )
	@Column( name = "activity_date", nullable = false )
	private Date activityDate;
	
	@Basic( optional = false )
	@Column( name = "activity_object_id", nullable = false)
	private Long activityObjectId;
	
	@Basic( optional = false )
	@Column( name = "activity_object_concept_id", nullable = false)
	private Long activityObjectConceptId;
	
	@Basic( optional = false )
	@Column( name = "creation_date", nullable = false )
	private Date creationDate;
	
	@Basic( optional = false )
	@Column( name = "last_modified_date", nullable = false )
	private Date lastModifiedDate;
	
	@Basic( optional = false )
	@Column( name = "last_modified_user", nullable = false )
	private Long lastModifiedUser;
	
	@Basic( optional = false )
	@Column( name = "deleted", nullable = false )
	private Long deleted;
	
	transient ActivityConcept concept;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}

	public Long getActivityObjectId() {
		return activityObjectId;
	}

	public void setActivityObjectId(Long activityObjectId) {
		this.activityObjectId = activityObjectId;
	}

	public Long getActivityObjectConceptId() {
		return activityObjectConceptId;
	}

	public void setActivityObjectConceptId(Long activityObjectClassId) {
		
		for (ActivityConcept concept : ActivityConcept.values()) {
			if(concept.getId().equals(activityObjectClassId)){
				this.concept = concept;
				break;
			}
		}
		this.activityObjectConceptId = activityObjectClassId;
	}
	
	public ActivityConcept getConcept() {
		return this.concept;
	}

	public void setConcept(ActivityConcept concept) {
		this.activityObjectConceptId = concept.getId();
		this.concept = concept;
	}
	

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	public Long getDeleted() {
		return deleted;
	}

	public void setDeleted(Long deleted) {
		this.deleted = deleted;
	}
	
}
