package com.sebatmedikal.remote.model.request;

import com.sebatmedikal.remote.domain.Brand;
import com.sebatmedikal.remote.domain.Product;
import com.sebatmedikal.remote.domain.User;

/**
 * Created by orhan on 3.06.2017.
 */
public class RequestModelGenerator {
    private static RequestModel prepareRequestModel(String accessToken, String operation) {
        RequestModel requestModel = new RequestModel();
        requestModel.setAccessToken(accessToken);
        requestModel.setOperation(operation);
        return requestModel;
    }

    //Base operations
    public static RequestModel findAll(String accessToken) {
        RequestModel requestModel = prepareRequestModel(accessToken, "findAll");
        return requestModel;
    }

    public static RequestModel findAllOnlyName(String accessToken) {
        RequestModel requestModel = prepareRequestModel(accessToken, "findAllOnlyName");
        return requestModel;
    }

    public static RequestModel findOne(String accessToken, String id) {
        RequestModel requestModel = prepareRequestModel(accessToken, "findOne");
        requestModel.setParameter01(id);
        return requestModel;
    }

    public static RequestModel createdBy(String accessToken, String id) {
        RequestModel requestModel = prepareRequestModel(accessToken, "createdBy");
        requestModel.setParameter01(id);
        return requestModel;
    }

    public static RequestModel delete(String accessToken, String id) {
        RequestModel requestModel = prepareRequestModel(accessToken, "delete");
        requestModel.setParameter01(id);
        return requestModel;
    }

    public static RequestModel page(String accessToken, String pageIndex) {
        RequestModel requestModel = prepareRequestModel(accessToken, "page");
        requestModel.setParameter01(pageIndex);
        return requestModel;
    }

    //User operations
    public static RequestModelUser userCreate(String accessToken, User user) {
        RequestModelUser requestModelUser = new RequestModelUser();
        requestModelUser.setAccessToken(accessToken);
        requestModelUser.setOperation("create");
        requestModelUser.setUser(user);
        return requestModelUser;
    }

    public static RequestModelUser userUpdate(String accessToken, User user) {
        RequestModelUser requestModelUser = new RequestModelUser();
        requestModelUser.setAccessToken(accessToken);
        requestModelUser.setOperation("update");
        requestModelUser.setUser(user);
        return requestModelUser;
    }

    public static RequestModel userLogin(String username, String password, String fcmRegistrationId) {
        RequestModel requestModel = new RequestModel();
        requestModel.setOperation("login");
        requestModel.setParameter01(username);
        requestModel.setParameter02(password);
        requestModel.setParameter03(fcmRegistrationId);
        return requestModel;
    }

    public static RequestModel userLogout(String accessToken, String username) {
        RequestModel requestModel = prepareRequestModel(accessToken, "logout");
        requestModel.setParameter01(username);
        return requestModel;
    }

    public static RequestModel userChangePassword(String accessToken, String username, String password, String newPassword) {
        RequestModel requestModel = prepareRequestModel(accessToken, "changePassword");
        requestModel.setParameter01(username);
        requestModel.setParameter02(password);
        requestModel.setParameter03(newPassword);
        return requestModel;
    }

    public static RequestModel userLoginUsers(String accessToken) {
        RequestModel requestModel = prepareRequestModel(accessToken, "loginUsers");
        return requestModel;
    }

    //Role operations
    public static RequestModel roleUsers(String accessToken, String roleId) {
        RequestModel requestModel = prepareRequestModel(accessToken, "users");
        requestModel.setParameter01(roleId);
        return requestModel;
    }

    //Product operations
    public static RequestModel productKind(String accessToken) {
        RequestModel requestModel = prepareRequestModel(accessToken, "count");
        return requestModel;
    }

    public static RequestModel productPrice(String accessToken, String price) {
        price = price.replaceAll(",", ".");
        RequestModel requestModel = prepareRequestModel(accessToken, "price");
        requestModel.setParameter01(price);
        return requestModel;
    }

    public static RequestModel productOperations(String accessToken, String productId) {
        RequestModel requestModel = prepareRequestModel(accessToken, "operations");
        requestModel.setParameter01(productId);
        return requestModel;
    }

    public static RequestModel productNewOperation(String accessToken, String productId, String count, String note) {
        RequestModel requestModel = prepareRequestModel(accessToken, "operation");
        requestModel.setParameter01(productId);
        requestModel.setParameter02(count);
        requestModel.setParameter03(note);
        return requestModel;
    }

    public static RequestModel proctStock(String accessToken, String productId) {
        RequestModel requestModel = prepareRequestModel(accessToken, "stock");
        requestModel.setParameter01(productId);
        return requestModel;
    }

    public static RequestModelProduct productCreate(String accessToken, Product product) {
        RequestModelProduct requestModelProduct = new RequestModelProduct();
        requestModelProduct.setAccessToken(accessToken);
        requestModelProduct.setOperation("create");
        requestModelProduct.setProduct(product);
        return requestModelProduct;
    }

    public static RequestModelProduct productUpdate(String accessToken, Product product) {
        RequestModelProduct requestModelProduct = new RequestModelProduct();
        requestModelProduct.setAccessToken(accessToken);
        requestModelProduct.setOperation("update");
        requestModelProduct.setProduct(product);
        return requestModelProduct;
    }

    //Operation operations
    public static RequestModel operationProduct(String accessToken, String operationId) {
        RequestModel requestModel = prepareRequestModel(accessToken, "product");
        requestModel.setParameter01(operationId);
        return requestModel;
    }

    //OperationType operations
    public static RequestModel operationTypeOperations(String accessToken, String operationTypeId) {
        RequestModel requestModel = prepareRequestModel(accessToken, "operations");
        requestModel.setParameter01(operationTypeId);
        return requestModel;
    }

    //Brand operations
    public static RequestModel brandProducts(String accessToken, String brandId) {
        RequestModel requestModel = prepareRequestModel(accessToken, "products");
        requestModel.setParameter01(brandId);
        return requestModel;
    }

    public static RequestModelBrand brandCreate(String accessToken, Brand brand) {
        RequestModelBrand requestModelBrand = new RequestModelBrand();
        requestModelBrand.setAccessToken(accessToken);
        requestModelBrand.setOperation("create");
        requestModelBrand.setBrand(brand);
        return requestModelBrand;
    }
}
