package homer.tastyworld.frontend.pos.creator.core.vkb;

import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.utils.misc.FileDirectories;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.AdaptiveTextHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualKeyboardPrompts {

    public static final int PROMPTS_QTY = 3;
    private static final File USED_PROMPTS_DIR = new File(AppConfig.APP_DATA_DIR, "VirtualKeyboardPrompts");
    private static final String USED_PROMPTS_FILE_EXTENSION = ".twvkp";  // TastyWorldVirtualKeyboardPrompts
    private static final Label[] PROMPTS_PLACE = new Label[PROMPTS_QTY];
    private static final Map<String, PromptsProcessor> processors = new ConcurrentHashMap<>();

    static {
        FileDirectories.create(USED_PROMPTS_DIR);
    }

    private static PromptsProcessor getProcessor(TextField field) {
        return processors.computeIfAbsent(
                field.getId(), name -> new PromptsProcessor(new File(USED_PROMPTS_DIR, name + USED_PROMPTS_FILE_EXTENSION))
        );
    }

    public static void setPromptsContainer(HBox promptsContainer) {
        promptsContainer.setSpacing(2);
        promptsContainer.setAlignment(Pos.CENTER);
        promptsContainer.setFillHeight(true);
        for (int i = 0; i < PROMPTS_QTY; i++) {
            promptsContainer.getChildren().add(createClickablePrompt(promptsContainer, i));
        }
    }

    private static AnchorPane createClickablePrompt(HBox promptsContainer, int index) {
        AnchorPane prompt = new AnchorPane();
        HBox.setHgrow(prompt, Priority.ALWAYS);
        prompt.prefHeightProperty().bind(promptsContainer.heightProperty().multiply(0.95));
        prompt.prefWidthProperty().bind(promptsContainer.widthProperty().divide(PROMPTS_QTY));
        PROMPTS_PLACE[index] = AdaptiveTextHelper.setTextCentre(prompt, "", 15, Color.web("#555555"));
        return prompt;
    }

    public static void setInputField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> setPrompts(field, newValue));
    }

    private static void setPrompts(TextField field, final String input) {
        PromptsProcessor processor = getProcessor(field);
        clean();
        String[] prompts = processor.get(input, PROMPTS_QTY);
        for (int i = 0; i < prompts.length; i++) {
            putClickablePrompt(field, prompts[i], i);
        }
    }

    private static void putClickablePrompt(TextField field, String prompt, int index) {
        PROMPTS_PLACE[index].setText(prompt);
        PROMPTS_PLACE[index].setOnMouseClicked(event -> {
            clean();
            field.setText(prompt);
        });
    }

    public static void appendVar(TextField field) {
        PromptsProcessor processor = getProcessor(field);
        processor.put(field.getText().trim());
        processor.save();  // TODO: optimize & save not for each time
    }

    public static void clean() {
        Arrays.stream(PROMPTS_PLACE).forEach(place -> {
            place.setText("");
            place.setOnMouseClicked(e -> {});
        });
    }

}
