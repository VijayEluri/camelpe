/**
 * 
 */
package com.acme.orderplacement.framework.service.aspect;

import java.lang.annotation.Annotation;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import com.acme.orderplacement.framework.aspect.log.AbstractExceptionLogger;

/**
 * <p>
 * An <code>Aspect</code> based on <a
 * href="http://www.eclipse.org/aspectj/">AspectJ</a> {@link Annotation} based
 * Aspect notation that logs all exception thrown at the <tt>Service Layer</tt>
 * boundary.
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
@Component(ServiceLayerExceptionLogger.ASPECT_NAME)
@Order(30)
@Aspect
@ManagedResource(objectName = "com.acme.orderplacement:layer=ServiceLayer,name=ServiceLayerExceptionLogger", description = "An aspect for logging exceptions thrown from the service layer")
public class ServiceLayerExceptionLogger extends AbstractExceptionLogger {

	// -------------------------------------------------------------------------
	// Fields
	// -------------------------------------------------------------------------

	public static final String ASPECT_NAME = "service.support.aspect.ServiceLayerExceptionLogger";

	// ------------------------------------------------------------------------
	// Pointcuts
	// ------------------------------------------------------------------------

	/**
	 * @see de.obergner.soa.order.aspect.log.AbstractExceptionLogger#exceptionLoggedMethods()
	 */
	@Override
	@Pointcut("com.acme.orderplacement.framework.service.meta.ServiceLayer.serviceOperations()")
	public void exceptionLoggedMethods() {
	}
}
