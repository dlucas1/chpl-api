package gov.healthit.chpl.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.dao.CertifiedProductDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.ActivityConcept;
import gov.healthit.chpl.domain.CertificationResult;
import gov.healthit.chpl.domain.CertifiedProduct;
import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.domain.PendingCertifiedProductDetails;
import gov.healthit.chpl.dto.CertifiedProductDTO;
import gov.healthit.chpl.dto.CertifiedProductDetailsDTO;
import gov.healthit.chpl.dto.PendingCertifiedProductDTO;
import gov.healthit.chpl.entity.CertificationStatusType;
import gov.healthit.chpl.entity.PendingCertifiedProductEntity;
import gov.healthit.chpl.manager.ActivityManager;
import gov.healthit.chpl.manager.CertificationBodyManager;
import gov.healthit.chpl.manager.CertifiedProductDetailsManager;
import gov.healthit.chpl.manager.CertifiedProductManager;
import gov.healthit.chpl.manager.PendingCertifiedProductManager;
import gov.healthit.chpl.upload.certifiedProduct.CertifiedProductUploadHandler;
import gov.healthit.chpl.upload.certifiedProduct.CertifiedProductUploadHandlerFactory;
import gov.healthit.chpl.validation.certifiedProduct.CertifiedProductValidator;
import gov.healthit.chpl.validation.certifiedProduct.CertifiedProductValidatorFactory;
import gov.healthit.chpl.web.controller.results.MeaningfulUseUserResults;
import gov.healthit.chpl.web.controller.results.PendingCertifiedProductResults;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="certified-products")
@RestController
@RequestMapping("/certified_products")
public class CertifiedProductController {
	
	private static final Logger logger = LogManager.getLogger(CertifiedProductController.class);
	
	@Autowired CertifiedProductUploadHandlerFactory uploadHandlerFactory;
	@Autowired CertifiedProductDetailsManager cpdManager;
	@Autowired CertifiedProductManager cpManager;
	@Autowired PendingCertifiedProductManager pcpManager;
	@Autowired CertificationBodyManager acbManager;
	@Autowired ActivityManager activityManager;
	@Autowired CertifiedProductValidatorFactory validatorFactory;
	@Autowired CertifiedProductDAO cpDao;
	@Autowired MeaningfulUseController meaningfulUseController;

