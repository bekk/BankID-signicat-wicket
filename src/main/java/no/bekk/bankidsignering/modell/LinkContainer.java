package no.bekk.bankidsignering.modell;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;

/**
 * Enkel container for eksterne url-er
 * @author Kjell Midtlyng - BEKK
 *
 */
public class LinkContainer extends WebMarkupContainer {
	public LinkContainer(String id, final String url) {
		super(id);

		add(
			new ExternalLink("link", url));
	}

}