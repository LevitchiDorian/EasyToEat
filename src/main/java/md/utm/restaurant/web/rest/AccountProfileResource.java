package md.utm.restaurant.web.rest;

import java.util.HashMap;
import java.util.Map;
import md.utm.restaurant.repository.LocationRepository;
import md.utm.restaurant.repository.UserProfileRepository;
import md.utm.restaurant.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public AccountProfileResource(UserProfileRepository userProfileRepository, LocationRepository locationRepository) {
        this.userProfileRepository = userProfileRepository;
        this.locationRepository = locationRepository;
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
     * Assigns (or clears) a location for a manager/staff user's profile.
     * Body: { "locationId": 3 }  or  { "locationId": null }
     */
    @PatchMapping("/admin/users/{login}/location")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> assignUserLocation(@PathVariable String login, @RequestBody Map<String, Object> body) {
        Object locIdObj = body.get("locationId");
        Long locationId = locIdObj != null ? Long.parseLong(locIdObj.toString()) : null;

        userProfileRepository
            .findByUserLogin(login)
            .ifPresent(profile -> {
                if (locationId != null) {
                    locationRepository.findById(locationId).ifPresent(profile::setLocation);
                } else {
                    profile.setLocation(null);
                }
                userProfileRepository.save(profile);
            });

        return ResponseEntity.noContent().build();
    }
}
