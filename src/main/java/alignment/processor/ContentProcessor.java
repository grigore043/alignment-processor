package alignment.processor;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentProcessor {

	private static final Logger LOGGER = Logger.getLogger(ContentProcessor.class.getSimpleName());

	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s|,|\\.|\"|\\\\|\\/");
	private static final Pattern BETWEEN_GROUPS_PATTERN = Pattern.compile("(?:[\\s\\S]+)");
	private static final Pattern SUFFIX_MASKING_PATTERN = Pattern.compile("(\\w*)?(?:[\\s\\S]+)");

	public static final String CARROT = "^";
	public static final String ASTERISK = "*";
	public static final String DASH = "-";
	public static final String SPACE = " ";

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
		this.asteriskContentPattern = createAndCompilePattern();
		this.carrotMaskingPattern = createCarrotMaskingPattern();
		LOGGER.info("\nCarrot masking pattern:" + carrotMaskingPattern.pattern());
		LOGGER.info("\nAsterisk masking pattern:" + this.asteriskContentPattern.pattern());
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

	private Pattern createAndCompilePattern() {
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
	@NotNull
	public String[] getCleanedContentAsArray() {
		return this.sourceContent.getCleanedContent().split(SPLIT_PATTERN.pattern());
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
	 * This method will replace certain characters inside the {@link StringBuilder} parameter
	 * based on the carrotMaskingPattern.
	 *
	 * @param stringBuilder a non null instance of {@link StringBuilder}.
	 * @return the same {@link StringBuilder} instance.
	 */
	public StringBuilder carrotMaskBuilder(@NotNull StringBuilder stringBuilder) {
		return maskBuilder(stringBuilder, this.carrotMaskingPattern, CARROT);
	}

	/**
	 * This method will replace certain characters inside the {@link StringBuilder} parameter
	 * based on the asteriskContentPattern.
	 *
	 * @param stringBuilder a non null instance of {@link StringBuilder}.
	 * @return the same {@link StringBuilder} instance.
	 */
	public StringBuilder asteriskMaskBuilder(@NotNull StringBuilder stringBuilder) {
		return maskBuilder(stringBuilder, this.asteriskContentPattern, ASTERISK);
	}

	/**
	 * This method will create a new instance of {@link StringBuilder} with the same size
	 * as the {@link SourceContent} 's raw content filed.
	 * It will contain the {@link SourceContent} 's cleaned content.
	 *
	 * @return new instance of {@link StringBuilder}.
	 */
	public StringBuilder cleanContentBuilder() {
		StringBuilder stringBuilder = new StringBuilder(StringUtils.repeat(
				SPACE,
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
		return stringBuilder;
	}

	/**
	 * This method will the masking of the given instance of {@link StringBuilder}
	 * based on the given instance of {@link Pattern}.
	 * For masking will be used the replacement parameter.
	 *
	 * @param stringBuilder a non null instance of {@link StringBuilder}.
	 * @param pattern       a non null compiled instance of {@link Pattern}.
	 * @param replacement   a non null replacement string.
	 * @return
	 */
	protected StringBuilder maskBuilder(@NotNull StringBuilder stringBuilder, @NotNull Pattern pattern, @NotNull String replacement) {
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
}
