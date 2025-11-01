package homer.tastyworld.frontend.starterpack.utils.ui.vkb;

import homer.tastyworld.frontend.starterpack.utils.ui.helpers.AdaptiveTextHelper;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.Nullable;

class VirtualKeyboardKeys {

    private static final String KEY_STYLE = (
            "-fx-background-color: #f0f0f0;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #dedede;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
    );
    private static final String KEY_STYLE_PRESSED = (
            "-fx-background-color: transparent;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
    );

    private static class Modifiers {

        private static final String KEY_STYLE_ACTIVE = (
                "-fx-background-color: #f0f0f0;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #ffd54f;" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;" +
                "-fx-cursor: hand;"
        );

        private final BooleanProperty isShiftDown = new SimpleBooleanProperty(false);
        private final BooleanProperty isCapsLockOn = new SimpleBooleanProperty(false);

        public AnchorPane createShift() {
            final AnchorPane shift = new AnchorPane();
            AdaptiveTextHelper.setTextCentre(shift, "Shift", 0.4, null);

            shift.setStyle(KEY_STYLE);
            shift.setOnMousePressed(e -> {
                shift.setStyle(KEY_STYLE_PRESSED);
                isShiftDown.setValue(!isShiftDown.get());
            });
            isShiftDown.addListener((observable, oldValue, newValue) -> {
                shift.setStyle(newValue.equals(true) ? KEY_STYLE_ACTIVE : KEY_STYLE);
            });
            shift.setFocusTraversable(false);

            return shift;
        }

        public AnchorPane createCapsLock() {
            final AnchorPane caps = new AnchorPane();
            AdaptiveTextHelper.setTextCentre(caps, "Caps", 0.4, null);

            caps.setStyle(KEY_STYLE);
            caps.setOnMousePressed(e -> {
                caps.setStyle(KEY_STYLE_PRESSED);
                isCapsLockOn.setValue(!isCapsLockOn.get());
            });
            isCapsLockOn.addListener((observable, oldValue, newValue) -> {
                caps.setStyle(newValue.equals(true) ? KEY_STYLE_ACTIVE : KEY_STYLE);
            });
            caps.setFocusTraversable(false);

            return caps;
        }

        public BooleanProperty isShiftDown() {
            return isShiftDown;
        }

        public BooleanProperty isCapsLockOn() {
            return isCapsLockOn;
        }

        public void releaseShift() {
            isShiftDown.setValue(false);
        }

    }

