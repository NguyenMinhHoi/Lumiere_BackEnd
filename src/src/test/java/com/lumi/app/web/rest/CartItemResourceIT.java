package com.lumi.app.web.rest;

import static com.lumi.app.domain.CartItemAsserts.*;
import static com.lumi.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumi.app.IntegrationTest;
import com.lumi.app.domain.CartItem;
import com.lumi.app.repository.CartItemRepository;
import com.lumi.app.repository.search.CartItemSearchRepository;
import com.lumi.app.service.dto.CartItemDTO;
import com.lumi.app.service.mapper.CartItemMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CartItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CartItemResourceIT {

    private static final Long DEFAULT_CART_ID = 1L;
    private static final Long UPDATED_CART_ID = 2L;

    private static final Long DEFAULT_PRODUCT_VARIANT_ID = 1L;
    private static final Long UPDATED_PRODUCT_VARIANT_ID = 2L;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Instant DEFAULT_ADDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ADDED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/cart-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cart-items/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private CartItemSearchRepository cartItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartItemMockMvc;

    private CartItem cartItem;

    private CartItem insertedCartItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItem createEntity() {
        return new CartItem()
            .cartId(DEFAULT_CART_ID)
            .productVariantId(DEFAULT_PRODUCT_VARIANT_ID)
            .quantity(DEFAULT_QUANTITY)
            .addedAt(DEFAULT_ADDED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItem createUpdatedEntity() {
        return new CartItem()
            .cartId(UPDATED_CART_ID)
            .productVariantId(UPDATED_PRODUCT_VARIANT_ID)
            .quantity(UPDATED_QUANTITY)
            .addedAt(UPDATED_ADDED_AT);
    }

    @BeforeEach
    void initTest() {
        cartItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCartItem != null) {
            cartItemRepository.delete(insertedCartItem);
            cartItemSearchRepository.delete(insertedCartItem);
            insertedCartItem = null;
        }
    }

    @Test
    @Transactional
    void createCartItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);
        var returnedCartItemDTO = om.readValue(
            restCartItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CartItemDTO.class
        );

        // Validate the CartItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCartItem = cartItemMapper.toEntity(returnedCartItemDTO);
        assertCartItemUpdatableFieldsEquals(returnedCartItem, getPersistedCartItem(returnedCartItem));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCartItem = returnedCartItem;
    }

    @Test
    @Transactional
    void createCartItemWithExistingId() throws Exception {
        // Create the CartItem with an existing ID
        cartItem.setId(1L);
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCartIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        // set the field null
        cartItem.setCartId(null);

        // Create the CartItem, which fails.
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        restCartItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkProductVariantIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        // set the field null
        cartItem.setProductVariantId(null);

        // Create the CartItem, which fails.
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        restCartItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        // set the field null
        cartItem.setQuantity(null);

        // Create the CartItem, which fails.
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        restCartItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAddedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        // set the field null
        cartItem.setAddedAt(null);

        // Create the CartItem, which fails.
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        restCartItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCartItems() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        // Get all the cartItemList
        restCartItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].cartId").value(hasItem(DEFAULT_CART_ID.intValue())))
            .andExpect(jsonPath("$.[*].productVariantId").value(hasItem(DEFAULT_PRODUCT_VARIANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].addedAt").value(hasItem(DEFAULT_ADDED_AT.toString())));
    }

    @Test
    @Transactional
    void getCartItem() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        // Get the cartItem
        restCartItemMockMvc
            .perform(get(ENTITY_API_URL_ID, cartItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cartItem.getId().intValue()))
            .andExpect(jsonPath("$.cartId").value(DEFAULT_CART_ID.intValue()))
            .andExpect(jsonPath("$.productVariantId").value(DEFAULT_PRODUCT_VARIANT_ID.intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.addedAt").value(DEFAULT_ADDED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCartItem() throws Exception {
        // Get the cartItem
        restCartItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCartItem() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        cartItemSearchRepository.save(cartItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());

        // Update the cartItem
        CartItem updatedCartItem = cartItemRepository.findById(cartItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCartItem are not directly saved in db
        em.detach(updatedCartItem);
        updatedCartItem
            .cartId(UPDATED_CART_ID)
            .productVariantId(UPDATED_PRODUCT_VARIANT_ID)
            .quantity(UPDATED_QUANTITY)
            .addedAt(UPDATED_ADDED_AT);
        CartItemDTO cartItemDTO = cartItemMapper.toDto(updatedCartItem);

        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cartItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCartItemToMatchAllProperties(updatedCartItem);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CartItem> cartItemSearchList = Streamable.of(cartItemSearchRepository.findAll()).toList();
                CartItem testCartItemSearch = cartItemSearchList.get(searchDatabaseSizeAfter - 1);

                assertCartItemAllPropertiesEquals(testCartItemSearch, updatedCartItem);
            });
    }

    @Test
    @Transactional
    void putNonExistingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(longCount.incrementAndGet());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(longCount.incrementAndGet());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(longCount.incrementAndGet());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cartItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCartItemWithPatch() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cartItem using partial update
        CartItem partialUpdatedCartItem = new CartItem();
        partialUpdatedCartItem.setId(cartItem.getId());

        partialUpdatedCartItem.cartId(UPDATED_CART_ID);

        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCartItem))
            )
            .andExpect(status().isOk());

        // Validate the CartItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCartItemUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCartItem, cartItem), getPersistedCartItem(cartItem));
    }

    @Test
    @Transactional
    void fullUpdateCartItemWithPatch() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cartItem using partial update
        CartItem partialUpdatedCartItem = new CartItem();
        partialUpdatedCartItem.setId(cartItem.getId());

        partialUpdatedCartItem
            .cartId(UPDATED_CART_ID)
            .productVariantId(UPDATED_PRODUCT_VARIANT_ID)
            .quantity(UPDATED_QUANTITY)
            .addedAt(UPDATED_ADDED_AT);

        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCartItem))
            )
            .andExpect(status().isOk());

        // Validate the CartItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCartItemUpdatableFieldsEquals(partialUpdatedCartItem, getPersistedCartItem(partialUpdatedCartItem));
    }

    @Test
    @Transactional
    void patchNonExistingCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(longCount.incrementAndGet());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cartItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(longCount.incrementAndGet());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCartItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(longCount.incrementAndGet());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cartItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCartItem() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);
        cartItemRepository.save(cartItem);
        cartItemSearchRepository.save(cartItem);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the cartItem
        restCartItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, cartItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCartItem() throws Exception {
        // Initialize the database
        insertedCartItem = cartItemRepository.saveAndFlush(cartItem);
        cartItemSearchRepository.save(cartItem);

        // Search the cartItem
        restCartItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + cartItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].cartId").value(hasItem(DEFAULT_CART_ID.intValue())))
            .andExpect(jsonPath("$.[*].productVariantId").value(hasItem(DEFAULT_PRODUCT_VARIANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].addedAt").value(hasItem(DEFAULT_ADDED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return cartItemRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected CartItem getPersistedCartItem(CartItem cartItem) {
        return cartItemRepository.findById(cartItem.getId()).orElseThrow();
    }

    protected void assertPersistedCartItemToMatchAllProperties(CartItem expectedCartItem) {
        assertCartItemAllPropertiesEquals(expectedCartItem, getPersistedCartItem(expectedCartItem));
    }

    protected void assertPersistedCartItemToMatchUpdatableProperties(CartItem expectedCartItem) {
        assertCartItemAllUpdatablePropertiesEquals(expectedCartItem, getPersistedCartItem(expectedCartItem));
    }
}
