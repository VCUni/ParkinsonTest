package it.VCUni.parkinsonTestServer.persistence;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;

import it.VCUni.parkinsonTestServer.settings.IDbConnection;

/**
 *  Spring singleton component, Provides JdbcPooledConnectionSource to the database for all DAO
*/
@Component
public class ConnectionSource {
	public final JdbcPooledConnectionSource source;

	@Autowired
	public ConnectionSource(IDbConnection connection) throws SQLException, IOException {
		source = new JdbcPooledConnectionSource(
			"jdbc:" + connection.getDbUrl(),
			connection.getDbUser(),
			connection.getDbPass()
		);

		/*	
			Risolve un problema di concorrenza in sqlite (modalità di cache e timeout prima di lanciare un eccezione).
			Sqlite è utilizzato solo ai fini della demo, in un ambiente reale si dovrebbe utilizzare postgres o
			mysql che sono pienamente compatibili con la libreria ORM in uso.
		*/
		
		if (connection.getDbUrl().startsWith("sqlite:")) {
			try (DatabaseConnection conn = source.getReadWriteConnection("")) {
				conn.queryForLong("pragma journal_mode = WAL;");
				conn.queryForLong("pragma busy_timeout = 1000;");
			}
		}
	}
}
