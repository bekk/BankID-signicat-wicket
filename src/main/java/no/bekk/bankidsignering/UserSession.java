package no.bekk.bankidsignering;

import java.util.ArrayList;
import java.util.List;


import no.bekk.bankidsignering.modell.Dokument;

import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

/**
 * Sesjonen som lagrer all info om dokumentene og om bruker har signert (klikket på dokumenter)
 * @author Kjell Midtlyng - BEKK
 *
 */
public class UserSession extends WebSession {
	public List<Dokument> multidokumenter;
	public List<Dokument> enkeltdokumenter;
	
	public UserSession(Request request) {
		super(request);
		multidokumenter = new ArrayList<Dokument>();
		enkeltdokumenter = new ArrayList<Dokument>();
	}
	
	public static UserSession get () {
		return (UserSession) WebSession.get();
	}

}
