/**
 * 
 */
package com.acme.orderplacement.jee.item.itemimport.internal.binding;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.transform.stream.StreamSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;
import org.xml.sax.SAXException;

import com.acme.orderplacement.jee.item.itemimport.internal.BindingResources;
import com.acme.orderplacement.service.item.dto.ItemDto;

/**
 * <p>
 * TODO: Insert short summary for ItemDtoBuilderBeanBinding1xTest
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
public class ItemDtoBuilderBeanBinding1xTest {

	// -------------------------------------------------------------------------
	// Static
	// -------------------------------------------------------------------------

	private static final String EXPECTED_ITEM_NUMBER = "ITM-23-567884";

	private static final String EXPECTED_ITEM_NAME = "Item Created Event";

	private static final String EXPECTED_ITEM_DESCRIPTION = "Description";

	private static final int EXPECTED_NUMBER_OF_SPECIFICATIONS = 1;

	// -------------------------------------------------------------------------
	// Fields
	// -------------------------------------------------------------------------

	private static ItemDto boundItem;

	// -------------------------------------------------------------------------
	// Fixture
	// -------------------------------------------------------------------------

	@BeforeClass
	public static void bindItem() throws IOException, SAXException {
		final StreamSource source = new StreamSource(
				ItemDtoBuilderBeanBinding1xTest.class
						.getResourceAsStream("ValidItemCreatedEvent-1.0.xml"));
		final Smooks smooks = new Smooks(
				BindingResources.ITEMCREATEDEVENT_1_X_BINDING);
		final JavaResult result = new JavaResult();

		smooks.filterSource(source, result);

		boundItem = ItemDto.class.cast(result
				.getBean(ItemDtoBuildingVisitor.CREATED_ITEM_BEAN_NAME));
	}

	// -------------------------------------------------------------------------
	// Tests
	// -------------------------------------------------------------------------

	/**
	 * Test method for
	 * {@link com.acme.orderplacement.jee.item.itemimport.internal.binding.ItemDtoBuilderBean#build()}
	 * .
	 */
	@Test
	public final void assertThatSmooksBindingCreatesANonNullItemDtoFromXml() {
		assertNotNull("Binding produced a null ItemDto", boundItem);
	}

	/**
	 * Test method for
	 * {@link com.acme.orderplacement.jee.item.itemimport.internal.binding.ItemDtoBuilderBean#build()}
	 * .
	 */
	@Test
	public final void assertThatSmooksBindingCorrectlyBindsItemNumber() {
		assertEquals("Binding did not correctly bind ItemNumber",
				EXPECTED_ITEM_NUMBER, boundItem.getItemNumber());
	}

	/**
	 * Test method for
	 * {@link com.acme.orderplacement.jee.item.itemimport.internal.binding.ItemDtoBuilderBean#build()}
	 * .
	 */
	@Test
	public final void assertThatSmooksBindingCorrectlyBindsItemName() {
		assertEquals("Binding did not correctly bind Name", EXPECTED_ITEM_NAME,
				boundItem.getName());
	}

	/**
	 * Test method for
	 * {@link com.acme.orderplacement.jee.item.itemimport.internal.binding.ItemDtoBuilderBean#build()}
	 * .
	 */
	@Test
	public final void assertThatSmooksBindingCorrectlyBindsItemDescription() {
		assertEquals("Binding did not correctly bind Description",
				EXPECTED_ITEM_DESCRIPTION, boundItem.getDescription());
	}

	/**
	 * Test method for
	 * {@link com.acme.orderplacement.jee.item.itemimport.internal.binding.ItemDtoBuilderBean#build()}
	 * .
	 */
	@Test
	public final void assertThatSmooksBindingCorrectlyBindsItemSpecifications() {
		assertEquals("Binding did not correctly bind ItemSpecifications",
				EXPECTED_NUMBER_OF_SPECIFICATIONS, boundItem
						.getSpecifications().size());
	}
}