	@ApiOperation(value="List all certified products", 
			notes="Default behavior is to return all certified products in the system. "
					+ " The optional 'versionId' parameter filters the certified products to those"
					+ " assigned to that version. The 'editable' parameter will return only those"
					+ " certified products that the logged in user has permission to edit as "
					+ " determined by ACB roles and authorities. Not all information about "
					+ " every certified product is returned. Call the /details service for more information.")
	@RequestMapping(value="/", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody List<CertifiedProduct> getCertifiedProductsByVersion(
			@RequestParam(required=false) Long versionId, @RequestParam(required=false, defaultValue="false") boolean editable) {
		List<CertifiedProductDetailsDTO> certifiedProductList = null;
		
		if(versionId != null && versionId > 0) {
			if(editable) {
				certifiedProductList = cpManager.getByVersionWithEditPermission(versionId);
			} else {
				certifiedProductList = cpManager.getByVersion(versionId);
			}
		} else {
			if(editable) {
				certifiedProductList = cpManager.getAllWithEditPermission();
			} else {
				certifiedProductList = cpManager.getAll();
			}
		}
		
		List<CertifiedProduct> products= new ArrayList<CertifiedProduct>();
		if(certifiedProductList != null && certifiedProductList.size() > 0) {
			for(CertifiedProductDetailsDTO dto : certifiedProductList) {
				CertifiedProduct result = new CertifiedProduct(dto);
				products.add(result);
			}
		}
		return products;
	}
	
	@ApiOperation(value="Get all details for a specified certified product.", 
			notes="Returns all information in the CHPL related to the specified certified product.")
	@RequestMapping(value="/{certifiedProductId}/details", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody CertifiedProductSearchDetails getCertifiedProductById(@PathVariable("certifiedProductId") Long certifiedProductId) throws EntityRetrievalException {
		CertifiedProductSearchDetails certifiedProduct =
				cpdManager.getCertifiedProductDetails(certifiedProductId);
		CertifiedProductValidator validator = validatorFactory.getValidator(certifiedProduct);
		if(validator != null) {
			validator.validate(certifiedProduct);
		}
		
		return certifiedProduct;
	}
	
	@ApiOperation(value="Update an existing certified product.", 
			notes="Updates the certified product after first validating the request. The logged in"
					+ " user must have ROLE_ADMIN or ROLE_ACB_ADMIN and have administrative "
					+ " authority on the ACB that certified the product. If a different ACB is passed in"
					+ " as part of the request, an ownership change will take place and the logged in "
					+ " user must have ROLE_ADMIN.")
	@RequestMapping(value="/update", method=RequestMethod.POST,
			produces="application/json; charset=utf-8")
	public ResponseEntity<CertifiedProductSearchDetails> updateCertifiedProduct(@RequestBody(required=true) CertifiedProductSearchDetails updateRequest) 
		throws EntityCreationException, EntityRetrievalException, InvalidArgumentsException, 
		JsonProcessingException, ValidationException {
		
		//make sure the ui didn't send any error or warning messages back
		updateRequest.setErrorMessages(new HashSet<String>());
		updateRequest.setWarningMessages(new HashSet<String>());
		//validate
		CertifiedProductValidator validator = validatorFactory.getValidator(updateRequest);
		if(validator != null) {
			validator.validate(updateRequest);
		}
		
		CertifiedProductSearchDetails existingProduct = cpdManager.getCertifiedProductDetails(updateRequest.getId());
		
		//make sure the old and new certification statuses aren't ONC bans
		if(!existingProduct.getCertificationStatus().get("id").toString().equals(
				updateRequest.getCertificationStatus().get("id"))) {
			//if the status is to or from suspended by onc make sure the user has admin
			if((existingProduct.getCertificationStatus().get("name").toString().equals(CertificationStatusType.SuspendedByOnc.toString()) 
				|| updateRequest.getCertificationStatus().get("name").toString().equals(CertificationStatusType.SuspendedByOnc.toString())
				|| existingProduct.getCertificationStatus().get("name").toString().equals(CertificationStatusType.TerminatedByOnc.toString())
				|| updateRequest.getCertificationStatus().get("name").toString().equals(CertificationStatusType.TerminatedByOnc.toString()))
				&& !Util.isUserRoleAdmin()) {
				updateRequest.getErrorMessages().add("User " + Util.getUsername() 
					+ " does not have permission to change certification status of " 
					+ existingProduct.getChplProductNumber() + " from " 
					+ existingProduct.getCertificationStatus().get("name").toString() + " to " 
					+ updateRequest.getCertificationStatus().get("name").toString());
			}
		}
		
		//has the unique id changed? if so, make sure it is still unique
		if(!existingProduct.getChplProductNumber().equals(updateRequest.getChplProductNumber())) {
			try {
				boolean isDup = cpManager.chplIdExists(updateRequest.getChplProductNumber());
				if(isDup) {
					updateRequest.getErrorMessages().add("The CHPL Product Number has changed. The new CHPL Product Number " + updateRequest.getChplProductNumber() + " must be unique among all other certified products but one already exists with the same ID. Edit a part of the CHPL product number (e.g. the version code) to make it unique.");
				}
			} catch(EntityRetrievalException ex) {}
		}
		
		if(updateRequest.getErrorMessages() != null && updateRequest.getErrorMessages().size() > 0) {
			throw new ValidationException(updateRequest.getErrorMessages(), updateRequest.getWarningMessages());
		}
		
		Long acbId = new Long(existingProduct.getCertifyingBody().get("id").toString());
		Long newAcbId = new Long(updateRequest.getCertifyingBody().get("id").toString());
		
		//if the ACF owner is changed this is a separate action with different security
		if(newAcbId != null && acbId.longValue() != newAcbId.longValue()) {
			cpManager.changeOwnership(updateRequest.getId(), newAcbId);
			CertifiedProductSearchDetails changedProduct = cpdManager.getCertifiedProductDetails(updateRequest.getId());
			activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, existingProduct.getId(), "Changed ACB ownership.", existingProduct, changedProduct);
			existingProduct = changedProduct;
		}
		
		CertifiedProductDTO toUpdate = new CertifiedProductDTO();
		toUpdate.setId(updateRequest.getId());
		if(updateRequest.getTestingLab() != null && !StringUtils.isEmpty(updateRequest.getTestingLab().get("id"))) {
			toUpdate.setTestingLabId(new Long(updateRequest.getTestingLab().get("id").toString()));
		}
		toUpdate.setCertificationBodyId(newAcbId);
		if(updateRequest.getPracticeType() != null && updateRequest.getPracticeType().get("id") != null) {
			toUpdate.setPracticeTypeId(new Long(updateRequest.getPracticeType().get("id").toString()));
		}
		if(updateRequest.getClassificationType() != null && updateRequest.getClassificationType().get("id") != null) {
			toUpdate.setProductClassificationTypeId(new Long(updateRequest.getClassificationType().get("id").toString()));
		}
		toUpdate.setProductVersionId(new Long(updateRequest.getVersion().getVersionId()));
		toUpdate.setCertificationStatusId(new Long(updateRequest.getCertificationStatus().get("id").toString()));
		toUpdate.setCertificationEditionId(new Long(updateRequest.getCertificationEdition().get("id").toString()));
		toUpdate.setReportFileLocation(updateRequest.getReportFileLocation());
		toUpdate.setSedReportFileLocation(updateRequest.getSedReportFileLocation());
		toUpdate.setSedIntendedUserDescription(updateRequest.getSedIntendedUserDescription());
		toUpdate.setSedTestingEnd(updateRequest.getSedTestingEnd());
		toUpdate.setAcbCertificationId(updateRequest.getAcbCertificationId());
		toUpdate.setOtherAcb(updateRequest.getOtherAcb());
		toUpdate.setIcs(updateRequest.getIcs());
		toUpdate.setAccessibilityCertified(updateRequest.getAccessibilityCertified());
		toUpdate.setProductAdditionalSoftware(updateRequest.getProductAdditionalSoftware());
		
		toUpdate.setTransparencyAttestationUrl(updateRequest.getTransparencyAttestationUrl());
		
		//set the pieces of the unique id
		if(!StringUtils.isEmpty(updateRequest.getChplProductNumber())) {
			if(updateRequest.getChplProductNumber().startsWith("CHP-")) {
				toUpdate.setChplProductNumber(updateRequest.getChplProductNumber());
			} else {
				String chplProductId = updateRequest.getChplProductNumber();
				String[] chplProductIdComponents = chplProductId.split("\\.");
				if(chplProductIdComponents == null || chplProductIdComponents.length != 9) {
					throw new InvalidArgumentsException("CHPL Product Id " + chplProductId + " is not in a format recognized by the system.");
				} else {
					toUpdate.setProductCode(chplProductIdComponents[4]);
					toUpdate.setVersionCode(chplProductIdComponents[5]);
					toUpdate.setIcsCode(chplProductIdComponents[6]);
					toUpdate.setAdditionalSoftwareCode(chplProductIdComponents[7]);
					toUpdate.setCertifiedDateCode(chplProductIdComponents[8]);
				}
				
				if(updateRequest.getCertificationDate() != null) {
					Date certDate = new Date(updateRequest.getCertificationDate());
					SimpleDateFormat dateCodeFormat = new SimpleDateFormat("yyMMdd");
					String dateCode = dateCodeFormat.format(certDate);
					toUpdate.setCertifiedDateCode(dateCode);
				}
				
				if(updateRequest.getCertificationResults() != null && updateRequest.getCertificationResults().size() > 0) {
					boolean hasSoftware = false;
					for(CertificationResult cert : updateRequest.getCertificationResults()) {
						if(cert.getAdditionalSoftware() != null && cert.getAdditionalSoftware().size() > 0) {
							hasSoftware = true;
						}
					}
					if(hasSoftware) {
						toUpdate.setAdditionalSoftwareCode("1");
					} else {
						toUpdate.setAdditionalSoftwareCode("0");
					}
				}
			}
		} 
		
		toUpdate = cpManager.update(acbId, toUpdate, updateRequest);
		
		//search for the product by id to get it with all the updates
		CertifiedProductSearchDetails changedProduct = cpdManager.getCertifiedProductDetails(updateRequest.getId());
		cpManager.checkSuspiciousActivity(existingProduct, changedProduct);
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, existingProduct.getId(), "Updated certified product " + changedProduct.getChplProductNumber() + ".", existingProduct, changedProduct);
		
		if(!changedProduct.getChplProductNumber().equals(existingProduct.getChplProductNumber())) {
			 HttpHeaders responseHeaders = new HttpHeaders();
			 responseHeaders.set("CHPL-Id-Changed", existingProduct.getChplProductNumber());
			return new ResponseEntity<CertifiedProductSearchDetails>(changedProduct, responseHeaders, HttpStatus.OK);
		}
		return new ResponseEntity<CertifiedProductSearchDetails>(changedProduct, HttpStatus.OK);
	}
	
