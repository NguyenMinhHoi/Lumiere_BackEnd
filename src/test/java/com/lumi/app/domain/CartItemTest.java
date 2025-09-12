package com.lumi.app.domain;

import static com.lumi.app.domain.CartItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartItem.class);
        CartItem cartItem1 = getCartItemSample1();
        CartItem cartItem2 = new CartItem();
        assertThat(cartItem1).isNotEqualTo(cartItem2);

        cartItem2.setId(cartItem1.getId());
        assertThat(cartItem1).isEqualTo(cartItem2);

        cartItem2 = getCartItemSample2();
        assertThat(cartItem1).isNotEqualTo(cartItem2);
    }
}