    private static final String[][] UNSHIFTED = new String[][] {
            { "ё", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "(", ")" },
            { "й", "ц", "у", "к", "е", "н", "г", "ш", "щ", "з", "х", "ъ", "." },
            { "ф", "ы", "в", "а", "п", "р", "о", "л", "д", "ж", "э" },
            { "я", "ч", "с", "м", "и", "т", "ь", "б", "ю", "," }
    };
    private static final String[][] SHIFTED = new String[][] {
            { "Ё", ":", "\"", "#", ";", "%", "^", "&", "*", "=", "-", "_", "+" },
            { "Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш", "Щ", "З", "Х", "Ъ", "!" },
            { "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д", "Ж", "Э" },
            { "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "?" }
    };
    private static final KeyCode[][] CODES = new KeyCode[][] {
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

    private static KeyEvent createKeyEvent(Object source, EventTarget target, EventType<KeyEvent> eventType, String character, KeyCode code, @Nullable Modifiers modifiers) {
        return new KeyEvent(
                source, target, eventType, character, code.toString(), code,
                modifiers != null && modifiers.isShiftDown().get(), false, false, false
        );
    }

    public static AnchorPane createSpaceKey(Node root, @Nullable Node target) {
        String character = " ";
        KeyCode code = KeyCode.SPACE;
        AnchorPane key = new AnchorPane();
        key.setFocusTraversable(false);

        key.setStyle(KEY_STYLE);
        key.setOnMousePressed(e -> {
            key.setStyle(KEY_STYLE_PRESSED);
            Node targetNode = target != null ? target : root.getScene().getFocusOwner();
            if (targetNode != null) {
                KeyEvent keyPressEvent = createKeyEvent(key, targetNode, KeyEvent.KEY_PRESSED, character, code, null);
                targetNode.fireEvent(keyPressEvent);
                KeyEvent keyReleasedEvent = createKeyEvent(key, targetNode, KeyEvent.KEY_RELEASED, character, code, null);
                targetNode.fireEvent(keyReleasedEvent);
                KeyEvent keyTypedEvent = createKeyEvent(key, targetNode, KeyEvent.KEY_TYPED, character, code, null);
                targetNode.fireEvent(keyTypedEvent);
            }
        });
        key.setOnMouseReleased(e -> {
            key.setStyle(KEY_STYLE);
        });
        key.setFocusTraversable(false);

        return key;
    }

    private static AnchorPane createKey(ObservableStringValue text, KeyCode code, Modifiers modifiers, Node root, @Nullable Node target) {
        AnchorPane key = new AnchorPane();
        key.setFocusTraversable(false);
        Label textLabel = AdaptiveTextHelper.setTextCentre(key, "", 0.6, null);
        textLabel.textProperty().bind(text);

        key.setStyle(KEY_STYLE);
        key.setOnMousePressed(e -> {
            key.setStyle(KEY_STYLE_PRESSED);
            Node targetNode = target != null ? target : root.getScene().getFocusOwner();
            if (targetNode != null) {
                String character = text.get().length() == 1 ? text.get() : KeyEvent.CHAR_UNDEFINED;
                KeyEvent keyPressEvent = createKeyEvent(key, targetNode, KeyEvent.KEY_PRESSED, character, code, modifiers);
                targetNode.fireEvent(keyPressEvent);
                KeyEvent keyReleasedEvent = createKeyEvent(key, targetNode, KeyEvent.KEY_RELEASED, character, code, modifiers);
                targetNode.fireEvent(keyReleasedEvent);
                if (!character.equals(KeyEvent.CHAR_UNDEFINED)) {
                    KeyEvent keyTypedEvent = createKeyEvent(key, targetNode, KeyEvent.KEY_TYPED, character, code, modifiers);
                    targetNode.fireEvent(keyTypedEvent);
                }
            }
        });
        key.setOnMouseReleased(e -> {
            key.setStyle(KEY_STYLE);
            modifiers.releaseShift();
        });
        key.setFocusTraversable(false);

        return key;
    }

    private static AnchorPane createNonshiftableKey(String text, KeyCode code, Modifiers modifiers, Node root, @Nullable Node target) {
        StringProperty textProperty = new SimpleStringProperty(text);
        return createKey(textProperty, code, modifiers, root, target);
    }

    private static AnchorPane createShiftableKey(String unshifted, String shifted, KeyCode code, Modifiers modifiers, Node root, @Nullable Node target) {
        final ReadOnlyBooleanProperty letter = new SimpleBooleanProperty(unshifted.length() == 1 && Character.isLetter(unshifted.charAt(0)));
        final StringBinding text = Bindings
                .when(modifiers.isShiftDown().or(modifiers.isCapsLockOn().and(letter)))
                .then(shifted)
                .otherwise(unshifted);
        return createKey(text, code, modifiers, root, target);
    }

    public static AnchorPane[][] createKeys(Node root, @Nullable Node target) {
        AnchorPane[][] keyRows = new AnchorPane[UNSHIFTED.length][];

        Modifiers modifiers = new Modifiers();

        AnchorPane[][] extraLeftKeys = new AnchorPane[][] {
                {},
                {},
                {modifiers.createCapsLock()},
                {modifiers.createShift()}
        };
        AnchorPane[][] extraRightKeys = new AnchorPane[][] {
                {createNonshiftableKey("<--", KeyCode.BACK_SPACE, modifiers, root, target)},
                {},
                {},
                {modifiers.createShift()}
        };

        for (int rowIndex = 0; rowIndex < UNSHIFTED.length; rowIndex++) {
            AnchorPane[] row = new AnchorPane[extraLeftKeys[rowIndex].length + UNSHIFTED[rowIndex].length + extraRightKeys[rowIndex].length];
            int keyIndex = 0;
            for (AnchorPane key : extraLeftKeys[rowIndex]) {
                row[keyIndex++] = key;
            }
            for (int i = 0; i < UNSHIFTED[rowIndex].length; i++) {
                row[keyIndex++] = createShiftableKey(
                        UNSHIFTED[rowIndex][i], SHIFTED[rowIndex][i], CODES[rowIndex][i], modifiers, root, target
                );
            }
            for (AnchorPane key : extraRightKeys[rowIndex]) {
                row[keyIndex++] = key;
            }
            keyRows[rowIndex] = row;
        }

        return keyRows;
    }

}
