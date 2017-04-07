package gov.healthit.chpl.web.controller;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.Address;
import gov.healthit.chpl.domain.Contact;
import gov.healthit.chpl.domain.Developer;
import gov.healthit.chpl.domain.DeveloperStatusEvent;
import gov.healthit.chpl.domain.TransparencyAttestationMap;
import gov.healthit.chpl.domain.UpdateDevelopersRequest;
import gov.healthit.chpl.dto.AddressDTO;
import gov.healthit.chpl.dto.ContactDTO;
import gov.healthit.chpl.dto.DeveloperACBMapDTO;
import gov.healthit.chpl.dto.DeveloperDTO;
import gov.healthit.chpl.dto.DeveloperStatusDTO;
import gov.healthit.chpl.dto.DeveloperStatusEventDTO;
import gov.healthit.chpl.manager.CertifiedProductManager;
import gov.healthit.chpl.manager.DeveloperManager;
import gov.healthit.chpl.manager.ProductManager;
import gov.healthit.chpl.web.controller.results.DeveloperResults;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "developers")
@RestController
@RequestMapping("/developers")
public class DeveloperController {

	@Autowired DeveloperManager developerManager;
	@Autowired ProductManager productManager;
	@Autowired CertifiedProductManager cpManager;

	@ApiOperation(value="List all developers in the system.", notes="")
	@RequestMapping(value="/", method=RequestMethod.GET, produces="application/json; charset=utf-8")
	public @ResponseBody DeveloperResults getDevelopers(
			@RequestParam(value = "showDeleted", required=false, defaultValue="false") boolean showDeleted){
		List<DeveloperDTO> developerList = null;
		if(showDeleted) {
			developerList = developerManager.getAllIncludingDeleted();
		} else {
			developerList = developerManager.getAll();
		}

		List<Developer> developers = new ArrayList<Developer>();
		if(developerList != null && developerList.size() > 0) {
			for(DeveloperDTO dto : developerList) {
				Developer result = new Developer(dto);
				developers.add(result);
			}
		}

		DeveloperResults results = new DeveloperResults();
		results.setDevelopers(developers);
		return results;
	}

	@ApiOperation(value="Get information about a specific developer.", 
			notes="")
	@RequestMapping(value="/{developerId}", method=RequestMethod.GET,
			produces="application/json; charset=utf-8")
	public @ResponseBody Developer getDeveloperById(@PathVariable("developerId") Long developerId) throws EntityRetrievalException {
		DeveloperDTO developer = developerManager.getById(developerId);
		
		Developer result = null;
		if(developer != null) {
			result = new Developer(developer);
		}
		return result;
	}
	
