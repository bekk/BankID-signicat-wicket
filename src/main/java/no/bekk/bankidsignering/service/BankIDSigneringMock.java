package no.bekk.bankidsignering.service;

/**
 * Mock-klasse av signeringen
 * @author Kjell Midtlyng
 *
 */
public class BankIDSigneringMock implements BankIDSignering {

	public String[] lagBankIDAppletEttDokumentEnSoker() {
		return new String[] { "http://localhost:8080/" };
	}

	public String[] lagBankIDAppletEttDokToSignatorer() {
		return new String[] { "http://localhost:8080/", "http://localhost:8080/" };
	}

}
