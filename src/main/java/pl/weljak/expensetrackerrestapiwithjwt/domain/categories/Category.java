package pl.weljak.expensetrackerrestapiwithjwt.domain.categories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.weljak.expensetrackerrestapiwithjwt.domain.transactions.Transaction;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "et_categories")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private EtUser etUserId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Transaction> transactions;

}
