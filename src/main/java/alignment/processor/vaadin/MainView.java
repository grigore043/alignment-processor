package alignment.processor.vaadin;

import alignment.processor.content.ContentProcessor;
import alignment.processor.content.SourceContent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@HtmlImport("styles/shared-styles.html")
@Route("")
public class MainView extends VerticalLayout {

	private static final String FILL_THE_INPUTS = "Please add content into fields";
	private static final String INPUT_IN_PROGRESS = "Input...";
	private static final String PROCESSING = "Processing...";
	private static final String DONE = "Done";
	private static final String EASY_READ_RESULT = "Easy read result";
	private static final String CLICKABLE_RESULT = "Clickable result";

	private TextField rawContentTextField = new TextField("Raw content");
	private TextField cleanedContentTextField = new TextField("Cleaned content");
	private Label processingStatusLabel = new Label(FILL_THE_INPUTS);
	private Div resultSectionClickable = new Div();
	private Div resultSectionEasyRead = new Div();

	private StringBuilder dashStringBuilder;


	public MainView() {
		addClassName("main-view");
		H1 header = new H1("Alignment Processor");
		Button processButton = new Button("Process");
		setHorizontalComponentAlignment(Alignment.END, processButton);

		rawContentTextField.setWidth("100%");
		cleanedContentTextField.setWidth("100%");
		processButton.addClickListener(createProcessButtonClickListener());
		rawContentTextField.addFocusListener(createInputFocusListener());
		cleanedContentTextField.addFocusListener(createInputFocusListener());

		add(header, rawContentTextField, cleanedContentTextField, processingStatusLabel, processButton);
		add(resultSectionClickable, resultSectionEasyRead);
	}

	@NotNull
	private ComponentEventListener<ClickEvent<Button>> createProcessButtonClickListener() {
		return buttonClickEvent -> {
			String rawContent = this.rawContentTextField.getValue();
			String cleanedContent = this.cleanedContentTextField.getValue();
			if (cleanedContent.length() == 0 || rawContent.length() == 0) {
				this.processingStatusLabel.setText("Empty content is not allowed");
			} else if (cleanedContent.length() >= rawContent.length()) {
				this.processingStatusLabel.setText("Cleaned content cannot be longer that raw content");
			} else {
				resultSectionClickable.removeAll();
				resultSectionEasyRead.removeAll();
				ContentProcessor contentProcessor = new ContentProcessor(new SourceContent(rawContent, cleanedContent));
				if (contentProcessor.canBeProcessed()) {
					processValidContent(contentProcessor);
				} else {
					this.processingStatusLabel.setText("Cleaned content is not part of the raw content");
				}
			}
		};
	}

	private void processValidContent(ContentProcessor contentProcessor) {
		processingStatusLabel.setText(PROCESSING);
		String dashString = StringUtils.repeat(
				ContentProcessor.DASH,
				contentProcessor.getSourceContent().getRawContent().length());
		Div rawContentStringDiv = new Div();
		rawContentStringDiv.setText(contentProcessor.getSourceContent().getRawContent());
		Div differenceStringDiv = new Div();
		differenceStringDiv.setText(
				contentProcessor
						.asteriskMaskBuilder(
								contentProcessor
										.carrotMaskBuilder(dashString)));
		Div formattedCleanedContentDiv = new Div();
		formattedCleanedContentDiv.setText(contentProcessor.getCleanContent(ContentProcessor.UNDERSCORE));
		processingStatusLabel.setText(DONE);
		resultSectionEasyRead.add(
				new Label(EASY_READ_RESULT),
				rawContentStringDiv,
				differenceStringDiv,
				formattedCleanedContentDiv);
		createSpanPair(contentProcessor, differenceStringDiv.getText());
	}

	private void createSpanPair(ContentProcessor contentProcessor, String difference) {
		List<String> rawContentList = contentProcessor.getRawContentAsList();
		ArrayList<String> cleanedContentList = new ArrayList<>(contentProcessor.getCleanedContentAsList());
		Div rawContentDiv = new Div();
		Div cleanedContentDiv = new Div();
		for (String rawWord : rawContentList) {
			String cleanedWord = cleanedContentList
					.stream()
					.filter(rawWord::contains)
					.findFirst()
					.orElse("");
			if (!cleanedWord.isEmpty()) {
				Span rawWordSpan = new Span(rawWord);
				rawWordSpan.addClassName("pair-" + rawContentList.indexOf(rawWord));
				rawContentDiv.add(rawWordSpan);
				Span cleanedWordSpan = new Span(cleanedWord);
				cleanedWordSpan.addClassName(rawWordSpan.getClassName());
				cleanedContentDiv.add(cleanedWordSpan);
				ComponentEventListener<ClickEvent<Span>> listener = createSpanClickEvent(rawWordSpan, cleanedWordSpan);
				rawWordSpan.addClickListener(listener);
				cleanedWordSpan.addClickListener(listener);
				cleanedContentDiv.add(new Span(" "));
				cleanedContentList.removeIf(cleanedWord::equals);
			} else {
				rawContentDiv.add(new Span(rawWord));
			}
			rawContentDiv.add(new Span(" "));

		}
		Div differenceDiv = new Div();
		differenceDiv.setText(difference);
		resultSectionClickable.add(
				new Label(CLICKABLE_RESULT),
				rawContentDiv,
//				differenceDiv,
				cleanedContentDiv);
	}

	@NotNull
	private ComponentEventListener<ClickEvent<Span>> createSpanClickEvent(Span rawWordSpan, Span cleanedWordSpan) {
		return clickEvent -> {
			if (rawWordSpan.getClassNames().contains("blue")) {
				rawWordSpan.getClassNames().remove("blue");
				cleanedWordSpan.getClassNames().remove("blue");
			} else {
				rawWordSpan.addClassName("blue");
				cleanedWordSpan.addClassName("blue");
			}
		};
	}

	private ComponentEventListener<FocusEvent<TextField>> createInputFocusListener() {
		return (ComponentEventListener<FocusEvent<TextField>>) componentEvent -> processingStatusLabel.setText(INPUT_IN_PROGRESS);
	}

}
