package md.utm.restaurant.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link md.utm.restaurant.domain.Location} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    @Size(max = 255)
    private String address;

    @NotNull
    @Size(max = 100)
    private String city;

    @NotNull
    @Size(max = 20)
    private String phone;

    @NotNull
    @Size(max = 100)
    private String email;

    private Double latitude;

    private Double longitude;

    @Min(value = 15)
    @Max(value = 480)
    private Integer reservationDurationOverride;

    @Min(value = 1)
    @Max(value = 365)
    private Integer maxAdvanceBookingDaysOverride;

    @Min(value = 0)
    @Max(value = 72)
    private Integer cancellationDeadlineOverride;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Instant createdAt;

    @NotNull
    private BrandDTO brand;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getReservationDurationOverride() {
        return reservationDurationOverride;
    }

    public void setReservationDurationOverride(Integer reservationDurationOverride) {
        this.reservationDurationOverride = reservationDurationOverride;
    }

    public Integer getMaxAdvanceBookingDaysOverride() {
        return maxAdvanceBookingDaysOverride;
    }

    public void setMaxAdvanceBookingDaysOverride(Integer maxAdvanceBookingDaysOverride) {
        this.maxAdvanceBookingDaysOverride = maxAdvanceBookingDaysOverride;
    }

    public Integer getCancellationDeadlineOverride() {
        return cancellationDeadlineOverride;
    }

    public void setCancellationDeadlineOverride(Integer cancellationDeadlineOverride) {
        this.cancellationDeadlineOverride = cancellationDeadlineOverride;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public BrandDTO getBrand() {
        return brand;
    }

    public void setBrand(BrandDTO brand) {
        this.brand = brand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocationDTO)) {
            return false;
        }

        LocationDTO locationDTO = (LocationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, locationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", phone='" + getPhone() + "'" +
            ", email='" + getEmail() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", reservationDurationOverride=" + getReservationDurationOverride() +
            ", maxAdvanceBookingDaysOverride=" + getMaxAdvanceBookingDaysOverride() +
            ", cancellationDeadlineOverride=" + getCancellationDeadlineOverride() +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", brand=" + getBrand() +
            "}";
    }
}
