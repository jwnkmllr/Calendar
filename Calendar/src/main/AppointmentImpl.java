package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

public class AppointmentImpl implements Appointment {
	private SharkKB kb;
	private ContextPoint cp;
	private Information i;
	public static final String APPOINTMENT_TYPE_STRING = "type_appointment";
	
	/**
	 * TODO: constructor should allow: 
	 * 					- creating a new appointment
	 * 					- create an appointment representation of an app. that already exists
	 * or: take calendar instance?
	 * 
	 * */
	public AppointmentImpl(SharkKB kb, String[] tags, String description, 
			User creator, Date start, long duration) {
		/* create and store CP, exceptions if unallowed. */
	}
	
	public AppointmentImpl(SharkKB kb, ContextPoint cp, Information i) {
		this.kb = kb;
		this.cp = cp;
		this.i = i;
	}
	
	@Override
	public Date getStartDate() {
		return new Date(cp.getContextCoordinates().getTime().getFrom());
	}

	@Override
	public Date getEndDate() {
		return new Date(cp.getContextCoordinates().getTime().getFrom() + 
				cp.getContextCoordinates().getTime().getDuration());
	}

	@Override
	public long getDuration() {
		return cp.getContextCoordinates().getTime().getDuration();
	}

	@Override
	public User getCreator() {
		return new UserImpl(cp.getContextCoordinates().getOriginator());
	}

	@Override
	public Collection<Comment> getComments() {
		// TODO retrieve live from stored cp.
		ArrayList<Comment> comments = new ArrayList<Comment>();
		Iterator<Information> e = cp.getInformation(getCommentName());
		while ( e.hasNext() ) {
			Information i = e.next();
			Comment c = new CommentImpl(kb, cp, i);
			comments.add(c);
			
		}
		//TODO: make sortable, rather with comparator than interface on comments?
		return comments;
	}

	@Override
	public String getDescription() {
		try {
			return i.getContentAsString();
		}
		catch (SharkKBException e) {
			throw new RuntimeException("Unable to convert description to String.");
		}
	}

	@Override
	public Collection<String> getTags() {
		//TODO: support several tags?
		ArrayList<String> tags = new ArrayList<String>();
		tags.add(Utils.uriToString(cp.getContextCoordinates().getTopic().getSI()[0]));
		
		return tags;
	}

	@Override
	public void addTag(String new_tag) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteTag(String existing_tag) {
		// TODO Auto-generated method stub
	}

	@Override
	public Comment createComment(String content) {
		Information i = cp.addInformation(content);
		Comment c = new CommentImpl(kb, cp, i);
		
		try {
			i.setName(getCommentName());
			i.setProperty("creator", kb.getOwner().getSI()[0]);
		}
		catch (SharkKBException e) {
			throw new RuntimeException("Unable to create comment.");
		}
		
		return c;
	}

	//TODO: move to Calendar?
	@Override
	public void removeFromCalendar() {
		Iterator<Information> it = cp.getInformation(this.getCommentName());
		while ( it.hasNext() ) {
			Information i = it.next();
			cp.removeInformation(i);
		}
		cp.removeInformation(i);
		this.i = null;
		this.cp = null;
		this.kb = null;
	}

	//TODO: remove Comment?

	@Override
	public String getLocation() {
		//return cp.getContextCoordinates().getLocation().getGeometry().getWKT();
		//TODO works?
System.out.println("cp: " + cp);
System.out.println("cc: " + cp.getContextCoordinates());
System.out.println("loc: " + cp.getContextCoordinates().getLocation());
System.out.println(L.contextSpace2String(cp.getContextCoordinates()));

		return cp.getContextCoordinates().getLocation().toString();
		//return "not yet implemented"; 
	}
	
	@Override
	public String toString() {
		return this.getTags() + "\nCreator: " + this.getCreator().getEmailAddress() 
				+ "\n" + getDescription() + "\nDate: " + getStartDate() + "\nLocation:\n"
				+ getLocation();
	}

	//TODO: final static attribute (without id)
	private String getCommentName() {
		return i.getUniqueID() + "_comment";
	}
	
	public String getID() {
		return i.getUniqueID();
	}
	
}
