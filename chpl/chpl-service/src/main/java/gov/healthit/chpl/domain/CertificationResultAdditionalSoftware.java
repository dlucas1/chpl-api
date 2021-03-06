package gov.healthit.chpl.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import gov.healthit.chpl.dto.CertificationResultAdditionalSoftwareDTO;

/**
 * Additional software that is relied upon by the Health IT Module to demonstrate its compliance with a certification criterion or criteria.
 * The additional software may be either another certified product listing or any other available software.
 */
@XmlType(namespace = "http://chpl.healthit.gov/listings")
@XmlAccessorType(XmlAccessType.FIELD)
public class CertificationResultAdditionalSoftware implements Serializable {
	private static final long serialVersionUID = -4131156681875211987L;
	
	/**
	 * Additional software to certification result mapping internal ID
	 */
	@XmlElement(required = true)
	private Long id;
	
	/**
	 * This variable demonstrates if the additional software relied upon by the Health IT Module is not a certified health IT product, the name of the additional software product relied upon. It is a string variable that does not take any restrictions on formatting or values and is applicable for 2014 and 2015 Edition. 
	 */
	@XmlElement(required = false, nillable=true)
	private String name;
	
	/**
	 * The version of the corresponding non-certified additional software relied upon by the Health IT Module. This variable is for 2014 and 2015 Edition, and a string variable that does not take any restrictions on formatting or values. 
	 */
	@XmlElement(required = false, nillable=true)
	private String version;
	
	/**
	 * This variable indicates if the additional software relied upon by the Health IT Module is also a certified health IT product, the unique CHPL ID of the additional software relied upon. 
	 * It is applicable for 2014 and 2015 Edition.It either takes CHIP-XXXXXX or  Edition.ATL.ACB.Developer.Product.Version.ICS.AdditionalSoftware.Date. 
	 * The CHPL ID format, Edition.ATL.ACB.Developer.Product.Version.ICS.AdditionalSoftware.Date, is coded using product-specific information. Edition = certification edition (ex. 2014, 2015); ATL = two digit code for the ONC Authorized Testing Laboratory (ATL); ACB = two digit code for the ONC Authorized Certifying Body (ACB); Developer = four digit code for the health IT product developer; Product = four character alphanumeric reference to the certified product; Version = two character alphanumeric reference to the version of the certified product; ICS = binary code indicating Inherited Certified Status (incremental); AdditionalSoftware = binary code indicating the requirement for additional software to meeting certification requirements (1 = yes; 0 = no); Date = Date of certification (format = YYMMDD).
	 */
	@XmlElement(required = false, nillable=true)
	private Long certifiedProductId;
	
	/**
	 * If the additional software relied upon by the Health IT Module is also a certified health IT product, the unique CHPL ID of the additional software relied upon. 
	 */
	@XmlElement(required = false, nillable=true)
	private String certifiedProductNumber;
	
	/**
	 * Additional software justification
	 */
	@XmlElement(required = false, nillable=true)
	private String justification;
	
	@XmlTransient
	private Long certificationResultId;
	
	/**
	 * For 2015 certified products, the concept of a 'grouping' is introduced to allow for sets of alternative additional software. At least one Additional Software within a particular grouping is required to meet a specific certification criteria.
	 */
	@XmlElement(required = false, nillable=true)
	private String grouping;
	
	public CertificationResultAdditionalSoftware() {
		super();
	}
	
	public CertificationResultAdditionalSoftware(CertificationResultAdditionalSoftwareDTO dto) {
		this.id = dto.getId();
		this.name = dto.getName();
		this.version = dto.getVersion();
		this.certifiedProductId = dto.getCertifiedProductId();
		this.justification = dto.getJustification();
		this.certificationResultId = dto.getCertificationResultId();
		this.certifiedProductNumber = dto.getCertifiedProductNumber();
		this.grouping = dto.getGrouping();
	}
	
	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Long getCertifiedProductId() {
		return certifiedProductId;
	}

	public void setCertifiedProductId(Long certifiedProductId) {
		this.certifiedProductId = certifiedProductId;
	}

	public Long getCertificationResultId() {
		return certificationResultId;
	}

	public void setCertificationResultId(Long certificationResultId) {
		this.certificationResultId = certificationResultId;
	}

	public String getCertifiedProductNumber() {
		return certifiedProductNumber;
	}

	public void setCertifiedProductNumber(String certifiedProductNumber) {
		this.certifiedProductNumber = certifiedProductNumber;
	}

	public String getGrouping() {
		return grouping;
	}

	public void setGrouping(String grouping) {
		this.grouping = grouping;
	}

}
