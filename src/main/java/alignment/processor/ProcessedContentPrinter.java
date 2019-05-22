package alignment.processor;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

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
		StringBuilder differenceBuilder = new StringBuilder(
				new StringBuilder(StringUtils.repeat(
						ContentProcessor.DASH,
						this.contentProcessor.getSourceContent().getRawContent().length())));
		differenceBuilder = this.contentProcessor.carrotMaskBuilder(
				this.contentProcessor.asteriskMaskBuilder(
						differenceBuilder.toString()).toString());
		LOGGER.info("\n\n"
				+ this.contentProcessor.getSourceContent().getRawContent()
				+ "\n"
				+ differenceBuilder
				+ "\n"
				+ this.contentProcessor.cleanContentBuilder());
	}
}
