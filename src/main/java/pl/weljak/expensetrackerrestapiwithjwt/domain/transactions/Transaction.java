package pl.weljak.expensetrackerrestapiwithjwt.domain.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "et_transactions")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private EtUser etUser;

    @Column(name = "amount", columnDefinition = "NUMERIC", length = 10, nullable = false)
    private Long amount;

    @Column(name = "note", nullable = false)
    private String note;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

}
