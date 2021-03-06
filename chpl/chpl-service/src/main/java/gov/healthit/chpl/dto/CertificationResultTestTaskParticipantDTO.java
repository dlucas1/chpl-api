package gov.healthit.chpl.dto;

import java.io.Serializable;

import gov.healthit.chpl.entity.listing.CertificationResultTestTaskParticipantEntity;

public class CertificationResultTestTaskParticipantDTO implements Serializable {
	private static final long serialVersionUID = 297606565899624167L;
	private Long id;
	private Long certTestTaskId;
	private Long testParticipantId;
	private TestParticipantDTO testParticipant;
	
	public CertificationResultTestTaskParticipantDTO(){}
	
	public CertificationResultTestTaskParticipantDTO(CertificationResultTestTaskParticipantEntity entity){		
		this.id = entity.getId();
		this.certTestTaskId = entity.getCertificationResultTestTaskId();
		this.testParticipantId = entity.getTestParticipantId();
		if(entity.getTestParticipant() != null) {
			this.testParticipant = new TestParticipantDTO(entity.getTestParticipant());
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTestParticipantId() {
		return testParticipantId;
	}

	public void setTestParticipantId(Long testParticipantId) {
		this.testParticipantId = testParticipantId;
	}

	public TestParticipantDTO getTestParticipant() {
		return testParticipant;
	}

	public void setTestParticipant(TestParticipantDTO testParticipant) {
		this.testParticipant = testParticipant;
	}

	public Long getCertTestTaskId() {
		return certTestTaskId;
	}

	public void setCertTestTaskId(Long certTestTaskId) {
		this.certTestTaskId = certTestTaskId;
	}
}
