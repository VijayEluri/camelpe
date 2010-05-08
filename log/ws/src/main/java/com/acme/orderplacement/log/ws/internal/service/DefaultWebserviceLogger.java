/**
 * 
 */
package com.acme.orderplacement.log.ws.internal.service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.acme.orderplacement.log.ws.internal.domain.WebserviceOperation;
import com.acme.orderplacement.log.ws.internal.domain.WebserviceRequest;
import com.acme.orderplacement.log.ws.internal.domain.WebserviceResponse;
import com.acme.orderplacement.log.ws.service.WebserviceLogger;
import com.acme.orderplacement.log.ws.service.WebserviceRequestDto;
import com.acme.orderplacement.log.ws.service.WebserviceResponseDto;

/**
 * <p>
 * TODO: Insert short summary for DefaultWebserviceLogger
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
@Service(WebserviceLogger.SERVICE_NAME)
@Transactional
public class DefaultWebserviceLogger implements WebserviceLogger {

	// -------------------------------------------------------------------------
	// Fields
	// -------------------------------------------------------------------------

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * <p>
	 * Our {@link javax.persistence.EntityManager <em>JPA EntityManager</em>}.
	 * </p>
	 */
	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	private EntityManager entityManager;

	// -------------------------------------------------------------------------
	// API
	// -------------------------------------------------------------------------

	/**
	 * @see com.acme.orderplacement.log.ws.service.WebserviceLogger#logWebserviceRequest(com.acme.orderplacement.log.ws.service.WebserviceRequestDto)
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Long logWebserviceRequest(
			final WebserviceRequestDto webserviceRequestDto)
			throws IllegalArgumentException, NoResultException {
		Validate.notNull(webserviceRequestDto, "webserviceRequestDto");

		this.log.debug("About to log webservice request [{}] ...",
				webserviceRequestDto);

		final TypedQuery<WebserviceOperation> wsOperationByName = this.entityManager
				.createNamedQuery(WebserviceOperation.Queries.BY_NAME,
						WebserviceOperation.class);
		wsOperationByName.setParameter("name", webserviceRequestDto
				.getOperationName());
		final WebserviceOperation referencedWebserviceOperation = wsOperationByName
				.getSingleResult();

		final WebserviceRequest webserviceRequest = new WebserviceRequest(
				webserviceRequestDto.getSourceIp(), webserviceRequestDto
						.getReceivedOn(), webserviceRequestDto.getContent(),
				referencedWebserviceOperation, webserviceRequestDto
						.getHeaders());
		this.entityManager.persist(webserviceRequest);

		this.log.debug("Webservice request [ID = {} | {}] successfully logged",
				webserviceRequest.getId(), webserviceRequestDto);

		return webserviceRequest.getId();
	}

	/**
	 * @see com.acme.orderplacement.log.ws.service.WebserviceLogger#logWebserviceResponse(com.acme.orderplacement.log.ws.service.WebserviceResponseDto)
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Long logWebserviceResponse(
			final WebserviceResponseDto webserviceResponseDto)
			throws IllegalArgumentException, NoResultException {
		Validate.notNull(webserviceResponseDto, "webserviceResponseDto");

		this.log.debug("About to log webservice response [{}] ...",
				webserviceResponseDto);

		final WebserviceRequest webserviceRequest = this.entityManager.find(
				WebserviceRequest.class, webserviceResponseDto
						.getReferencedRequestId());
		if (webserviceRequest == null) {
			final String error = String
					.format(
							"Webservice response to log [%1$s] references a non existing webservice request [ID = %2$d]",
							webserviceResponseDto.toString(),
							webserviceResponseDto.getReferencedRequestId());
			this.log.error(error);

			throw new IllegalArgumentException(error);
		}

		final WebserviceResponse webserviceResponse = new WebserviceResponse(
				webserviceResponseDto.getSentOn(), webserviceResponseDto
						.getContent(), webserviceRequest);
		this.entityManager.persist(webserviceResponse);

		this.log.debug(
				"Webservice response [ID = {} | {}] successfully logged",
				webserviceResponse.getId(), webserviceResponseDto);

		return webserviceResponse.getId();
	}
}
