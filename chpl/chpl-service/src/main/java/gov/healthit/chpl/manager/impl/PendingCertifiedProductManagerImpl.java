package gov.healthit.chpl.manager.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.auth.dao.UserDAO;
import gov.healthit.chpl.auth.manager.UserManager;
import gov.healthit.chpl.caching.CacheNames;
import gov.healthit.chpl.dao.CQMCriterionDAO;
import gov.healthit.chpl.dao.CertificationStatusDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dao.MacraMeasureDAO;
import gov.healthit.chpl.dao.PendingCertifiedProductDAO;
import gov.healthit.chpl.domain.ActivityConcept;
import gov.healthit.chpl.domain.CQMCriterion;
import gov.healthit.chpl.domain.CQMResultDetails;
import gov.healthit.chpl.domain.CertificationResult;
import gov.healthit.chpl.domain.MacraMeasure;
import gov.healthit.chpl.domain.PendingCertifiedProductDetails;
import gov.healthit.chpl.dto.CQMCriterionDTO;
import gov.healthit.chpl.dto.CertificationBodyDTO;
import gov.healthit.chpl.dto.MacraMeasureDTO;
import gov.healthit.chpl.dto.PendingCertificationResultDTO;
import gov.healthit.chpl.dto.PendingCertifiedProductDTO;
import gov.healthit.chpl.entity.PendingCertifiedProductEntity;
import gov.healthit.chpl.manager.ActivityManager;
import gov.healthit.chpl.manager.CertificationBodyManager;
import gov.healthit.chpl.manager.PendingCertifiedProductManager;
import gov.healthit.chpl.upload.certifiedProduct.CertifiedProductUploadHandlerFactory;
import gov.healthit.chpl.util.CertificationResultRules;
import gov.healthit.chpl.validation.certifiedProduct.CertifiedProductValidator;
import gov.healthit.chpl.validation.certifiedProduct.CertifiedProductValidatorFactory;

@Service
public class PendingCertifiedProductManagerImpl implements PendingCertifiedProductManager {
	private static final Logger logger = LogManager.getLogger(PendingCertifiedProductManagerImpl.class);

	@Autowired private CertificationResultRules certRules;
	@Autowired CertifiedProductUploadHandlerFactory uploadHandlerFactory;
	@Autowired CertifiedProductValidatorFactory validatorFactory;
	
	@Autowired PendingCertifiedProductDAO pcpDao;
	@Autowired CertificationStatusDAO statusDao;
	@Autowired CertificationBodyManager acbManager;
	@Autowired UserManager userManager;
	@Autowired UserDAO userDAO;
	@Autowired private CQMCriterionDAO cqmCriterionDAO;
	@Autowired private MacraMeasureDAO macraDao;
	private List<CQMCriterion> cqmCriteria = new ArrayList<CQMCriterion>();
	private List<MacraMeasure> macraMeasures = new ArrayList<MacraMeasure>();
	
	@Autowired
	private ActivityManager activityManager;
	
	@PostConstruct
	public void setup() {
		loadCQMCriteria();
		loadCriteriaMacraMeasures();
	}

	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ACB_ADMIN') or hasRole('ROLE_ACB_STAFF')")
	public PendingCertifiedProductDetails getById(List<CertificationBodyDTO> userAcbs, Long id) 
			throws EntityNotFoundException, EntityRetrievalException, AccessDeniedException {
		
		PendingCertifiedProductDTO pendingCp = pcpDao.findById(id);
		if(pendingCp == null) {
			throw new EntityNotFoundException("Could not find pending certified product with id " + id);
		}
		boolean userHasAcbPermissions = false;
		for(CertificationBodyDTO acb : userAcbs) {
			if(acb.getId() != null && 
					pendingCp.getCertificationBodyId() != null && 
					acb.getId().longValue() == pendingCp.getCertificationBodyId().longValue()) {
				userHasAcbPermissions = true;
			}
		}
		
		if(!userHasAcbPermissions) {
			throw new AccessDeniedException("Permission denied on ACB " + pendingCp.getCertificationBodyId() + " for user " + Util.getCurrentUser().getSubjectName());
		}
		
		//the user has permission so continue getting the pending cp
		updateCertResults(pendingCp);
		validate(pendingCp);

		PendingCertifiedProductDetails pcpDetails = new PendingCertifiedProductDetails(pendingCp);
		addAllVersionsToCmsCriterion(pcpDetails);
		addAllMeasuresToCertificationCriteria(pcpDetails);
		
		return pcpDetails;
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or "
			+ "((hasRole('ROLE_ACB_ADMIN') or hasRole('ROLE_ACB_STAFF')) and "
			+ "hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin))")
	public PendingCertifiedProductDetails getById(Long acbId, Long id) throws EntityRetrievalException {
		PendingCertifiedProductDTO dto = pcpDao.findById(id);
		updateCertResults(dto);
		validate(dto);

		PendingCertifiedProductDetails pcpDetails = new PendingCertifiedProductDetails(dto);
		addAllVersionsToCmsCriterion(pcpDetails);
		addAllMeasuresToCertificationCriteria(pcpDetails);
		
		return pcpDetails;
	}
	
