package model.BEAN;

public class Link {
	int ID;
	boolean type;
	String link;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public boolean isType() {
		return type;
	}
	public void setType(boolean type) {
		this.type = type;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Link(int ID, String link, boolean type) {
        this.ID = ID;
        this.link = link;
        this.type = type;
    }
	public Link() {
		
	}
	public String getTypeText() {
        return type ? "DOC → PDF" : "PDF → DOC";
    }
	public String toString() {
        return "Link{" +
                "id=" + ID +
                ", link='" + link + '\'' +
                ", type=" + (type ? "DOC→PDF" : "PDF→DOC") +
                '}';
    }
}
