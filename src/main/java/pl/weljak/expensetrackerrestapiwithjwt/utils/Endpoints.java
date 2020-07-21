package pl.weljak.expensetrackerrestapiwithjwt.utils;

public class Endpoints {
    private static final String AUTH_ENDPOINT = "/auth";
    public static final String AUTH_LOGIN_ENDPOINT = AUTH_ENDPOINT + "/login";
    public static final String AUTH_REGISTER_ENDPOINT = AUTH_ENDPOINT + "/register";

    private static final String USER_ENDPOINT = "/user";
    public static final String USER_DETAILS_ENDPOINT = USER_ENDPOINT + "/details";
    public static final String USERS_DETAILS_ENDPOINT = USER_DETAILS_ENDPOINT + "/{id}";
}
