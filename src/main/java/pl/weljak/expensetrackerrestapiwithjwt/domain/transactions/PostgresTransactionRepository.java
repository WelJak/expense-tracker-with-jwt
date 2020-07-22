package pl.weljak.expensetrackerrestapiwithjwt.domain.transactions;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

import java.util.List;

public interface PostgresTransactionRepository extends JpaRepository<Transaction, String> {
    Transaction findByTransactionId(String transactionId);

    List<Transaction> findAllByEtUser(EtUser etUser);

    List<Transaction> findAllByCategory(Category category);
}
