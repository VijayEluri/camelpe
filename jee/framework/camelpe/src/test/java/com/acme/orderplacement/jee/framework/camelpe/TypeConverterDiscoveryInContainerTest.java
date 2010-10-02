/**
 * 
 */
package com.acme.orderplacement.jee.framework.camelpe;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.TypeConverter;
import org.apache.camel.spi.TypeConverterRegistry;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.acme.orderplacement.jee.framework.camelpe.typeconverter_samples.InstanceMethodTypeConverter;

/**
 * <p>
 * TODO: Insert short summary for TypeConverterDiscoveryInContainerTest
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class TypeConverterDiscoveryInContainerTest {

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------

	@Inject
	private TypeConverterDiscovery classUnderTest;

	// -------------------------------------------------------------------------
	// Test fixture
	// -------------------------------------------------------------------------

	@Deployment
	public static JavaArchive createTestArchive() {
		final JavaArchive testModule = ShrinkWrap.create(JavaArchive.class,
				"test.jar").addPackages(false,
				InstanceMethodTypeConverter.class.getPackage())
				.addServiceProvider(Extension.class, CamelExtension.class)
				.addManifestResource(new ByteArrayAsset("<beans/>".getBytes()),
						ArchivePaths.create("beans.xml"));

		return testModule;
	}

	// -------------------------------------------------------------------------
	// Tests
	// -------------------------------------------------------------------------
	/**
	 * Test method for
	 * {@link com.acme.orderplacement.jee.framework.camelpe.TypeConverterDiscovery#registerIn(org.apache.camel.CamelContext)}
	 * .
	 */
	@Test
	public final void assertThatRegisterInDoesRegisterAllDiscoveredTypeConverters() {
		final TypeConverterRegistry typeConverterRegistryMock = createNiceMock(TypeConverterRegistry.class);
		typeConverterRegistryMock.addFallbackTypeConverter(
				(TypeConverter) anyObject(), eq(false));
		expectLastCall().once();
		typeConverterRegistryMock.addTypeConverter(eq(String.class),
				eq(Object.class), (TypeConverter) anyObject());
		expectLastCall().once();

		final CamelContext camelContextMock = createNiceMock(CamelContext.class);
		expect(camelContextMock.getTypeConverterRegistry()).andReturn(
				typeConverterRegistryMock).anyTimes();
		replay(typeConverterRegistryMock, camelContextMock);

		this.classUnderTest.registerIn(camelContextMock);

		verify(typeConverterRegistryMock, camelContextMock);
	}

}
