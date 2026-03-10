package md.utm.restaurant.web.rest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import md.utm.restaurant.domain.UserProfile;
import md.utm.restaurant.domain.enumeration.UserRole;
import md.utm.restaurant.repository.LocationRepository;
import md.utm.restaurant.repository.UserProfileRepository;
import md.utm.restaurant.repository.UserRepository;
import md.utm.restaurant.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for fetching the current user's profile role.
 * GET /api/account/profile → { role: string, locationId: Long }
 */
@RestController
@RequestMapping("/api")
public class AccountProfileResource {

    private final UserProfileRepository userProfileRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public AccountProfileResource(
        UserProfileRepository userProfileRepository,
        LocationRepository locationRepository,
        UserRepository userRepository
    ) {
        this.userProfileRepository = userProfileRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/account/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyProfile() {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (login == null) return ResponseEntity.ok(Map.of());

        return userProfileRepository
            .findByUserLogin(login)
            .map(up -> {
                Map<String, Object> result = new HashMap<>();
                result.put("role", up.getRole() != null ? up.getRole().name() : null);
                result.put("locationId", up.getLocation() != null ? up.getLocation().getId() : null);
                return ResponseEntity.ok(result);
            })
            .orElse(ResponseEntity.ok(Map.of()));
    }

    /**
     * PATCH /api/admin/users/{login}/location
     * Assigns (or clears) a location + role for a manager/staff user's profile.
     * Creates the profile row if it doesn't exist yet.
     * Body: { "locationId": 3, "role": "MANAGER" }  or  { "locationId": null }
     */
    @PatchMapping("/admin/users/{login}/location")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Void> assignUserLocation(@PathVariable String login, @RequestBody Map<String, Object> body) {
        Object locIdObj = body.get("locationId");
        Long locationId = locIdObj != null ? Long.parseLong(locIdObj.toString()) : null;

        Object roleObj = body.get("role");
        UserRole role = null;
        if (roleObj != null) {
            try {
                role = UserRole.valueOf(roleObj.toString());
            } catch (IllegalArgumentException ignored) {}
        }

        UserProfile profile = userProfileRepository
            .findByUserLogin(login)
            .orElseGet(() -> {
                // Auto-create profile for users that don't have one yet
                UserProfile newProfile = new UserProfile();
                newProfile.setCreatedAt(Instant.now());
                newProfile.setRole(UserRole.CLIENT);
                userRepository.findOneByLogin(login).ifPresent(newProfile::setUser);
                return newProfile;
            });

        if (role != null) {
            profile.setRole(role);
        }

        if (locationId != null) {
            locationRepository.findById(locationId).ifPresent(profile::setLocation);
        } else {
            profile.setLocation(null);
        }

        userProfileRepository.save(profile);
        return ResponseEntity.noContent().build();
    }
}
