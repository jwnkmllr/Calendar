package main;

import java.util.Date;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;

public class CommentImpl implements Comment {
	private SharkKB kb;
	private ContextPoint cp;
	private Information i;
	
	

	
	public CommentImpl(SharkKB kb, ContextPoint cp, Information i) {
		this.kb = kb;
		this.cp = cp;
		this.i = i;
	}
	
	/**
	 * @return the email address identifying the creator of this comment
	 */
	@Override
	public String getCreator() {
		// set "creator" as static field...
		String name = i.getProperty("creator");
		return name;
	}

	@Override
	public String getContent() {
		String c = "";
		try {
			c = i.getContentAsString();
		}
		catch (SharkKBException e) {
			throw new RuntimeException("Unable to load comment description");
		}
		
		return c;
	}

	@Override
	public Date getCreationDate() {
		return new Date(i.creationTime());
	}
	
	@Override
	public String toString() {
		return getCreator() + " at " + getCreationDate() + 
				":\n" + getContent(); 
	}


}