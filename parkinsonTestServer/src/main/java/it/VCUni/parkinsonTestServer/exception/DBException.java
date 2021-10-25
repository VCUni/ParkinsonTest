package it.VCUni.parkinsonTestServer.exception;

public class DBException extends Exception{
	String exception;
	
	public DBException(String e){
		exception = e;
	}
	
	public String toString() {
		return "Exception in persistence package " + exception;
	}

}