	@ApiOperation(value="Update a developer or merge developers.", 
			notes="This method serves two purposes: to update a single developer's information and to merge two developers into one. "
					+ " A user of this service should pass in a single developerId to update just that developer. "
					+ " If multiple developer IDs are passed in, the service performs a merge meaning that a new developer "
					+ " is created with all of the information provided (name, address, etc.) and all of the prodcuts "
					+ " previously assigned to the developerId's specified are reassigned to the newly created developer. The "
					+ " old developers are then deleted. "
					+ " The logged in user must have ROLE_ADMIN, ROLE_ACB_ADMIN, or ROLE_ACB_STAFF. ")
	@RequestMapping(value="/update", method= RequestMethod.POST, 
			consumes= MediaType.APPLICATION_JSON_VALUE,
			produces="application/json; charset=utf-8")
	public Developer updateDeveloper(@RequestBody(required=true) UpdateDevelopersRequest developerInfo) 
			throws InvalidArgumentsException, EntityCreationException, EntityRetrievalException, JsonProcessingException {
		DeveloperDTO result = null;
		
		if(developerInfo.getDeveloperIds().size() > 1) {
			//merge these developers into one 
			// - create a new developer with the rest of the passed in information
			DeveloperDTO toCreate = new DeveloperDTO();
			toCreate.setDeveloperCode(developerInfo.getDeveloper().getDeveloperCode());
			toCreate.setName(developerInfo.getDeveloper().getName());
			toCreate.setWebsite(developerInfo.getDeveloper().getWebsite());
			
			if(developerInfo.getDeveloper().getStatusEvents() != null && 
					developerInfo.getDeveloper().getStatusEvents().size() > 0) {
				List<String> statusErrors = validateDeveloperStatusEvents(developerInfo.getDeveloper().getStatusEvents());
				if(statusErrors != null && statusErrors.size() > 0) {
					//can only have one error message here for the status text so just pick the first one
					throw new InvalidArgumentsException(statusErrors.get(0));
				}				
				for(DeveloperStatusEvent providedStatusHistory : developerInfo.getDeveloper().getStatusEvents()) {
					DeveloperStatusDTO status = new DeveloperStatusDTO();
					status.setStatusName(providedStatusHistory.getStatus().getStatus());
					DeveloperStatusEventDTO toCreateHistory = new DeveloperStatusEventDTO();
					toCreateHistory.setStatus(status);
					toCreateHistory.setStatusDate(providedStatusHistory.getStatusDate());
					toCreate.getStatusEvents().add(toCreateHistory);
				}
				//if no history is passed in, an Active status gets added in the DAO
				//when the new developer is created
			}
			
			Address developerAddress = developerInfo.getDeveloper().getAddress();
			if(developerAddress != null) {
				AddressDTO toCreateAddress = new AddressDTO();
				toCreateAddress.setStreetLineOne(developerAddress.getLine1());
				toCreateAddress.setStreetLineTwo(developerAddress.getLine2());
				toCreateAddress.setCity(developerAddress.getCity());
				toCreateAddress.setState(developerAddress.getState());
				toCreateAddress.setZipcode(developerAddress.getZipcode());
				toCreateAddress.setCountry(developerAddress.getCountry());
				toCreate.setAddress(toCreateAddress);
			}
			Contact developerContact = developerInfo.getDeveloper().getContact();
			if(developerContact != null) {
				ContactDTO toCreateContact = new ContactDTO();
				toCreateContact.setEmail(developerContact.getEmail());
				toCreateContact.setFirstName(developerContact.getFirstName());
				toCreateContact.setLastName(developerContact.getLastName());
				toCreateContact.setPhoneNumber(developerContact.getPhoneNumber());
				toCreateContact.setTitle(developerContact.getTitle());
				toCreate.setContact(toCreateContact);
			}
			result = developerManager.merge(developerInfo.getDeveloperIds(), toCreate);
			//re-query because the developer code isn't filled in otherwise
			result = developerManager.getById(result.getId());
		} else if(developerInfo.getDeveloperIds().size() == 1) {
			//update the information for the developer id supplied in the database
			DeveloperDTO toUpdate = new DeveloperDTO();
			toUpdate.setDeveloperCode(developerInfo.getDeveloper().getDeveloperCode());
			toUpdate.setId(developerInfo.getDeveloperIds().get(0));
			toUpdate.setName(developerInfo.getDeveloper().getName());
			toUpdate.setWebsite(developerInfo.getDeveloper().getWebsite());
			for(TransparencyAttestationMap attMap : developerInfo.getDeveloper().getTransparencyAttestations()) {
				DeveloperACBMapDTO devMap = new DeveloperACBMapDTO();
				devMap.setAcbId(attMap.getAcbId());
				devMap.setAcbName(attMap.getAcbName());
				devMap.setTransparencyAttestation(attMap.getAttestation());
				toUpdate.getTransparencyAttestationMappings().add(devMap);
			}	
			
			if(developerInfo.getDeveloper().getStatusEvents() != null && 
					developerInfo.getDeveloper().getStatusEvents().size() > 0) {
				List<String> statusErrors = validateDeveloperStatusEvents(developerInfo.getDeveloper().getStatusEvents());
				if(statusErrors != null && statusErrors.size() > 0) {
					//can only have one error message here for the status text so just pick the first one
					throw new InvalidArgumentsException(statusErrors.get(0));
				}
				
				for(DeveloperStatusEvent providedStatusHistory : developerInfo.getDeveloper().getStatusEvents()) {
					DeveloperStatusDTO status = new DeveloperStatusDTO();
					status.setId(providedStatusHistory.getStatus().getId());
					status.setStatusName(providedStatusHistory.getStatus().getStatus());
					DeveloperStatusEventDTO toCreateHistory = new DeveloperStatusEventDTO();
					toCreateHistory.setId(providedStatusHistory.getId());
					toCreateHistory.setDeveloperId(providedStatusHistory.getDeveloperId());
					toCreateHistory.setStatus(status);
					toCreateHistory.setStatusDate(providedStatusHistory.getStatusDate());
					toUpdate.getStatusEvents().add(toCreateHistory);
				}
			} else {
				throw new InvalidArgumentsException("The developer must have a current status specified.");
			}
			
			if(developerInfo.getDeveloper().getAddress() != null) {
				AddressDTO address = new AddressDTO();
				address.setId(developerInfo.getDeveloper().getAddress().getAddressId());
				address.setStreetLineOne(developerInfo.getDeveloper().getAddress().getLine1());
				address.setStreetLineTwo(developerInfo.getDeveloper().getAddress().getLine2());
				address.setCity(developerInfo.getDeveloper().getAddress().getCity());
				address.setState(developerInfo.getDeveloper().getAddress().getState());
				address.setZipcode(developerInfo.getDeveloper().getAddress().getZipcode());
				address.setCountry(developerInfo.getDeveloper().getAddress().getCountry());
				toUpdate.setAddress(address);
			}
			if(developerInfo.getDeveloper().getContact() != null) {
				Contact developerContact = developerInfo.getDeveloper().getContact();
				ContactDTO toUpdateContact = new ContactDTO();
				toUpdateContact.setEmail(developerContact.getEmail());
				toUpdateContact.setFirstName(developerContact.getFirstName());
				toUpdateContact.setLastName(developerContact.getLastName());
				toUpdateContact.setPhoneNumber(developerContact.getPhoneNumber());
				toUpdateContact.setTitle(developerContact.getTitle());
				toUpdate.setContact(toUpdateContact);
			}
			
			result = developerManager.update(toUpdate);
		}
		
		if(result == null) {
			throw new EntityCreationException("There was an error inserting or updating the developer information.");
		}
		Developer restResult = new Developer(result);
		return restResult;
	}
	
