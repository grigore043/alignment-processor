package alignment.processor.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ContentLoader {

	private static final Logger LOGGER = Logger.getLogger(ContentLoader.class.getSimpleName());
	private static final String CONTENT_FILE_PATH = "target/classes/content";

	/**
	 * This method will return an instace of {@link SourceContent} with the content
	 * from the /resource/content file.
	 *
	 * @return instance of {@link SourceContent}
	 */
	public SourceContent getSourceContent() {
		try (Scanner scanner = new Scanner(new File(CONTENT_FILE_PATH))) {
			String rawContent = null;
			String cleanedContent = null;
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.startsWith("#") || line.isEmpty())
					continue;
				if (rawContent == null) {
					rawContent = line;
					continue;
				}
				if (cleanedContent == null)
					cleanedContent = line;
			}
			assert rawContent != null : "No raw content has been found";
			assert cleanedContent != null : "No cleaned content has been found";
			return new SourceContent(rawContent, cleanedContent);

		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Content wasn't found under " + CONTENT_FILE_PATH, e);
		}
		return null;
	}

}
