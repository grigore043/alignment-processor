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

import java.util.Iterator;
import java.util.List;

@HtmlImport("styles/shared-styles.html")
@Route("")
public class MainView extends VerticalLayout {

	private static final String FILL_THE_INPUTS = "Please add content into fields";
	private static final String INPUT_IN_PROGRESS = "Input...";
	private static final String PROCESSING = "Processing...";
	private static final String DONE = "Done";

	private Button processButton = new Button("Process");
	private TextField rawContentTextField = new TextField("Raw content");
	private TextField cleanedContentTextField = new TextField("Cleaned content");
	private Label processingStatusLabel = new Label(FILL_THE_INPUTS);
	private Div resultSectionClickable = new Div();
	private Div resultSectionNonClickable = new Div();

	private StringBuilder dashStringBuilder;


	public MainView() {
		addClassName("main-view");
		H1 header = new H1("Alignment Processor");
		setHorizontalComponentAlignment(Alignment.END, processButton);

		rawContentTextField.setWidth("100%");
		cleanedContentTextField.setWidth("100%");
		processButton.addClickListener(getButtonClickListener());
		rawContentTextField.addFocusListener(getFocusListener());
		cleanedContentTextField.addFocusListener(getFocusListener());

		add(header, rawContentTextField, cleanedContentTextField, processingStatusLabel, processButton);
		add(resultSectionClickable, resultSectionNonClickable);
	}

	@NotNull
	private ComponentEventListener<ClickEvent<Button>> getButtonClickListener() {
		return buttonClickEvent -> {
			String rawContent = this.rawContentTextField.getValue();
			String cleanedContent = this.cleanedContentTextField.getValue();
			if (cleanedContent.length() == 0 || rawContent.length() == 0) {
				this.processingStatusLabel.setText("Empty content is not allowed");
			} else if (cleanedContent.length() >= rawContent.length()) {
				this.processingStatusLabel.setText("Cleaned content cannot be longer that raw content");
			} else {
				resultSectionClickable.removeAll();
				resultSectionNonClickable.removeAll();
				SourceContent sourceContent = new SourceContent(rawContent, cleanedContent);
				ContentProcessor contentProcessor = new ContentProcessor(sourceContent);
				if (contentProcessor.canBeProcessed()) {
					processingStatusLabel.setText(PROCESSING);
					String dashString = StringUtils.repeat(
							ContentProcessor.DASH,
							contentProcessor.getSourceContent().getRawContent().length());
					Div rawContentStringDiv = new Div();
					rawContentStringDiv.setText(sourceContent.getRawContent());
					Div differenceStringDiv = new Div();
					differenceStringDiv.setText(contentProcessor.
							asteriskMaskBuilder(contentProcessor.
									carrotMaskBuilder(dashString)));
					Div formattedCleanedContentDiv = new Div();
					formattedCleanedContentDiv.setText(contentProcessor.getCleanContent(ContentProcessor.UNDERSCORE));

					processingStatusLabel.setText(DONE);
					resultSectionNonClickable.add(
							rawContentStringDiv,
							differenceStringDiv,
							formattedCleanedContentDiv);
					createSpanPair(contentProcessor, differenceStringDiv.getText());
				} else {
					this.processingStatusLabel.setText("Cleaned content is not part of the raw content");
				}
			}
		};
	}

	public void createSpanPair(ContentProcessor contentProcessor, String difference) {
		List<String> rawContentList = contentProcessor.getRawContentAsList();
		List<String> cleanedContentList = contentProcessor.getCleanedContentAsList();
		Div rawContentDiv = new Div();
		Div cleanedContentDiv = new Div();
		Iterator<String> rawContentIterator = rawContentList.iterator();
		while (rawContentIterator.hasNext()) {
			String rawWord = rawContentIterator.next();
			String cleanedWord = cleanedContentList.stream().filter(rawWord::contains).findFirst().orElse("");
			if (!cleanedWord.isEmpty()) {
				Span rawWordSpan = new Span(rawWord);
				rawWordSpan.addClassName("pair-" + rawContentList.indexOf(rawWord));
				rawContentDiv.add(rawWordSpan);
				Span cleanedWordSpan = new Span(cleanedWord);
				cleanedWordSpan.addClassName(rawWordSpan.getClassName());
				cleanedContentDiv.add(cleanedWordSpan);
				ComponentEventListener<ClickEvent<Span>> listener = clickEvent -> {
					if (rawWordSpan.getClassNames().contains("blue")) {
						rawWordSpan.getClassNames().remove("blue");
						cleanedWordSpan.getClassNames().remove("blue");
					} else {
						rawWordSpan.addClassName("blue");
						cleanedWordSpan.addClassName("blue");
					}
				};
				rawWordSpan.addClickListener(listener);
				cleanedWordSpan.addClickListener(listener);
				cleanedContentDiv.add(new Span(" "));
			} else {
				rawContentDiv.add(new Span(rawWord));
			}
			rawContentDiv.add(new Span(" "));

		}
		Div differenceDiv = new Div();
		differenceDiv.setText(difference);
		resultSectionClickable.add(rawContentDiv);
		resultSectionClickable.add(differenceDiv);
		resultSectionClickable.add(cleanedContentDiv);
	}

	private ComponentEventListener<FocusEvent<TextField>> getFocusListener() {
		return (ComponentEventListener<FocusEvent<TextField>>) componentEvent -> processingStatusLabel.setText(INPUT_IN_PROGRESS);
	}

}
