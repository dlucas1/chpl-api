package gov.healthit.chpl.dto;

import gov.healthit.chpl.entity.TestToolEntity;

public class TestToolDTO {
	private Long id;
	private String name;
	private String description;
	
	public TestToolDTO(){}
	
	public TestToolDTO(TestToolEntity entity){		
		this.id = entity.getId();
		this.name = entity.getName();
		this.description = entity.getDescription();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
