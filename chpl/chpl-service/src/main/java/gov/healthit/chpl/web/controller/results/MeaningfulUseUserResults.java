package gov.healthit.chpl.web.controller.results;

import java.util.ArrayList;
import java.util.List;

import gov.healthit.chpl.domain.MeaningfulUseUser;

public class MeaningfulUseUserResults {
	private List<MeaningfulUseUser> results;
	private List<MeaningfulUseUser> errors;

	public MeaningfulUseUserResults() {
		results = new ArrayList<MeaningfulUseUser>();
	}
	
	public MeaningfulUseUserResults(List<MeaningfulUseUser> results) {
		this.results = results;
	}

	public List<MeaningfulUseUser> getMeaningfulUseUsers() {
		return results;
	}

	public void setMeaningfulUseUsers(List<MeaningfulUseUser> results) {
		this.results = results;
	}

	public List<MeaningfulUseUser> getErrors() {
		return errors;
	}

	public void setErrors(List<MeaningfulUseUser> errors) {
		this.errors = errors;
	}
}