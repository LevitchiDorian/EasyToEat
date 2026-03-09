package md.utm.restaurant.web.rest;

import java.time.LocalDate;
import java.util.Optional;
import md.utm.restaurant.security.SecurityUtils;
import md.utm.restaurant.service.FloorPlanService;
import md.utm.restaurant.service.dto.FloorPlanDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the staff floor plan view.
 */
@RestController
public class FloorPlanResource {

    private final FloorPlanService floorPlanService;

    public FloorPlanResource(FloorPlanService floorPlanService) {
        this.floorPlanService = floorPlanService;
    }

    /**
     * GET /api/staff/floor-plan/my?date=YYYY-MM-DD
     * Returns floor plan for the current user's assigned location.
     */
    @GetMapping("/api/staff/floor-plan/my")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_STAFF','ROLE_USER')")
    public ResponseEntity<FloorPlanDTO> getMyFloorPlan(@RequestParam(required = false) LocalDate date) {
        LocalDate effectiveDate = date != null ? date : LocalDate.now();
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        Optional<FloorPlanDTO> result = floorPlanService.buildForCurrentUser(login, effectiveDate);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/staff/floor-plan/{locationId}?date=YYYY-MM-DD
     * Admin/Manager endpoint — returns floor plan for any location.
     */
    @GetMapping("/api/staff/floor-plan/{locationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<FloorPlanDTO> getFloorPlan(@PathVariable Long locationId, @RequestParam(required = false) LocalDate date) {
        LocalDate effectiveDate = date != null ? date : LocalDate.now();
        Optional<FloorPlanDTO> result = floorPlanService.buildForLocation(locationId, effectiveDate);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/public/floor-plan/{locationId}?date=YYYY-MM-DD
     * Public endpoint — returns floor plan with table availability (no reservation details).
     */
    @GetMapping("/api/public/floor-plan/{locationId}")
    public ResponseEntity<FloorPlanDTO> getPublicFloorPlan(@PathVariable Long locationId, @RequestParam(required = false) LocalDate date) {
        LocalDate effectiveDate = date != null ? date : LocalDate.now();
        Optional<FloorPlanDTO> result = floorPlanService.buildPublicForLocation(locationId, effectiveDate);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
