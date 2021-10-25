package it.VCUni.parkinsonTestServer.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Stream;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;

import it.VCUni.parkinsonTestServer.exception.DBException;

/**
 *  Abstract Class for a generic DAO, contains common code to inizialize dao and database
*/
public abstract class AbstractDao<TKey, TEntity> {
	final private Class<TEntity> entity;
	final public Dao<TEntity, TKey> dao;
	final protected JdbcConnectionSource source;

	/**
	 * @param source
	 * @param entity
	 * @throws DBException
	 * @throws IOException
	 */
	public AbstractDao(ConnectionSource source, Class<TEntity> entity) throws DBException, IOException {
		this.source = source.source;
		this.entity = entity;
		try {
			dao = DaoManager.createDao(this.source, entity);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
		initializeTable();
	}

	
	/**
	 * @throws DBException
	 */
	private void initializeTable() throws DBException {
		try {
			TableUtils.createTableIfNotExists(dao.getConnectionSource(), entity);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
	}

	
	/**
	 * @throws DBException
	 */
	public void dropTable() throws DBException {
		try {
			TableUtils.dropTable(source, entity, true);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
		initializeTable();
	}

	
	public Where<TEntity, TKey> where() {
		return dao.queryBuilder().where();
	}
	
	
	/**
	 * @return
	 * @throws DBException
	 */
	public Stream<TEntity> all() throws DBException {
		try {
			return dao.queryForAll().stream();
		} catch(SQLException ex) {throw new DBException(ex.toString());}
	}

	
	/**
	 * @param id
	 * @return
	 * @throws DBException
	 */
	public TEntity findByID(TKey id) throws DBException {
		try {
			return dao.queryForId(id);
		} catch(SQLException ex) {throw new DBException(ex.toString());}
	}
}

