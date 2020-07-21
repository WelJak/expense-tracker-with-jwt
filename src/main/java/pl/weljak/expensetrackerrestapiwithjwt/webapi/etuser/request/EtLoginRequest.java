package pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.request;

import lombok.Value;

@Value
public class EtLoginRequest {
    private String username;
    private String password;
}
