package gov.healthit.chpl.certifiedProduct.upload;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import gov.healthit.chpl.web.controller.InvalidArgumentsException;

@Service
public class CertifiedProductUploadHandlerFactoryImpl implements CertifiedProductUploadHandlerFactory {
	private static int NUM_FIELDS_2014 = 276;
	
	@Autowired private NewCertifiedProductHandler2011 handler2011;
	@Autowired private NewCertifiedProductHandler2014 handler2014;
	@Autowired private UpdateOrDeleteCertifiedProductHandler updateHandler;
	
	private CertifiedProductUploadHandlerFactoryImpl() {}
	
	@Override
	public CertifiedProductUploadHandler getHandler(CSVRecord heading, CSVRecord cpRecord) throws InvalidArgumentsException {
		CertifiedProductUploadHandler handler = null;
		
		//what type of handler do we need?
		CertifiedProductUploadType uploadType = CertifiedProductUploadType.valueOf(cpRecord.get(1).toUpperCase());

		if(uploadType == CertifiedProductUploadType.UPDATE ||
				uploadType == CertifiedProductUploadType.DELETE) {
			handler = updateHandler;
		} else if(uploadType == CertifiedProductUploadType.NEW) {
			if(heading.size() == NUM_FIELDS_2014) {
				handler = handler2014;
			} else {
				throw new InvalidArgumentsException("Expected " + NUM_FIELDS_2014 + " fields in the record but found " + cpRecord.size());
			}
		} else {
			throw new InvalidArgumentsException("Cannot handle '" + uploadType + "' upload type.");
		}
			
		handler.setRecord(cpRecord);
		handler.setHeading(heading);
		return handler;
	}
}