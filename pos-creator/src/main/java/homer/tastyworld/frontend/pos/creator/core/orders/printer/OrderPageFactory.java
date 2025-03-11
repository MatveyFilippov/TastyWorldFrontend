package homer.tastyworld.frontend.pos.creator.core.orders.printer;

import homer.tastyworld.frontend.pos.creator.panes.dynamic.AddProductParentPane;
import homer.tastyworld.frontend.starterpack.api.Request;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.AppDateTime;
import homer.tastyworld.frontend.starterpack.base.AppLogger;
import homer.tastyworld.frontend.starterpack.base.utils.managers.printer.PrinterPageFactory;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import org.apache.hc.core5.http.Method;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderPageFactory implements PrinterPageFactory {

    private static final AppLogger logger = AppLogger.getFor(OrderPageFactory.class);
    private static final int WIDTH = 48;
    private static final int MIN_DOTS = 5;
    private static final String CHARSET_NAME = "CP866";
    private static final Map<Long, Map<String, Object>> productCache = new ConcurrentHashMap<>();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private OrderPageFactory(Map<String, Object> orderInfo) {
        try {
            initPrinter();
            setOrderName((String) orderInfo.get("NAME"));

            addCenteredText("ТОВАРНЫЙ ЧЕК");
            addCenteredText((String) MyParams.getClientPointInfo().get("NAME"));
            addDivider('~');

            String paidAt = AppDateTime.backendToLocal(AppDateTime.parseDateTime(
                    (String) orderInfo.get("PAID_AT")
            )).format(AppDateTime.DATETIME_FORMAT);
            addCenteredText(paidAt);
            addDivider('=');

            setItems(TypeChanger.toSortedLongArray(orderInfo.get("ITEM_IDs")));
            setPrice(TypeChanger.toBigDecimal(orderInfo.get("TOTAL_PRICE")));
            addCenteredText("СПАСИБО ЗА ВИЗИТ!");
            setEmptyLine();
        } catch (IOException ex) {
            logger.error("Occurred exception while creating page to print", ex);
        }
    }

    private void initPrinter() throws IOException {
        output.write(new byte[] {0x1B, 0x40});
    }

    private void setOrderName(String name) throws IOException {
        output.write(new byte[] {0x1B, 0x21, 0x38});  // 4x high + 2x width
        addCenteredText(name);
        output.write(new byte[] {0x1B, 0x21, 0x00});  // Clean styles
    }

    private void setItems(Long[] itemIDs) throws IOException {
        for (Long itemID : itemIDs) {
            Request request = new Request("/order/read_item", Method.GET);
            request.putInBody("id", itemID);
            addItemLine(request.request().getResultAsJSON());
        }
        addDivider('=');
    }

    private void setPrice(BigDecimal total) throws IOException {
        addRightAligned("ИТОГО: " + total + " р");
        addDivider('~');
    }

    private void setEmptyLine() throws IOException {
        output.write("\n\n\n\n".getBytes(StandardCharsets.US_ASCII));
    }

    private void addItemLine(Map<String, Object> itemInfo) throws IOException {
        long productID = TypeChanger.toLong(itemInfo.get("PRODUCT_ID"));
        Map<String, Object> productInfo = productCache.computeIfAbsent(productID, id -> {
            Request request = new Request("/product/read", Method.GET);
            request.putInBody("id", productID);
            return request.request().getResultAsJSON();
        });

        String qty = TypeChanger.toBigDecimal(itemInfo.get("PEACE_QTY")) + (productInfo.get("PIECE_TYPE").equals("ONE_HUNDRED_GRAMS") ? " Гр" : " Шт");
        BigDecimal price = TypeChanger.toBigDecimal(itemInfo.get("ITEM_PRICE"));

        String leftPart = productInfo.get("NAME") + " " + qty;
        String rightPart = price + " р";
        String trimmedLeft = truncate(leftPart, WIDTH - rightPart.length() - MIN_DOTS);

        String line = String.format(
                "%s %s%s",
                trimmedLeft,
                new String(new char[WIDTH - trimmedLeft.length() - rightPart.length()]).replace('\0', '.'), rightPart
        );

        output.write(line.getBytes(CHARSET_NAME));
        output.write("\n".getBytes(StandardCharsets.US_ASCII));
    }

    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength-3) + ".." : text;
    }

    private void addCenteredText(String text) throws IOException {
        output.write(0x1B);
        output.write(0x61);
        output.write(0x01);
        output.write(text.getBytes(CHARSET_NAME));
        output.write("\n".getBytes(StandardCharsets.US_ASCII));
        output.write(0x1B);
        output.write(0x61);
        output.write(0x00);
    }

    private void addRightAligned(String text) throws IOException {
        String formatted = String.format("%" + WIDTH + "s", text);
        output.write(formatted.getBytes(CHARSET_NAME));
        output.write("\n".getBytes(StandardCharsets.US_ASCII));
    }

    private void addDivider(char symbol) throws IOException {
        output.write(new String(new char[WIDTH]).replace('\0', symbol).getBytes(CHARSET_NAME));
        output.write("\n".getBytes(StandardCharsets.US_ASCII));
    }

    public static OrderPageFactory getFor(long orderID) {
        Request request = new Request("/order/read", Method.GET);
        request.putInBody("id", orderID);
        return new OrderPageFactory(request.request().getResultAsJSON());
    }

    @Override
    public byte[] getPage() {
        return output.toByteArray();
    }

}
