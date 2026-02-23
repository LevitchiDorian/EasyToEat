package md.utm.restaurant.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BrandCriteriaTest {

    @Test
    void newBrandCriteriaHasAllFiltersNullTest() {
        var brandCriteria = new BrandCriteria();
        assertThat(brandCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void brandCriteriaFluentMethodsCreatesFiltersTest() {
        var brandCriteria = new BrandCriteria();

        setAllFilters(brandCriteria);

        assertThat(brandCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void brandCriteriaCopyCreatesNullFilterTest() {
        var brandCriteria = new BrandCriteria();
        var copy = brandCriteria.copy();

        assertThat(brandCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(brandCriteria)
        );
    }

    @Test
    void brandCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var brandCriteria = new BrandCriteria();
        setAllFilters(brandCriteria);

        var copy = brandCriteria.copy();

        assertThat(brandCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(brandCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var brandCriteria = new BrandCriteria();

        assertThat(brandCriteria).hasToString("BrandCriteria{}");
    }

    private static void setAllFilters(BrandCriteria brandCriteria) {
        brandCriteria.id();
        brandCriteria.name();
        brandCriteria.logoUrl();
        brandCriteria.coverImageUrl();
        brandCriteria.primaryColor();
        brandCriteria.secondaryColor();
        brandCriteria.website();
        brandCriteria.contactEmail();
        brandCriteria.contactPhone();
        brandCriteria.defaultReservationDuration();
        brandCriteria.maxAdvanceBookingDays();
        brandCriteria.cancellationDeadlineHours();
        brandCriteria.isActive();
        brandCriteria.createdAt();
        brandCriteria.locationsId();
        brandCriteria.categoriesId();
        brandCriteria.promotionsId();
        brandCriteria.distinct();
    }

    private static Condition<BrandCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getLogoUrl()) &&
                condition.apply(criteria.getCoverImageUrl()) &&
                condition.apply(criteria.getPrimaryColor()) &&
                condition.apply(criteria.getSecondaryColor()) &&
                condition.apply(criteria.getWebsite()) &&
                condition.apply(criteria.getContactEmail()) &&
                condition.apply(criteria.getContactPhone()) &&
                condition.apply(criteria.getDefaultReservationDuration()) &&
                condition.apply(criteria.getMaxAdvanceBookingDays()) &&
                condition.apply(criteria.getCancellationDeadlineHours()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getLocationsId()) &&
                condition.apply(criteria.getCategoriesId()) &&
                condition.apply(criteria.getPromotionsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BrandCriteria> copyFiltersAre(BrandCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getLogoUrl(), copy.getLogoUrl()) &&
                condition.apply(criteria.getCoverImageUrl(), copy.getCoverImageUrl()) &&
                condition.apply(criteria.getPrimaryColor(), copy.getPrimaryColor()) &&
                condition.apply(criteria.getSecondaryColor(), copy.getSecondaryColor()) &&
                condition.apply(criteria.getWebsite(), copy.getWebsite()) &&
                condition.apply(criteria.getContactEmail(), copy.getContactEmail()) &&
                condition.apply(criteria.getContactPhone(), copy.getContactPhone()) &&
                condition.apply(criteria.getDefaultReservationDuration(), copy.getDefaultReservationDuration()) &&
                condition.apply(criteria.getMaxAdvanceBookingDays(), copy.getMaxAdvanceBookingDays()) &&
                condition.apply(criteria.getCancellationDeadlineHours(), copy.getCancellationDeadlineHours()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getLocationsId(), copy.getLocationsId()) &&
                condition.apply(criteria.getCategoriesId(), copy.getCategoriesId()) &&
                condition.apply(criteria.getPromotionsId(), copy.getPromotionsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
