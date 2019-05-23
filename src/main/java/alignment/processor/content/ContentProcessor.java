package alignment.processor.content;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentProcessor {

	private static final Logger LOGGER = Logger.getLogger(ContentProcessor.class.getSimpleName());

	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s|,|\\.|\"|\\\\|\\/");
	private static final Pattern BETWEEN_GROUPS_PATTERN = Pattern.compile("(?:[\\s\\S]+)");
	private static final Pattern SUFFIX_MASKING_PATTERN = Pattern.compile("(\\w*)?(?:.*)");

	public static final String CARROT = "^";
	public static final String ASTERISK = "*";
	public static final String DASH = "-";
	public static final String SPACE = " ";
	public static final String UNDERSCORE = "_";

	private final SourceContent sourceContent;
	private final Pattern asteriskContentPattern;
	private final Pattern carrotMaskingPattern;

	/**
	 * Constructor of {@link ContentProcessor}.
	 * Require a non null instance of {@link SourceContent}.
	 *
	 * @param sourceContent content required for processing.
	 */
	public ContentProcessor(@NotNull SourceContent sourceContent) {
		this.sourceContent = sourceContent;
		this.asteriskContentPattern = createAsteriskMaskingPattern();
		this.carrotMaskingPattern = createCarrotMaskingPattern();
		LOGGER.info("\nCarrot masking pattern:" + carrotMaskingPattern.pattern());
		LOGGER.info("\nAsterisk masking pattern:" + this.asteriskContentPattern.pattern());
	}

	/**
	 * If the cleaned content is not part of the raw connect then this method will return false.
	 *
	 * @return processable state of the content.
	 */
	public boolean canBeProcessed() {
		Matcher matcher = this.asteriskContentPattern.matcher(this.getSourceContent().getRawContent());
		return matcher.find() && matcher.groupCount() == getCleanedContentAsArray().length;
	}

	private Pattern createCarrotMaskingPattern() {
		StringBuilder patternBuilder = new StringBuilder();
		for (String word : getCleanedContentAsArray()) {
			patternBuilder
					.append("(?:")
					.append(word)
					.append(")")
					.append(SUFFIX_MASKING_PATTERN);
		}
		return Pattern.compile(patternBuilder.toString());
	}

	private Pattern createAsteriskMaskingPattern() {
		StringBuilder patternBuilder = new StringBuilder();
		Iterator<String> wordsIterator = new ArrayList<>(Arrays.asList(getCleanedContentAsArray())).iterator();
		while (wordsIterator.hasNext()) {
			String word = wordsIterator.next();
			patternBuilder
					.append("(")
					.append(word)
					.append(")");
			if (wordsIterator.hasNext()) {
				patternBuilder.append(BETWEEN_GROUPS_PATTERN.pattern());
			}
		}
		return Pattern.compile(patternBuilder.toString());
	}

	/**
	 * This method will return all the cleaned content as array od strings.
	 *
	 * @return every word from cleaned content as an array.
	 */

	public String[] getCleanedContentAsArray() {
		return this.sourceContent.getCleanedContent().split(SPLIT_PATTERN.pattern());
	}

	public List<String> getCleanedContentAsList() {
		return Arrays.asList(this.sourceContent.getCleanedContent().split(SPLIT_PATTERN.pattern()));
	}

	public List<String> getRawContentAsList() {
		return Arrays.asList(this.sourceContent.getRawContent().split(SPLIT_PATTERN.pattern()));
	}


	/**
	 * This method will return the current {@link SourceContent} instance.
	 *
	 * @return a {@link SourceContent} instance.
	 */
	public SourceContent getSourceContent() {
		return sourceContent;
	}

	/**
	 * This method will replace certain characters inside the {@link String} parameter
	 * based on the carrotMaskingPattern.
	 *
	 * @param maskingTarget a non null instance of {@link String}.
	 * @return a {@link String} instance.
	 */
	public String carrotMaskBuilder(@NotNull String maskingTarget) {
		return maskBuilder(maskingTarget, this.carrotMaskingPattern, CARROT).toString();
	}

	/**
	 * This method will replace certain characters inside the {@link String} parameter
	 * based on the asteriskContentPattern.
	 *
	 * @param maskingTarget a non null instance of {@link String}.
	 * @return a {@link String} instance.
	 */
	public String asteriskMaskBuilder(@NotNull String maskingTarget) {
		return maskBuilder(maskingTarget, this.asteriskContentPattern, ASTERISK).toString();
	}

	/**
	 * This method will the masking of the given instance of {@link StringBuilder}
	 * based on the given instance of {@link Pattern}.
	 * For masking will be used the replacement parameter.
	 *
	 * @param maskingTarget a non null instance of {@link String}.
	 * @param pattern       a non null compiled instance of {@link Pattern}.
	 * @param replacement   a non null replacement string.
	 * @return
	 */
	protected StringBuilder maskBuilder(@NotNull String maskingTarget, @NotNull Pattern pattern, @NotNull String replacement) {
		StringBuilder stringBuilder = new StringBuilder(maskingTarget);
		Matcher matcher = pattern.matcher(this.sourceContent.getRawContent());
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				if (matcher.group(i) == null || matcher.group(i).isEmpty()) {
					continue;
				}
				stringBuilder.replace(
						matcher.start(i),
						matcher.end(i),
						StringUtils.repeat(replacement, matcher.group(i).length()));
			}
		}
		return stringBuilder;
	}

	/**
	 * This method will create a new instance of {@link String} with the same size
	 * as the {@link SourceContent} 's raw content filed.
	 * It will contain the {@link SourceContent} 's cleaned content.
	 *
	 * @return new instance of {@link StringBuilder}.
	 */
	public String getCleanContent() {
		return cleanContentBuilder(null);
	}

	/**
	 * This method will create a new instance of {@link String} with the same size
	 * as the {@link SourceContent} 's raw content filed.
	 * It will contain the formatted {@link SourceContent} 's cleaned content.
	 *
	 * @param spaceMask masking string for space character.
	 * @return new instance of {@link StringBuilder}.
	 */
	public String getCleanContent(@NotNull String spaceMask) {
		return cleanContentBuilder(spaceMask);
	}

	private String cleanContentBuilder(String builderFill) {
		String fill = (builderFill == null) ? SPACE : builderFill;
		StringBuilder stringBuilder = new StringBuilder(StringUtils.repeat(
				fill,
				this.sourceContent.getRawContent().length()));
		Matcher matcher = asteriskContentPattern.matcher(this.sourceContent.getRawContent());
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				stringBuilder.replace(
						matcher.start(i),
						matcher.end(i),
						matcher.group(i));
			}
		}
		return stringBuilder.toString();
	}
}
