package com.lumi.app.service.mapper;

import com.lumi.app.domain.Employee;
import com.lumi.app.service.dto.EmployeeDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Employee} and its DTO {@link EmployeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {}
