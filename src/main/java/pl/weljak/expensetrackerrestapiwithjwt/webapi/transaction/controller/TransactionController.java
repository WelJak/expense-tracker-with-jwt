package pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.weljak.expensetrackerrestapiwithjwt.domain.categories.Category;
import pl.weljak.expensetrackerrestapiwithjwt.domain.transactions.Transaction;
import pl.weljak.expensetrackerrestapiwithjwt.security.userdetails.UserPrinciple;
import pl.weljak.expensetrackerrestapiwithjwt.service.category.CategoryService;
import pl.weljak.expensetrackerrestapiwithjwt.service.transaction.TransactionService;
import pl.weljak.expensetrackerrestapiwithjwt.utils.Endpoints;
import pl.weljak.expensetrackerrestapiwithjwt.utils.EtResponse;
import pl.weljak.expensetrackerrestapiwithjwt.utils.EtResponseUtils;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.request.CreateTransactionRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.request.UpdateTransactionRequest;
import pl.weljak.expensetrackerrestapiwithjwt.webapi.transaction.response.TransactionDetailsResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    @PostMapping(Endpoints.TRANSACTION_CREATE_ENDPOINT)
    public ResponseEntity<EtResponse> createTransaction(@AuthenticationPrincipal UserPrinciple userPrinciple, @RequestBody CreateTransactionRequest createTransactionRequest) {
        log.info("Creating new transaction for user: {}", userPrinciple.getEtUserId());
        Category category = categoryService.findCategoryByCategoryId(createTransactionRequest.getCategoryId());
        Transaction transaction = transactionService.createTransaction(category,userPrinciple.getCurrentEtUser(), createTransactionRequest.getAmount(), createTransactionRequest.getNote());
        return EtResponseUtils.success(Endpoints.TRANSACTION_CREATE_ENDPOINT, toTransactionDetailsResponse(transaction), "Transaction created", HttpStatus.CREATED);
    }

    @GetMapping(Endpoints.TRANSACTION_DETAILS_ENDPOINT)
    public ResponseEntity<EtResponse> findTransactionById(@AuthenticationPrincipal UserPrinciple userPrinciple, @PathVariable String id) {
        log.info("Fetching details of transaction with id: {}, for user: {}", id, userPrinciple.getEtUserId());
        Transaction transaction = transactionService.findTransactionById(id);
        return EtResponseUtils.success(Endpoints.TRANSACTION_DETAILS_ENDPOINT, toTransactionDetailsResponse(transaction), "Fetched transaction details", HttpStatus.OK);
    }

    @DeleteMapping(Endpoints.TRANSACTIONS_DELETE_ENDPOINT)
    public ResponseEntity<EtResponse> deleteTransaction(@AuthenticationPrincipal UserPrinciple userPrinciple, @PathVariable String id){
        log.info("Deleting transaction with id: {}. Commissioned by user: {}", id, userPrinciple.getEtUserId());
        transactionService.deleteTransaction(id);
        return EtResponseUtils.noContent();
    }

    @PutMapping(Endpoints.TRANSACTION_UPDATE_ENDPOINT)
    public ResponseEntity<EtResponse> updateTransaction(@AuthenticationPrincipal UserPrinciple userPrinciple, @RequestBody UpdateTransactionRequest updateTransactionRequest) {
        log.info("Updating transaction with id: {}, commissioned by: {}", updateTransactionRequest.getTransactionId(), userPrinciple.getEtUserId());
        Category category = categoryService.findCategoryByCategoryId(updateTransactionRequest.getCategoryId());
        Transaction transaction = transactionService.updateTransaction(updateTransactionRequest.getTransactionId(), updateTransactionRequest.getAmount(), updateTransactionRequest.getNote(), category);
        return EtResponseUtils.success(Endpoints.TRANSACTION_UPDATE_ENDPOINT, toTransactionDetailsResponse(transaction), "Transaction updated", HttpStatus.CREATED);
    }

    @GetMapping(Endpoints.TRANSACTIONS_ENDPOINT)
    public ResponseEntity<EtResponse> findAllCurrentUserTransactions(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        log.info("Fetching all transaction of user: {}", userPrinciple.getEtUserId());
        List<Transaction> transactions = transactionService.findAllTransactionsByEtUser(userPrinciple.getCurrentEtUser());
        return EtResponseUtils.success(Endpoints.TRANSACTIONS_ENDPOINT, toTransactionDetailsResponseList(transactions), "Fetched all users transactions", HttpStatus.OK);
    }

    @GetMapping(Endpoints.TRANSACTIONS_BY_CATEGORY_ENDPOINT)
    public ResponseEntity<EtResponse> findAllCurrentUserTransactionsByCategory(@AuthenticationPrincipal UserPrinciple userPrinciple, @PathVariable String category) {
        log.info("Fetching all transactions with category: {} for user: {}", category, userPrinciple.getEtUserId());
        Category categoryFilter = categoryService.findCategoryByCategoryId(category);
        List<Transaction> transactions = transactionService.findAllTransactionByCategory(categoryFilter);
        return EtResponseUtils.success(Endpoints.TRANSACTIONS_BY_CATEGORY_ENDPOINT, toTransactionDetailsResponseList(transactions), "Fetched all users transactions", HttpStatus.OK);
    }

    private TransactionDetailsResponse toTransactionDetailsResponse(Transaction transaction) {
        return new TransactionDetailsResponse(
                transaction.getTransactionId(),
                transaction.getCategory().getCategoryId(),
                transaction.getCategory().getTitle(),
                transaction.getEtUser().getUserId(),
                transaction.getAmount(),
                transaction.getNote(),
                transaction.getTransactionDate()
        );
    }

    private List<TransactionDetailsResponse> toTransactionDetailsResponseList(List<Transaction> transactions) {
        List<TransactionDetailsResponse> detailsResponseList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            detailsResponseList.add(toTransactionDetailsResponse(transaction));
        }
        return detailsResponseList;
    }
}
