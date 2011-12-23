package no.bekk.bankidsignering.sider;

import java.util.Arrays;

import no.bekk.bankidsignering.UserSession;
import no.bekk.bankidsignering.modell.Dokument;
import no.bekk.bankidsignering.modell.LinkContainer;
import no.bekk.bankidsignering.service.BankIDSignering;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Signeringsside for enkeldokumenter
 * @author Kjell Midtlyng - BEKK
 *
 */
public class SigneringSideEnkel extends WebPage {

	UserSession session = UserSession.get();

	@SpringBean
	BankIDSignering bankIDSignering;
	
	public SigneringSideEnkel() {
		if (session.enkeltdokumenter.isEmpty()) {
			lagDokumenterHvisDeIkkeFinnes();
		}

		add(
			new WebMarkupContainer("bankid")
			.add(new ListView<Dokument>("dokumenter", session.enkeltdokumenter) {
				@Override
				protected void populateItem(ListItem<Dokument> item) {
					final Dokument dok = item.getModelObject();
					final LinkContainer container;
					item.add(
							new Label("navn", dok.navn),
							container = new LinkContainer("hovedsignator", dok.hovedSignatorUrl)
					);
					container.setEnabled(!dok.erSignertHoved);
					container.add(signertVedKlikkBehvaior(dok, container));
				}
			})
			
		);
	}

	/**
	 * Instansiering av dokumenter
	 */
	private void lagDokumenterHvisDeIkkeFinnes() {
		session.enkeltdokumenter = Arrays.asList(new Dokument[] {
				new Dokument("Dokument1", bankIDSignering.lagBankIDAppletEttDokumentEnSoker()[0]),
				new Dokument("Dokument2", bankIDSignering.lagBankIDAppletEttDokumentEnSoker()[0]),
				new Dokument("Dokument3", bankIDSignering.lagBankIDAppletEttDokumentEnSoker()[0])
				});
	}

	/**
	 * Behavior som disabler linken etter Œ ha trykt 
	 */
	private AjaxEventBehavior signertVedKlikkBehvaior(final Dokument dok, final LinkContainer container) {
		return new AjaxEventBehavior("onclick") {
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				dok.erSignertHoved = true;
				target.addComponent(container);
				
			}
		};
	}
}
