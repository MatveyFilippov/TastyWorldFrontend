package homer.tastyworld.frontend.starterpack.base.utils.managers.printer;

import homer.tastyworld.frontend.starterpack.base.AppLogger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class PrinterPageFactory {

    protected static final AppLogger logger = AppLogger.getFor(PrinterPageFactory.class);
    public final int WIDTH;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    protected PrinterPageFactory(int width) {
        this.WIDTH = width;
    }

    protected abstract void setContent() throws Exception;

    public byte[] getPage() {
        try {
            output.write(new byte[] {0x1B, 0x40});  // Init printer
            setContent();
            return output.toByteArray();
        } catch (Exception ex) {
            logger.error("Occurred exception while creating page to print", ex);
        }
        return output.toByteArray();
    }

    protected void setFontStyle(byte[] style) throws IOException {
        output.write(style);
    }

    protected void dropFontStyle() throws IOException {
        setFontStyle(new byte[] {0x1B, 0x21, 0x00});
    }

    protected void addEmptyLines(int qty) throws IOException {
        StringBuilder result = new StringBuilder(qty);
        for (int i = 0; i < qty; i++) {
            result.append("\n");
        }
        output.write(result.toString().getBytes(StandardCharsets.US_ASCII));
    }

    protected void addLine(String line) throws IOException {
        output.write(line.getBytes("CP866"));
        addEmptyLines(1);
    }

    protected void addLineCenter(String line) throws IOException {
        output.write(0x1B);
        output.write(0x61);
        output.write(0x01);
        addLine(line);
        output.write(0x1B);
        output.write(0x61);
        output.write(0x00);
    }

    protected void addLineRight(String line) throws IOException {
        addLine(String.format("%" + WIDTH + "s", line));
    }

    protected void addLineLeft(String line) throws IOException {
        addLine(line);
    }

    protected void addFullLine(String leftPart, char sep, String rightPart) throws IOException {
        final int leftMaxLen = WIDTH - rightPart.length();
        leftPart = leftPart.length() > leftMaxLen ? leftPart.substring(0, leftMaxLen-3) + ".." : leftPart;
        final String separator = " " + new String(new char[WIDTH - leftPart.length() - rightPart.length() - 2]).replace('\0', sep) + " ";
        addLine(leftPart + separator + rightPart);
    }

    protected void addDivider(char symbol) throws IOException {
        addLine(new String(new char[WIDTH]).replace('\0', symbol));
    }

}
