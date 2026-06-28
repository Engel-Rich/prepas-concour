package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.commandes.admin.UpdateUserAdminDto;
import com.mutrix.prepa.application.dto.mappers.UserResponseMapper;
import com.mutrix.prepa.application.dto.response.ConcourResponseDTO;
import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.application.dto.response.subscription.TransactionResponse;
import com.mutrix.prepa.application.usecases.admin.ListUsersAdminUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.infrastructure.mappers.ConcoursEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionRepository;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Utilisateurs", description = "Gestion des utilisateurs par l'administrateur")
public class AdminUsersController {

    private final ListUsersAdminUseCase listUsersAdminUseCase;
    private final UsersServices         usersServices;
    private final SubscriptionServices  subscriptionServices;
    private final TransactionServices   transactionServices;
    private final SubscriptionRepository subscriptionRepository;

    // ── Profil admin connecté ──────────────────────────────────────────────────

    @Operation(summary = "Profil de l'administrateur connecté")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponseFormat<UserResponse>> getProfile(
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                UserResponseMapper.mapFromUser(principal.getUser())));
    }

    // ── Liste paginée ──────────────────────────────────────────────────────────

    @Operation(summary = "Lister tous les utilisateurs")
    @GetMapping("/users")
    public ResponseEntity<ApiResponseFormat<Page<UserResponse>>> listUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                listUsersAdminUseCase.execute(page, size)));
    }

    // ── Détail d'un utilisateur ────────────────────────────────────────────────

    @Operation(summary = "Récupérer un utilisateur par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponseFormat<UserResponse>> getById(
            @Parameter(description = "UUID de l'utilisateur") @PathVariable UUID userId) {
        UserModel user = usersServices.getUserById(userId);
        if (user == null) throw new EntityNotFoundException("Utilisateur introuvable : " + userId);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(UserResponseMapper.mapFromUser(user)));
    }

    // ── Mise à jour ────────────────────────────────────────────────────────────

    @Operation(summary = "Modifier les informations d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour"),
            @ApiResponse(responseCode = "404", description = "Introuvable")
    })
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponseFormat<UserResponse>> update(
            @Parameter(description = "UUID de l'utilisateur") @PathVariable UUID userId,
            @RequestBody UpdateUserAdminDto dto) {

        UserModel user = usersServices.getUserById(userId);
        if (user == null) throw new EntityNotFoundException("Utilisateur introuvable : " + userId);

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) user.setName(dto.getFullName());
        if (dto.getEmail()    != null && !dto.getEmail().isBlank())    user.setEmail(dto.getEmail());
        if (dto.getPhone()    != null && !dto.getPhone().isBlank())    user.setPhone(dto.getPhone());
        if (dto.getIsActive() != null)                                  user.setIsActive(dto.getIsActive());

        UserModel saved = usersServices.updateUser(user, null);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(UserResponseMapper.mapFromUser(saved)));
    }

    // ── Concours d'un utilisateur ──────────────────────────────────────────────

    @Operation(summary = "Concours auxquels un utilisateur est abonné (distincts)")
    @GetMapping("/users/{userId}/concours")
    public ResponseEntity<ApiResponseFormat<List<ConcourResponseDTO>>> getConcoursByUser(
            @Parameter(description = "UUID de l'utilisateur") @PathVariable UUID userId) {

        List<ConcourResponseDTO> concours = subscriptionRepository
                .findDistinctConcoursByUserId(userId)
                .stream()
                .map(c -> {
                    var domain = ConcoursEntityMapper.toConcoursDomainModel(c);
                    return ConcourResponseDTO.builder()
                            .id(domain.getId())
                            .name(domain.getName())
                            .description(domain.getDescription())
                            .logoUrl(domain.getLogoUrl())
                            .isActive(domain.getIsActive())
                            .createdAt(domain.getCreatedAt())
                            .build();
                })
                .toList();

        return ResponseEntity.ok(ApiResponseFormat.fromResponse(concours));
    }

    // ── Abonnements d'un utilisateur ───────────────────────────────────────────

    @Operation(summary = "Abonnements d'un utilisateur")
    @GetMapping("/users/{userId}/subscriptions")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> getSubscriptionsByUser(
            @Parameter(description = "UUID de l'utilisateur") @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var result = subscriptionServices.searchByUser(userId, page, size)
                .map(SubscriptionResponse::fromDomain);
        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }

    // ── Transactions d'un utilisateur ─────────────────────────────────────────

    @Operation(summary = "Transactions d'un utilisateur")
    @GetMapping("/users/{userId}/transactions")
    public ResponseEntity<ApiResponseFormat<PageResponse<TransactionResponse>>> getTransactionsByUser(
            @Parameter(description = "UUID de l'utilisateur") @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var result = transactionServices.listByUser(userId, page, size)
                .map(TransactionResponse::fromDomain);
        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }
}
