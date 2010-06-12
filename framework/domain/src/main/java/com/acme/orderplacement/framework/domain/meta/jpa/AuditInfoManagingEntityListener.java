/**
 * 
 */
package com.acme.orderplacement.framework.domain.meta.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.orderplacement.framework.common.auth.PrincipalAccess;
import com.acme.orderplacement.framework.domain.meta.AuditInfo;
import com.acme.orderplacement.framework.domain.meta.AuditableDomainObject;

/**
 * <p>
 * A <tt>JPA</tt> <code>EntityListener</code> for initially setting and updating
 * an {@link AuditableDomainObject <code>AuditableDomainObject</code>}'s
 * {@link AuditInfo <code>AuditInfo</code>} component, using information
 * obtained from the <code>SecurityContext</code> associated with the current
 * thread.
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
public class AuditInfoManagingEntityListener {

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------

	/**
	 * Our faithful logger.
	 */
	private final Logger log = LoggerFactory.getLogger(getClass());

	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------

	/**
	 * @param <T>
	 * @param auditableEntity
	 */
	@PrePersist
	public <T extends AuditableDomainObject<? extends Serializable>> void fillAuditInfoBeforePersisting(
			final T auditableEntity) throws IllegalStateException {
		final AuditInfo auditInfo = auditableEntity.getAuditInfo();
		if (!auditInfo.isNew()) {
			final String error = "An entity to be peristed for the first time should have an empty AuditInfo component. "
					+ "However, this entity's ["
					+ auditableEntity
					+ "] AuditInfo [" + auditInfo + "] is not empty.";
			this.log.error(error);

			throw new IllegalStateException(error);
		}

		final String currentUser = currentUser();
		final Date currentDate = new Date();
		auditInfo.setCreatedBy(currentUser);
		auditInfo.setCreatedOn(currentDate);
		auditInfo.setLastUpdatedBy(currentUser);
		auditInfo.setLastUpdatedOn(currentDate);
		this.log.debug(
				"The entity [{}] is about to be persisted for the first time. "
						+ "Its AuditInfo has been updated to [{}].",
				auditableEntity, auditInfo);
	}

	/**
	 * @param <T>
	 * @param auditableEntity
	 */
	@PreUpdate
	public <T extends AuditableDomainObject<? extends Serializable>> void fillAuditInfoBeforeUpdating(
			final T auditableEntity) throws IllegalStateException {
		final AuditInfo auditInfo = auditableEntity.getAuditInfo();
		if (!auditInfo.isComplete()) {
			final String error = "An entity to be updated should have a completely filled AuditInfo component. "
					+ "However, this entity's ["
					+ auditableEntity
					+ "] AuditInfo [" + auditInfo + "] is not complete.";
			this.log.error(error);

			throw new IllegalStateException(error);
		}

		final String currentUser = currentUser();
		final Date currentDate = new Date();
		auditInfo.setLastUpdatedBy(currentUser);
		auditInfo.setLastUpdatedOn(currentDate);
		this.log.debug("The entity [{}] is about to be updated. "
				+ "Its AuditInfo has been updated to [{}].", auditableEntity,
				auditInfo);
	}

	// ------------------------------------------------------------------------
	// Internal
	// ------------------------------------------------------------------------

	/**
	 * @return
	 * @throws IllegalStateException
	 */
	protected String currentUser() throws IllegalStateException {
		return PrincipalAccess.FACTORY.getPrincipalAccess().currentPrincipal()
				.getName();
	}
}
