package com.lumi.app.service.impl;

import com.lumi.app.domain.Orders;
import com.lumi.app.repository.OrdersRepository;
import com.lumi.app.repository.search.OrdersSearchRepository;
import com.lumi.app.service.OrdersService;
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
public class OrdersServiceImpl implements OrdersService {

    private static final Logger LOG = LoggerFactory.getLogger(OrdersServiceImpl.class);

    private final OrdersRepository ordersRepository;

    private final OrdersMapper ordersMapper;

    private final OrdersSearchRepository ordersSearchRepository;

    public OrdersServiceImpl(OrdersRepository ordersRepository, OrdersMapper ordersMapper, OrdersSearchRepository ordersSearchRepository) {
        this.ordersRepository = ordersRepository;
        this.ordersMapper = ordersMapper;
        this.ordersSearchRepository = ordersSearchRepository;
    }

    @Override
    public OrdersDTO save(OrdersDTO ordersDTO) {
        LOG.debug("Request to save Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        ordersSearchRepository.index(orders);
        return ordersMapper.toDto(orders);
    }

    @Override
    public OrdersDTO update(OrdersDTO ordersDTO) {
        LOG.debug("Request to update Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        ordersSearchRepository.index(orders);
        return ordersMapper.toDto(orders);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdersDTO> findOne(Long id) {
        LOG.debug("Request to get Orders : {}", id);
        return ordersRepository.findById(id).map(ordersMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Orders : {}", id);
        ordersRepository.deleteById(id);
        ordersSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdersDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Orders for query {}", query);
        return ordersSearchRepository.search(query, pageable).map(ordersMapper::toDto);
    }
}
