package it.VCUni.parkinsonTestServer.settings;

// Interfaccia per un file di configurazione che contiene le credenziali di connessione al database 
// ed il path per la creazione della cartella Userdocuments

public interface IDbConnection {
	String getDbUrl();
	String getDbUser();
	String getDbPass();
	String getFilePath();
	String getVenvScriptPath();
}
