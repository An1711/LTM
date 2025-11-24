package model.BO;
import model.DAO.RegisterDAO;
public class RegisterBO {
	private RegisterDAO dao = new RegisterDAO();
	public boolean register(String username, String password) {
		return dao.register(username, password);
	}
	public boolean isUsernameExists(String username) {
		return dao.isUsernameExists(username);
	}
}
