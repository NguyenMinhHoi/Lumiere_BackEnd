package com.lumi.app.domain;

import static com.lumi.app.domain.CustomerTestSamples.*;
import static com.lumi.app.domain.SurveyResponseTestSamples.*;
import static com.lumi.app.domain.SurveyTestSamples.*;
import static com.lumi.app.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveyResponseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveyResponse.class);
        SurveyResponse surveyResponse1 = getSurveyResponseSample1();
        SurveyResponse surveyResponse2 = new SurveyResponse();
        assertThat(surveyResponse1).isNotEqualTo(surveyResponse2);

        surveyResponse2.setId(surveyResponse1.getId());
        assertThat(surveyResponse1).isEqualTo(surveyResponse2);

        surveyResponse2 = getSurveyResponseSample2();
        assertThat(surveyResponse1).isNotEqualTo(surveyResponse2);
    }

    @Test
    void surveyTest() {
        SurveyResponse surveyResponse = getSurveyResponseRandomSampleGenerator();
        Survey surveyBack = getSurveyRandomSampleGenerator();

        surveyResponse.setSurvey(surveyBack);
        assertThat(surveyResponse.getSurvey()).isEqualTo(surveyBack);

        surveyResponse.survey(null);
        assertThat(surveyResponse.getSurvey()).isNull();
    }

    @Test
    void customerTest() {
        SurveyResponse surveyResponse = getSurveyResponseRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        surveyResponse.setCustomer(customerBack);
        assertThat(surveyResponse.getCustomer()).isEqualTo(customerBack);

        surveyResponse.customer(null);
        assertThat(surveyResponse.getCustomer()).isNull();
    }

    @Test
    void ticketTest() {
        SurveyResponse surveyResponse = getSurveyResponseRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        surveyResponse.setTicket(ticketBack);
        assertThat(surveyResponse.getTicket()).isEqualTo(ticketBack);

        surveyResponse.ticket(null);
        assertThat(surveyResponse.getTicket()).isNull();
    }
}
