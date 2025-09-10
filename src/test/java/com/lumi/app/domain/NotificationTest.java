package com.lumi.app.domain;

import static com.lumi.app.domain.CustomerTestSamples.*;
import static com.lumi.app.domain.NotificationTestSamples.*;
import static com.lumi.app.domain.SurveyTestSamples.*;
import static com.lumi.app.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void ticketTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        notification.setTicket(ticketBack);
        assertThat(notification.getTicket()).isEqualTo(ticketBack);

        notification.ticket(null);
        assertThat(notification.getTicket()).isNull();
    }

    @Test
    void customerTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        notification.setCustomer(customerBack);
        assertThat(notification.getCustomer()).isEqualTo(customerBack);

        notification.customer(null);
        assertThat(notification.getCustomer()).isNull();
    }

    @Test
    void surveyTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Survey surveyBack = getSurveyRandomSampleGenerator();

        notification.setSurvey(surveyBack);
        assertThat(notification.getSurvey()).isEqualTo(surveyBack);

        notification.survey(null);
        assertThat(notification.getSurvey()).isNull();
    }
}
