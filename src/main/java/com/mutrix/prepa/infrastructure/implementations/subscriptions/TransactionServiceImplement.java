package com.mutrix.prepa.infrastructure.implementations.subscriptions;

import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.infrastructure.mappers.subscriptions.TransactionMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImplement implements TransactionServices {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction save(Transaction transaction) {
        return transactionMapper.toModel(transactionRepository.save(transactionMapper.toEntity(transaction)));
    }

    @Override
    public Transaction update(Transaction transaction) {
        return transactionMapper.toModel(transactionRepository.save(transactionMapper.toEntity(transaction)));
    }

    @Override
    public Optional<Transaction> findByReference(String reference) {
        return transactionRepository.findByReference(reference).map(transactionMapper::toModel);
    }

    @Override
    public Optional<Transaction> findLatestBySubscriptionId(UUID subscriptionId) {
        return transactionRepository.findTopBySubscription_IdOrderByCreatedAtDesc(subscriptionId)
                .map(transactionMapper::toModel);
    }

    @Override
    public List<Transaction> findAllPending() {
        return transactionRepository.findAllPendingWithProvider(TransactionStatus.PENDING)
                .stream().map(transactionMapper::toModel).toList();
    }

    @Override
    public Transaction getById(UUID id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable avec l'id : " + id));
    }

    @Override
    public Page<Transaction> list(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return transactionRepository.findAllByUser_Id(userId, pageable)
                .map(transactionMapper::toModel);
    }

    @Override
    public Page<Transaction> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return transactionRepository.findAll(pageable)
                .map(transactionMapper::toModel);
    }

    @Override
    public Page<Transaction> listBySession(UUID sessionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return transactionRepository.findAllBySubscription_Sessions_Id(sessionId, pageable)
                .map(transactionMapper::toModel);
    }

    @Override
    public Page<Transaction> listByConcours(UUID concoursId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return transactionRepository.findAllBySubscription_Sessions_Concours_Id(concoursId, pageable)
                .map(transactionMapper::toModel);
    }

    @Override
    public Page<Transaction> listByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return transactionRepository.findAllByUser_Id(userId, pageable)
                .map(transactionMapper::toModel);
    }

    @Override
    public void delete(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction introuvable avec l'id : " + id);
        }
        transactionRepository.deleteById(id);
    }
}
