package alignment.processor;

import java.util.logging.Logger;

/**
 * Hello world!
 */
public class AlignmentProcessor {

	private final static Logger LOGGER = Logger.getLogger(AlignmentProcessor.class.getSimpleName());

	public static void main(String[] args) {
		ContentLoader contentLoader = new ContentLoader();
		ContentProcessor contentProcessor = new ContentProcessor(contentLoader.getSourceContent());
		new ProcessedContentPrinter(contentProcessor).print();
	}
}
