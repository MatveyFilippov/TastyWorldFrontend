package homer.tastyworld.frontend.starterpack.api.sra.entity.menu;

import homer.tastyworld.frontend.starterpack.api.engine.Request;
import homer.tastyworld.frontend.starterpack.api.engine.Requester;
import homer.tastyworld.frontend.starterpack.api.sra.tools.RequestUtils;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheManager;
import homer.tastyworld.frontend.starterpack.utils.managers.cache.CacheableFunction;
import org.apache.hc.core5.http.Method;
import java.util.Arrays;
import java.util.List;

public class MenuUtils {

    private record IDaActive(long id, boolean isOnlyActive) {}

    private record AddProductsToCategoryRequestBody(
            List<Long> products_id
    ) {}

    private static final CacheableFunction<Long, MenuCategory> cacheableGetCategory = CacheManager.getForFunction(MenuCategory::get);
    private static final CacheableFunction<Long, Product> cacheableGetProduct = CacheManager.getForFunction(Product::get);
    private static final CacheableFunction<Long, ProductTopping> cacheableGetProductTopping = CacheManager.getForFunction(ProductTopping::get);
    private static final CacheableFunction<Boolean, List<MenuCategory.GetCategoryResponseBody>> cacheableGetAllCategories = CacheManager.getForFunction(
            onlyActive -> {
                Request getAllCategoriesRequest = Request.of(
                        RequestUtils.getURI("/menu/categories?only_active=" + onlyActive), Method.GET, RequestUtils.getBearerAuthorization()
                );
                return Requester.exchange(
                        getAllCategoriesRequest,
                        response -> RequestUtils.processEntityArrayResponse(response, 200, MenuCategory.GetCategoryResponseBody.class)
                );
            }
    );
    private static final CacheableFunction<Boolean, List<Product.GetProductResponseBody>> cacheableGetAllProducts = CacheManager.getForFunction(
            onlyActive -> {
                Request getAllProductsRequest = Request.of(
                        RequestUtils.getURI("/menu/products?only_active=" + onlyActive), Method.GET, RequestUtils.getBearerAuthorization()
                );
                return Requester.exchange(
                        getAllProductsRequest,
                        response -> RequestUtils.processEntityArrayResponse(response, 200, Product.GetProductResponseBody.class)
                );
            }
    );
    private static final CacheableFunction<IDaActive, List<ProductTopping.GetProductToppingResponseBody>> cacheableGetAllProductToppings = CacheManager.getForFunction(
            pair -> {
                Request getAllProductToppingsRequest = Request.of(
                        RequestUtils.getURI("/menu/product_toppings?product_id=" + pair.id() + "&only_active=" + pair.isOnlyActive()), Method.GET, RequestUtils.getBearerAuthorization()
                );
                return Requester.exchange(
                        getAllProductToppingsRequest,
                        response -> RequestUtils.processEntityArrayResponse(response, 200, ProductTopping.GetProductToppingResponseBody.class)
                );
            }
    );
    private static final CacheableFunction<IDaActive, List<Product.GetProductResponseBody>> cacheableGetAllCategoryProducts = CacheManager.getForFunction(
            pair -> {
                Request getAllCategoryProductsRequest = Request.of(
                        RequestUtils.getURI("/menu/categories/" + pair.id() + "/products?only_active=" + pair.isOnlyActive()), Method.GET, RequestUtils.getBearerAuthorization()
                );
                return Requester.exchange(
                        getAllCategoryProductsRequest,
                        response -> RequestUtils.processEntityArrayResponse(response, 200, Product.GetProductResponseBody.class)
                );
            }
    );

    public static MenuCategory[] getAllCategories(boolean onlyActive) {
        List<MenuCategory.GetCategoryResponseBody> getAllCategoriesResponseBodies = cacheableGetAllCategories.applyWithCache(onlyActive);
        MenuCategory[] allCategories = new MenuCategory[getAllCategoriesResponseBodies.size()];
        for (int i = 0; i < getAllCategoriesResponseBodies.size(); i++) {
            MenuCategory category;
            if (cacheableGetCategory.isCached(getAllCategoriesResponseBodies.get(i).menu_category_id())) {
                category = cacheableGetCategory.applyWithCache(getAllCategoriesResponseBodies.get(i).menu_category_id());
            } else {
                category = MenuCategory.of(getAllCategoriesResponseBodies.get(i));
                cacheableGetCategory.cache(getAllCategoriesResponseBodies.get(i).menu_category_id(), category);
            }
            allCategories[i] = category;
        }
        return allCategories;
    }

