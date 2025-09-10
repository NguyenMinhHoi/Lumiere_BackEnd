package com.lumi.app.service;

import com.lumi.app.domain.Orders;
import com.lumi.app.repository.OrdersRepository;
import com.lumi.app.repository.search.OrdersSearchRepository;
import com.lumi.app.service.dto.OrdersDTO;
import com.lumi.app.service.mapper.OrdersMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumi.app.domain.Orders}.
 */
@Service
@Transactional
public class OrdersService {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersService.class);

    private final OrdersRepository ordersRepository;

    private final OrdersMapper ordersMapper;

    private final OrdersSearchRepository ordersSearchRepository;

    public OrdersService(OrdersRepository ordersRepository, OrdersMapper ordersMapper, OrdersSearchRepository ordersSearchRepository) {
        this.ordersRepository = ordersRepository;
        this.ordersMapper = ordersMapper;
        this.ordersSearchRepository = ordersSearchRepository;
    }

    /**
     * Save a orders.
     *
     * @param ordersDTO the entity to save.
     * @return the persisted entity.
     */
    public OrdersDTO save(OrdersDTO ordersDTO) {
        LOG.debug("Request to save Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        ordersSearchRepository.index(orders);
        return ordersMapper.toDto(orders);
    }

    /**
     * Update a orders.
     *
     * @param ordersDTO the entity to save.
     * @return the persisted entity.
     */
    public OrdersDTO update(OrdersDTO ordersDTO) {
        LOG.debug("Request to update Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        ordersSearchRepository.index(orders);
        return ordersMapper.toDto(orders);
    }

    /**
     * Partially update a orders.
     *
     * @param ordersDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrdersDTO> partialUpdate(OrdersDTO ordersDTO) {
        LOG.debug("Request to partially update Orders : {}", ordersDTO);

        return ordersRepository
            .findById(ordersDTO.getId())
            .map(existingOrders -> {
                ordersMapper.partialUpdate(existingOrders, ordersDTO);

                return existingOrders;
            })
            .map(ordersRepository::save)
            .map(savedOrders -> {
                ordersSearchRepository.index(savedOrders);
                return savedOrders;
            })
            .map(ordersMapper::toDto);
    }

    /**
     * Get all the orders with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<OrdersDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ordersRepository.findAllWithEagerRelationships(pageable).map(ordersMapper::toDto);
    }

    /**
     * Get one orders by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrdersDTO> findOne(Long id) {
        LOG.debug("Request to get Orders : {}", id);
        return ordersRepository.findOneWithEagerRelationships(id).map(ordersMapper::toDto);
    }

    /**
     * Delete the orders by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Orders : {}", id);
        ordersRepository.deleteById(id);
        ordersSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the orders corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrdersDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Orders for query {}", query);
        return ordersSearchRepository.search(query, pageable).map(ordersMapper::toDto);
    }
}