	private List<String> validateDeveloperStatusEvents(List<DeveloperStatusEvent> statusEvents) {
		List<String> errors = new ArrayList<String>();
		if(statusEvents == null || statusEvents.size() == 0) {
			errors.add("The developer must have at least a current status specified.");
		} else {
			//sort the status events by date
			statusEvents.sort(new Comparator<DeveloperStatusEvent>() {

				@Override
				public int compare(DeveloperStatusEvent o1, DeveloperStatusEvent o2) {
					if(o1 == null && o2 != null) {
						return -1;
					} else if(o1 != null && o2 == null) {
						return 1;
					} else if(o1 == null && o2 == null) {
						return 0;
					} else {
						//neither are null, compare the dates
						return o1.getStatusDate().compareTo(o2.getStatusDate());
					}
				}
			});
			
			//now that the list is sorted by date, make sure no two statuses next to each other are the same
			Iterator<DeveloperStatusEvent> iter = statusEvents.iterator();
			DeveloperStatusEvent prev = null, curr = null;
			while(iter.hasNext()) {
				if(prev == null) {
					prev = iter.next();
				} else if(curr == null){
					curr = iter.next();
				} else {
					prev = curr;
					curr = iter.next();
				}
				
				if(prev != null && curr != null && 
					prev.getStatus().getStatus().equalsIgnoreCase(curr.getStatus().getStatus())) {
					errors.add("The status '" + prev.getStatus().getStatus() + "' cannot be listed twice in a row.");
				}
			}
		}
		return errors;
	}
}
