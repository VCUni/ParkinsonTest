package it.VCUni.parkinsonTestServer.persistence.users;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import it.VCUni.parkinsonTestServer.entity.User;
import it.VCUni.parkinsonTestServer.persistence.tests.TestDb;

/**
 *  database's entity that represents a registered user
*/
@DatabaseTable
public class UserDb{
	public static final String DATEBIRTH = "dateBirth";
	public static final String NAME = "name";
	public static final String SURNAME = "surname";
	public static final String PASSWORD = "password";
	public static final String CF = "cf";
	public static final String TESTS = "user";
	public static final String ROLE = "role";
	
	@DatabaseField(columnName = "id", generatedId = true)
	private int id;
	
	@DatabaseField(columnName = DATEBIRTH, canBeNull = false)
	protected String dateBirth;
	
	@DatabaseField(columnName = CF, unique = true, canBeNull = false)
	protected String cf;
	
	@DatabaseField(columnName = NAME, canBeNull = false)
	protected String name;
	
	@DatabaseField(columnName = ROLE, canBeNull = false)
	protected String role;

	@DatabaseField(columnName = SURNAME, canBeNull = false)
	protected String surname;

	@DatabaseField(columnName = PASSWORD, canBeNull = false)
	protected String password;
	
	/**
	 * @return the password
	 */
	protected String getPassword() {return password;}
	
	/**
	 * @param password
	 */
	protected void setPassword(String password) { this.password = password; }

	@ForeignCollectionField(foreignFieldName = TESTS)
	protected ForeignCollection<TestDb> tests;
	

	public UserDb() {}

	
	/**
	 * @param user
	 */
	public UserDb(User user) {
		name = user.getName();
		surname = user.getSurname();
		cf = user.getCf();
		dateBirth = user.getDateBirth();
		role = user.getRole();
	}
	

	/**
	 * @return User
	 */
	public User toUser() {
		return new User(id, name, surname, cf, dateBirth, role);
	}
	
	/**
	 * @return the cf
	 */
	public String getCf() {return cf;}
}

