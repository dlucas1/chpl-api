package gov.healthit.chpl.domain;

import java.io.Serializable;
import java.util.List;

public class UpdateDevelopersRequest implements Serializable {
	private static final long serialVersionUID = -2815301261501080802L;
	private List<Long> developerIds;
	private Developer developer;
	public List<Long> getDeveloperIds() {
		return developerIds;
	}
	public void setDeveloperIds(List<Long> developerIds) {
		this.developerIds = developerIds;
	}
	public Developer getDeveloper() {
		return developer;
	}
	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}
}
