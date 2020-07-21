package pl.weljak.expensetrackerrestapiwithjwt.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EtResponse {
    private String timeStamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;
    private int responseCode;
    private String status;
    private Boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object payload;
}
