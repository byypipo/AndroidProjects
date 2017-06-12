package com.sebatmedikal.mapper;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.sebatmedikal.remote.configuration.ErrorCodes;
import com.sebatmedikal.remote.domain.Brand;
import com.sebatmedikal.remote.domain.Operation;
import com.sebatmedikal.remote.domain.OperationType;
import com.sebatmedikal.remote.domain.Product;
import com.sebatmedikal.remote.domain.Role;
import com.sebatmedikal.remote.domain.Stock;
import com.sebatmedikal.remote.domain.User;
import com.sebatmedikal.remote.model.response.ResponseModel;
import com.sebatmedikal.remote.model.response.ResponseModelError;
import com.sebatmedikal.remote.model.response.ResponseModelLogin;
import com.sebatmedikal.remote.model.response.ResponseModelSuccess;
import com.sebatmedikal.util.NullUtil;

public class Mapper {
    private static ObjectMapper mapper = new ObjectMapper();

    public static ResponseModel responseModelMapper(String content) {
        try {
            ResponseModelSuccess responseModelSuccess = null;
            ResponseModelError responseModelError = null;
            ResponseModelLogin responseModelLogin = null;

            for (int i = 0; i < 3; i++) {
                try {
                    if (i == 0) {
                        responseModelLogin = mapper.readValue(content, ResponseModelLogin.class);
                    } else if (i == 1) {
                        responseModelSuccess = mapper.readValue(content, ResponseModelSuccess.class);
                    } else {
                        responseModelError = mapper.readValue(content, ResponseModelError.class);
                    }

                    break;
                } catch (UnrecognizedPropertyException ignoredException) {
                }
            }

            if (NullUtil.isNotNull(responseModelSuccess)) {
                return responseModelSuccess;
            }

            if (NullUtil.isNotNull(responseModelLogin)) {
                return responseModelLogin;
            }

            if (NullUtil.isNotNull(responseModelError)) {
                return responseModelError;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModelError().setErrorCode(ErrorCodes.UNKNOWN_EXCEPTION);
        }
        return null;
    }

    public static Brand brandMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            Brand brand = mapper.readValue(contentString, Brand.class);
            return brand;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Brand> brandListMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            List<Brand> brandList = mapper.readValue(contentString,
                    new TypeReference<List<Brand>>() {
                    });
            return brandList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Operation operationMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            Operation operation = mapper.readValue(contentString,
                    Operation.class);
            return operation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Operation> operationListMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            List<Operation> operationList = mapper.readValue(
                    contentString, new TypeReference<List<Operation>>() {
                    });
            return operationList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static OperationType operationTypeMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            OperationType operationType = mapper.readValue(contentString,
                    OperationType.class);
            return operationType;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<OperationType> operationTypeListMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            List<OperationType> operationTypeList = mapper.readValue(
                    contentString,
                    new TypeReference<List<OperationType>>() {
                    });
            return operationTypeList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Product productMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            Product product = mapper.readValue(contentString,
                    Product.class);
            return product;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Product> productListMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            List<Product> productList = mapper.readValue(contentString,
                    new TypeReference<List<Product>>() {
                    });
            return productList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Role roleMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            Role role = mapper.readValue(contentString, Role.class);
            return role;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Role> roleListMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            List<Role> roleList = mapper.readValue(contentString,
                    new TypeReference<List<Role>>() {
                    });
            return roleList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Stock stockMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            Stock stock = mapper.readValue(contentString, Stock.class);
            return stock;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Stock> stockListMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            List<Stock> stockList = mapper.readValue(contentString,
                    new TypeReference<List<Stock>>() {
                    });
            return stockList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> userListMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            List<User> userList = mapper.readValue(contentString, new TypeReference<List<User>>() {
            });
            return userList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User userMapper(Object content) {
        try {
            String contentString = prepareContentString(content);

            User user = mapper.readValue(contentString, User.class);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String prepareContentString(Object content) throws Exception {
        String contentString = (String) content;
        if (!(content instanceof String)) {
            contentString = mapper.writeValueAsString(content);
        }

        return contentString;
    }
}
