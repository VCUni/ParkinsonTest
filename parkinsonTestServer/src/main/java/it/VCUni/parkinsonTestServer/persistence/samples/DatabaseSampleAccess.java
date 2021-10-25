package it.VCUni.parkinsonTestServer.persistence.samples;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.VCUni.parkinsonTestServer.exception.DBException;
import it.VCUni.parkinsonTestServer.persistence.AbstractDao;
import it.VCUni.parkinsonTestServer.persistence.ConnectionSource;

/**
 *  DAO Class for test's samples
*/
@Component
public class DatabaseSampleAccess extends AbstractDao<Integer, SampleDb> {
	
	@Autowired
	public DatabaseSampleAccess(ConnectionSource source) throws DBException, IOException {		
		super(source, SampleDb.class);
	}
	
	
	/**
	 * @param str
	 * @throws DBException
	 */
	public void createSample(String str) throws DBException {
		SampleDb sampledb = new SampleDb();
		sampledb.sample = str;
		
		try {
			dao.create(sampledb);
		} catch (SQLException ex) {
			throw new DBException(ex.toString());
		}
	}
		
				
	/**
	 * @return List of samples as String
	 * @throws DBException
	 */
	public List<String> randSample() throws DBException {
		List<String> array;
		try {
			array = dao.queryBuilder().orderByRaw("RANDOM()").limit((long) 4).query().stream().map(x -> x.getSample()).collect(Collectors.toList());
			if(array.size()==0 || array==null) throw new SQLException();
		} catch(SQLException ex) {throw new DBException(ex.toString());}
		return array;
	}
			

}
