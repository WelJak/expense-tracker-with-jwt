package pl.weljak.expensetrackerrestapiwithjwt.utils;

public class Endpoints {
    private static final String AUTH_ENDPOINT = "/auth";
    public static final String AUTH_LOGIN_ENDPOINT = AUTH_ENDPOINT + "/login";
    public static final String AUTH_REGISTER_ENDPOINT = AUTH_ENDPOINT + "/register";

    private static final String USER_ENDPOINT = "/user";
    public static final String USER_DETAILS_ENDPOINT = USER_ENDPOINT + "/details";
    public static final String USERS_DETAILS_ENDPOINT = USER_DETAILS_ENDPOINT + "/{id}";

    private static final String CATEGORY_ENDPOINT = "/category";
    public static final String CATEGORY_CREATE_ENDPOINT = CATEGORY_ENDPOINT + "/create";
    public static final String CATEGORY_DETAILS_ENDPOINT = CATEGORY_ENDPOINT + "/{id}";
    public static final String CATEGORIES_ENDPOINT = "/categories";
    public static final String CATEGORY_UPDATE_ENDPOINT = CATEGORY_ENDPOINT + "/update";
   // public static final String CATEGORY_UPDATES_ENDPOINT = CATEGORY_UPDATE_ENDPOINT + "/{id}";

    private static final String TRANSACTION_ENDPOINT = "/transaction";
    public static final String TRANSACTION_CREATE_ENDPOINT = TRANSACTION_ENDPOINT + "/create";
    public static final String TRANSACTION_DETAILS_ENDPOINT = TRANSACTION_ENDPOINT + "/{id}";
    private static final String TRANSACTION_DELETE_ENDPOINT = TRANSACTION_ENDPOINT + "/delete";
    public static final String TRANSACTIONS_DELETE_ENDPOINT = TRANSACTION_DELETE_ENDPOINT + "/{id}";
    public static final String TRANSACTION_UPDATE_ENDPOINT = TRANSACTION_ENDPOINT + "/update";
    public static final String TRANSACTIONS_ENDPOINT = "/transactions";
    public static final String TRANSACTIONS_BY_CATEGORY_ENDPOINT = TRANSACTIONS_ENDPOINT + "/{category}";
}
