package it.VCUni.parkinsonTestServer.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.ReportNotFoundException;
import it.VCUni.parkinsonTestServer.interfaces.ReportAccess;

@Component
public class ReportHandler {

	 @Autowired
	 ReportAccess reports;
	 
	 @Autowired
	 Logger log;
	 
	 public String getResult(String modkey, String expkey) throws ReportNotFoundException, DBException {
		 return reports.getResult(modkey, expkey);
	 }
}
