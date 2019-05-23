package alignment.processor;

import alignment.processor.content.ContentProcessor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.page.Page;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class ProcessedContentPrinter {

	private final static Logger LOGGER = Logger.getLogger(ProcessedContentPrinter.class.getSimpleName());

	private ContentProcessor contentProcessor;

	/**
	 * Constructor of {@link ProcessedContentPrinter} class.
	 *
	 * @param contentProcessor a non null instance of {@link ContentProcessor}.
	 */
	public ProcessedContentPrinter(@NotNull ContentProcessor contentProcessor) {
		this.contentProcessor = contentProcessor;
	}

	public void print() {
		String difference = StringUtils.repeat(
				ContentProcessor.DASH,
				this.contentProcessor.getSourceContent().getRawContent().length());
		difference = this.contentProcessor.carrotMaskBuilder(
				this.contentProcessor.asteriskMaskBuilder(difference));
		LOGGER.info("\n\n"
				+ this.contentProcessor.getSourceContent().getRawContent()
				+ "\n"
				+ difference
				+ "\n"
				+ this.contentProcessor.getCleanContent());
	}
}
