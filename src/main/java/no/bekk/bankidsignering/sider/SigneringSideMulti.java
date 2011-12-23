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
 * Signeringsside for ett dokument og flere signat¿rer
 * @author Kjell Midtlyng - BEKK
 *
 */
public class SigneringSideMulti extends WebPage {

	UserSession session = UserSession.get();
	
	@SpringBean
	BankIDSignering bankIDSignering;

	public SigneringSideMulti() {
		if (session.multidokumenter.isEmpty()) {
			lagDokumenterHvisDeIkkeFinnes();
		}

		add(
			new WebMarkupContainer("bankid")
			.add(new ListView<Dokument>("dokumenter", session.multidokumenter) {
				@Override
				protected void populateItem(ListItem<Dokument> item) {
					final Dokument dok = item.getModelObject();
					final LinkContainer containerHoved;
					final LinkContainer containerMed;
					item.add(
							new Label("navn", dok.navn),
							containerHoved = new LinkContainer("hovedsignator", dok.hovedSignatorUrl), 
							containerMed = new LinkContainer("medsignator", dok.medSignatorUrl)
					);
					containerHoved.setEnabled(!dok.erSignertHoved);
					containerMed.setEnabled(!dok.erSignertMed);
					containerHoved.add(signerVedKlikkBehavior(dok, containerMed, true));
					containerMed.add(signerVedKlikkBehavior(dok, containerMed, false));

				}
			})
		);
	}

	/**
	 * Instansiering av dokumenter
	 */
	private void lagDokumenterHvisDeIkkeFinnes() {

		String[] forsteDokument = bankIDSignering.lagBankIDAppletEttDokToSignatorer();
		String[] andreDokument = bankIDSignering.lagBankIDAppletEttDokToSignatorer();
		String[] tredjeDokument = bankIDSignering.lagBankIDAppletEttDokToSignatorer();
		String[] fjerdeDokument = bankIDSignering.lagBankIDAppletEttDokToSignatorer();

		session.multidokumenter = Arrays.asList(new Dokument[] {
				new Dokument("Dokument1", forsteDokument[0],
						forsteDokument[1]),
				new Dokument("Dokument2", andreDokument[0],
						andreDokument[1]),
				new Dokument("Dokument3", tredjeDokument[0],
						tredjeDokument[1]),
				new Dokument("Dokument4", fjerdeDokument[0],
						fjerdeDokument[1]) });

	}

	/**
	 * Behavior som disabler linken etter Œ ha trykt 
	 */
	private AjaxEventBehavior signerVedKlikkBehavior(final Dokument dok, final LinkContainer container, final boolean erHovedSignator) {
		return new AjaxEventBehavior("onclick") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				if(erHovedSignator){
					dok.erSignertHoved = true;
				}
				else{
					dok.erSignertMed = true;
				}
				target.addComponent(container);
			}
		};
	}
}
