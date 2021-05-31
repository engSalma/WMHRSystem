package com.workmotion.net.service.impl;

import com.workmotion.net.service.EmployeeService;
import com.workmotion.net.domain.Employee;
import com.workmotion.net.domain.enumeration.HiringEvents;
import com.workmotion.net.domain.enumeration.HiringStatus;
import com.workmotion.net.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Employee}.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    
	private final StateMachineFactory<HiringStatus, HiringEvents> factory;

	private static final String EMPLOYEE_ID_HEADER = "employeeId";

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,StateMachineFactory<HiringStatus, HiringEvents> factory) {
        this.employeeRepository = employeeRepository;
    	this.factory = factory;
    }

    /**
     * create new Employee 
     * Employee status at first creation is ADDED 
     * @return Employee
     */
    @Override
    public Employee save(Employee employee) {
        log.debug("Request to save Employee : {}", employee);
        employee.setStatus(HiringStatus.ADDED);
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employee> findAll(Pageable pageable) {
        log.debug("Request to get all Employees");
        return employeeRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findOne(Long id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Employee : {}", id);
        employeeRepository.deleteById(id);
    }
    
    
    @Override
	public StateMachine<HiringStatus, HiringEvents> checking(Long employeeId) {
        log.debug("Request to move Employee to checked state : {}", employeeId);

		StateMachine<HiringStatus, HiringEvents> stateMachine = this.build(employeeId);

		Message<HiringEvents> fulfillmentMessage = MessageBuilder.withPayload(HiringEvents.CHECCKING)
				.setHeader(EMPLOYEE_ID_HEADER, employeeId)
				.build();

		stateMachine.sendEvent(fulfillmentMessage);
		return stateMachine;
	}
	
	
    
    /**
     * move employee from Checking State to Approved State
     * @param id
     * @return
     */
    @Override
    public StateMachine<HiringStatus, HiringEvents> approveEmployeeHiring(Long employeeId) {
        log.debug("Request to move Employee to approved state : {}", employeeId);

    	StateMachine<HiringStatus, HiringEvents> stateMachine = this.build(employeeId);

		Message<HiringEvents> fulfillmentMessage = MessageBuilder.withPayload(HiringEvents.ONBOARDING)
				.setHeader(EMPLOYEE_ID_HEADER, employeeId)
				.build();

		stateMachine.sendEvent(fulfillmentMessage);
		return stateMachine;
	 }
	 
	 /**
	     * move employee from Checking State to Rejected State
	     * @param id
	     * @return
	     */
    @Override
    public StateMachine<HiringStatus, HiringEvents> rejectEmployeeHiring(Long employeeId) {
        log.debug("Request to move Employee to rejected state : {}", employeeId);

		 StateMachine<HiringStatus, HiringEvents> stateMachine = this.build(employeeId);

			Message<HiringEvents> fulfillmentMessage = MessageBuilder.withPayload(HiringEvents.REJECTING)
					.setHeader(EMPLOYEE_ID_HEADER, employeeId)
					.build();

			stateMachine.sendEvent(fulfillmentMessage);
			return stateMachine;
	 }
	
	/**
	 * 
	 * by this we mean that the Employee entity and the state machine is
	 * aligned with one another.
	 *
	 * This will make sure the state machine is in a correct state in which
	 * the state will come from the Employee entity itself. That's why the 'state'
	 * field exist on the Employee entity.
	 *
	 * If the Employee on the DB is not on a valid state yet, then this 'build()'
	 * method needs to reflect that to state machine. Otherwise the state machine
	 * and the entity from the DB will have a mismatch, and we do not want that mismatch
	 * because the event/transition will not be handled properly.
	 *
	 * Take note that the 'STATE MACHINE' and the 'Employee' entity is not the same, in this method
	 * we make sure that they're on the same side and not on the different side.
	 *
	 * @param employeeId
	 * @return
	 */
	private StateMachine<HiringStatus, HiringEvents> build(Long employeeId) {
		Optional<Employee> employee = this.employeeRepository.findById(employeeId); // Retrieve employee from DB
		String employeeIdKey = String.valueOf(employee.get().getId()); // Convert the ID to String
		StateMachine<HiringStatus, HiringEvents> stateMachine =
				this.factory.getStateMachine(employeeIdKey); // Get the StateMachine with the specific ID

		stateMachine.stop(); //stop state machine from running

		/**
		 * Override the state machine's state/event/transition
		 *
		 * This is useful when you need to add new metadata for the
		 * state machine before going to a certain state/event/transition.
		 *
		 */
		stateMachine.getStateMachineAccessor()
				.doWithAllRegions(sma -> {

					StateMachineInterceptor<HiringStatus, HiringEvents> interceptor = null;

					/**
					 * This interceptor exists if you want to persist the state of the state machine
					 * to the DB or something else, maybe add some metadata on the DB about the current
					 * information of the state machine's state or something like that.
					 */
					sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<HiringStatus, HiringEvents>() {

						@Override
						public Message<HiringEvents> preEvent(Message<HiringEvents> message, StateMachine<HiringStatus, HiringEvents> stateMachine) {

							log.info("[XXXX] PRE EVENT");
							log.info("[XXXX] MESSAGE " + message);
							log.info("[XXXX] STATE MACHINE " + stateMachine);

							return super.preEvent(message, stateMachine);
						}

						@Override
						public StateContext<HiringStatus, HiringEvents> preTransition(StateContext<HiringStatus, HiringEvents> stateContext) {

							log.info("[XXXX] PRE TRANSITION:");
							log.info("[XXXX] STATE CONTEXT " + stateContext);

							return super.preTransition(stateContext);
						}

						@Override
						public void preStateChange(State<HiringStatus, HiringEvents> state, Message<HiringEvents> message, Transition<HiringStatus, HiringEvents> transition, StateMachine<HiringStatus, HiringEvents> stateMachine) {

							Optional.ofNullable(message).ifPresent(msg -> {
								Optional.ofNullable(
										Long.class.cast(msg.getHeaders().getOrDefault(EMPLOYEE_ID_HEADER, -1L)))
										.ifPresent(employeeId -> {
											Optional<Employee> employee1 = employeeRepository.findById(employeeId);
											employee1.get().setStatus(state.getId()); // This is the one responsible for changing the state
											employeeRepository.save(employee1.get());

										});
							});
						}

						@Override
						public StateContext<HiringStatus, HiringEvents> postTransition(StateContext<HiringStatus, HiringEvents> stateContext) {

							log.info("[XXXX] POST TRANSITION:");
							log.info("[XXXX] STATE CONTEXT " + stateContext);

							return super.postTransition(stateContext);
						}

						@Override
						public void postStateChange(State<HiringStatus, HiringEvents> state, Message<HiringEvents> message, Transition<HiringStatus, HiringEvents> transition, StateMachine<HiringStatus, HiringEvents> stateMachine) {
							log.info("[XXXX] POST STATE CHANGE");

							log.info("[XXXX] State: " + state);
							log.info("[XXXX] Message: " + message);
							log.info("[XXXX] Transition: " + transition);
							log.info("[XXXX] State Machine: " + stateMachine);

							super.postStateChange(state, message, transition, stateMachine);
						}
					});

					/**
					 * This tells the state machine to force itself to go to a separated state
					 * instead of defaulting to it's initial state.
					 */
					sma.resetStateMachine(
							new DefaultStateMachineContext<>(
									employee.get().getStatus(),
									null,
									null,
									null
							)
					);
				});

		stateMachine.start(); // start the state machine once again
		return stateMachine;
	}
}
