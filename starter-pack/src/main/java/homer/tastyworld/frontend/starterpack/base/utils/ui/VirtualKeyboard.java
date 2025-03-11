package homer.tastyworld.frontend.starterpack.base.utils.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VirtualKeyboard {

    private final VBox root;
    private ReadOnlyObjectProperty<Node> target;

    public VirtualKeyboard(ReadOnlyObjectProperty<Node> target, AnchorPane keyboardPane) {
        this.target = target;
        this.root = new VBox(10);
        root.setFillWidth(true);
        root.prefWidthProperty().bind(keyboardPane.widthProperty());
        root.prefHeightProperty().bind(keyboardPane.heightProperty());
        root.setPadding(new Insets(15));
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        keyboardPane.getChildren().add(root);

        final Modifiers modifiers = new Modifiers(root);

        final String[][] unshifted = new String[][] {
                { "ё", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "(", ")" },
                { "й", "ц", "у", "к", "е", "н", "г", "ш", "щ", "з", "х", "ъ", "." },
                { "ф", "ы", "в", "а", "п", "р", "о", "л", "д", "ж", "э" },
                { "я", "ч", "с", "м", "и", "т", "ь", "б", "ю", "," }
        };
        final String[][] shifted = new String[][] {
                { "Ё", ":", "\"", "#", ";", "%", "^", "&", "*", "=", "-", "_", "+" },
                { "Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш", "Щ", "З", "Х", "Ъ", "!" },
                { "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д", "Ж", "Э" },
                { "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "?" }
        };
        final KeyCode[][] codes = new KeyCode[][] {
                {
                        KeyCode.BACK_QUOTE, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3,
                        KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7,
                        KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.DIGIT0, KeyCode.SUBTRACT,
                        KeyCode.EQUALS
                },
                {
                        KeyCode.Q, KeyCode.W, KeyCode.E, KeyCode.R, KeyCode.T, KeyCode.Y,
                        KeyCode.U, KeyCode.I, KeyCode.O, KeyCode.P, KeyCode.OPEN_BRACKET,
                        KeyCode.CLOSE_BRACKET, KeyCode.BACK_SLASH
                },
                {
                        KeyCode.A, KeyCode.S, KeyCode.D, KeyCode.F, KeyCode.G, KeyCode.H,
                        KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.SEMICOLON, KeyCode.QUOTE
                },
                {
                        KeyCode.Z, KeyCode.X, KeyCode.C, KeyCode.V, KeyCode.B, KeyCode.N,
                        KeyCode.M, KeyCode.COMMA, KeyCode.PERIOD, KeyCode.SLASH
                }
        };

        final Button backspace = createNonshiftableButton("<--", KeyCode.BACK_SPACE, modifiers);

        final Node[][] extraLeftButtons = new Node[][] { {}, {}, {modifiers.capsLockKey()}, {modifiers.shiftKey()} };
        final Node[][] extraRightButtons = new Node[][] { {backspace}, {}, {}, {modifiers.secondShiftKey()} };

        for (int row = 0; row < unshifted.length; row++) {
            HBox hbox = new HBox(15);
            hbox.setAlignment(Pos.CENTER);
            hbox.setFillHeight(true);
            hbox.prefWidthProperty().bind(root.widthProperty());
            hbox.prefHeightProperty().bind(root.heightProperty().divide(unshifted.length + 2));
            root.getChildren().add(hbox);

            hbox.getChildren().addAll(extraLeftButtons[row]);
            for (int k = 0; k < unshifted[row].length; k++) {
                hbox.getChildren().add(
                        createShiftableButton(unshifted[row][k], shifted[row][k], codes[row][k], modifiers)
                );
            }
            hbox.getChildren().addAll(extraRightButtons[row]);
        }

        final Button spaceBar = createNonshiftableButton(" ", KeyCode.SPACE, modifiers);
        spaceBar.setMaxWidth(Double.POSITIVE_INFINITY);
        HBox.setHgrow(spaceBar, Priority.ALWAYS);

        final HBox bottomRow = new HBox(15);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.getChildren().addAll(spaceBar);
        root.getChildren().add(bottomRow);
        root.getChildren().add(new AnchorPane());
    }

    public VirtualKeyboard(AnchorPane keyboardPane) { this(null, keyboardPane); }

    private Button createShiftableButton(final String unshifted, final String shifted, final KeyCode code, Modifiers modifiers) {
        final ReadOnlyBooleanProperty letter = new SimpleBooleanProperty(unshifted.length() == 1 && Character.isLetter(unshifted.charAt(0)));
        final StringBinding text = Bindings.when(
                modifiers.shiftDown().or(modifiers.capsLockOn().and(letter))
        ).then(shifted).otherwise(unshifted);
        return createButton(text, code, modifiers);
    }

    private Button createNonshiftableButton(final String text, final KeyCode code, final Modifiers modifiers) {
        StringProperty textProperty = new SimpleStringProperty(text);
        return createButton(textProperty, code, modifiers);
    }

    private Button createButton(final ObservableStringValue text, final KeyCode code, final Modifiers modifiers) {
        final Button button = new Button();
        button.textProperty().bind(text);
        button.styleProperty().bind(Bindings.concat(
                "-fx-font-size: ", root.heightProperty().divide(15).asString("%.0f"), "px;"
        ));

        button.setFocusTraversable(false);

        button.setOnAction(event -> {
            final Node targetNode = this.target != null ? this.target.get() : root.getScene().getFocusOwner();

            if (targetNode != null) {
                final String character = text.get().length() == 1 ? text.get() : KeyEvent.CHAR_UNDEFINED;
                final KeyEvent keyPressEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_PRESSED, character, code, modifiers);
                targetNode.fireEvent(keyPressEvent);
                final KeyEvent keyReleasedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_RELEASED, character, code, modifiers);
                targetNode.fireEvent(keyReleasedEvent);
                if (!character.equals(KeyEvent.CHAR_UNDEFINED)) {
                    final KeyEvent keyTypedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_TYPED, character, code, modifiers);
                    targetNode.fireEvent(keyTypedEvent);
                }
                modifiers.releaseKeys();
            }
        });
        return button;
    }

    private KeyEvent createKeyEvent(Object source, EventTarget target, EventType<KeyEvent> eventType, String character, KeyCode code, Modifiers modifiers) {
        return new KeyEvent(
                source, target, eventType, character, code.toString(), code,
                modifiers.shiftDown().get(), false, false, false
        );
    }

    private static class Modifiers {

        private final ToggleButton shift;
        private final ToggleButton shift2;
        private final ToggleButton capsLock;
        private final VBox root;

        Modifiers(VBox root) {
            this.root = root;

            this.shift = createToggle("Shift");
            this.shift2 = createToggle("Shift");
            this.capsLock = createToggle("Caps");

            shift2.selectedProperty().bindBidirectional(shift.selectedProperty());
        }

        private ToggleButton createToggle(final String text) {
            final ToggleButton tb = new ToggleButton(text);
            tb.styleProperty().bind(Bindings.concat(
                    "-fx-font-size: ", root.heightProperty().divide(15).asString("%.0f"), "px;"
            ));
            tb.setFocusTraversable(false);
            return tb;
        }

        public Node shiftKey() {
            return shift;
        }

        public Node secondShiftKey() {
            return shift2;
        }

        public Node capsLockKey() {
            return capsLock;
        }

        public BooleanProperty shiftDown() {
            return shift.selectedProperty();
        }

        public BooleanProperty capsLockOn() {
            return capsLock.selectedProperty();
        }

        public void releaseKeys() {
            shift.setSelected(false);
        }

    }

    public void setTarget(ReadOnlyObjectProperty<Node> target) {
        this.target = target;
    }

}
