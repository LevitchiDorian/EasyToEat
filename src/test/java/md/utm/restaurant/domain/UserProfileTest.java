package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfile.class);
        UserProfile userProfile1 = getUserProfileSample1();
        UserProfile userProfile2 = new UserProfile();
        assertThat(userProfile1).isNotEqualTo(userProfile2);

        userProfile2.setId(userProfile1.getId());
        assertThat(userProfile1).isEqualTo(userProfile2);

        userProfile2 = getUserProfileSample2();
        assertThat(userProfile1).isNotEqualTo(userProfile2);
    }

    @Test
    void locationTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        userProfile.setLocation(locationBack);
        assertThat(userProfile.getLocation()).isEqualTo(locationBack);

        userProfile.location(null);
        assertThat(userProfile.getLocation()).isNull();
    }
}
