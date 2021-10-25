package it.VCUni.parkinsonTestServer.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DbConnectionFile implements IDbConnection{

	@JsonProperty("dbUrl")
	private String dbUrl;
	
	@JsonProperty("dbUser")
	private String dbUser;
	
	@JsonProperty("dbPass")
	private String dbPass;
	
	@JsonProperty("filePath")
	private String filePath;
	
	@JsonProperty("venvScriptPath")
	private String venvScriptPath;
	
	@Override
	public String getDbUrl() {return dbUrl;}
	
	@Override
	public String getDbUser() {return dbUser;}
	
	@Override
	public String getDbPass() {return dbPass;}
	
	@Override
	public String getFilePath() {return filePath;}
	
	public String getVenvScriptPath() {return venvScriptPath;}
	
// Costruttore JSON
	public DbConnectionFile() {}
	
// Costruttore per il testing
	/**
	 * @param dbUrl
	 * @param dbUser
	 * @param dbPass
	 * @param filePath
	 * @param venvScriptPath
	 */
	public DbConnectionFile(String dbUrl, String dbUser, String dbPass, String filePath, String venvScriptPath) {
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPass = dbPass;
		this.filePath = filePath;
		this.venvScriptPath = venvScriptPath;
	}

	
}