	@Override
	@Transactional (readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or "
			+ "((hasRole('ROLE_ACB_ADMIN') or hasRole('ROLE_ACB_STAFF')) and "
			+ "hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin))")
	public List<PendingCertifiedProductDTO> getPendingCertifiedProductsByAcb(Long acbId) {
		List<PendingCertifiedProductDTO> products = pcpDao.findByAcbId(acbId);
		validate(products);
		
		return products;
	}
	
	@Override
	@Transactional
	@CacheEvict(value = {CacheNames.FIND_BY_ACB_ID}, allEntries=true)
	@PreAuthorize("(hasRole('ROLE_ACB_STAFF') or hasRole('ROLE_ACB_ADMIN')) "
			+ "and hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin)")
	public PendingCertifiedProductDTO createOrReplace(Long acbId, PendingCertifiedProductEntity toCreate) 
		throws EntityRetrievalException, EntityCreationException, JsonProcessingException {
		Long existingId = pcpDao.findIdByOncId(toCreate.getUniqueId());
		if(existingId != null) {
			pcpDao.delete(existingId);
		}
		
		//insert the record
		PendingCertifiedProductDTO pendingCpDto = pcpDao.create(toCreate);
		updateCertResults(pendingCpDto);
		validate(pendingCpDto);
		
		String activityMsg = "Certified product "+pendingCpDto.getProductName()+" is pending.";
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_PENDING_CERTIFIED_PRODUCT, pendingCpDto.getId(), activityMsg, null, pendingCpDto);
		
