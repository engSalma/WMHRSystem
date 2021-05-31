package com.workmotion.net.service;

import com.workmotion.net.domain.Employee;
import com.workmotion.net.domain.enumeration.HiringEvents;
import com.workmotion.net.domain.enumeration.HiringStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.statemachine.StateMachine;

import java.util.Optional;

/**
 * Service Interface for managing {@link Employee}.
 */
public interface EmployeeService {

    /**
     * Save a employee.
     *
     * @param employee the entity to save.
     * @return the persisted entity.
     */
    Employee save(Employee employee);

    /**
     * Get all the employees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Employee> findAll(Pageable pageable);


    /**
     * Get the "id" employee.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Employee> findOne(Long id);

    /**
     * Delete the "id" employee.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
    /**
     * move employee from ADDing State to Checking State
     * @param id
     * @return
     */
    StateMachine<HiringStatus, HiringEvents> checking(Long id);
    
    /**
     * move employee from Checking State to Approved State
     * @param id
     * @return
     */
	 StateMachine<HiringStatus, HiringEvents> approveEmployeeHiring(Long employeeId) ;
	 
	 /**
	     * move employee from Checking State to Rejected State
	     * @param id
	     * @return
	     */
	 StateMachine<HiringStatus, HiringEvents> rejectEmployeeHiring(Long employeeId) ;

}
