package no.bekk.bankidsignering.service;

import java.net.URL;
import java.util.Properties;

import com.signicat.services.client.documentactionservicev3.Document;
import com.signicat.services.client.documentactionservicev3.DocumentAction;
import com.signicat.services.client.documentactionservicev3.DocumentActionEndPoint;
import com.signicat.services.client.documentactionservicev3.DocumentActionRequest;
import com.signicat.services.client.documentactionservicev3.DocumentActionResponse;
import com.signicat.services.client.documentactionservicev3.DocumentActionServiceLocator;
import com.signicat.services.client.documentactionservicev3.Signature;
import com.signicat.services.client.documentactionservicev3.Subject;
import com.signicat.services.client.documentactionservicev3.Task;

/**
 * For å signere: 
 * Brukernavn: 03082011000/10109001290
 * Engangskode: otp
 * Passord: qwer1234
 * @author Signicat/Kjell Midtlyng - BEKK
 *
 */
public class BankIDSigneringImpl implements BankIDSignering {

	private static final String RETURNER_TIL_URL_ETTER_ENKELTSIGNATUR = "http://localhost:8080/enkeltsignatur/";
	private static final String RETURNer_TIL_URL_ETTER_MULTISIGNATUR = "http://localhost:8080/multisignatur/";
	private static final String SIGNICAT_PROFIL = "demo";
	private static final String SIGNICAT_PASSORD = "Bond007";
	private static final String SIGNICAT_DOCUMENT_ACTION_SERVICE = "https://test.signicat.com/ws/documentactionservice-v3";
	private static final String SIGNICAT_SERVICE_URL = "https://test.signicat.com/std/docaction";
	private static final String SIGNICAT_PRIMAER_FODSELSNUMMER = "03082011000";
	private static final String SIGNICAT_CO_FODSELSNUMMER = "10109001290";

	private Properties conf = new Properties();
	public String[] signeringsUrl;

	public String[] lagBankIDAppletEttDokumentEnSoker() {
		String error = null;
		conf.setProperty("documentactionservice", SIGNICAT_DOCUMENT_ACTION_SERVICE);
		conf.setProperty("serviceUrl", SIGNICAT_SERVICE_URL);
		conf.setProperty("mimeType", "text/plain");
		conf.setProperty("service", "shared");
		conf.setProperty("password", SIGNICAT_PASSORD);
		conf.setProperty("profile", SIGNICAT_PROFIL);
		conf.put("loginMethods", new String[] { "bankid" });
		conf.put("signatureMethods", new String[] { "nbid-sign" });

		// Some logging
		System.out.println("Configuration: " + conf);

		// Send request and start signing if everything is ok so far
		try {
			if (error == null) {

				// Locate Web Service
				DocumentActionServiceLocator locator = new DocumentActionServiceLocator();
				DocumentActionEndPoint service = locator.getDocumentActionServiceEndPointPort(new URL(conf.getProperty("documentactionservice")));

				// Create request

				// First we define the document to be signed
				Document documentToBeSigned = new Document();
				documentToBeSigned.setDescription("Dokumentbeskrivelse");
				documentToBeSigned.setMimeType(conf.getProperty("mimeType"));

				// NB! Plain text must be UTF-8 encoded
				documentToBeSigned.setData(("Dette er teksten du skal signere").getBytes("UTF-8"));

				// The document is provided in this request
				documentToBeSigned.setSource("PROVIDED");

				// Then we enclose the document in a DocumentAction
				DocumentAction documentAction = new DocumentAction();
				documentAction.setDocument(documentToBeSigned);

				// We want this document to be signed
				documentAction.setType("SIGN");
				// We define the subject that should sign the document
				Subject signer = new Subject();
				signer.setNationalId(SIGNICAT_PRIMAER_FODSELSNUMMER);

				// Signer name is optional, but may sometimes
				// produce better error messages
				signer.setFirstName("Fornavn");
				signer.setLastName("Etternavn");

				// We create a Task where the document should
				// be signed by the subject
				Task signatureTask = new Task();
				signatureTask.setDocumentAction(new DocumentAction[] { documentAction });
				signatureTask.setSubject(signer);
				// We have to define a unique id for every task. It only has to be
				// unique in the scope of the request, so "1" is a good choice.
				signatureTask.setId("1");

				// Authentication is set to "artifact", since we are going to redirect
				// the current user directly to signicat.com
				// signatureTask.setAuthentication(new Authentication(null,
				// null, true));

				// Signature should contain a list of eID methods the end user
				// may use for signing
				signatureTask.setSignature(new Signature[] { new Signature(null, (String[]) conf.get("signatureMethods"), null) });

				// Define where we want the user to be returned
				String urlToThisPage = RETURNER_TIL_URL_ETTER_ENKELTSIGNATUR;
				String targetUrl = urlToThisPage.substring(0, urlToThisPage.lastIndexOf("/") + 1) + "";
				signatureTask.setOnTaskComplete(targetUrl);
				signatureTask.setOnTaskCancel(targetUrl);
				signatureTask.setOnTaskPostpone(targetUrl);

				// At last, we embed the task in a DocumentActionRequest
				DocumentActionRequest docActionReq = new DocumentActionRequest();
				docActionReq.setVersion("3.0");
				docActionReq.setService(conf.getProperty("service"));
				docActionReq.setPassword(conf.getProperty("password"));
				docActionReq.setProfile(conf.getProperty("profile"));
				docActionReq.setClientReference("");
				docActionReq.setTask(new Task[] { signatureTask });

				System.out.println("Calling web service to register document ... (" + conf.getProperty("documentactionservice") + ")");
				DocumentActionResponse docActionResponse = service.send(docActionReq);
				System.out.println("Response recieved with request id: " + docActionResponse.getRequestId());

				// Save requestid
				conf.setProperty("requestid", docActionResponse.getRequestId());

				// Redirect user to test.signicat.com
				// with artifact. The artifact authenticates the user at
				// test.signicat.com.
				String serviceUrlWithParameters = conf.getProperty("serviceUrl");
				serviceUrlWithParameters += "?documentArtifact=" + docActionResponse.getArtifact();
				serviceUrlWithParameters += "&request_id=" + docActionResponse.getRequestId();
				serviceUrlWithParameters += "&task_id=" + "1";
				System.out.println("Redirects the user to " + serviceUrlWithParameters);

				// response.sendRedirect(serviceUrlWithParameters);
				return new String[] { serviceUrlWithParameters };
			}
		} catch (Exception e) {
			error = "Exception: " + e.getMessage() + " (" + e.getClass().getName() + "). Stacktrace was printed to standard err.";
			e.printStackTrace(System.err);
		}
		return new String[] { "http://localhost:8080/" };
	}

