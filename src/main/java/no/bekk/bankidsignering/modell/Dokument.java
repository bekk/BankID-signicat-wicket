package no.bekk.bankidsignering.modell;

import java.io.Serializable;

/**
 * Domeneobjekt for å holde på dokumentinfo og signeringsstatus (les: vi sjekker ikke om dokumentet er signert, men om linken er klikket på)
 * @author Kjell Midtlyng - BEKK
 *
 */
public class Dokument implements Serializable {
	
	public String navn;
	public String hovedSignatorUrl;
	public String medSignatorUrl;
	public boolean erSignertHoved;
	public boolean erSignertMed;
	
	public Dokument(String navn, String hovedSokerUrl, String medSokerUrl) {
		this.navn = navn;
		this.hovedSignatorUrl = hovedSokerUrl;
		this.medSignatorUrl = medSokerUrl;
	}
	
	public Dokument(String navn, String hovedSokerUrl) {
		this(navn,hovedSokerUrl,null);
	}
	
}
