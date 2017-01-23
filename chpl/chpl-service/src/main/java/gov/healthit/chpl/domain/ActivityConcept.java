package gov.healthit.chpl.domain;

import java.io.Serializable;

public enum ActivityConcept implements Serializable {
	
	ACTIVITY_CONCEPT_CERTIFIED_PRODUCT(1L, "CERTIFIED_PRODUCT"),
	ACTIVITY_CONCEPT_PRODUCT(2L, "PRODUCT"),
	ACTIVITY_CONCEPT_DEVELOPER(3L, "DEVELOPER"),
	ACTIVITY_CONCEPT_CERTIFICATION(4L, "CERTIFICATION"),
	ACTIVITY_CONCEPT_CQM(5L, "CQM"),
	ACTIVITY_CONCEPT_CERTIFICATION_BODY(6L, "CERTIFICATION_BODY"),
	ACTIVITY_CONCEPT_VERSION(7L, "VERSION"),
	ACTIVITY_CONCEPT_USER(8L, "USER"),
	ACTIVITY_CONCEPT_ATL(9L, "ATL"),
	ACTIVITY_CONCEPT_PENDING_CERTIFIED_PRODUCT(10L, "PENDING_CERTIFIED_PRODUCT"),
	ACTIVITY_CONCEPT_API_KEY(11L, "API_KEY"),
	ACTIVITY_CONCEPT_ANNOUNCEMENT(12L, "ANNOUNCEMENT"),
	ACTIVITY_CONCEPT_CERTIFICATIONID(13L, "CERTIFICATION_ID");
	
	private final Long id;
	private final String name;
	
	private ActivityConcept(Long id, String name){
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
}
