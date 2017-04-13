package gov.healthit.chpl.domain;

import java.io.Serializable;

public class Statistics implements Serializable {
	private static final long serialVersionUID = 6977674702447513779L;
	Long totalDevelopers;
	Long totalDevelopersWith2014Listings;
	Long totalDevelopersWith2015Listings;
	Long totalCertifiedProducts;
	Long totalCPsActive2014Listings;
	Long totalCPsActive2015Listings;
	Long totalCPsActiveListings;
	Long totalListings;
	Long totalActive2014Listings;
	Long totalActive2014ListingsCertifiedByDrummond;
	Long totalActive2014ListingsCertifiedByICSALabs;
	Long totalActive2014ListingsCertifiedByInfoGard;
	Long totalActive2015Listings;
	Long totalActive2015ListingsCertifiedByDrummond;
	Long totalActive2015ListingsCertifiedByICSALabs;
	Long totalActive2015ListingsCertifiedByInfoGard;
	Long total2014Listings;
	Long total2015Listings;
	Long total2011Listings;
	Long totalSurveillanceActivities;
	Long totalOpenSurveillanceActivities;
	Long totalClosedSurveillanceActivities;
	Long totalNonConformities;
	Long totalOpenNonconformities;
	Long totalClosedNonconformities;
	
	public Statistics(){}
	
	public Statistics(Long totalDevelopers, Long totalDevelopersWith2014Listings, Long totalDevelopersWith2015Listings, Long totalCertifiedProducts,
	Long totalCPsActive2014Listings, Long totalCPsActive2015Listings, Long totalCPsActiveListings, Long totalListings, Long totalActive2014Listings, 
	Long totalActive2014ListingsCertifiedByDrummond, Long totalActive2014ListingsCertifiedByICSALabs, Long totalActive2014ListingsCertifiedByInfoGard,
	Long totalActive2015Listings, Long totalActive2015ListingsCertifiedByDrummond, Long totalActive2015ListingsCertifiedByICSALabs,
	Long totalActive2015ListingsCertifiedByInfoGard, Long total2014Listings, Long total2015Listings, Long total2011Listings, Long totalSurveillanceActivities, 
	Long totalOpenSurveillanceActivities, Long totalClosedSurveillanceActivities, Long totalNonConformities, Long totalOpenNonconformities, Long totalClosedNonconformities){
		setTotalDevelopers(totalDevelopers);
		setTotalDevelopersWith2014Listings(totalDevelopersWith2014Listings);
		setTotalDevelopersWith2015Listings(totalDevelopersWith2015Listings); 
		setTotalCertifiedProducts(totalCertifiedProducts);
		setTotalCPsActive2014Listings(totalCPsActive2014Listings); 
		setTotalCPsActive2015Listings(totalCPsActive2015Listings); 
		setTotalCPsActiveListings(totalCPsActiveListings); 
		setTotalListings(totalListings); 
		setTotalActive2014Listings(totalActive2014Listings); 
		setTotalActive2014ListingsCertifiedByDrummond(totalActive2014ListingsCertifiedByDrummond);
		setTotalActive2014ListingsCertifiedByICSALabs(totalActive2014ListingsCertifiedByICSALabs);
		setTotalActive2014ListingsCertifiedByInfoGard(totalActive2014ListingsCertifiedByInfoGard);
		setTotalActive2015Listings(totalActive2015Listings); 
		setTotalActive2015ListingsCertifiedByDrummond(totalActive2015ListingsCertifiedByDrummond);
		setTotalActive2015ListingsCertifiedByICSALabs(totalActive2015ListingsCertifiedByICSALabs);
		setTotalActive2015ListingsCertifiedByInfoGard(totalActive2015ListingsCertifiedByInfoGard);
		setTotal2014Listings(total2014Listings); 
		setTotal2015Listings(total2015Listings); 
		setTotal2011Listings(total2011Listings); 
		setTotalSurveillanceActivities(totalSurveillanceActivities);
		setTotalOpenSurveillanceActivities(totalOpenSurveillanceActivities); 
		setTotalClosedSurveillanceActivities(totalClosedSurveillanceActivities); 
		setTotalNonConformities(totalNonConformities); 
		setTotalOpenNonconformities(totalOpenNonconformities); 
		setTotalClosedNonconformities(totalClosedNonconformities);
	}
	
