package pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.response;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TransactionDetailsResponse {
    private String id;
    private String categoryId;
    private String categoryTitle;
    private String userId;
    private Long amount;
    private String note;
    private LocalDateTime timestamp;
}
