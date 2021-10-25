package it.VCUni.parkinsonTestServer.entity;

// Rappresenta il test di un utente
/**
 * Represents user's test
*/
import java.util.List;
import java.util.Objects;

public class Test {
	
	private int id;
	private String result;
	private String user;
	private TestStatus status;
	private List<String> samplelist;
	private List<String> publicKey;
	
	public Test(int id, String result, String user, List<String> samplelist, List<String> publicKey, TestStatus status) {
		this.id = id;
		this.result = result;
		this.samplelist = samplelist;
		this.user = user;
		this.publicKey = publicKey;
		this.status = status;
	}

	
	/**
	 * @return the id
	 */
	public TestStatus getStatus() {
		return status;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the price
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the samplelist
	 */
	public List<String> getSampleList() {
		return samplelist;
	}
	
	/**
	 * @return the publicKey
	 */
	public List<String> getPublicKey() {
		return publicKey;
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, id, result, samplelist, publicKey);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Test))
			return false;
		Test other = (Test) obj;
		return id == other.id && result == other.result
				&& Objects.equals(user, other.user) && Objects.equals(samplelist, other.samplelist)
				&& Objects.equals(publicKey, other.publicKey);
	}

	
}
