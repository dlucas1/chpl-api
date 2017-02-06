package gov.healthit.chpl.domain;

import java.io.Serializable;

public class MeaningfulUseUser implements Serializable {
	private static final long serialVersionUID = -4837801214615959268L;
	private String productNumber;
	private Long certifiedProductId;
	private Long numberOfUsers;
	private Integer csvLineNumber;
	private String error;
	
	public MeaningfulUseUser(){}
	
	public MeaningfulUseUser(String productNumber,  Long numberOfUsers){
		this.productNumber = productNumber;
		this.numberOfUsers = numberOfUsers;
	}
	
	public MeaningfulUseUser(String productNumber, Long certifiedProductId, Long numberOfUsers, Integer csvLineNumber){
		this.productNumber = productNumber;
		this.certifiedProductId = certifiedProductId;
		this.numberOfUsers = numberOfUsers;
		this.csvLineNumber = csvLineNumber;
	}
	
	public void setCertifiedProductId(Long certifiedProductId){
		this.certifiedProductId = certifiedProductId;
	}
	
	public Long getCertifiedProductId(){
		return this.certifiedProductId;
	}
	
	public void setProductNumber(String productNumber){
		this.productNumber = productNumber;
	}
	
	public String getProductNumber(){
		return this.productNumber;
	}

	public Long getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Long numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public Integer getCsvLineNumber() {
		return csvLineNumber;
	}

	public void setCsvLineNumber(Integer csvLineNumber) {
		this.csvLineNumber = csvLineNumber;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
