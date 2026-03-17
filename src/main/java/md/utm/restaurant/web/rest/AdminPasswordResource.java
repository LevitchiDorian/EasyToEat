package md.utm.restaurant.web.rest;

import java.util.Map;
import md.utm.restaurant.repository.UserRepository;
import md.utm.restaurant.security.AuthoritiesConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only endpoint for setting a user's password directly.
 * Used when creating staff / chef / manager accounts via the admin panel.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class AdminPasswordResource {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminPasswordResource(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PatchMapping("/users/{login}/set-password")
    @Transactional
    public ResponseEntity<Void> setUserPassword(@PathVariable String login, @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");
        if (newPassword == null || newPassword.trim().length() < 4) {
            return ResponseEntity.badRequest().build();
        }
        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
            });
        return ResponseEntity.ok().build();
    }
}
