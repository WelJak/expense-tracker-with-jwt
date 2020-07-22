package pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.request;

import lombok.Value;

@Value
public class UpdateTransactionRequest {
    private String transactionId;
    private Long amount;
    private String note;
    private String categoryId;
}
