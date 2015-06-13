package test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;

import main.Appointment;
import main.Calendar;
import main.CalendarImpl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * This test show a problem (possibly bug?) when trying to access the location 
 * dimension of the CC of a CP  retrieved from a SharkKB
 * 
 * 
 * @author n
 *
 */
public class RetrieveLocationFromKBTest {
	
	private Calendar cal;
	private String wkt_example = "POINT (30 10)";
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		cal = new CalendarImpl("alice", "alice@example.com", "http://localhost", 5555);
		cal.createAppointment(new String[] {"mytag"}, new Date(), 60*1000*60, wkt_example, "mydescription");
		
		
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	/**
	 * Retrieving location works if directly created used CP is used 
	 */
	@Test
	public void get_location_from_created_appointment() {
		Appointment a = cal.createAppointment(new String[] {"mytag"}, new Date(), 60*1000*60, wkt_example, "mydescription");
		assertNotNull(a.getLocation());
	}
	
	/**
	 * Retrieving location does not work when CP is retrieved from SharkKB
	 */
	@Test
	public void get_location_from_retrieved_appointment() {
		
		Collection<Appointment> apps = cal.getAppointments(null, null, null, 0, wkt_example);

		for ( Appointment app : apps ) {
			assertNotNull(app.getLocation(), "Location could not be retrieved.");
		}
	}
	
	/**
	 * Retrieving time works with CP retrieved from SharkKB
	 */
	@Test
	public void get_time_from_retrieved_appointment() {
		Collection<Appointment> apps = cal.getAppointments(null, null, null, 0, wkt_example);
		for ( Appointment app : apps ) {
			assertNotNull(app.getStartDate());
		}		
	}
}
