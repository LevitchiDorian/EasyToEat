package md.utm.restaurant.web.rest;

import java.util.HashMap;
import java.util.Map;
import md.utm.restaurant.repository.UserProfileRepository;
import md.utm.restaurant.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for fetching the current user's profile role.
 * GET /api/account/profile → { role: string, locationId: Long }
 */
@RestController
@RequestMapping("/api")
public class AccountProfileResource {

    private final UserProfileRepository userProfileRepository;

    public AccountProfileResource(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
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
}
