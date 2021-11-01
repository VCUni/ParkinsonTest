package it.VCUni.parkinsonTestServer.persistence.reports;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import com.j256.ormlite.stmt.SelectArg;

import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.exception.ReportNotFoundException;
import it.VCUni.parkinsonTestServer.exception.InvalidPublicKeyException;
import it.VCUni.parkinsonTestServer.interfaces.ReportAccess;
import it.VCUni.parkinsonTestServer.persistence.AbstractDao;
import it.VCUni.parkinsonTestServer.persistence.ConnectionSource;

/**
 *  DAO Class for medical reports
*/
@Component
public class DatabaseReportAccess extends AbstractDao<Integer, ReportDb> implements ReportAccess {

	public DatabaseReportAccess(ConnectionSource s) throws DBException, IOException {
		super(s, ReportDb.class);
	}
	
	
	@Override
	public void generateReport(String modkey, String expkey, String result) throws InvalidPublicKeyException, DBException{
		
		ReportDb report = new ReportDb();
		
		
		if(modkey.equals("") || expkey.equals("") || result.equals("")) throw new InvalidPublicKeyException();
		
		report.exponent = expkey;
		report.modulus = modkey;
		report.result = result;
		
		try {
			dao.create(report);
		} catch (SQLException ex) {
			throw new DBException(ex.toString());
		}
		return;
	}


	@Override
	public String getResult(String modkey, String expkey) throws ReportNotFoundException, DBException {
		try {
			ReportDb report = where().eq(ReportDb.MODKEY, new SelectArg(modkey)).and()
					.eq(ReportDb.EXPKEY, new SelectArg(expkey)).queryForFirst();
			
			if (report == null)
				throw new ReportNotFoundException();
			return report.result;
		} catch(SQLException ex) {throw new DBException(ex.toString());}
	}

}
