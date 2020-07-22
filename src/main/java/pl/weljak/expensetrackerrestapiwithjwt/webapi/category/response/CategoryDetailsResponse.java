package pl.weljak.expensetrackerrestapiwithjwt.webapi.category.response;

import lombok.Value;

@Value
public class CategoryDetailsResponse {
    private String id;
    private String userId;
    private String title;
    private String description;
}