    public static Product[] getAllProducts(boolean onlyActive) {
        List<Product.GetProductResponseBody> getAllProductsResponseBodies = cacheableGetAllProducts.applyWithCache(onlyActive);
        Product[] allProducts = new Product[getAllProductsResponseBodies.size()];
        for (int i = 0; i < getAllProductsResponseBodies.size(); i++) {
            Product product;
            if (cacheableGetProduct.isCached(getAllProductsResponseBodies.get(i).product_id())) {
                product = cacheableGetProduct.applyWithCache(getAllProductsResponseBodies.get(i).product_id());
            } else {
                product = Product.of(getAllProductsResponseBodies.get(i));
                cacheableGetProduct.cache(getAllProductsResponseBodies.get(i).product_id(), product);
            }
            allProducts[i] = product;
        }
        return allProducts;
    }

    public static ProductTopping[] getAllProductToppings(long productID, boolean onlyActive) {
        List<ProductTopping.GetProductToppingResponseBody> getAllProductToppingsResponseBodies = cacheableGetAllProductToppings.applyWithCache(new IDaActive(productID, onlyActive));
        ProductTopping[] allProductToppings = new ProductTopping[getAllProductToppingsResponseBodies.size()];
        for (int i = 0; i < getAllProductToppingsResponseBodies.size(); i++) {
            ProductTopping productTopping;
            if (cacheableGetProductTopping.isCached(getAllProductToppingsResponseBodies.get(i).product_topping_id())) {
                productTopping = cacheableGetProductTopping.applyWithCache(getAllProductToppingsResponseBodies.get(i).product_topping_id());
            } else {
                productTopping = ProductTopping.of(getAllProductToppingsResponseBodies.get(i));
                cacheableGetProductTopping.cache(getAllProductToppingsResponseBodies.get(i).product_topping_id(), productTopping);
            }
            allProductToppings[i] = productTopping;
        }
        return allProductToppings;
    }

    public static Product[] getAllCategoryProducts(long categoryID, boolean onlyActive) {
        List<Product.GetProductResponseBody> getAllCategoryProductsResponseBodies = cacheableGetAllCategoryProducts.applyWithCache(new IDaActive(categoryID, onlyActive));
        Product[] allCategoryProducts = new Product[getAllCategoryProductsResponseBodies.size()];
        for (int i = 0; i < getAllCategoryProductsResponseBodies.size(); i++) {
            Product product;
            if (cacheableGetProduct.isCached(getAllCategoryProductsResponseBodies.get(i).product_id())) {
                product = cacheableGetProduct.applyWithCache(getAllCategoryProductsResponseBodies.get(i).product_id());
            } else {
                product = Product.of(getAllCategoryProductsResponseBodies.get(i));
                cacheableGetProduct.cache(getAllCategoryProductsResponseBodies.get(i).product_id(), product);
            }
            allCategoryProducts[i] = product;
        }
        return allCategoryProducts;
    }

    public static MenuCategory getOrCreateCategoryInstance(long categoryID) {
        return cacheableGetCategory.applyWithCache(categoryID);
    }

    public static Product getOrCreateProductInstance(long productID) {
        return cacheableGetProduct.applyWithCache(productID);
    }

    public static ProductTopping getOrCreateProductToppingInstance(long productToppingID) {
        return cacheableGetProductTopping.applyWithCache(productToppingID);
    }

    public void addProductsToCategory(long categoryID, long[] productsID) {
        AddProductsToCategoryRequestBody addProductsToCategoryRequestBody = new AddProductsToCategoryRequestBody(
                Arrays.stream(productsID).boxed().toList()
        );
        Request addProductsToCategoryRequest = Request.of(
                RequestUtils.getURI("/menu/categories/" + categoryID + "/products"), Method.PATCH, RequestUtils.getBearerAuthorization(),
                addProductsToCategoryRequestBody
        );
        Requester.exchange(
                addProductsToCategoryRequest,
                response -> RequestUtils.processEmptyResponse(response, 200)
        );
    }

}
