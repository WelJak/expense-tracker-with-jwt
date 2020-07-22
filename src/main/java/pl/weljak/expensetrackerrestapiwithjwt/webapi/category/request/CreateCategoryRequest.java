package pl.weljak.expensetrackerrestapiwithjwt.webapi.category.request;

import lombok.Value;

@Value
public class CreateCategoryRequest {
    private String title;
    private String description;
}
