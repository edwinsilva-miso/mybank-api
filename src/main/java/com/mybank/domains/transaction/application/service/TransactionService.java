package com.mybank.domains.transaction.application.service;

import com.mybank.domains.account.domain.entity.Account;
import com.mybank.domains.account.domain.repository.AccountRepository;
import com.mybank.domains.transaction.application.dto.CreateTransactionRequest;
import com.mybank.domains.transaction.application.dto.TransactionResponse;
import com.mybank.domains.transaction.domain.entity.Transaction;
import com.mybank.domains.transaction.domain.service.TransactionDomainService;
import com.mybank.domains.user.domain.entity.User;
import com.mybank.domains.user.domain.repository.UserRepository;
import com.mybank.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final TransactionDomainService transactionDomainService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public TransactionResponse createTransaction(CreateTransactionRequest request, Long userId) {
        log.info("Creating transaction for user: {}, type: {}, amount: {}", userId, request.getType(), request.getAmount());

        // Validar la request
        request.validate();

        // Obtener el usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND", "TRANSACTION"));

        // Crear la transacción
        Transaction transaction = new Transaction();
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setFee(request.getFee());
        transaction.setTax(request.getTax());
        transaction.setDescription(request.getDescription());
        transaction.setNotes(request.getNotes());
        transaction.setUser(user);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);

        // Calcular totalAmount antes de la validación
        if (transaction.getTotalAmount() == null) {
            transaction.setTotalAmount(transaction.getAmount().add(transaction.getFee()).add(transaction.getTax()));
        }

        // Configurar cuentas según el tipo de transacción
        configureTransactionAccounts(transaction, request);

        // Guardar la transacción
        Transaction savedTransaction = transactionDomainService.createTransaction(transaction);

        log.info("Transaction created successfully: {}", savedTransaction.getTransactionNumber());

        return TransactionResponse.fromEntity(savedTransaction);
    }

    public TransactionResponse processTransaction(Long transactionId) {
        log.info("Processing transaction: {}", transactionId);

        Transaction processedTransaction = transactionDomainService.processTransaction(transactionId);

        log.info("Transaction processed successfully: {}", processedTransaction.getTransactionNumber());

        return TransactionResponse.fromEntity(processedTransaction);
    }

    public TransactionResponse getTransactionById(Long transactionId) {
        log.info("Getting transaction by ID: {}", transactionId);

        Transaction transaction = transactionDomainService.findById(transactionId);

        return TransactionResponse.fromEntity(transaction);
    }

    public TransactionResponse getTransactionByNumber(String transactionNumber) {
        log.info("Getting transaction by number: {}", transactionNumber);

        Transaction transaction = transactionDomainService.findByTransactionNumber(transactionNumber)
                .orElseThrow(() -> new BusinessException("Transaction not found", "TRANSACTION_NOT_FOUND", "TRANSACTION"));

        return TransactionResponse.fromEntity(transaction);
    }

    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        log.info("Getting transactions for user: {}", userId);

        List<Transaction> transactions = transactionDomainService.findByUserId(userId);

        return transactions.stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        log.info("Getting transactions for account: {}", accountId);

        List<Transaction> transactions = transactionDomainService.findByAccountId(accountId);

        return transactions.stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting transactions for user: {} between {} and {}", userId, startDate, endDate);

        List<Transaction> transactions = transactionDomainService.findByUserIdAndDateRange(userId, startDate, endDate);

        return transactions.stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getPendingTransactions() {
        log.info("Getting all pending transactions");

        List<Transaction> transactions = transactionDomainService.findPendingTransactions();

        return transactions.stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private void configureTransactionAccounts(Transaction transaction, CreateTransactionRequest request) {
        switch (request.getType()) {
            case DEPOSIT:
            case WITHDRAWAL:
            case PAYMENT:
                if (request.getAccountId() != null) {
                    Account account = accountRepository.findById(request.getAccountId())
                            .orElseThrow(() -> new BusinessException("Account not found", "ACCOUNT_NOT_FOUND", "TRANSACTION"));
                    transaction.setAccount(account);
                }
                break;

            // case TRANSFER:
            //     if (request.getSourceAccountId() != null) {
            //         Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
            //                 .orElseThrow(() -> new BusinessException("Source account not found", "SOURCE_ACCOUNT_NOT_FOUND", "TRANSACTION"));
            //         transaction.setSourceAccount(sourceAccount);
            //     }

            //     if (request.getDestinationAccountId() != null) {
            //         Account destinationAccount = accountRepository.findById(request.getDestinationAccountId())
            //                 .orElseThrow(() -> new BusinessException("Destination account not found", "DESTINATION_ACCOUNT_NOT_FOUND", "TRANSACTION"));
            //         transaction.setDestinationAccount(destinationAccount);
            //     }
            //     break;

            default:
                throw new BusinessException("Unsupported transaction type", "UNSUPPORTED_TRANSACTION_TYPE", "TRANSACTION");
        }
    }
} 