package homer.tastyworld.frontend.starterpack.utils.managers.external.printer;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class PrinterPageFactory {

    public static final int DEFAULT_WIDTH = 48;
    protected static final AppLogger logger = AppLogger.getFor(PrinterPageFactory.class);
    private int width = DEFAULT_WIDTH;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    protected abstract void setContent() throws Exception;

    public final byte[] getPage() {
        try {
            output.write(new byte[] {0x1B, 0x40});  // Init printer
            setContent();
            return output.toByteArray();
        } catch (Exception ex) {
            logger.error("Occurred exception while creating page to print", ex);
        }
        return output.toByteArray();
    }

    protected final void setFontStyle(byte[] style) throws IOException {
        output.write(style);
    }

    protected final void setFontStyle(int width) {
        this.width = width;
    }

    protected final void setFontStyle(byte[] style, int width) throws IOException {
        setFontStyle(style);
        setFontStyle(width);
    }

    protected final void dropFontStyle() throws IOException {
        setFontStyle(new byte[] {0x1B, 0x21, 0x00}, DEFAULT_WIDTH);
    }

    protected final void addEmptyLines(int qty) throws IOException {
        output.write("\n".repeat(Math.max(0, qty)).getBytes(StandardCharsets.US_ASCII));
    }

    protected final void addLine(String line) throws IOException {
        output.write(line.getBytes("CP866"));
        addEmptyLines(1);
    }

    protected final void addLineCenter(String line) throws IOException {
        output.write(0x1B);
        output.write(0x61);
        output.write(0x01);
        addLine(line);
        output.write(0x1B);
        output.write(0x61);
        output.write(0x00);
    }

    protected final void addLineRight(String line) throws IOException {
        addLine(String.format("%" + width + "s", line));
    }

    protected final void addLineLeft(String line) throws IOException {
        addLine(line);
    }

    protected final void addFullLine(String leftPart, char sep, String rightPart) throws IOException {
        int lines = 1;
        if (leftPart.length() > width) {
            lines++;
        }
        if (rightPart.length() > width) {
            lines++;
        }
        if (leftPart.length() + rightPart.length() > width && lines == 1) {
            lines++;
        }
        // rightPart = rightPart.length() > width ? rightPart.substring(0, width-3) + ".." : rightPart;
        // final int leftMaxLen = width - rightPart.length();
        // leftPart = leftPart.length() > leftMaxLen ? leftPart.substring(0, leftMaxLen-3) + ".." : leftPart;
        final String separator = " " + new String(new char[(width * lines) - leftPart.length() - rightPart.length() - 2]).replace('\0', sep) + " ";
        addLine(leftPart + separator + rightPart);
    }

    protected final void addDivider(char symbol) throws IOException {
        addLine(new String(new char[width]).replace('\0', symbol));
    }

}
