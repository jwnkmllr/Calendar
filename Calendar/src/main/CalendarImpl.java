package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.knowledgeBase.sync.SyncKP;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;



public class CalendarImpl implements Calendar {
	private SharkKB kb;
	private SyncKB skb;
	private SyncKP skp;
	J2SEAndroidSharkEngine engine;
	private String host;
	private int port;

	
	//TODO: pass host only as as checked URI
	public CalendarImpl(String username, String email, String host, int port) {
		this.host = host;
		this.port = port;
		engine = new J2SEAndroidSharkEngine();
		kb = new InMemoSharkKB();
		PeerSemanticTag owner = InMemoSharkKB.createInMemoPeerSemanticTag(
													username, new String[] {email}, new String[] {host + ":" + port} );
		kb.setOwner(owner);
		try {
			/* TODO: load FS-KB */
			skb = new SyncKB(kb);
			skp = new SyncKP(engine, skb, 2);
		}
		catch (SharkKBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasAppointments() {
		boolean has_appointments = false;
		try {
			has_appointments = kb.getAllContextPoints().hasMoreElements();
		}
		catch (SharkKBException e) {
			e.printStackTrace();
		}
		return has_appointments;
	}

	@Override
	public void deleteAppointment(Appointment appointment)
			throws IllegalArgumentException {
		appointment.removeFromCalendar();
	}

	@Override
	public void addCommentToAppointment(User creator, String content,
			Appointment appointment) throws IllegalArgumentException {
		appointment.createComment(content);
	}

	@Override
	public Collection<User> getAllUsers() {
		LinkedList<User> users = new LinkedList<User>();
		try {
			Enumeration<PeerSemanticTag> enu = kb.getPeerSTSet().peerTags();
			while (enu.hasMoreElements()) {
				PeerSemanticTag pst = enu.nextElement();
				users.add(new UserImpl(pst));
			}
		}
		catch (SharkKBException e) {
			throw new RuntimeException("Unable to retrieve users");
		}
		return null;
	}

	@Override
	public Collection<Appointment> getAppointmentsByTags(String[] tags) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Appointment> getAppointmentsByTime(Date d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Appointment> getAppointmentsByLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Appointment> getAppointmentsByCreator(User creator) {
		return getAppointments(null, creator, null, 0, null);

	}

	public Collection<Appointment> getAppointments(String tag, User creator,
			Date start, long duration, String location_wkt) {
		ArrayList<Appointment> appointments = new ArrayList<Appointment>();
		try {
			SemanticTag topic = tag == null ? null : kb.createSemanticTag(tag, Utils.stringToURI(tag));
			TimeSemanticTag time = start == null ? null : kb.createTimeSemanticTag(start.getTime(), duration);
System.out.println("get: location passed: " + location_wkt);
			SharkGeometry geo = InMemoSharkGeometry.createGeomByWKT(location_wkt);
			SpatialSemanticTag location = kb.getSpatialSTSet().createSpatialSemanticTag(location_wkt, new String[]{ Utils.stringToURI(location_wkt) }, geo);
				PeerSemanticTag origin = creator == null ? null : InMemoSharkKB.createInMemoPeerSemanticTag(creator.getName(), creator.getEmailAddress(), creator.getTcpAddress());
			ContextCoordinates cc = kb.createContextCoordinates(
					topic, 
					origin,
					null, 
					null, 
					time, 
					location, 
					SharkCS.DIRECTION_INOUT);
	System.out.println("get cc location: " + L.semanticTag2String(cc.getLocation()));
			Enumeration<ContextPoint> e = SharkCSAlgebra.extract(kb, cc).contextPoints();
			while ( e.hasMoreElements() ) {
				ContextPoint cp = e.nextElement();
				Iterator<Information> e_info = cp.getInformation(AppointmentImpl.APPOINTMENT_TYPE_STRING);
				while ( e_info.hasNext() ) {
					//TODO: check for Appointment/Comment Type
					Information curr_i = e_info.next();
					appointments.add(new AppointmentImpl(kb, cp, curr_i));
				}
			}
		}
		catch (SharkKBException e) {
			e.printStackTrace();
		}
		return appointments;
	}

	@Override
	public Collection<Appointment> getAllAppointments() {
		return getAppointments(null, null, null, 0, null);
	}

	@Override
	public Appointment createAppointment(String[] tags, Date start,
			long duration, String location_wkt, String description) {
		try {
			TimeSemanticTag time = kb
					.createTimeSemanticTag(start.getTime(), duration);
			SharkGeometry geo = InMemoSharkGeometry.createGeomByWKT(location_wkt);
			SpatialSemanticTag location = kb.getSpatialSTSet().createSpatialSemanticTag(location_wkt, new String[]{ Utils.stringToURI(location_wkt) }, geo);
			SemanticTag curr_tag = kb.createSemanticTag(tags[0],
					Utils.stringToURI(tags[0]));
			ContextCoordinates cc = kb.createContextCoordinates(curr_tag,
					kb.getOwner(), kb.getOwner(), null, time, location,
					SharkCS.DIRECTION_INOUT);
			ContextPoint cp = kb.createContextPoint(cc);
			Information i = cp.addInformation(description);
			AppointmentImpl a = new AppointmentImpl(kb, cp, i);
			i.setName(AppointmentImpl.APPOINTMENT_TYPE_STRING);
			return a;
		}
		catch (SharkKBException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to create user");
		}
	}

	@Override
	public User createUser(String name, String email, String tcp) {
		User u = null;
		PeerSemanticTag pst = null;
		try {
			pst = kb.createPeerSemanticTag(name, email, tcp);
		}
		catch (SharkKBException e) {
			e.printStackTrace();
		}
		u = new UserImpl(pst);
		return u;
	}

	@Override
	public void removeUser(String email) {
		try {
			kb.getPeerSTSet().removeSemanticTag(InMemoSharkKB.createInMemoPeerSemanticTag(email, email, "tcp://example.com"));
		}
		catch (SharkKBException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public boolean userExists(String email) {
		boolean exists = false;
		PeerSemanticTag pst = InMemoSharkKB.createInMemoPeerSemanticTag(email, email, "tcp://example.com");
		try {
			exists = kb.getPeerSTSet().fragment(pst).peerTags().hasMoreElements();
		}
		catch (SharkKBException e) {
		}
		return exists;
	}

	@Override
	public void sendAndReceiveAppointments() {
		try {
			engine.startTCP(port);
			//engine.publishAllKP();
			/*TODO
			 * for all peers to receive from:
			 * 		engine_bob.publishAllKP(peers);

			 * */
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public User getUser(String email) {
		PeerSemanticTag user = null;
		try {
			user = kb.getPeerSTSet().getSemanticTag(new String[] { email });
		}
		catch (SharkKBException e) {
			e.printStackTrace();
		}
		return new UserImpl(user);
	}
}