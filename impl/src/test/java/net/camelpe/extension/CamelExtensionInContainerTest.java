/**
 * Copyright (C) 2010.
 * Olaf Bergner.
 * Hamburg, Germany. olaf.bergner@gmx.de
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package net.camelpe.extension;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import java.util.Date;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import net.camelpe.extension.advanced_samples.AdvancedConsumer;
import net.camelpe.extension.advanced_samples.AdvancedProcessor;
import net.camelpe.extension.advanced_samples.AdvancedProducer;
import net.camelpe.extension.advanced_samples.AdvancedRoutes;
import net.camelpe.extension.camel.spi.CdiRegistry;
import net.camelpe.extension.camelpe_samples.SampleProducer;
import net.camelpe.extension.camelpe_samples.SampleRoutes;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * Test {@link CdiRegistry <code>CdiRegistry</code>}.
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 */
@RunWith(Arquillian.class)
public class CamelExtensionInContainerTest {

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------

	@Inject
	private CamelContext camelContext;

	@Inject
	private SampleProducer sampleProducer;

	@Inject
	private AdvancedProducer advancedProducer;

	@Inject
	private AdvancedProcessor advancedProcessor;

	@Inject
	private AdvancedConsumer advancedConsumer;

	// -------------------------------------------------------------------------
	// Test fixture
	// -------------------------------------------------------------------------

	@Deployment(testable = true)
	public static JavaArchive createTestArchive() {
		final JavaArchive testModule = ShrinkWrap
		        .create(JavaArchive.class, "test.jar")
		        .addPackages(false, SampleRoutes.class.getPackage(),
		                AdvancedRoutes.class.getPackage())
		        .addAsServiceProvider(Extension.class, CamelExtension.class)
		        .addAsManifestResource(
		                new ByteArrayAsset("<beans/>".getBytes()),
		                ArchivePaths.create("beans.xml"));

		return testModule;
	}

	// -------------------------------------------------------------------------
	// Tests
	// -------------------------------------------------------------------------

	@Test
	public void assertThatCamelExtensionDiscoversAndRegistersRoute() {
		assertFalse(
		        "Camel CDI extension should have registered at least one Route "
		                + "with CamelContext. This, however, is not the case.",
		        this.camelContext.getRouteDefinitions().isEmpty());
	}

	@Test
	public void assertThatRouteDiscoveredAndRegisteredByCamelExtensionBasicallyWorks()
	        throws Exception {
		final String testMessage = "Test message";

		final MockEndpoint mockEndpoint = this.camelContext.getEndpoint(
		        SampleRoutes.SAMPLE_TARGET_EP, MockEndpoint.class);
		mockEndpoint.expectedMinimumMessageCount(1);

		final ProducerTemplate producerTemplate = this.camelContext
		        .createProducerTemplate();
		producerTemplate.sendBodyAndHeader(SampleRoutes.SAMPLE_SOURCE_EP,
		        testMessage, "foo", "bar");

		mockEndpoint.assertIsSatisfied();
	}

	@Test
	public void assertThatCdiConfiguredProducerBasicallyWorks()
	        throws Exception {
		final String testMessage = "Test message";

		final MockEndpoint mockEndpoint = this.camelContext.getEndpoint(
		        SampleRoutes.SAMPLE_TARGET_EP, MockEndpoint.class);
		mockEndpoint.expectedMinimumMessageCount(1);

		this.sampleProducer.sendBody(testMessage);

		mockEndpoint.assertIsSatisfied();
	}

	@Test
	public void assertThatAdvancedRouteDiscoveredAndRegisteredByCamelExtensionWorks() {
		final Date testMessage = new Date();

		this.advancedProducer.sendBody(testMessage);

		assertEquals("Test message was not processed by test processor", 1,
		        this.advancedProcessor.getCounter().get());
		this.advancedProcessor.getCounter().set(0);
		assertEquals("Test message was not consumed by test consumer",
		        testMessage.getTime(), this.advancedConsumer.getTimestamp()
		                .get());
		this.advancedConsumer.getTimestamp().set(-1L);
	}
}
