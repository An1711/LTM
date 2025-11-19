package model.BO;

import model.BEAN.Account;
import model.DAO.CheckLoginDAO;

public class CheckLoginBO {

	CheckLoginDAO CheckLoginDAO = new CheckLoginDAO();
	public Account checkLogin(String username, String password) {
		return CheckLoginDAO.checkLogin(username, password);
	}
}