	public String[] lagBankIDAppletEttDokToSignatorer() {
		String error = null;
		conf.setProperty("documentactionservice", SIGNICAT_DOCUMENT_ACTION_SERVICE);
		conf.setProperty("serviceUrl", SIGNICAT_SERVICE_URL);
		conf.setProperty("mimeType", "text/plain");
		conf.setProperty("service", "shared");
		conf.setProperty("password", SIGNICAT_PASSORD);
		conf.setProperty("profile", SIGNICAT_PROFIL);
		conf.put("loginMethods", new String[] { "bankid" });
		conf.put("signatureMethods", new String[] { "nbid-sign" });

		// Some logging
		System.out.println("Configuration: " + conf);

		// Stores configuration in the http session so
		// postprocessing.jsp can access them after the signature is done.

		// Send request and start signing if everyting is ok so far
		try {
			if (error == null) {

				// Locate Web Service
				DocumentActionServiceLocator locator = new DocumentActionServiceLocator();
				DocumentActionEndPoint service = locator.getDocumentActionServiceEndPointPort(new URL(conf.getProperty("documentactionservice")));

				// Create request

				// First we define the document to be signed
				Document documentToBeSigned = new Document();
				documentToBeSigned.setDescription("Dokumentbeskrivelse");
				documentToBeSigned.setMimeType(conf.getProperty("mimeType"));

				// NB! Plain text must be UTF-8 encoded
				documentToBeSigned.setData(("Dette er teksten du skal signere p√•").getBytes("UTF-8"));

				// The document is provided in this request
				documentToBeSigned.setSource("PROVIDED");

				// Then we enclose the document in a DocumentAction
				DocumentAction documentAction = new DocumentAction();
				documentAction.setDocument(documentToBeSigned);

				// We want this document to be signed
				documentAction.setType("SIGN");
				// We define the subject that should sign the document
				Subject signer = new Subject();
				signer.setNationalId(SIGNICAT_PRIMAER_FODSELSNUMMER);

				Subject secondSigner = new Subject();
				secondSigner.setNationalId(SIGNICAT_CO_FODSELSNUMMER);
				// Signer name is optional, but may sometimes
				// produce better error messages
				signer.setFirstName("Fornavn");
				signer.setLastName("Etternavn");

				secondSigner.setFirstName("Fornavn2");
				secondSigner.setLastName("Etternavn2");

				// We create a Task where the document should
				// be signed by the subject
				Task signatureTask = new Task();
				signatureTask.setDocumentAction(new DocumentAction[] { documentAction });
				signatureTask.setSubject(signer);
				// We have to define a unique id for every task. It only has to
				// be
				// unique in the scope of the request, so "1" is a good choice.
				signatureTask.setId("1");

				// Authentication is set to "artifact", since we are going to
				// redirect
				// the current user directly to signicat.com
				// signatureTask.setAuthentication(new Authentication(null,
				// null, true));

				// Signature should contain a list of eID methods the end user
				// may use for signing
				signatureTask.setSignature(new Signature[] { new Signature(null, (String[]) conf.get("signatureMethods"), null) });

				// Task 2
				Task secondSignatureTask = new Task();
				secondSignatureTask.setDocumentAction(new DocumentAction[] { documentAction });
				secondSignatureTask.setSubject(secondSigner);
				// We have to define a unique id for every task. It only has to
				// be
				// unique in the scope of the request, so "1" is a good choice.
				secondSignatureTask.setId("2");

				// Authentication is set to "artifact", since we are going to
				// redirect
				// the current user directly to signicat.com
				// secondSignatureTask.setAuthentication(new
				// Authentication(null, null, true));

				// Signature should contain a list of eID methods the end user
				// may use for signing
				secondSignatureTask.setSignature(new Signature[] { new Signature(null, (String[]) conf.get("signatureMethods"), null) });

				// Define where we want the user to be returned
				String urlToThisPage = RETURNer_TIL_URL_ETTER_MULTISIGNATUR;
				String targetUrl = urlToThisPage.substring(0, urlToThisPage.lastIndexOf("/") + 1) + "";
				signatureTask.setOnTaskComplete(targetUrl);
				signatureTask.setOnTaskCancel(targetUrl);
				signatureTask.setOnTaskPostpone(targetUrl);

				// At last, we embed the task in a DocumentActionRequest
				DocumentActionRequest docActionReq = new DocumentActionRequest();
				docActionReq.setVersion("3.0");
				docActionReq.setService(conf.getProperty("service"));
				docActionReq.setPassword(conf.getProperty("password"));
				docActionReq.setProfile(conf.getProperty("profile"));
				docActionReq.setClientReference("");
				docActionReq.setTask(new Task[] { signatureTask, secondSignatureTask });

				System.out.println("Calling web service to register document ... (" + conf.getProperty("documentactionservice") + ")");
				DocumentActionResponse docActionResponse = service.send(docActionReq);
				System.out.println("Response recieved with request id: " + docActionResponse.getRequestId());

				// Save requestid
				conf.setProperty("requestid", docActionResponse.getRequestId());

				// Redirect user to test.signicat.com
				// with artifact. The artifact authenticates the user at
				// test.signicat.com.
				String serviceUrlWithParameters = conf.getProperty("serviceUrl");
				serviceUrlWithParameters += "?documentArtifact=" + docActionResponse.getArtifact();
				serviceUrlWithParameters += "&request_id=" + docActionResponse.getRequestId();
				String serviceUrlWithParametersFirstSigner = serviceUrlWithParameters + "&task_id=" + "1";
				String serviceUrlWithParametersSecondSigner = serviceUrlWithParameters + "&task_id=" + "2";
				System.out.println("Redirects the user to " + serviceUrlWithParametersFirstSigner + " and " + serviceUrlWithParametersSecondSigner);

				// response.sendRedirect(serviceUrlWithParameters);
				return new String[] { serviceUrlWithParametersFirstSigner, serviceUrlWithParametersSecondSigner };
			}
		} catch (Exception e) {
			error = "Exception: " + e.getMessage() + " (" + e.getClass().getName() + "). Stacktrace was printed to standard err.";
			e.printStackTrace(System.err);
		}
		return new String[] { "http://localhost:8080/", "http://localhost:8080/" };
	}

}
