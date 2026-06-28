package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.mappers.UserResponseMapper;
import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.interfaces.RolesServices;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.UserRepository;
import com.mutrix.prepa.infrastructure.mappers.UserEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Rôles", description = "Gestion des rôles et attribution aux utilisateurs")
public class AdminRolesController {

    private final RolesServices  rolesServices;
    private final UsersServices  usersServices;
    private final UserRepository userRepository;

    // ── Liste tous les rôles ───────────────────────────────────────────────

    @Operation(summary = "Lister tous les rôles")
    @GetMapping("/roles")
    public ResponseEntity<ApiResponseFormat<List<Map<String, Object>>>> listRoles() {
        List<Map<String, Object>> result = rolesServices.findAll().stream()
                .map(r -> {
                    long count = userRepository.countByRoleId(r.getId());
                    return Map.<String, Object>of(
                            "id",         r.getId(),
                            "name",       r.getName(),
                            "usersCount", count
                    );
                })
                .toList();
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(result));
    }

    // ── Créer un rôle ─────────────────────────────────────────────────────

    @Operation(summary = "Créer un nouveau rôle")
    @PostMapping("/roles")
    public ResponseEntity<ApiResponseFormat<Roles>> createRole(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Le nom du rôle est requis.");

        String normalized = name.trim().toUpperCase();

        if (rolesServices.findRoleByName(normalized).isPresent())
            throw new IllegalArgumentException("Le rôle '" + normalized + "' existe déjà.");

        Roles created = rolesServices.saveRoles(new Roles(normalized));
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(created));
    }

    // ── Supprimer un rôle ─────────────────────────────────────────────────

    @Operation(summary = "Supprimer un rôle")
    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<ApiResponseFormat<Void>> deleteRole(@PathVariable UUID roleId) {
        rolesServices.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Rôle introuvable : " + roleId));
        rolesServices.deleteById(roleId);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(null));
    }

    // ── Utilisateurs par rôle ──────────────────────────────────────────────

    @Operation(summary = "Lister les utilisateurs ayant un rôle donné")
    @GetMapping("/roles/{roleId}/users")
    public ResponseEntity<ApiResponseFormat<PageResponse<UserResponse>>> getUsersByRole(
            @PathVariable UUID roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        rolesServices.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Rôle introuvable : " + roleId));

        Page<UserResponse> result = userRepository
                .findByRoleId(roleId, PageRequest.of(page, size))
                .map(e -> UserResponseMapper.mapFromUser(UserEntityMapper.toDomainModel(e)));

        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }

    // ── Assigner un rôle à un utilisateur ─────────────────────────────────

    @Operation(summary = "Assigner un rôle à un utilisateur")
    @PostMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponseFormat<UserResponse>> assignRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {

        UserModel user = usersServices.getUserById(userId);
        if (user == null) throw new EntityNotFoundException("Utilisateur introuvable : " + userId);

        Roles role = rolesServices.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Rôle introuvable : " + roleId));

        boolean alreadyHas = user.getRoles() != null &&
                user.getRoles().stream().anyMatch(r -> r.getId().equals(roleId));

        if (!alreadyHas) {
            List<Roles> roles = new ArrayList<>(user.getRoles() != null ? user.getRoles() : List.of());
            roles.add(role);
            user.setRoles(roles);
            user = usersServices.updateUser(user, null);
        }

        return ResponseEntity.ok(ApiResponseFormat.fromResponse(UserResponseMapper.mapFromUser(user)));
    }

    // ── Retirer un rôle d'un utilisateur ──────────────────────────────────

    @Operation(summary = "Retirer un rôle d'un utilisateur")
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponseFormat<UserResponse>> removeRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {

        UserModel user = usersServices.getUserById(userId);
        if (user == null) throw new EntityNotFoundException("Utilisateur introuvable : " + userId);

        if (user.getRoles() != null) {
            List<Roles> roles = user.getRoles().stream()
                    .filter(r -> !r.getId().equals(roleId))
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            user.setRoles(roles);
            user = usersServices.updateUser(user, null);
        }

        return ResponseEntity.ok(ApiResponseFormat.fromResponse(UserResponseMapper.mapFromUser(user)));
    }
}
