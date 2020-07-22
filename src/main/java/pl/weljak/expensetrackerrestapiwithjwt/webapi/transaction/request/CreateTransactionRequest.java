package pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.request;

import lombok.Value;

@Value
public class CreateTransactionRequest {
    private String categoryId;
    private Long amount;
    private String note;
}
