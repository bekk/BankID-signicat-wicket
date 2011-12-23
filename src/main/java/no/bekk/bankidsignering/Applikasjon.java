package no.bekk.bankidsignering;

import no.bekk.bankidsignering.sider.SigneringSideEnkel;
import no.bekk.bankidsignering.sider.SigneringSideMulti;

import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.stereotype.Component;

/**
 * Klasse som starter applikasjonen
 * @author Kjell Midtlyng - BEKK
 *
 */
@Component
public class Applikasjon extends WebApplication {

	@Override
	public Class<? extends Page> getHomePage() {
		return SigneringSideMulti.class;
	}

	@Override
	public void init() {
        super.init();
        addComponentInstantiationListener(newSpringComponentInjector());


        mountBookmarkablePage("/multisignatur", SigneringSideMulti.class);
        mountBookmarkablePage("/enkelsignatur", SigneringSideEnkel.class);

        getPageSettings().setVersionPagesByDefault(false);
        getSessionSettings().setMaxPageMaps(50);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

	}

	@Override
	public Session newSession(Request request, Response response) {
		System.out.println("creates new session");
		return new UserSession(request);
	}

	protected SpringComponentInjector newSpringComponentInjector() {
		return new SpringComponentInjector(this);
	}
}
