package pl.weljak.expensetrackerrestapiwithjwt.service.transaction;

import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.domain.transactions.Transaction;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Category category, EtUser etUser, Long amount, String note);

    Transaction findTransactionById(String transactionId);

    void deleteTransaction(String transactionId);

    Transaction updateTransaction(String transactionId, Long amount, String note, Category category);

    List<Transaction> findAllTransactionsByEtUser(EtUser etUser);

    List<Transaction> findAllTransactionByCategory(Category category);
}
