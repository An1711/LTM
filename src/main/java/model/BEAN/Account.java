package model.BEAN;

public class Account {
	int ID;
	String username,password;
	
	public int getID() {
		return ID;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public String getPasword() {
		return password;
	}

	public void setPasword(String pasword) {
		this.password = pasword;
	}

	public void setID(int ID) {
		this.ID=ID;
	}
}
