package gov.healthit.chpl.web.controller.results;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import gov.healthit.chpl.dto.CertifiedProductDetailsDTO;

public class CertificationIdLookupResults implements Serializable {
	private static final long serialVersionUID = 494982326653301352L;

	static public class Product {
		private Long id;
		private String name;
		private String version;
		private String chplProductNumber;
		private String year;
		private String practiceType;
		private String acb;
		private String vendor;
		private String classification;
		private String additionalSoftware;

		public Product(CertifiedProductDetailsDTO dto) {
			this.id = dto.getId();
			this.name = dto.getProduct().getName();
			this.version = dto.getVersion().getVersion();
			if(!StringUtils.isEmpty(dto.getChplProductNumber())) {
				this.setChplProductNumber(dto.getChplProductNumber());
			} else {
				this.setChplProductNumber(dto.getYearCode() + "." + dto.getTestingLabCode() + "." + dto.getCertificationBodyCode() + "." + 
						dto.getDeveloper().getDeveloperCode() + "." + dto.getProductCode() + "." + dto.getVersionCode() + 
						"." + dto.getIcsCode() + "." + dto.getAdditionalSoftwareCode() + 
						"." + dto.getCertifiedDateCode());
			}			
			this.year = dto.getYear();
			this.practiceType = dto.getPracticeTypeName();
			this.acb = dto.getCertificationBodyName();
			this.vendor = dto.getDeveloper().getName();
			this.classification = dto.getProductClassificationName();
            this.additionalSoftware = "";
            try {
                if (null != dto.getProductAdditionalSoftware()) {
                    this.additionalSoftware = URLEncoder.encode(dto.getProductAdditionalSoftware(), "UTF-8");
                }
            } catch (UnsupportedEncodingException ex) {
                // Do nothing
            }
		}
		
		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
			this.id = id;
		}

		public String getYear() {
			return this.year;
		}
		
		public void setYear(String year) {
			this.year = year;
		}
		
		public String getVersion() {
			return this.version;
		}
		
		public void setVersion(String version) {
			this.version = version;
		}
		
		public String getChplProductNumber() {
			return this.chplProductNumber;
		}
		
		public void setChplProductNumber(String chplProductNumber) {
			this.chplProductNumber = chplProductNumber;
		}

		public String getName() {
			return this.name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getPracticeType() {
			return this.practiceType;
		}
		
		public void setPracticeType(String practiceType) {
			this.practiceType = practiceType;
		}
		
		public String getAcb() {
			return this.acb;
		}
		
		public void setAcb(String acb) {
			this.acb = acb;
		}
		
		public String getVendor() {
			return this.vendor;
		}

		public void setVendor(String vendor) {
			this.vendor = vendor;
		}
		
		public String getClassification() {
			return this.classification;
		}
		
		public void setClassification(String classification) {
			this.classification = classification;
		}

		public String getAdditionalSoftware() {
			return this.additionalSoftware;
		}

		public void setAdditionalSoftware(String additionalSoftware) {
			this.additionalSoftware = additionalSoftware;
		}
	}
	
	private List<Product> products = new ArrayList<Product>();
	private String ehrCertificationId;
	private String year;
	private Set<String> criteria = null;
	private Set<String> cqms = null;

	public String getYear() {
		return this.year;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String getEhrCertificationId() {
		return this.ehrCertificationId;
	}
	
	public void setEhrCertificationId(String ehrCertificationId) {
		this.ehrCertificationId = ehrCertificationId;
	}

	public Set<String> getCriteria() {
		return this.criteria;
	}

	public void setCriteria(Set<String> criteria) {
		this.criteria = criteria;
	}
	
	public Set<String> getCqms() {
		return this.cqms;
	}

	public void setCqms(Set<String> cqms) {
		this.cqms = cqms;
	}
	
}
