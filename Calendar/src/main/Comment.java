package main;

import java.util.Date;

public interface Comment {
	
	public String getCreator();
	
	public String getContent();
	
	public Date getCreationDate();
	
	
	/* TODO: sortable by creation date.
	 * 
	 * */
}
