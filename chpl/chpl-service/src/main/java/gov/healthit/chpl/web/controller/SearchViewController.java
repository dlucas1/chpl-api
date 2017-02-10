package gov.healthit.chpl.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.healthit.chpl.dao.CertifiedProductSearchResultDAO;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.domain.CriteriaSpecificDescriptiveModel;
import gov.healthit.chpl.domain.KeyValueModel;
import gov.healthit.chpl.domain.KeyValueModelStatuses;
import gov.healthit.chpl.domain.PopulateSearchOptions;
import gov.healthit.chpl.domain.SearchOption;
import gov.healthit.chpl.domain.SearchRequest;
import gov.healthit.chpl.domain.SearchResponse;
import gov.healthit.chpl.domain.SurveillanceRequirementOptions;
import gov.healthit.chpl.domain.search.BasicSearchResponse;
import gov.healthit.chpl.entity.CertificationStatusType;
import gov.healthit.chpl.manager.CertifiedProductDetailsManager;
import gov.healthit.chpl.manager.CertifiedProductSearchManager;
import gov.healthit.chpl.manager.DeveloperManager;
import gov.healthit.chpl.manager.SearchMenuManager;
import gov.healthit.chpl.web.controller.results.DecertifiedDeveloperResults;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class SearchViewController {
	
	@Autowired Environment env;
	
	@Autowired
	private SearchMenuManager searchMenuManager;
	
	@Autowired
	private CertifiedProductSearchManager certifiedProductSearchManager;
	
	@Autowired
	private CertifiedProductDetailsManager certifiedProductDetailsManager;
	
	@Autowired
	private DeveloperManager developerManager;
	
	@Autowired
	private CertifiedProductSearchResultDAO certifiedProductSearchResultDao;
	
	private static final Logger logger = LogManager.getLogger(SearchViewController.class);

	@ApiOperation(value="Get basic data about all certified products in the system.", 
			notes="")
	@RequestMapping(value="/certified_products", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody BasicSearchResponse getAllCertifiedProducts() {
		logger.info("Calling ChplCacheManager.search() from SearchViewController");
		BasicSearchResponse response = certifiedProductSearchManager.search();
		return response;
	}
	
	@ApiOperation(value="Get all data about a certified product.", 
			notes="")
	@RequestMapping(value="/certified_product_details", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody CertifiedProductSearchDetails getCertifiedProductDetails(@RequestParam("productId") Long id) throws EntityRetrievalException{
		
		CertifiedProductSearchDetails product = certifiedProductDetailsManager.getCertifiedProductDetails(id);
		return product;
	}

	@ApiOperation(value="Download the entire CHPL as XML.", 
			notes="Once per day, the entire certified product listing is written out to an XML "
					+ "file on the CHPL servers. This method allows any user to download that XML file. "
					+ "It is formatted in such a way that users may import it into Microsoft Excel or any other XML "
					+ "tool of their choosing.")
	@RequestMapping(value="/download", method=RequestMethod.GET,
			produces="application/xml")
	public void download(@RequestParam(value="edition", required=false) String edition, 
			@RequestParam(value="format", defaultValue="xml", required=false) String format,
			HttpServletRequest request, HttpServletResponse response) throws IOException {	
		String downloadFileLocation = env.getProperty("downloadFolderPath");
		File downloadFile = new File(downloadFileLocation);
		if(!downloadFile.exists() || !downloadFile.canRead()) {
			response.getWriter().write("Cannot read download file at " + downloadFileLocation + ". File does not exist or cannot be read.");
			return;
		}
		
		if(downloadFile.isDirectory()) {
			//find the most recent file in the directory and use that
			File[] children = downloadFile.listFiles();
			if(children == null || children.length == 0) {
				response.getWriter().write("No files for download were found in directory " + downloadFileLocation);
				return;
			} else {
				if(!StringUtils.isEmpty(edition)) {
					//make sure it's a 4 character year
					edition = edition.trim();
					if(!edition.startsWith("20")) {
						edition = "20"+edition;
					}
				} else {
					edition = "all";
				}
				
				if(!StringUtils.isEmpty(format) && format.equalsIgnoreCase("csv")) {
					format = "csv";
				} else {
					format = "xml";
				}
				
				File newestFileWithFormat = null;
				for(int i = 0; i < children.length; i++) {
					
					if(children[i].getName().matches("^chpl-" + edition + "-.+\\." + format+"$")) {
						if(newestFileWithFormat == null) {
							newestFileWithFormat = children[i];
						} else {
							if(children[i].lastModified() > newestFileWithFormat.lastModified()) {
								newestFileWithFormat = children[i];
							}
						}
					}
				}
				if(newestFileWithFormat != null) {
					downloadFile = newestFileWithFormat;
				} else {
					response.getWriter().write("Downloadable files exist, but none were found for certification edition " + edition + " and format " + format);
					return;
				}
			}
		}
		
		logger.info("Downloading " + downloadFile.getName());
		
		FileInputStream inputStream = new FileInputStream(downloadFile);

		// set content attributes for the response
		response.setContentType("application/xml");
		response.setContentLength((int) downloadFile.length());
	 
		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
		response.setHeader(headerKey, headerValue);
	 
		// get output stream of the response
		OutputStream outStream = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead = -1;
	 
		// write bytes read from the input stream into the output stream
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}
	 
		inputStream.close();
		outStream.close();
	}
	
	@ApiOperation(value="Search the CHPL", 
			notes="If paging parameters are not specified, the first 20 records are returned by default.")
	@RequestMapping(value="/search", method=RequestMethod.GET,
			produces={"application/json; charset=utf-8", "application/xml"})
	public @ResponseBody SearchResponse simpleSearch(
			@RequestParam("searchTerm") String searchTerm, 
			@RequestParam(value = "pageNumber", required = false) Integer pageNumber, 
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "sortDescending", required = false) Boolean sortDescending
			) throws EntityRetrievalException {
		
		
		if (pageNumber == null){
			pageNumber = 0;
		}
		
		if (pageSize == null){
			pageSize = 20;
		}
		
		SearchRequest searchRequest = new SearchRequest();
		
		searchRequest.setSearchTerm(searchTerm);
		searchRequest.setPageNumber(pageNumber);
		searchRequest.setPageSize(pageSize);
		
		if (orderBy != null){
			searchRequest.setOrderBy(orderBy);
		}
		
		if (sortDescending != null){
			searchRequest.setSortDescending(sortDescending);
		}
		
		return certifiedProductSearchManager.search(searchRequest);
		
	}
	
	@ApiOperation(value="Advanced search for the CHPL", 
			notes="Search the CHPL by specifycing multiple fields of the data to search. "
					+ "If paging fields are not specified, the first 20 records are returned by default.")
	@RequestMapping(value="/search", method= RequestMethod.POST, 
			consumes= MediaType.APPLICATION_JSON_VALUE,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchResponse advancedSearch(
			@RequestBody SearchRequest searchFilters) throws InvalidArgumentsException {
		//check date params for format
		SimpleDateFormat format = new SimpleDateFormat(SearchRequest.CERTIFICATION_DATE_SEARCH_FORMAT);
		
		if(!StringUtils.isEmpty(searchFilters.getCertificationDateStart())) {
			try {
				format.parse(searchFilters.getCertificationDateStart());
			} catch(ParseException ex) {
				logger.error("Could not parse " + searchFilters.getCertificationDateStart() + " as date in the format " + SearchRequest.CERTIFICATION_DATE_SEARCH_FORMAT);
				throw new InvalidArgumentsException("Certification Date format expected is " + SearchRequest.CERTIFICATION_DATE_SEARCH_FORMAT + " Cannot parse " + searchFilters.getCertificationDateStart());
			}
		}
		if(!StringUtils.isEmpty(searchFilters.getCertificationDateEnd())) {
			try {
				format.parse(searchFilters.getCertificationDateEnd());
			} catch(ParseException ex) {
				logger.error("Could not parse " + searchFilters.getCertificationDateEnd() + " as date in the format " + SearchRequest.CERTIFICATION_DATE_SEARCH_FORMAT);
				throw new InvalidArgumentsException("Certification Date format expected is " + SearchRequest.CERTIFICATION_DATE_SEARCH_FORMAT + " Cannot parse " + searchFilters.getCertificationDateEnd());
			}
		}
		
		return certifiedProductSearchManager.search(searchFilters);
	}
	
	@ApiOperation(value="Get all possible classifications in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/classification_types", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody Set<KeyValueModel> getClassificationNames() {
		return searchMenuManager.getClassificationNames();
	}
	
	@ApiOperation(value="Get all possible certificaiton editions in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/certification_editions", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody Set<KeyValueModel> getEditionNames() {
		return searchMenuManager.getEditionNames(false);
	}
	
	@ApiOperation(value="Get all possible certification statuses in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/certification_statuses", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody Set<KeyValueModel> getCertificationStatuses() {
		return searchMenuManager.getCertificationStatuses();
	}
	
	@ApiOperation(value="Get all possible practice types in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/practice_types", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody Set<KeyValueModel> getPracticeTypeNames() {
		return searchMenuManager.getPracticeTypeNames();
	}
	
	@ApiOperation(value="Get all possible product names in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/products", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody Set<KeyValueModelStatuses> getProductNames() {
		return searchMenuManager.getProductNames();
	}
	
	@ApiOperation(value="Get all possible developer names in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/developers", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody Set<KeyValueModelStatuses> getDeveloperNames() {
		return searchMenuManager.getDeveloperNames();
	}
	
	@ApiOperation(value="Get all possible ACBs in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/certification_bodies", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody Set<KeyValueModel> getCertBodyNames() {
		return searchMenuManager.getCertBodyNames();
	}
	
	@ApiOperation(value="Get all possible education types in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/education_types", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getEducationTypes() {
		Set<KeyValueModel> data = searchMenuManager.getEducationTypes();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible test participant age ranges in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/age_ranges", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getAgeRanges() {
		Set<KeyValueModel> data = searchMenuManager.getAgeRanges();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible test functionality options in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/test_functionality", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getTestFunctionality() {
		Set<KeyValueModel> data = searchMenuManager.getTestFunctionality();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible test tool options in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/test_tools", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getTestTools() {
		Set<KeyValueModel> data = searchMenuManager.getTestTools();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible test standard options in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/test_standards", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getTestStandards() {
		Set<KeyValueModel> data = searchMenuManager.getTestStandards();
		SearchOption result = new SearchOption();
		result.setExpandable(true);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible qms standard options in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/qms_standards", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getQmsStandards() {
		Set<KeyValueModel> data = searchMenuManager.getQmsStandards();
		SearchOption result = new SearchOption();
		result.setExpandable(true);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible targeted user options in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/targeted_users", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getTargetedUsers() {
		Set<KeyValueModel> data = searchMenuManager.getTargetedUesrs();
		SearchOption result = new SearchOption();
		result.setExpandable(true);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible UCD process options in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/ucd_processes", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getUcdProcesses() {
		Set<KeyValueModel> data = searchMenuManager.getUcdProcesses();
		SearchOption result = new SearchOption();
		result.setExpandable(true);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible accessibility standard options in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/accessibility_standards", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getAccessibilityStandards() {
		Set<KeyValueModel> data = searchMenuManager.getAccessibilityStandards();
		SearchOption result = new SearchOption();
		result.setExpandable(true);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible macra measure options in the CHPL", 
			notes="This is useful for knowing what values one might possibly search for.")
	@RequestMapping(value="/data/macra_measures", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchOption getMacraMeasures() {
		Set<CriteriaSpecificDescriptiveModel> data = searchMenuManager.getMacraMeasures();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible developer status options in the CHPL")
	@RequestMapping(value="/data/developer_statuses", method=RequestMethod.GET,
				produces = "application/json; charset=utf-8")
	public @ResponseBody SearchOption getDeveloperStatuses() {
		Set<KeyValueModel> data = searchMenuManager.getDeveloperStatuses();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible surveillance type options in the CHPL")
	@RequestMapping(value="/data/surveillance_types", method=RequestMethod.GET,
				produces = "application/json; charset=utf-8")
	public @ResponseBody SearchOption getSurveillanceTypes() {
		Set<KeyValueModel> data = searchMenuManager.getSurveillanceTypes();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible surveillance result type options in the CHPL")
	@RequestMapping(value="/data/surveillance_result_types", method=RequestMethod.GET,
				produces = "application/json; charset=utf-8")
	public @ResponseBody SearchOption getSurveillanceResultTypes() {
		Set<KeyValueModel> data = searchMenuManager.getSurveillanceResultTypes();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible surveillance requirement type options in the CHPL")
	@RequestMapping(value="/data/surveillance_requirement_types", method=RequestMethod.GET,
				produces = "application/json; charset=utf-8")
	public @ResponseBody SearchOption getSurveillanceRequirementTypes() {
		Set<KeyValueModel> data = searchMenuManager.getSurveillanceRequirementTypes();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible surveillance requirement options in the CHPL")
	@RequestMapping(value="/data/surveillance_requirements", method=RequestMethod.GET,
				produces = "application/json; charset=utf-8")
	public @ResponseBody SurveillanceRequirementOptions getSurveillanceRequirementOptions() {
		SurveillanceRequirementOptions data = searchMenuManager.getSurveillanceRequirementOptions();
		return data;
	}
	
	@ApiOperation(value="Get all possible nonconformity status type options in the CHPL")
	@RequestMapping(value="/data/nonconformity_status_types", method=RequestMethod.GET,
				produces = "application/json; charset=utf-8")
	public @ResponseBody SearchOption getNonconformityStatusTypes() {
		Set<KeyValueModel> data = searchMenuManager.getNonconformityStatusTypes();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all possible nonconformity type options in the CHPL")
	@RequestMapping(value="/data/nonconformity_types", method=RequestMethod.GET,
				produces = "application/json; charset=utf-8")
	public @ResponseBody SearchOption getNonconformityTypeOptions() {
		Set<KeyValueModel> data = searchMenuManager.getNonconformityTypeOptions();
		SearchOption result = new SearchOption();
		result.setExpandable(false);
		result.setData(data);
		return result;
	}
	
	@ApiOperation(value="Get all search options in the CHPL", 
			notes="This returns all of the other /data/{something} results in one single response.")
	@RequestMapping(value="/data/search_options", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody PopulateSearchOptions getPopulateSearchData(
			@RequestParam(value = "simple", required = false) Boolean simple
			) throws EntityRetrievalException {
		if (simple == null){
			simple = false;
		}
		
		PopulateSearchOptions searchOptions = new PopulateSearchOptions();
		searchOptions.setCertBodyNames(searchMenuManager.getCertBodyNames());
		searchOptions.setEditions(searchMenuManager.getEditionNames(simple));
		searchOptions.setCertificationStatuses(searchMenuManager.getCertificationStatuses());
		searchOptions.setPracticeTypeNames(searchMenuManager.getPracticeTypeNames());
		searchOptions.setProductClassifications(searchMenuManager.getClassificationNames());
		searchOptions.setProductNames(searchMenuManager.getProductNames());
		searchOptions.setDeveloperNames(searchMenuManager.getDeveloperNames());
		searchOptions.setCqmCriterionNumbers(searchMenuManager.getCQMCriterionNumbers(simple));
		searchOptions.setCertificationCriterionNumbers(searchMenuManager.getCertificationCriterionNumbers(simple));
		
		return searchOptions;
	}
	
	@ApiOperation(value="Get all developer decertifications in the CHPL", 
			notes="This returns all decertified developers.")
	@RequestMapping(value="/decertifications/developers", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody DecertifiedDeveloperResults getDecertifiedDevelopers() throws EntityRetrievalException {
		DecertifiedDeveloperResults ddr = developerManager.getDecertifiedDevelopers();
		return ddr;
	}
	
	@ApiOperation(value="Get all decertified certified products in the CHPL", 
			notes="Returns all decertified certified products, their decertified statuses, and the total count of decertified certified products as the recordCount.")
	@RequestMapping(value="/decertifications/certified_products", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchResponse getDecertifiedCertifiedProducts (
			@RequestParam(value = "pageNumber", required = false) Integer pageNumber, 
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "sortDescending", required = false) Boolean sortDescending) throws EntityRetrievalException {
		SearchResponse resp = new SearchResponse();
		
		if (pageNumber == null){
			pageNumber = 0;
		}
		
		SearchRequest searchRequest = new SearchRequest();
		List<String> allowedCertificationStatuses = new ArrayList<String>();
		allowedCertificationStatuses.add(String.valueOf(CertificationStatusType.WithdrawnByAcb));
		allowedCertificationStatuses.add(String.valueOf(CertificationStatusType.WithdrawnByDeveloperUnderReview));
		allowedCertificationStatuses.add(String.valueOf(CertificationStatusType.TerminatedByOnc));
		
		searchRequest.setCertificationStatuses(allowedCertificationStatuses);
		
		searchRequest.setPageNumber(pageNumber);
		if(pageSize == null){
			searchRequest.setPageSize(certifiedProductSearchResultDao.countMultiFilterSearchResults(searchRequest).intValue());
		}
		
		if (orderBy != null){
			searchRequest.setOrderBy(orderBy);
		}
		
		if (sortDescending != null){
			searchRequest.setSortDescending(sortDescending);
		}
		
		resp = certifiedProductSearchManager.search(searchRequest);
		
		return resp;
	}
	
	@ApiOperation(value="Get decertified certified products in the CHPL with inactive certificates", 
			notes="Returns only decertified certified products with inactive certificates. "
					+ "Includes their decertified statuses and the total count of decertified certified products as the recordCount.")
	@RequestMapping(value="/decertifications/inactive_certificates", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody SearchResponse getDecertifiedInactiveCertificateCertifiedProducts (
			@RequestParam(value = "pageNumber", required = false) Integer pageNumber, 
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "sortDescending", required = false) Boolean sortDescending) throws EntityRetrievalException {
		SearchResponse resp = new SearchResponse();
		
		if (pageNumber == null){
			pageNumber = 0;
		}
		
		SearchRequest searchRequest = new SearchRequest();
		List<String> allowedCertificationStatuses = new ArrayList<String>();
		allowedCertificationStatuses.add(String.valueOf(CertificationStatusType.WithdrawnByDeveloper));
		
		searchRequest.setCertificationStatuses(allowedCertificationStatuses);
		
		searchRequest.setPageNumber(pageNumber);
		if(pageSize == null){
			searchRequest.setPageSize(certifiedProductSearchResultDao.countMultiFilterSearchResults(searchRequest).intValue());
		}
		
		if (orderBy != null){
			searchRequest.setOrderBy(orderBy);
		}
		
		if (sortDescending != null){
			searchRequest.setSortDescending(sortDescending);
		}
		
		resp = certifiedProductSearchManager.search(searchRequest);
		
		return resp;
	}
	
}
