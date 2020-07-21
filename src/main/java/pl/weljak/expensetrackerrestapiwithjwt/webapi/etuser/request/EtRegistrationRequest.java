package pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.request;

import lombok.Value;

@Value
public class EtRegistrationRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
}
