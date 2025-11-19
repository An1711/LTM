package model.BO;
import java.util.Vector;

import model.BEAN.Link;
import model.DAO.*;
public class GetDataBO {
	private ConvertToPDFDAO convertDAO = new ConvertToPDFDAO();
	
	public Vector<Link> getList(int userID){
		return new Vector<>(convertDAO.getListLinkById(userID));
	}
}
