package gov.healthit.chpl.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gov.healthit.chpl.dto.CertificationResultTestFunctionalityDTO;

/**
 * Any optional, alternative, ambulatory (2015 only), or inpatient (2015 only) 
 * capabilities within a certification criterion to which the Health IT module was tested and 
 * certified.
 * For example, within the 2015 certification criteria 170.315(a), the optional functionality to 
 * include a "reason for order" field should be denoted as "(a)(1)(ii)". 
 * You can find a list of potential values in the 2014 or 2015 Functionality and 
 * Standards Reference Tables. 
 *
 */
@XmlType(namespace = "http://chpl.healthit.gov/listings")
@XmlAccessorType(XmlAccessType.FIELD)
public class CertificationResultTestFunctionality implements Serializable {
	private static final long serialVersionUID = -1647645050538126758L;
	
	/**
	 * Functionality tested to certification result mapping internal ID
	 */
	@XmlElement(required = true)
	private Long id;
	
	/**
	 * Functionality tested internal ID
	 */
	@XmlElement(required = true)
	private Long testFunctionalityId;
	
	/**
	 * Description of functionality tested
	 */
	@XmlElement(required = false, nillable=true)
	private String description;
	
	/**
	 * Name of functionality tested
	 */
	@XmlElement(required = true)
	private String name;
	
	/**
	 * Edition (2014, 2015) to which the functionality tested is applicable.
	 */
	@XmlElement(required = true)
	private String year;
	
	public CertificationResultTestFunctionality() {
		super();
	}
	
	public CertificationResultTestFunctionality(CertificationResultTestFunctionalityDTO dto) {
		this.id = dto.getId();
		this.testFunctionalityId = dto.getTestFunctionalityId();
		this.description = dto.getTestFunctionalityName();
		this.name = dto.getTestFunctionalityNumber();
		this.year = dto.getTestFunctionalityEdition();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTestFunctionalityId() {
		return testFunctionalityId;
	}

	public void setTestFunctionalityId(Long testFunctionalityId) {
		this.testFunctionalityId = testFunctionalityId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String name) {
		this.description = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

}
