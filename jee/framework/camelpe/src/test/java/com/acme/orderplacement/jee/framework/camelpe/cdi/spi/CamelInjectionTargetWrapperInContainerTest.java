/**
 * 
 */
package com.acme.orderplacement.jee.framework.camelpe.cdi.spi;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.ResolutionException;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ByteArrayAsset;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.acme.orderplacement.jee.framework.camelpe.camel.spi.CdiRegistry;
import com.acme.orderplacement.jee.framework.camelpe.cdi.spi.beans.BeanHavingEndpointInjectAndInjectAnnotatedField;
import com.acme.orderplacement.jee.framework.camelpe.cdi.spi.beans.BeanHavingEndpointInjectAndProducesAnnotatedField;
import com.acme.orderplacement.jee.framework.camelpe.cdi.spi.beans.BeanHavingEndpointInjectAnnotatedField;
import com.acme.orderplacement.jee.framework.camelpe.cdi.spi.beans.BeanHavingNoEndpointInjectAnnotatedField;

/**
 * <p>
 * Test {@link CdiRegistry <code>CdiRegistry</code>}.
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class CamelInjectionTargetWrapperInContainerTest {

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------

	@Inject
	private BeanManager beanManager;

	private final CamelContext camelContext = new DefaultCamelContext();

	// -------------------------------------------------------------------------
	// Test fixture
	// -------------------------------------------------------------------------

	@Deployment
	public static JavaArchive createTestArchive() {
		final JavaArchive testModule = ShrinkWrap.create("test.jar",
				JavaArchive.class).addPackages(true,
				BeanHavingNoEndpointInjectAnnotatedField.class.getPackage())
				.addManifestResource(new ByteArrayAsset("<beans/>".getBytes()),
						ArchivePaths.create("beans.xml"));

		return testModule;
	}

	// -------------------------------------------------------------------------
	// Tests
	// -------------------------------------------------------------------------

	@Test
	public void assertThatInjectionTargetForReturnsOriginalInjectionTargetForBeanHavingNoEndpointInjectAnnotatedFields() {
		final AnnotatedType<BeanHavingNoEndpointInjectAnnotatedField> annotatedType = this.beanManager
				.createAnnotatedType(BeanHavingNoEndpointInjectAnnotatedField.class);
		final InjectionTarget<BeanHavingNoEndpointInjectAnnotatedField> originalInjectionTarget = this.beanManager
				.createInjectionTarget(annotatedType);

		final InjectionTarget<BeanHavingNoEndpointInjectAnnotatedField> newInjectionTarget = CamelInjectionTargetWrapper
				.injectionTargetFor(annotatedType, originalInjectionTarget,
						this.camelContext);

		assertSame(
				"injectionTargetFor("
						+ annotatedType
						+ ", "
						+ originalInjectionTarget
						+ ", "
						+ this.camelContext
						+ ") should have returned the InjectionTarget passed in as the supplied "
						+ "AnnotatedType does not define any field annotated with @EndpointInject, "
						+ "yet it returned a different InjectionTarget",
				originalInjectionTarget, newInjectionTarget);
	}

	@Test
	public void assertThatInjectionTargetForReturnsWrappedInjectionTargetForBeanHavingEndpointInjectAnnotatedField() {
		final AnnotatedType<BeanHavingEndpointInjectAnnotatedField> annotatedType = this.beanManager
				.createAnnotatedType(BeanHavingEndpointInjectAnnotatedField.class);
		final InjectionTarget<BeanHavingEndpointInjectAnnotatedField> originalInjectionTarget = this.beanManager
				.createInjectionTarget(annotatedType);

		final InjectionTarget<BeanHavingEndpointInjectAnnotatedField> newInjectionTarget = CamelInjectionTargetWrapper
				.injectionTargetFor(annotatedType, originalInjectionTarget,
						this.camelContext);

		assertTrue(
				"injectionTargetFor("
						+ annotatedType
						+ ", "
						+ originalInjectionTarget
						+ ", "
						+ this.camelContext
						+ ") should have returned a wrapper for the InjectionTarget passed in as the supplied "
						+ "AnnotatedType does define a field annotated with @EndpointInject, "
						+ "yet it didn't", CamelInjectionTargetWrapper.class
						.isAssignableFrom(newInjectionTarget.getClass()));
	}

	@Test(expected = ResolutionException.class)
	public void assertThatInjectionTargetForRejectsBeanHavingEndpointInjectAndInjectAnnotatedField() {
		final AnnotatedType<BeanHavingEndpointInjectAndInjectAnnotatedField> annotatedType = this.beanManager
				.createAnnotatedType(BeanHavingEndpointInjectAndInjectAnnotatedField.class);
		final InjectionTarget<BeanHavingEndpointInjectAndInjectAnnotatedField> originalInjectionTarget = this.beanManager
				.createInjectionTarget(annotatedType);

		CamelInjectionTargetWrapper.injectionTargetFor(annotatedType,
				originalInjectionTarget, this.camelContext);
	}

	@Test(expected = ResolutionException.class)
	public void assertThatInjectionTargetForRejectsBeanHavingEndpointInjectAndProducesAnnotatedField() {
		final AnnotatedType<BeanHavingEndpointInjectAndProducesAnnotatedField> annotatedType = this.beanManager
				.createAnnotatedType(BeanHavingEndpointInjectAndProducesAnnotatedField.class);
		final InjectionTarget<BeanHavingEndpointInjectAndProducesAnnotatedField> originalInjectionTarget = this.beanManager
				.createInjectionTarget(annotatedType);

		CamelInjectionTargetWrapper.injectionTargetFor(annotatedType,
				originalInjectionTarget, this.camelContext);
	}

	@Test
	public void assertThatCamelInjectionTargetWrapperInjectsEndpointIntoEndpointInjectAnnotatedField()
			throws Exception {
		final Endpoint endpointToInject = new MockEndpoint(
				BeanHavingEndpointInjectAnnotatedField.ENDPOINT_URI);
		this.camelContext.addEndpoint(
				BeanHavingEndpointInjectAnnotatedField.ENDPOINT_URI,
				endpointToInject);

		final AnnotatedType<BeanHavingEndpointInjectAnnotatedField> annotatedType = this.beanManager
				.createAnnotatedType(BeanHavingEndpointInjectAnnotatedField.class);
		final InjectionTarget<BeanHavingEndpointInjectAnnotatedField> originalInjectionTarget = this.beanManager
				.createInjectionTarget(annotatedType);

		final Bean<BeanHavingEndpointInjectAnnotatedField> bean = (Bean<BeanHavingEndpointInjectAnnotatedField>) this.beanManager
				.getBeans(BeanHavingEndpointInjectAnnotatedField.NAME)
				.iterator().next();
		final CreationalContext<BeanHavingEndpointInjectAnnotatedField> creationalContext = this.beanManager
				.createCreationalContext(bean);
		final BeanHavingEndpointInjectAnnotatedField instance = bean
				.create(creationalContext);

		final InjectionTarget<BeanHavingEndpointInjectAnnotatedField> wrappedInjectionTarget = CamelInjectionTargetWrapper
				.injectionTargetFor(annotatedType, originalInjectionTarget,
						this.camelContext);
		wrappedInjectionTarget.inject(instance, creationalContext);

		assertNotNull(
				"inject("
						+ instance
						+ ", "
						+ creationalContext
						+ ") should have injected a ProducerTemplate into the @EnpointInject annotated field of the supplied instance, yet it didn't",
				instance.producerTemplate);
	}
}
