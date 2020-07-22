package pl.weljak.expensetrackerrestapiwithjwt.webapi.category.request;

import lombok.Value;

@Value
public class UdpateCategoryRequest {
    private String id;
    private String title;
    private String description;
}
