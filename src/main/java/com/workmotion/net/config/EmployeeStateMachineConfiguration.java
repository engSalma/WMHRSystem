package com.workmotion.net.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import com.workmotion.net.domain.enumeration.HiringEvents;
import com.workmotion.net.domain.enumeration.HiringStatus;
import com.workmotion.net.service.impl.EmployeeServiceImpl;

@Configuration
@EnableStateMachineFactory
public class EmployeeStateMachineConfiguration  extends StateMachineConfigurerAdapter<HiringStatus, HiringEvents> {

    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

  	
@Override
public void configure(StateMachineTransitionConfigurer<HiringStatus, HiringEvents> transitions) throws Exception {
	// @formatter:off
    transitions
            .withExternal()
				.source(HiringStatus.ADDED)
				.target(HiringStatus.INCHECK)
				.event(HiringEvents.CHECCKING)
            .and()
            .withExternal()
				.source(HiringStatus.INCHECK)
				.target(HiringStatus.APPROVED)
				.event(HiringEvents.ONBOARDING)
            .and()
            .withExternal()
				.source(HiringStatus.INCHECK)
				.target(HiringStatus.REJECTED)
				.event(HiringEvents.REJECTING);
}

@Override
public void configure(StateMachineStateConfigurer<HiringStatus, HiringEvents> states) throws Exception {
	states
			.withStates()
			.initial(HiringStatus.ADDED)
			/**
			 * Purpose of this 'stateEntry()' handler is to provide
			 * some additional logic when a specific STATE is entered,
			 * in this case, it is the 'SUBMITTED' state;
			 *
			 * Maybe you want to add a DB logic, do some notification stuff
			 * or add some message to the message queue or whatever the hell you want;
			 */
			.state(HiringStatus.INCHECK)
			.end(HiringStatus.APPROVED)
			.end(HiringStatus.REJECTED);
}

// Configuration Stuff
@Override
public void configure(StateMachineConfigurationConfigurer<HiringStatus, HiringEvents> config) throws Exception {
	
	StateMachineListenerAdapter<HiringStatus, HiringEvents> adapter = new StateMachineListenerAdapter<HiringStatus, HiringEvents>() {
		@Override
		public void stateChanged(State<HiringStatus, HiringEvents> from, State<HiringStatus, HiringEvents> to) {
			log.info(String.format("[XXXX] State changed(from: %s, to: %s)", from + "", to + ""));

		}
	};

	config.withConfiguration()
			.autoStartup(false)
			.listener(adapter);
}



}


