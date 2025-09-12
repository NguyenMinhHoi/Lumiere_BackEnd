package com.lumi.app.domain;

import static com.lumi.app.domain.CartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cart.class);
        Cart cart1 = getCartSample1();
        Cart cart2 = new Cart();
        assertThat(cart1).isNotEqualTo(cart2);

        cart2.setId(cart1.getId());
        assertThat(cart1).isEqualTo(cart2);

        cart2 = getCartSample2();
        assertThat(cart1).isNotEqualTo(cart2);
    }
}
