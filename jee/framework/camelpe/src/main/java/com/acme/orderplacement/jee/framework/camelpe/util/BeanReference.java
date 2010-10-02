/**
 * 
 */
package com.acme.orderplacement.jee.framework.camelpe.util;

import java.util.Set;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.ResolutionException;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.commons.lang.Validate;

/**
 * <p>
 * TODO: Insert short summary for BeanReference
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
public class BeanReference<T> {

	// -------------------------------------------------------------------------
	// Fields
	// -------------------------------------------------------------------------

	private final BeanManager beanManager;

	private final Class<T> beanType;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public BeanReference(final Class<T> beanType, final BeanManager beanManager)
			throws IllegalArgumentException {
		Validate.notNull(beanType, "beanType");
		Validate.notNull(beanManager, "beanManager");
		this.beanType = beanType;
		this.beanManager = beanManager;
	}

	// -------------------------------------------------------------------------
	// API
	// -------------------------------------------------------------------------

	public T get() throws ResolutionException {
		final Set<Bean<?>> matchingBeans = this.beanManager
				.getBeans(this.beanType);
		if (matchingBeans.size() < 1) {
			throw new UnsatisfiedResolutionException("Could not find any ["
					+ this.beanType.getName() + "] Bean in BeanManager ["
					+ this.beanManager + "]");
		}
		if (matchingBeans.size() > 1) {
			throw new AmbiguousResolutionException("Found more than one ["
					+ matchingBeans.size() + "] [" + this.beanType.getName()
					+ "] Beans in BeanManager [" + this.beanManager + "]: "
					+ matchingBeans);
		}
		final Bean<T> uniqueMatchingBean = (Bean<T>) matchingBeans.iterator()
				.next();

		return this.beanType.cast(this.beanManager.getReference(
				uniqueMatchingBean, this.beanType, this.beanManager
						.createCreationalContext(uniqueMatchingBean)));
	}

	// -------------------------------------------------------------------------
	// equals(), hashCode(), toString()
	// -------------------------------------------------------------------------

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.beanManager == null) ? 0 : this.beanManager.hashCode());
		result = prime * result
				+ ((this.beanType == null) ? 0 : this.beanType.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BeanReference<?> other = (BeanReference<?>) obj;
		if (this.beanManager == null) {
			if (other.beanManager != null) {
				return false;
			}
		} else if (!this.beanManager.equals(other.beanManager)) {
			return false;
		}
		if (this.beanType == null) {
			if (other.beanType != null) {
				return false;
			}
		} else if (!this.beanType.equals(other.beanType)) {
			return false;
		}
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BeanReference [beanManager=" + this.beanManager + ", beanType="
				+ this.beanType + "]";
	}
}
