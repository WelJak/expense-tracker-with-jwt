package pl.weljak.expensetrackerrestapiwithjwt.webapi.category.request;

import lombok.Value;

@Value
public class UpdateCategoryRequest {
    private String id;
    private String title;
    private String description;
}
