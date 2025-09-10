package com.lumi.app.domain;

import static com.lumi.app.domain.CustomerTestSamples.*;
import static com.lumi.app.domain.OrdersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrdersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Orders.class);
        Orders orders1 = getOrdersSample1();
        Orders orders2 = new Orders();
        assertThat(orders1).isNotEqualTo(orders2);

        orders2.setId(orders1.getId());
        assertThat(orders1).isEqualTo(orders2);

        orders2 = getOrdersSample2();
        assertThat(orders1).isNotEqualTo(orders2);
    }

    @Test
    void customerTest() {
        Orders orders = getOrdersRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        orders.setCustomer(customerBack);
        assertThat(orders.getCustomer()).isEqualTo(customerBack);

        orders.customer(null);
        assertThat(orders.getCustomer()).isNull();
    }
}
