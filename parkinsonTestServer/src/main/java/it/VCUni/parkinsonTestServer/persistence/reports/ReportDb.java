package it.VCUni.parkinsonTestServer.persistence.reports;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *  Database's entity that represents a report
*/
@DatabaseTable
public class ReportDb {

	public final static String ID = "id";
	public final static String MODKEY = "modkey";
	public final static String EXPKEY = "expkey";
	public final static String RESULT = "result";
			
	@DatabaseField(canBeNull = false, columnName = MODKEY)
	protected String modulus;

	@DatabaseField(canBeNull = false, columnName = EXPKEY)
	protected String exponent;

	@DatabaseField(canBeNull = false, columnName = RESULT)
	protected String result;
	
	@DatabaseField(columnName = "id", generatedId = true)
	private int id;


}
