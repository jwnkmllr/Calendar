package main;

import java.util.Collection;
import java.util.Date;

public interface Calendar {

	/*query appointment:
	 * - by creator
	 * - by location
	 * - by by time
	 * - by tag
	 */
	public Collection<Appointment> getAppointmentsByTags(String[] tags);
	
	public Collection<Appointment> getAppointmentsByTime(Date d);
	
	public Collection<Appointment> getAppointmentsByLocation(/* ? */);
	
	public Collection<Appointment> getAppointmentsByCreator(User creator);
	
	public Collection<Appointment> getAllAppointments();
	
	public Collection<Appointment> getAppointments(String tag, User creator,
			Date start, long duration, String location_wkt);


	public boolean hasAppointments();
	
	public void deleteAppointment(Appointment appointment) throws IllegalArgumentException;
	
	public Appointment createAppointment(String[] tags, Date start, long duration, String location_wkt, String description);
	
	
	
	public void addCommentToAppointment(User creator, String content, Appointment appointment) 
			throws IllegalArgumentException; 
	
	
	public User createUser(String name, String email, String tcp);
	
	public void removeUser(String email);
	
	public User getUser(String email);

	public Collection<User> getAllUsers();
	
	public boolean userExists(String email);
	
	public void sendAndReceiveAppointments();
}