	@ApiOperation(value="List pending certified products.", 
			notes="Pending certified products are created via CSV file upload and are left in the 'pending' state "
					+ " until validated and approved by an appropriate ACB administrator.")
	@RequestMapping(value="/pending", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody PendingCertifiedProductResults getPendingCertifiedProducts() throws EntityRetrievalException {		
		List<PendingCertifiedProductDTO> allProductDtos = pcpManager.getPending();
				
		List<PendingCertifiedProductDetails> result = new ArrayList<PendingCertifiedProductDetails>();
		for(PendingCertifiedProductDTO product : allProductDtos) {
			PendingCertifiedProductDetails pcpDetails = new PendingCertifiedProductDetails(product);
			pcpManager.addAllVersionsToCmsCriterion(pcpDetails);
			pcpManager.addAllMeasuresToCertificationCriteria(pcpDetails);
			result.add(pcpDetails);
		}
		PendingCertifiedProductResults results = new PendingCertifiedProductResults();
		results.getPendingCertifiedProducts().addAll(result);
		return results;
	}
	
	@ApiOperation(value="List a specific pending certified product.", 
			notes="")
	@RequestMapping(value="/pending/{pcpId}", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody PendingCertifiedProductDetails getPendingCertifiedProductById(@PathVariable("pcpId") Long pcpId) throws EntityRetrievalException {
		PendingCertifiedProductDetails details = pcpManager.getById(pcpId);	
		return details;
	}
	
	@ApiOperation(value="Reject a pending certified product.", 
			notes="Essentially deletes a pending certified product. ROLE_ACB_ADMIN, ROLE_ACB_STAFF "
					+ " and administrative authority on the ACB is required.")
	@RequestMapping(value="/pending/{pcpId}/reject", method=RequestMethod.POST,
			produces="application/json; charset=utf-8")
	public @ResponseBody String rejectPendingCertifiedProducts(@PathVariable("pcpId") Long id) throws EntityRetrievalException, JsonProcessingException, EntityCreationException {
		pcpManager.reject(id);
		return "{\"success\" : true }";
	}
	
	@ApiOperation(value="Confirm a pending certified product.", 
			notes="Creates a new certified product in the system based on all of the information "
					+ " passed in on the request. This information may differ from what was previously "
					+ " entered for the pending certified product during upload. It will first be validated "
					+ " to check for errors, then a new certified product is created, and the old pending certified"
					+ " product will be removed. ROLE_ACB_ADMIN or ROLE_ACB_STAFF "
					+ " and administrative authority on the ACB is required.")
	@RequestMapping(value="/pending/confirm", method=RequestMethod.POST,
			produces="application/json; charset=utf-8")
	public synchronized @ResponseBody CertifiedProductSearchDetails confirmPendingCertifiedProduct(@RequestBody(required = true) PendingCertifiedProductDetails pendingCp) 
		throws InvalidArgumentsException, ValidationException, EntityCreationException, EntityRetrievalException, JsonProcessingException {
		
		String acbIdStr = pendingCp.getCertifyingBody().get("id").toString();
		if(StringUtils.isEmpty(acbIdStr)) {
			throw new InvalidArgumentsException("An ACB ID must be supplied in the request body");
		}
		
		PendingCertifiedProductDTO pcpDto = new PendingCertifiedProductDTO(pendingCp);
		CertifiedProductValidator validator = validatorFactory.getValidator(pcpDto);
		if(validator != null) {
			validator.validate(pcpDto);
		}
		if(pcpDto.getErrorMessages() != null && pcpDto.getErrorMessages().size() > 0) {
			throw new ValidationException(pcpDto.getErrorMessages(), pcpDto.getWarningMessages());
		}
		
		Long acbId = new Long(acbIdStr);
		CertifiedProductDTO createdProduct = cpManager.createFromPending(acbId, pcpDto);
		pcpManager.confirm(pendingCp.getId());
		
		CertifiedProductSearchDetails result = cpdManager.getCertifiedProductDetails(createdProduct.getId());
		return result;
	}
	
	@ApiOperation(value="DEPRECATED. Upload a file to update the number of meaningful use users for each CHPL Product Number", 
			notes="Accepts a CSV file with chpl_product_number and num_meaningful_use_users to update the number of meaningful use users for each CHPL Product Number."
					+ " The user uploading the file must have ROLE_ADMIN or ROLE_ONC_STAFF ")
	@RequestMapping(value="/meaningful_use_users/upload", method=RequestMethod.POST,
			produces="application/json; charset=utf-8") 
	@Deprecated
	public @ResponseBody MeaningfulUseUserResults uploadMeaningfulUseUsers(@RequestParam("file") MultipartFile file) throws ValidationException, MaxUploadSizeExceededException {
		return meaningfulUseController.uploadMeaningfulUseUsers(file);
	}
	
	@ApiOperation(value="Upload a file with certified products", 
			notes="Accepts a CSV file with very specific fields to create pending certified products. "
					+ " The user uploading the file must have ROLE_ACB_ADMIN or ROLE_ACB_STAFF "
					+ " and administrative authority on the ACB(s) specified in the file.")
	@RequestMapping(value="/upload", method=RequestMethod.POST,
			produces="application/json; charset=utf-8") 
	public @ResponseBody PendingCertifiedProductResults upload(@RequestParam("file") MultipartFile file) throws 
			ValidationException, MaxUploadSizeExceededException {
		if (file.isEmpty()) {
			throw new ValidationException("You cannot upload an empty file!");
		}
		
		if(!file.getContentType().equalsIgnoreCase("text/csv") &&
				!file.getContentType().equalsIgnoreCase("application/vnd.ms-excel")) {
			throw new ValidationException("File must be a CSV document.");
		}
		
		List<PendingCertifiedProductDetails> uploadedProducts = new ArrayList<PendingCertifiedProductDetails>();
		
		BufferedReader reader = null;
		CSVParser parser = null;
		try {
			reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
			parser = new CSVParser(reader, CSVFormat.EXCEL);
			
			List<CSVRecord> records = parser.getRecords();
			if(records.size() <= 1) {
				throw new ValidationException("The file appears to have a header line with no other information. Please make sure there are at least two rows in the CSV file.");
			}
			
			Set<String> handlerErrors = new HashSet<String>();
			List<PendingCertifiedProductEntity> cpsToAdd = new ArrayList<PendingCertifiedProductEntity>();
			
			//parse the entire file into groups of records, one group per product
			CSVRecord heading = null; 
			Set<String> uniqueIdsFromFile = new HashSet<String>();
			Set<String> duplicateIdsFromFile = new HashSet<String>();
			List<CSVRecord> rows = new ArrayList<CSVRecord>();
			for(int i = 0; i < records.size(); i++) {
				CSVRecord currRecord = records.get(i);
				
				if(heading == null && !StringUtils.isEmpty(currRecord.get(1)) 
						&& currRecord.get(1).equals("RECORD_STATUS__C")) {
					//have to find the heading first
					heading = currRecord;
				} else if(heading != null) {
					if(!StringUtils.isEmpty(currRecord.get(0))) {
						String currUniqueId = currRecord.get(0);
						String currStatus = currRecord.get(1);
						
						if(currStatus.equalsIgnoreCase("NEW")) {
							if(!currUniqueId.contains("XXXX") && uniqueIdsFromFile.contains(currUniqueId)) {
								handlerErrors.add("Multiple products with unique id " + currUniqueId + " were found in the file.");
								duplicateIdsFromFile.add(currUniqueId);
							} else {
								uniqueIdsFromFile.add(currUniqueId);

								//parse the previous recordset
								if(rows.size() > 0) {
									try {
										CertifiedProductUploadHandler handler = uploadHandlerFactory.getHandler(heading, rows);
										PendingCertifiedProductEntity pendingCp = handler.handle();
										cpsToAdd.add(pendingCp);
									}
									catch(InvalidArgumentsException ex) {
										handlerErrors.add(ex.getMessage());
									}
								}
								rows.clear();
							}
						}
						
						if(!duplicateIdsFromFile.contains(currUniqueId)) {
							rows.add(currRecord);
						}
					}
				}
				
				//add the last object
				if(i == records.size()-1 && !rows.isEmpty()) {
					try {
						CertifiedProductUploadHandler handler = uploadHandlerFactory.getHandler(heading, rows);
						PendingCertifiedProductEntity pendingCp = handler.handle();
						cpsToAdd.add(pendingCp);
					}
					catch(InvalidArgumentsException ex) {
						handlerErrors.add(ex.getMessage());
					}
				}
			}
			
			if(handlerErrors.size() > 0) {
				throw new ValidationException(handlerErrors, null);
			}
			
			Set<String> allErrors = new HashSet<String>();
			for(PendingCertifiedProductEntity cpToAdd : cpsToAdd) {
				if(cpToAdd.getErrorMessages() != null && cpToAdd.getErrorMessages().size() > 0) {
					allErrors.addAll(cpToAdd.getErrorMessages());
				}
			}
			if(allErrors.size() > 0) {
				throw new ValidationException(allErrors, null);
			} else {
				for(PendingCertifiedProductEntity cpToAdd : cpsToAdd) {
					try {
						PendingCertifiedProductDTO pendingCpDto = pcpManager.createOrReplace(cpToAdd.getCertificationBodyId(), cpToAdd);
						PendingCertifiedProductDetails details = new PendingCertifiedProductDetails(pendingCpDto);
						uploadedProducts.add(details);
					} catch(EntityCreationException ex) {
						logger.error("Error creating pending certified product: " + cpToAdd.getUniqueId());
					} catch(EntityRetrievalException ex) {
						logger.error("Error retreiving pending certified product.", ex);
					}
				}				
			}
		} catch(IOException ioEx) {
			logger.error("Could not get input stream for uploaded file " + file.getName());			
			throw new ValidationException("Could not get input stream for uploaded file " + file.getName());
		} finally {
			 try { parser.close(); } catch(Exception ignore) {}
			try { reader.close(); } catch(Exception ignore) {}
		}
		
		PendingCertifiedProductResults results = new PendingCertifiedProductResults();
		results.getPendingCertifiedProducts().addAll(uploadedProducts);
		return results;
	}
}
