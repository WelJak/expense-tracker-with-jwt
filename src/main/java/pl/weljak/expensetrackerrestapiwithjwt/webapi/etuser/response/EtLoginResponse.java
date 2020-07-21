package pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.response;

import lombok.Value;

@Value
public class EtLoginResponse {
    private String tokenType;
    private String token;
}
