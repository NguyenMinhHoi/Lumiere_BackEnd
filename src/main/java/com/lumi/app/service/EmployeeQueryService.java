package com.lumi.app.service;

import com.lumi.app.domain.Employee;
import com.lumi.app.repository.EmployeeRepository;
import com.lumi.app.repository.search.EmployeeSearchRepository;
import com.lumi.app.service.criteria.EmployeeCriteria;
import com.lumi.app.service.dto.EmployeeDTO;
import com.lumi.app.service.mapper.EmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Employee} entities in the database.
 * The main input is a {@link EmployeeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EmployeeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EmployeeQueryService extends QueryService<Employee> {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeQueryService.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final EmployeeSearchRepository employeeSearchRepository;

    public EmployeeQueryService(
        EmployeeRepository employeeRepository,
        EmployeeMapper employeeMapper,
        EmployeeSearchRepository employeeSearchRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.employeeSearchRepository = employeeSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link EmployeeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> findByCriteria(EmployeeCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Employee> specification = createSpecification(criteria);
        return employeeRepository.findAll(specification, page).map(employeeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EmployeeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Employee> specification = createSpecification(criteria);
        return employeeRepository.count(specification);
    }

    /**
     * Function to convert {@link EmployeeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Employee> createSpecification(EmployeeCriteria criteria) {
        Specification<Employee> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Employee_.id),
                buildStringSpecification(criteria.getCode(), Employee_.code),
                buildStringSpecification(criteria.getFullName(), Employee_.fullName),
                buildStringSpecification(criteria.getEmail(), Employee_.email),
                buildStringSpecification(criteria.getPhone(), Employee_.phone),
                buildSpecification(criteria.getRole(), Employee_.role),
                buildSpecification(criteria.getStatus(), Employee_.status),
                buildStringSpecification(criteria.getDepartment(), Employee_.department),
                buildRangeSpecification(criteria.getJoinedAt(), Employee_.joinedAt),
                buildRangeSpecification(criteria.getCreatedAt(), Employee_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Employee_.updatedAt)
            );
        }
        return specification;
    }
}
