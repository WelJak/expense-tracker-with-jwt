package pl.weljak.expensetrackerrestapiwithjwt.webapi.etuser.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EtUserDetailsResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
