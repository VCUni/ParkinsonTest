package it.VCUni.parkinsonTestServer.entity;

import java.util.Objects;

/**
 *  Represents registered user
*/
public class User {
	
	private String cf,name,surname,dateBirth, role;
	private int id;
	
	
	public User(int id, String name, String surname, String cf, String dateBirth, String role) {
		this.cf = cf;
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.dateBirth = dateBirth;
		this.role = role;
	}
	
	public User(String name, String surname, String cf, String dateBirth, String role) {
		this.cf = cf;
		this.dateBirth = dateBirth;
		this.name = name;
		this.surname = surname;
		this.role = role;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, cf, dateBirth, name, surname, role);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		return Objects.equals(cf, other.cf) && Objects.equals(dateBirth, other.dateBirth)
				&& Objects.equals(name, other.name) && Objects.equals(surname, other.surname)
				&& Objects.equals(id, other.id) && Objects.equals(role, other.role);
	}

	/**
	 * @return the id
	 */	
	public int getId() {
		return id;
	}
	
	
	/**
	 * @return the cf
	 */
	public String getCf() {
		return cf;
	}

	
	 /**
	  * @return the name
	  */	 
	public String getName() {
		return name;
	}

	
	/**
	 * @return the dateBirth
	 */	 
	public String getDateBirth() {
		return dateBirth;
	}
	
	
	/**
	 * @return the role
	 */	 
	public String getRole() {
		return role;
	}

	
	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}


}
