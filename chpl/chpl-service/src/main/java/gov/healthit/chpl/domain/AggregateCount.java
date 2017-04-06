package gov.healthit.chpl.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.healthit.chpl.app.ReflectiveHelper;
import gov.healthit.chpl.dto.CertifiedProductDetailsDTO;

/** Description: Provides aggregate counts for objects
 * 
 * @author dlucas
 *
 */
public class AggregateCount implements Serializable {
	private static final long serialVersionUID = -6137989717222340192L;
	private Integer totalCount;
	private List<? extends Object> objectList = new ArrayList<Object>();
	
	public AggregateCount(){
	}
	
	/**
	 * Creates an AggregateCount object using a list of generic objects
	 * @param objectList - list of objects to count
	 */
	public AggregateCount(List<? extends Object> objectList){
		this.objectList = objectList;
		if(objectList != null){
			this.totalCount = objectList.size();
		}
	}
	
	public Integer getTotalCount(){
		return this.totalCount;
	}

	/**
	 * Uses AggregateCount.objectList, a startDate & endDate, and fieldName to find the total number of 
	 * the given field for the list of objects in the given time period
	 * @param startDate
	 * @param endDate
	 * @param dateFieldName
	 * @return
	 */
	public Integer getCountDuringPeriodUsingField(Date startDate, Date endDate, String dateFieldName) {
		Integer counter = 0;
		for(Object obj : objectList){
				Date objectDate = ReflectiveHelper.get(obj, dateFieldName);
				if((objectDate.after(startDate) || objectDate.equals(startDate)) && (objectDate.before(endDate) || objectDate.equals(endDate))){
					counter++;
				}
		}	
		return counter;
	}
	
	/**
	 * Uses AggregateCount.objectList, a startDate & endDate, creationDateFieldName + lastModifiedDateFieldName, and deletionFieldName
	 * to find the total number of a given field for the list of objects in the given time period
	 * @param startDate
	 * @param endDate
	 * @param creationDateFieldName
	 * @param lastModifiedDateFieldName
	 * @param deletionFieldName
	 * @return
	 */
	public Integer getCountDuringPeriod(Date startDate, Date endDate, String creationDateFieldName, String lastModifiedDateFieldName, String deletionFieldName){
		Integer counter = 0;
		for(Object obj : objectList){
				Date objectCreationDate = ReflectiveHelper.get(obj, creationDateFieldName);
				Date objectLastModifiedDate = ReflectiveHelper.get(obj, lastModifiedDateFieldName);
				Boolean isDeleted = ReflectiveHelper.get(obj, deletionFieldName);
				if(isDeleted == false){
					if((objectCreationDate.after(startDate) || objectCreationDate.equals(startDate)) && (objectCreationDate.before(endDate) || objectCreationDate.equals(endDate))){
						counter++;
					}
				}
				else{
					if((objectCreationDate.after(startDate) || objectCreationDate.equals(startDate)) 
							&& (objectCreationDate.before(endDate) || objectCreationDate.equals(endDate))
							&& (objectLastModifiedDate.after(endDate) || objectLastModifiedDate.equals(endDate))){
						counter++;
					}
					else{
						System.out.println("Does not meet either criteria");
					}
				}
		}	
		return counter;
	}
	
	/**
	 * Uses AggregateCount.objectList, a startDate & endDate, creationDateFieldName + lastModifiedDateFieldName, and deletionFieldName
	 * to find the total number of a given field for the list of objects in the given time period
	 * @param startDate
	 * @param endDate
	 * @param creationDateFieldName
	 * @param lastModifiedDateFieldName
	 * @param deletionFieldName
	 * @return
	 */
	public Integer getCountDuringPeriodForCertifiedProduct(List<CertifiedProductDetailsDTO> cpDetails, Date startDate, Date endDate, String creationDateFieldName, String lastModifiedDateFieldName, String deletionFieldName){
		Integer counter = 0;
		for(CertifiedProductDetailsDTO dto : cpDetails){
				if(dto.getProduct().getDeleted() == false){
					if((dto.getCreationDate().after(startDate) || dto.getCreationDate().equals(startDate)) && (dto.getCreationDate().before(endDate) || dto.getCreationDate().equals(endDate))){
						counter++;
					}
				}
				else{
					if((dto.getCreationDate().after(startDate) || dto.getCreationDate().equals(startDate)) 
							&& (dto.getCreationDate().before(endDate) || dto.getCreationDate().equals(endDate))
							&& (dto.getLastModifiedDate().after(endDate) || dto.getLastModifiedDate().equals(endDate))){
						counter++;
					}
				}
		}	
		return counter;
	}
	
	/**
	 * Uses AggregateCount.objectList, an endDate, and fieldName to find the total number of the given field for the list of objects before the endDate
	 * @param endDate
	 * @param fieldName
	 * @return
	 */
	public Integer getCountBeforeEndDateUsingField(Date endDate, String fieldName) {
		Integer counter = 0;
		for(Object obj : objectList){
				Date objectDate = ReflectiveHelper.get(obj, fieldName);
				if((objectDate.before(endDate) || objectDate.equals(endDate))){
					counter++;
				}
		}	
		return counter;
	}
	
}