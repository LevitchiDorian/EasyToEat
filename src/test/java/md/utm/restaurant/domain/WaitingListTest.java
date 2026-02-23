package md.utm.restaurant.domain;

import static md.utm.restaurant.domain.DiningRoomTestSamples.*;
import static md.utm.restaurant.domain.LocationTestSamples.*;
import static md.utm.restaurant.domain.WaitingListTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import md.utm.restaurant.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WaitingListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WaitingList.class);
        WaitingList waitingList1 = getWaitingListSample1();
        WaitingList waitingList2 = new WaitingList();
        assertThat(waitingList1).isNotEqualTo(waitingList2);

        waitingList2.setId(waitingList1.getId());
        assertThat(waitingList1).isEqualTo(waitingList2);

        waitingList2 = getWaitingListSample2();
        assertThat(waitingList1).isNotEqualTo(waitingList2);
    }

    @Test
    void locationTest() {
        WaitingList waitingList = getWaitingListRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        waitingList.setLocation(locationBack);
        assertThat(waitingList.getLocation()).isEqualTo(locationBack);

        waitingList.location(null);
        assertThat(waitingList.getLocation()).isNull();
    }

    @Test
    void roomTest() {
        WaitingList waitingList = getWaitingListRandomSampleGenerator();
        DiningRoom diningRoomBack = getDiningRoomRandomSampleGenerator();

        waitingList.setRoom(diningRoomBack);
        assertThat(waitingList.getRoom()).isEqualTo(diningRoomBack);

        waitingList.room(null);
        assertThat(waitingList.getRoom()).isNull();
    }
}
