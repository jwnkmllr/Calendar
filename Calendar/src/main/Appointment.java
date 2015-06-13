package main;

import java.util.Collection;
import java.util.Date;

public interface Appointment {

	public Date getStartDate();
	public Date getEndDate();
	public long getDuration();
	
	public User getCreator();
	
	public void removeFromCalendar();
	
	
	public Collection<Comment> getComments();
	public Comment createComment(String content);
	
	public String getDescription();
	
	public Collection<String> getTags();
	public void addTag(String new_tag);
	public void deleteTag(String existing_tag);
	
	//TODO: location as own class?
	public String getLocation();
	
}
