package com.lumi.app.domain;

import static com.lumi.app.domain.ProductTestSamples.*;
import static com.lumi.app.domain.SupplementTestSamples.*;
import static com.lumi.app.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumi.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupplementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Supplement.class);
        Supplement supplement1 = getSupplementSample1();
        Supplement supplement2 = new Supplement();
        assertThat(supplement1).isNotEqualTo(supplement2);

        supplement2.setId(supplement1.getId());
        assertThat(supplement1).isEqualTo(supplement2);

        supplement2 = getSupplementSample2();
        assertThat(supplement1).isNotEqualTo(supplement2);
    }

    @Test
    void productTest() {
        Supplement supplement = getSupplementRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        supplement.setProduct(productBack);
        assertThat(supplement.getProduct()).isEqualTo(productBack);

        supplement.product(null);
        assertThat(supplement.getProduct()).isNull();
    }

    @Test
    void supplierTest() {
        Supplement supplement = getSupplementRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        supplement.setSupplier(supplierBack);
        assertThat(supplement.getSupplier()).isEqualTo(supplierBack);

        supplement.supplier(null);
        assertThat(supplement.getSupplier()).isNull();
    }
}
