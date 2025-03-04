package homer.tastyworld.frontend.poscreator.core.vkb;

import homer.tastyworld.frontend.starterpack.base.config.AppConfig;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.Text;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualKeyboardPrompts {

    private static final String pathBeforeFileName;
    private static final Map<String, PromptsProcessor> processors = new ConcurrentHashMap<>();
    private static ObservableList<Node> promptsContainer;

    static {
        File dir = new File(AppConfig.APP_DATA_DIR, "VirtualKeyboardPrompts");
        dir.mkdirs();
        pathBeforeFileName = dir.getAbsolutePath() + File.separator;
    }

    private static PromptsProcessor getProcessor(TextField field) {
        final String processorName = field.getId();
        return processors.computeIfAbsent(
                processorName, name -> new PromptsProcessor(pathBeforeFileName + processorName + ".vkbp")
        );
    }

    public static void setPromptsContainer(HBox promptsContainer) {
        promptsContainer.setSpacing(15);
        promptsContainer.setAlignment(Pos.CENTER);
        promptsContainer.setFillHeight(true);
        VirtualKeyboardPrompts.promptsContainer = promptsContainer.getChildren();
    }

    public static void setInputField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> setPrompts(field, newValue));
    }

    private static void setPrompts(TextField field, final String input) {
        PromptsProcessor processor = getProcessor(field);
        clean();
        Arrays.stream(processor.get(input, 3)).forEach(prompt -> promptsContainer.add(getClickablePrompt(field, prompt)));
    }

    private static Node getClickablePrompt(TextField field, String prompt) {
        AnchorPane pane = new AnchorPane();
        Text.setTextCentre(pane, prompt, Text.getAdaptiveFontSize(pane, 5), null);
        pane.setOnMouseClicked(event -> {
            clean();
            field.setText(prompt);
        });
        return pane;
    }

    public static void appendVar(TextField field) {
        PromptsProcessor processor = getProcessor(field);
        processor.put(field.getText().trim());
        processor.save();  // TODO: optimize & save not for each time
    }

    public static void clean() {
        promptsContainer.clear();
    }

}
