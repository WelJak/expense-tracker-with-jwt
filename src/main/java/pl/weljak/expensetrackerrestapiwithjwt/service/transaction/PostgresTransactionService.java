package pl.weljak.expensetrackerrestapiwithjwt.service.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.domain.transactions.PostgresTransactionRepository;
import pl.weljak.expensetrackerrestapiwithjwt.domain.transactions.Transaction;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.EtUser;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresTransactionService implements TransactionService {
    private final PostgresTransactionRepository transactionRepository;
    private final Clock clock;

    @Override
    public Transaction createTransaction(Category category, EtUser etUser, Long amount, String note) {
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                category,
                etUser,
                amount,
                note,
                LocalDateTime.now(clock)
        );
        transactionRepository.save(transaction);
        return transaction;
    }

    @Override
    public Transaction findTransactionById(String transactionId) {
        log.info("Finding transaction with id: {}", transactionId);
        return transactionRepository.findByTransactionId(transactionId);
    }

    @Override
    @Transactional
    public void deleteTransaction(String transactionId) {
        log.info("Deleting transaction with id: {}", transactionId);
        Transaction transaction = transactionRepository.findByTransactionId(transactionId);
        transactionRepository.delete(transaction);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(String transactionId, Long amount, String note, Category category) {
        log.info("Updating transaction with id: {}", transactionId);
        Transaction transaction = transactionRepository.findByTransactionId(transactionId);
        transaction.setAmount(Optional.ofNullable(amount).orElseGet(transaction::getAmount));
        transaction.setNote(Optional.ofNullable(note).orElseGet(transaction::getNote));
        transaction.setCategory(Optional.ofNullable(category).orElseGet(transaction::getCategory));
        transactionRepository.save(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findAllTransactionsByEtUser(EtUser etUser) {
        return transactionRepository.findAllByEtUser(etUser);
    }

    @Override
    public List<Transaction> findAllTransactionByCategory(Category category) {
        return transactionRepository.findAllByCategory(category);
    }
}