	public Long getTotalDevelopers() {
		return totalDevelopers;
	}
	public void setTotalDevelopers(Long totalDevelopers) {
		this.totalDevelopers = totalDevelopers;
	}
	public Long getTotalDevelopersWith2014Listings() {
		return totalDevelopersWith2014Listings;
	}
	public void setTotalDevelopersWith2014Listings(Long totalDevelopersWith2014Listings) {
		this.totalDevelopersWith2014Listings = totalDevelopersWith2014Listings;
	}
	public Long getTotalDevelopersWith2015Listings() {
		return totalDevelopersWith2015Listings;
	}
	public void setTotalDevelopersWith2015Listings(Long totalDevelopersWith2015Listings) {
		this.totalDevelopersWith2015Listings = totalDevelopersWith2015Listings;
	}
	public Long getTotalCertifiedProducts() {
		return totalCertifiedProducts;
	}
	public void setTotalCertifiedProducts(Long totalCertifiedProducts) {
		this.totalCertifiedProducts = totalCertifiedProducts;
	}
	public Long getTotalCPsActive2014Listings() {
		return totalCPsActive2014Listings;
	}
	public void setTotalCPsActive2014Listings(Long totalCPsActive2014Listings) {
		this.totalCPsActive2014Listings = totalCPsActive2014Listings;
	}
	public Long getTotalCPsActive2015Listings() {
		return totalCPsActive2015Listings;
	}
	public void setTotalCPsActive2015Listings(Long totalCPsActive2015Listings) {
		this.totalCPsActive2015Listings = totalCPsActive2015Listings;
	}
	public Long getTotalCPsActiveListings() {
		return totalCPsActiveListings;
	}
	public void setTotalCPsActiveListings(Long totalCPsActiveListings) {
		this.totalCPsActiveListings = totalCPsActiveListings;
	}
	public Long getTotalListings() {
		return totalListings;
	}
	public void setTotalListings(Long totalListings) {
		this.totalListings = totalListings;
	}
	public Long getTotalActive2014Listings() {
		return totalActive2014Listings;
	}
	public void setTotalActive2014Listings(Long totalActive2014Listings) {
		this.totalActive2014Listings = totalActive2014Listings;
	}
	public Long getTotalActive2015Listings() {
		return totalActive2015Listings;
	}
	public void setTotalActive2015Listings(Long totalActive2015Listings) {
		this.totalActive2015Listings = totalActive2015Listings;
	}
	public Long getTotal2014Listings() {
		return total2014Listings;
	}
	public void setTotal2014Listings(Long total2014Listings) {
		this.total2014Listings = total2014Listings;
	}
	public Long getTotal2015Listings() {
		return total2015Listings;
	}
	public void setTotal2015Listings(Long total2015Listings) {
		this.total2015Listings = total2015Listings;
	}
	public Long getTotal2011Listings() {
		return total2011Listings;
	}
	public void setTotal2011Listings(Long total2011Listings) {
		this.total2011Listings = total2011Listings;
	}
	public Long getTotalSurveillanceActivities() {
		return totalSurveillanceActivities;
	}
	public void setTotalSurveillanceActivities(Long totalSurveillanceActivities) {
		this.totalSurveillanceActivities = totalSurveillanceActivities;
	}
	public Long getTotalOpenSurveillanceActivities() {
		return totalOpenSurveillanceActivities;
	}
	public void setTotalOpenSurveillanceActivities(Long totalOpenSurveillanceActivities) {
		this.totalOpenSurveillanceActivities = totalOpenSurveillanceActivities;
	}
	public Long getTotalClosedSurveillanceActivities() {
		return totalClosedSurveillanceActivities;
	}
	public void setTotalClosedSurveillanceActivities(Long totalClosedSurveillanceActivities) {
		this.totalClosedSurveillanceActivities = totalClosedSurveillanceActivities;
	}
	public Long getTotalNonConformities() {
		return totalNonConformities;
	}
	public void setTotalNonConformities(Long totalNonConformities) {
		this.totalNonConformities = totalNonConformities;
	}
	public Long getTotalOpenNonconformities() {
		return totalOpenNonconformities;
	}
	public void setTotalOpenNonconformities(Long totalOpenNonconformities) {
		this.totalOpenNonconformities = totalOpenNonconformities;
	}
	public Long getTotalClosedNonconformities() {
		return totalClosedNonconformities;
	}
	public void setTotalClosedNonconformities(Long totalClosedNonconformities) {
		this.totalClosedNonconformities = totalClosedNonconformities;
	}

	public Long getTotalActive2014ListingsCertifiedByDrummond() {
		return totalActive2014ListingsCertifiedByDrummond;
	}

	public void setTotalActive2014ListingsCertifiedByDrummond(Long totalActive2014ListingsCertifiedByDrummond) {
		this.totalActive2014ListingsCertifiedByDrummond = totalActive2014ListingsCertifiedByDrummond;
	}

	public Long getTotalActive2014ListingsCertifiedByICSALabs() {
		return totalActive2014ListingsCertifiedByICSALabs;
	}

	public void setTotalActive2014ListingsCertifiedByICSALabs(Long totalActive2014ListingsCertifiedByICSALabs) {
		this.totalActive2014ListingsCertifiedByICSALabs = totalActive2014ListingsCertifiedByICSALabs;
	}

	public Long getTotalActive2014ListingsCertifiedByInfoGard() {
		return totalActive2014ListingsCertifiedByInfoGard;
	}

	public void setTotalActive2014ListingsCertifiedByInfoGard(Long totalActive2014ListingsCertifiedByInfoGard) {
		this.totalActive2014ListingsCertifiedByInfoGard = totalActive2014ListingsCertifiedByInfoGard;
	}

	public Long getTotalActive2015ListingsCertifiedByDrummond() {
		return totalActive2015ListingsCertifiedByDrummond;
	}

	public void setTotalActive2015ListingsCertifiedByDrummond(Long totalActive2015ListingsCertifiedByDrummond) {
		this.totalActive2015ListingsCertifiedByDrummond = totalActive2015ListingsCertifiedByDrummond;
	}

	public Long getTotalActive2015ListingsCertifiedByICSALabs() {
		return totalActive2015ListingsCertifiedByICSALabs;
	}

	public void setTotalActive2015ListingsCertifiedByICSALabs(Long totalActive2015ListingsCertifiedByICSALabs) {
		this.totalActive2015ListingsCertifiedByICSALabs = totalActive2015ListingsCertifiedByICSALabs;
	}

	public Long getTotalActive2015ListingsCertifiedByInfoGard() {
		return totalActive2015ListingsCertifiedByInfoGard;
	}

	public void setTotalActive2015ListingsCertifiedByInfoGard(Long totalActive2015ListingsCertifiedByInfoGard) {
		this.totalActive2015ListingsCertifiedByInfoGard = totalActive2015ListingsCertifiedByInfoGard;
	}
	
}
