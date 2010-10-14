/**
 * 
 */
package com.acme.orderplacement.jee.framework.camel.jmslog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.orm.jpa.annotation.JpaEntityManagerFactory;

import com.acme.orderplacement.jee.framework.jmslog.internal.JmsMessageExchangeLoggerBean;
import com.acme.orderplacement.jee.framework.jmslog.internal.domain.JmsMessageExchange;
import com.obergner.acme.orderplacement.integration.inbound.external.event.EventHeaderSpec;
import com.obergner.acme.orderplacement.integration.inbound.external.event.EventProcessingContext;

/**
 * <p>
 * TODO: Insert short summary for
 * IncomingMessageExchangeLoggingProcessorIntegrationTest
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@DataSet("IncomingMessageExchangeLoggingProcessorIntegrationTest.xml")
@JpaEntityManagerFactory(persistenceUnit = "jee.framework.hsqldb.jmsLogPU")
public class IncomingMessageExchangeLoggingProcessorIntegrationTest {

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------

	@TestedObject
	private JmsMessageExchangeLoggerBean messageLoggerBean;

	@InjectIntoByType
	@PersistenceContext(unitName = "jee.framework.jmsLogPU")
	private EntityManager entityManager;

	// ------------------------------------------------------------------------
	// Tests
	// ------------------------------------------------------------------------

	/**
	 * Test method for
	 * {@link com.acme.orderplacement.jee.framework.camel.jmslog.IncomingMessageExchangeLoggingProcessor#process(org.apache.camel.Exchange)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public final void assertThatProcessStoresInMessageInDatabase()
			throws Exception {
		final String messageId = "MID-TEST-456-66-89";
		final String body = "TEST-456-66-89";

		final DefaultMessage inMessage = new DefaultMessage();
		inMessage.setHeader("JMSMessageID", messageId);
		inMessage.setBody(body, String.class);

		final Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.setIn(inMessage);

		final EventProcessingContext eventProcessingContextMock = createNiceMock(EventProcessingContext.class);
		expect(eventProcessingContextMock.getEventType()).andReturn(
				(String) EventHeaderSpec.EVENT_TYPE.defaultValue());
		replay(eventProcessingContextMock);

		final IncomingMessageExchangeLoggingProcessor classUnderTest = new IncomingMessageExchangeLoggingProcessor(
				this.messageLoggerBean, eventProcessingContextMock);
		classUnderTest.process(exchange);

		final TypedQuery<JmsMessageExchange> messageByGuid = this.entityManager
				.createNamedQuery(JmsMessageExchange.Queries.BY_GUID,
						JmsMessageExchange.class);
		messageByGuid.setParameter("guid", messageId);
		final List<JmsMessageExchange> storedMessages = messageByGuid
				.getResultList();
		assertFalse("process(" + exchange + ") did NOT store message",
				storedMessages.isEmpty());
	}

	/**
	 * Test method for
	 * {@link com.acme.orderplacement.jee.framework.camel.jmslog.IncomingMessageExchangeLoggingProcessor#process(org.apache.camel.Exchange)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public final void assertThatProcessStoresHeadersInDatabase()
			throws Exception {
		final String messageId = "MID-TEST-456-66-89";
		final String body = "TEST-456-66-89";

		final Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("header1", new Object());
		headers.put("header2", new Object());

		final DefaultMessage inMessage = new DefaultMessage();
		inMessage.setBody(body, String.class);
		inMessage.setHeaders(headers);
		inMessage.setHeader("JMSMessageID", messageId);

		final Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.setIn(inMessage);

		final EventProcessingContext eventProcessingContextMock = createNiceMock(EventProcessingContext.class);
		expect(eventProcessingContextMock.getEventType()).andReturn(
				(String) EventHeaderSpec.EVENT_TYPE.defaultValue());
		replay(eventProcessingContextMock);

		final IncomingMessageExchangeLoggingProcessor classUnderTest = new IncomingMessageExchangeLoggingProcessor(
				this.messageLoggerBean, eventProcessingContextMock);
		classUnderTest.process(exchange);

		final TypedQuery<JmsMessageExchange> messageByGuid = this.entityManager
				.createNamedQuery(JmsMessageExchange.Queries.BY_GUID,
						JmsMessageExchange.class);
		messageByGuid.setParameter("guid", messageId);
		final JmsMessageExchange storedMessage = messageByGuid
				.getSingleResult();
		assertEquals("process(" + exchange + ") did NOT store message headers",
				3, storedMessage.getHeaders().size());
	}

	/**
	 * Test method for
	 * {@link com.acme.orderplacement.jee.framework.camel.jmslog.IncomingMessageExchangeLoggingProcessor#process(org.apache.camel.Exchange)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public final void assertThatProcessStoresNullHeadersInDatabase()
			throws Exception {
		final String messageId = "MID-TEST-456-66-89";
		final String body = "TEST-456-66-89";

		final Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("header1", null);
		headers.put("header2", null);

		final DefaultMessage inMessage = new DefaultMessage();
		inMessage.setBody(body, String.class);
		inMessage.setHeaders(headers);
		inMessage.setHeader("JMSMessageID", messageId);

		final Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.setIn(inMessage);

		final EventProcessingContext eventProcessingContextMock = createNiceMock(EventProcessingContext.class);
		expect(eventProcessingContextMock.getEventType()).andReturn(
				(String) EventHeaderSpec.EVENT_TYPE.defaultValue());
		replay(eventProcessingContextMock);

		final IncomingMessageExchangeLoggingProcessor classUnderTest = new IncomingMessageExchangeLoggingProcessor(
				this.messageLoggerBean, eventProcessingContextMock);
		classUnderTest.process(exchange);

		final TypedQuery<JmsMessageExchange> messageByGuid = this.entityManager
				.createNamedQuery(JmsMessageExchange.Queries.BY_GUID,
						JmsMessageExchange.class);
		messageByGuid.setParameter("guid", messageId);
		final JmsMessageExchange storedMessage = messageByGuid
				.getSingleResult();
		assertEquals("process(" + exchange
				+ ") did NOT store null message headers", 3, storedMessage
				.getHeaders().size());
	}
}
