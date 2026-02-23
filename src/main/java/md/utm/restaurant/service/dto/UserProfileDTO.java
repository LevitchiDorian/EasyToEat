package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import md.utm.restaurant.domain.enumeration.UserRole;

/**
 * A DTO for the {@link md.utm.restaurant.domain.UserProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfileDTO implements Serializable {

    private Long id;

    @Size(max = 20)
    private String phone;

    @Size(max = 500)
    private String avatarUrl;

    @NotNull
    private UserRole role;

    @Size(max = 10)
    private String preferredLanguage;

    private Boolean receiveEmailNotifications;

    private Boolean receivePushNotifications;

    @Min(value = 0)
    private Integer loyaltyPoints;

    @NotNull
    private Instant createdAt;

    private UserDTO user;

    private LocationDTO location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Boolean getReceiveEmailNotifications() {
        return receiveEmailNotifications;
    }

    public void setReceiveEmailNotifications(Boolean receiveEmailNotifications) {
        this.receiveEmailNotifications = receiveEmailNotifications;
    }

    public Boolean getReceivePushNotifications() {
        return receivePushNotifications;
    }

    public void setReceivePushNotifications(Boolean receivePushNotifications) {
        this.receivePushNotifications = receivePushNotifications;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfileDTO)) {
            return false;
        }

        UserProfileDTO userProfileDTO = (UserProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfileDTO{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", avatarUrl='" + getAvatarUrl() + "'" +
            ", role='" + getRole() + "'" +
            ", preferredLanguage='" + getPreferredLanguage() + "'" +
            ", receiveEmailNotifications='" + getReceiveEmailNotifications() + "'" +
            ", receivePushNotifications='" + getReceivePushNotifications() + "'" +
            ", loyaltyPoints=" + getLoyaltyPoints() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            ", location=" + getLocation() +
            "}";
    }
}
