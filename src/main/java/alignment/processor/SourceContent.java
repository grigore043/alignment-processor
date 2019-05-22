package alignment.processor;


import org.jetbrains.annotations.NotNull;

public class SourceContent {

	private String rawContent;
	private String cleanedContent;

	public SourceContent(@NotNull String rawContent, @NotNull String cleanedContent) {
		this.rawContent = rawContent;
		this.cleanedContent = cleanedContent;
	}

	public String getRawContent() {
		return rawContent;
	}

	public String getCleanedContent() {
		return cleanedContent;
	}
}
