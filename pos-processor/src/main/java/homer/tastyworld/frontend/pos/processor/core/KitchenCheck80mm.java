package homer.tastyworld.frontend.pos.processor.core;

//import javax.print.*;
//import java.io.ByteArrayOutputStream;
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;

public class KitchenCheck80mm {

//    static class Product {
//        String name;
//        int amount;
//        boolean isWeight;
//        List<Component> components;
//
//        Product(String name, int amount, boolean isWeight, List<Component> components) {
//            this.name = name;
//            this.amount = amount;
//            this.isWeight = isWeight;
//            this.components = components;
//        }
//    }
//
//    static class Component {
//        String name;
//        int amount;
//        boolean isWeight;
//
//        Component(String name, int amount, boolean isWeight) {
//            this.name = name;
//            this.amount = amount;
//            this.isWeight = isWeight;
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            final int WIDTH = 24; // Ширина при 4x увеличении
//            final String ORDER_NUMBER = "258";
//
//            // Состав заказа
//            List<Product> products = new ArrayList<>();
//
//            List<Component> shawarmaComponents = new ArrayList<>();
//            shawarmaComponents.add(new Component("Соус", 0, false));
//            shawarmaComponents.add(new Component("Овощи", 1, false));
//            products.add(new Product("Шаурма", 2, false, shawarmaComponents));
//
//            List<Component> shashlikComponents = new ArrayList<>();
//            shashlikComponents.add(new Component("Маринад", 200, true));
//            shashlikComponents.add(new Component("Гарнир", 1, false));
//            products.add(new Product("Шашлык", 450, true, shashlikComponents));
//
//            // Печать позиций
//            for(Product p : products) {
//                addProductWithComponents(output, p, WIDTH);
//                addDivider(output, WIDTH, '-');
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void addProductWithComponents(ByteArrayOutputStream output, Product p, int width) throws Exception {
//        // Основная строка
//        String mainLine = String.format("%s %s",
//                truncate(p.name, 10),
//                p.isWeight ? p.amount + "г" : p.amount + "шт");
//
//        output.write(mainLine.getBytes("CP866"));
//        output.write("\n".getBytes());
//
//        // Компоненты
//        for(Component c : p.components) {
//            String componentLine = String.format("  %-8s %s",
//                    truncate(c.name, 8),
//                    c.isWeight ? c.amount + "г" : c.amount + "шт");
//
//            output.write(componentLine.getBytes("CP866"));
//            output.write("\n".getBytes());
//        }
//    }
//
//    private static String truncate(String text, int maxLength) {
//        return text.length() > maxLength ? text.substring(0, maxLength-1) + "." : text;
//    }

}