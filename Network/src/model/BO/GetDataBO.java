package model.BO;
import java.util.Vector;

import model.BEAN.Link;
import model.DAO.*;
public class GetDataBO {
	private static ConvertToPDFDAO convertDAO = new ConvertToPDFDAO();
	
	public static Vector<Link> getList(int userID){
		return new Vector<>(convertDAO.getListLinkById(userID));
	}
}