		return pendingCpDto;
	}
	
	@Override
	@Transactional
	@CacheEvict(value = {CacheNames.FIND_BY_ACB_ID}, allEntries=true)
	@PreAuthorize("hasRole('ROLE_ACB_STAFF') or hasRole('ROLE_ACB_ADMIN')")
	public void deletePendingCertifiedProduct(List<CertificationBodyDTO> userAcbs, Long pendingProductId) 
			throws EntityRetrievalException, EntityNotFoundException, EntityCreationException, 
			AccessDeniedException, JsonProcessingException {
		
		PendingCertifiedProductDTO pendingCp = pcpDao.findById(pendingProductId);
		if(pendingCp == null) {
			throw new EntityNotFoundException("Could not find pending certified product with id " + pendingProductId);
		}
		boolean userHasAcbPermissions = false;
		for(CertificationBodyDTO acb : userAcbs) {
			if(acb.getId() != null && 
					pendingCp.getCertificationBodyId() != null && 
					acb.getId().longValue() == pendingCp.getCertificationBodyId().longValue()) {
				userHasAcbPermissions = true;
			}
		}
		
		if(!userHasAcbPermissions) {
			throw new AccessDeniedException("Permission denied on ACB " + pendingCp.getCertificationBodyId() + " for user " + Util.getCurrentUser().getSubjectName());
		}
		
		pcpDao.delete(pendingProductId);
		
		String activityMsg = "Pending certified product "+pendingCp.getProductName()+" has been rejected.";
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_PENDING_CERTIFIED_PRODUCT, pendingCp.getId(), activityMsg, pendingCp, null);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = {CacheNames.FIND_BY_ACB_ID}, allEntries=true)
	@PreAuthorize("(hasRole('ROLE_ACB_STAFF') or hasRole('ROLE_ACB_ADMIN')) "
			+ "and hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin)")
	public void deletePendingCertifiedProduct(Long acbId, Long pendingProductId) throws EntityRetrievalException, JsonProcessingException, EntityCreationException {
		
		PendingCertifiedProductDTO pendingCpDto = pcpDao.findById(pendingProductId);
		pcpDao.delete(pendingProductId);
		
		String activityMsg = "Pending certified product "+pendingCpDto.getProductName()+" has been rejected.";
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_PENDING_CERTIFIED_PRODUCT, pendingCpDto.getId(), activityMsg, pendingCpDto, null);

		
	}
	
	@Override
	@Transactional
	@CacheEvict(value = {CacheNames.FIND_BY_ACB_ID}, allEntries=true)
	@PreAuthorize("(hasRole('ROLE_ACB_STAFF') or hasRole('ROLE_ACB_ADMIN')) "
			+ "and hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin)")
	public void confirm(Long acbId, Long pendingProductId) throws EntityRetrievalException, JsonProcessingException, EntityCreationException {
		
		PendingCertifiedProductDTO pendingCpDto = pcpDao.findById(pendingProductId);
		pcpDao.delete(pendingProductId);
		
		String activityMsg = "Pending certified product "+pendingCpDto.getProductName()+" has been confirmed.";
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_PENDING_CERTIFIED_PRODUCT, pendingCpDto.getId(), activityMsg, pendingCpDto, pendingCpDto);
		
	}
	
	private void updateCertResults(PendingCertifiedProductDTO dto) {
		List<PendingCertifiedProductDTO> products = new ArrayList<PendingCertifiedProductDTO>();
		products.add(dto);
		updateCertResults(products);
	}
	
	private void updateCertResults(List<PendingCertifiedProductDTO> products) {
		for(PendingCertifiedProductDTO product : products) {
			for(PendingCertificationResultDTO certResult : product.getCertificationCriterion()) {
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.GAP)) {
					certResult.setGap(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.G1_SUCCESS)) {
					certResult.setG1Success(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.G2_SUCCESS)) {
					certResult.setG2Success(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.API_DOCUMENTATION)) {
					certResult.setApiDocumentation(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.PRIVACY_SECURITY)) {
					certResult.setPrivacySecurityFramework(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.SED)) {
					certResult.setSed(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.UCD_FIELDS)) {
					certResult.setUcdProcesses(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.ADDITIONAL_SOFTWARE)) {
					certResult.setAdditionalSoftware(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.FUNCTIONALITY_TESTED)) {
					certResult.setTestFunctionality(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.STANDARDS_TESTED)) {
					certResult.setTestStandards(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.TEST_DATA)) {
					certResult.setTestData(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.TEST_PROCEDURE_VERSION)) {
					certResult.setTestProcedures(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.TEST_TOOLS_USED)) {
					certResult.setTestTools(null);
				}
				if(!certRules.hasCertOption(certResult.getNumber(), CertificationResultRules.TEST_TASK)) {
					certResult.setTestTasks(null);
				}
			}
		}
	}
	
	private void loadCriteriaMacraMeasures() {
		List<MacraMeasureDTO> dtos = macraDao.findAll();
		for(MacraMeasureDTO dto : dtos) {
			MacraMeasure measure = new MacraMeasure(dto);
			macraMeasures.add(measure);
		}
	}
	
	private void loadCQMCriteria() {		
		List<CQMCriterionDTO> dtos = cqmCriterionDAO.findAll();
		for (CQMCriterionDTO dto: dtos) {
			CQMCriterion criterion = new CQMCriterion();
			criterion.setCmsId(dto.getCmsId());
			criterion.setCqmCriterionTypeId(dto.getCqmCriterionTypeId());
			criterion.setCqmDomain(dto.getCqmDomain());
			criterion.setCqmVersionId(dto.getCqmVersionId());
			criterion.setCqmVersion(dto.getCqmVersion());
			criterion.setCriterionId(dto.getId());
			criterion.setDescription(dto.getDescription());
			criterion.setNqfNumber(dto.getNqfNumber());
			criterion.setNumber(dto.getNumber());
			criterion.setTitle(dto.getTitle());
			cqmCriteria.add(criterion);
		}
	}
	
	private List<CQMCriterion> getAvailableCQMVersions(){
		List<CQMCriterion> criteria = new ArrayList<CQMCriterion>();
		
		for (CQMCriterion criterion : cqmCriteria){
			if(!StringUtils.isEmpty(criterion.getCmsId()) && criterion.getCmsId().startsWith("CMS")) {
				criteria.add(criterion);
			}
		}
		return criteria;
	}
	
	private void validate(List<PendingCertifiedProductDTO> products) {
		for(PendingCertifiedProductDTO dto : products) {
			CertifiedProductValidator validator = validatorFactory.getValidator(dto);
			if(validator != null) {
				validator.validate(dto);
			}
		}
	}
	
	private void validate(PendingCertifiedProductDTO... products) {
		for(PendingCertifiedProductDTO dto : products) {
			CertifiedProductValidator validator = validatorFactory.getValidator(dto);
			if(validator != null) {
				validator.validate(dto);
			}
		}
	}
	
	public void addAllVersionsToCmsCriterion(PendingCertifiedProductDetails pcpDetails) {
		//now add allVersions for CMSs
		String certificationEdition = pcpDetails.getCertificationEdition().get("name").toString();
		if (!certificationEdition.startsWith("2011")){
			List<CQMCriterion> cqms = getAvailableCQMVersions();
			for(CQMCriterion cqm : cqms) {
				boolean cqmExists = false;
				for(CQMResultDetails details : pcpDetails.getCqmResults()) {
					if(cqm.getCmsId().equals(details.getCmsId())) {
						cqmExists = true;
						details.getAllVersions().add(cqm.getCqmVersion());
					}
				}
				if(!cqmExists) {
					CQMResultDetails result = new CQMResultDetails();
					result.setCmsId(cqm.getCmsId());
					result.setNqfNumber(cqm.getNqfNumber());
					result.setNumber(cqm.getNumber());
					result.setTitle(cqm.getTitle());
					result.setDescription(cqm.getDescription());
					result.setSuccess(Boolean.FALSE);
					result.getAllVersions().add(cqm.getCqmVersion());
					result.setTypeId(cqm.getCqmCriterionTypeId());
					pcpDetails.getCqmResults().add(result);
				}
			}
		}
	}
	
	@Override
	public void addAllMeasuresToCertificationCriteria(PendingCertifiedProductDetails pcpDetails) {
		//now add allMeasures for criteria
		for(CertificationResult cert : pcpDetails.getCertificationResults()) {
			for(MacraMeasure measure : macraMeasures) {
				if(measure.getCriteria().getNumber().equals(cert.getNumber())) {
					cert.getAllowedMacraMeasures().add(measure);
				}
			}
		}
	}
}
