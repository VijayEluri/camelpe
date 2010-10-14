/**
 * 
 */
package com.acme.orderplacement.jee.framework.camelpe.configuration_samples;

import org.apache.camel.spi.ClassResolver;
import org.apache.camel.spi.FactoryFinder;
import org.apache.camel.spi.FactoryFinderResolver;

import com.acme.orderplacement.jee.framework.camelpe.CamelContextModifying;

/**
 * <p>
 * TODO: Insert short summary for SampleFactoryFinderResolver
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
@CamelContextModifying
public class SampleFactoryFinderResolver implements FactoryFinderResolver {

	/**
	 * @see org.apache.camel.spi.FactoryFinderResolver#resolveDefaultFactoryFinder(org.apache.camel.spi.ClassResolver)
	 */
	@Override
	public FactoryFinder resolveDefaultFactoryFinder(
			final ClassResolver classResolver) {
		return null;
	}

	/**
	 * @see org.apache.camel.spi.FactoryFinderResolver#resolveFactoryFinder(org.apache.camel.spi.ClassResolver,
	 *      java.lang.String)
	 */
	@Override
	public FactoryFinder resolveFactoryFinder(
			final ClassResolver classResolver, final String resourcePath) {
		return null;
	}
}