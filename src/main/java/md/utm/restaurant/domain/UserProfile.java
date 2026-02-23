package md.utm.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import md.utm.restaurant.domain.enumeration.UserRole;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserProfile.
 */
@Entity
@Table(name = "user_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 500)
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Size(max = 10)
    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @Column(name = "receive_email_notifications")
    private Boolean receiveEmailNotifications;

    @Column(name = "receive_push_notifications")
    private Boolean receivePushNotifications;

    @Min(value = 0)
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "hours", "rooms", "localPromotions", "menuOverrides", "brand" }, allowSetters = true)
    private Location location;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public UserProfile phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public UserProfile avatarUrl(String avatarUrl) {
        this.setAvatarUrl(avatarUrl);
        return this;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserRole getRole() {
        return this.role;
    }

    public UserProfile role(UserRole role) {
        this.setRole(role);
        return this;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPreferredLanguage() {
        return this.preferredLanguage;
    }

    public UserProfile preferredLanguage(String preferredLanguage) {
        this.setPreferredLanguage(preferredLanguage);
        return this;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Boolean getReceiveEmailNotifications() {
        return this.receiveEmailNotifications;
    }

    public UserProfile receiveEmailNotifications(Boolean receiveEmailNotifications) {
        this.setReceiveEmailNotifications(receiveEmailNotifications);
        return this;
    }

    public void setReceiveEmailNotifications(Boolean receiveEmailNotifications) {
        this.receiveEmailNotifications = receiveEmailNotifications;
    }

    public Boolean getReceivePushNotifications() {
        return this.receivePushNotifications;
    }

    public UserProfile receivePushNotifications(Boolean receivePushNotifications) {
        this.setReceivePushNotifications(receivePushNotifications);
        return this;
    }

    public void setReceivePushNotifications(Boolean receivePushNotifications) {
        this.receivePushNotifications = receivePushNotifications;
    }

    public Integer getLoyaltyPoints() {
        return this.loyaltyPoints;
    }

    public UserProfile loyaltyPoints(Integer loyaltyPoints) {
        this.setLoyaltyPoints(loyaltyPoints);
        return this;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public UserProfile createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserProfile user(User user) {
        this.setUser(user);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UserProfile location(Location location) {
        this.setLocation(location);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((UserProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", avatarUrl='" + getAvatarUrl() + "'" +
            ", role='" + getRole() + "'" +
            ", preferredLanguage='" + getPreferredLanguage() + "'" +
            ", receiveEmailNotifications='" + getReceiveEmailNotifications() + "'" +
            ", receivePushNotifications='" + getReceivePushNotifications() + "'" +
            ", loyaltyPoints=" + getLoyaltyPoints() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
