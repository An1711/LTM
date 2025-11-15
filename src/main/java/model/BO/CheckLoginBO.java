package model.BO;

import model.BEAN.Account;
import model.DAO.CheckLoginDAO;

public class CheckLoginBO {
	public static Account checkLogin(String username, String password) {
		return CheckLoginDAO.checkLogin(username,password);
	}
}
