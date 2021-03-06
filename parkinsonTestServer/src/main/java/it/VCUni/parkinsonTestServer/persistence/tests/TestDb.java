package it.VCUni.parkinsonTestServer.persistence.tests;

import java.util.ArrayList;
import java.util.Arrays;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import it.VCUni.parkinsonTestServer.entity.Test;
import it.VCUni.parkinsonTestServer.entity.TestStatus;
import it.VCUni.parkinsonTestServer.entity.UploadStatus;
import it.VCUni.parkinsonTestServer.persistence.users.UserDb;

/**
 *  database's entity that represents a user's test
*/
@DatabaseTable
public class TestDb {

	public final static String ID = "id";
	public final static String USER = "user";
	public final static String AUDIOURL = "audioUrl";
	public final static String PUBMOD = "pubMod";
	public final static String PUBEXP = "pubExp";
	public final static String TRAINRESULT = "trainresult";
	public final static String STATUS = "teststatus";
	
	public final static String UPLOADSTATUS = "uploadstatus";
	
	@DatabaseField(generatedId = true, columnName = ID)
	protected int id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = USER)
	protected UserDb user;
	
	@DatabaseField(canBeNull = false)
	protected String sample1;

	@DatabaseField(canBeNull = false)
	protected String sample2;
	
	@DatabaseField(canBeNull = false)
	protected String sample3;
	
	@DatabaseField(canBeNull = false)
	protected String sample4;
	
	@DatabaseField(canBeNull = true)
	protected String url1;
	
	@DatabaseField(canBeNull = true)
	protected String url2;
	
	@DatabaseField(canBeNull = true)
	protected String url3;
	
	@DatabaseField(canBeNull = true)
	protected String url4;
	
	@DatabaseField(canBeNull = true, columnName = PUBMOD)
	protected String pubMod;

	@DatabaseField(canBeNull = true, columnName = PUBEXP)
	protected String pubExp;
	
	@DatabaseField(canBeNull = false, columnName = STATUS)
	protected TestStatus status;
	
	@DatabaseField(canBeNull = true, columnName = TRAINRESULT)
	protected String trainresult;
	
	@DatabaseField(canBeNull = false, columnName = UPLOADSTATUS)
	protected UploadStatus uploadstatus;
	
	
	/**
	 * @return Test
	 */
	public Test toTest() {
		ArrayList<String> samplelist = new ArrayList<String>();
		
		if(url4==null) samplelist.add(0, sample4);
		if(url3==null) samplelist.add(0, sample3);
		if(url2==null) samplelist.add(0, sample2);
		if(url1==null) samplelist.add(0, sample1);
		
		return new Test(id, user.getCf(), samplelist, Arrays.asList(pubMod, pubExp), status);
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @return the user
	 */
	public UserDb getUser() {
		return user;
	}
	
	/**
	 * @param pubMod the pubMod to set
	 */
	public void setPubMod(String pubMod) {
		this.pubMod = pubMod;
	}
	
	/**
	 * @param pubExp the pubExp to set
	 */
	public void setPubExp(String pubExp) {
		this.pubExp = pubExp;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(TestStatus status) {
		this.status = status;
	}
	
	/**
	 * @return the status
	 */
	public TestStatus getStatus() {
		return status;
	}
	
	/**
	 * @param trainresult the trainresult to set
	 */
	public void setTrainResult(String result) {
		this.trainresult = result;
	}
	
}
