package it.VCUni.parkinsonTestServer.persistence.samples;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *  Database's entity that represents a sample
*/
@DatabaseTable
public class SampleDb {

	public final static String ID = "id";
	public final static String SAMPLE = "sample";
		
	@DatabaseField(canBeNull = false, unique = true, columnName = SAMPLE)
	protected String sample;

	@DatabaseField(columnName = "id", generatedId = true)
	private int id;


	/**
	 * @return test's sample
	 */
	public String toString() {
		return getSample();
	}

	/**
	 * @return the user
	 */
	public String getSample() {
		return sample;
	}
	
	
}
