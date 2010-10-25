/**
 * 
 */
package com.acme.orderplacement.jee.item.ws;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.orderplacement.framework.service.exception.entity.EntityAlreadyRegisteredException;
import com.acme.orderplacement.jee.item.wsapi.Item;
import com.acme.orderplacement.jee.item.wsapi.ItemAlreadyRegisteredFault;
import com.acme.orderplacement.jee.item.wsapi.ItemAlreadyRegisteredFault_Exception;
import com.acme.orderplacement.jee.item.wsapi.ItemSpecification;
import com.acme.orderplacement.jee.item.wsapi.ItemStorageServicePortType;
import com.acme.orderplacement.jee.item.wsapi.ItemstorageNamespaces;
import com.acme.orderplacement.jee.item.wsapi.RegisterItemRequest;
import com.acme.orderplacement.jee.item.wsapi.RegisterItemResponse;
import com.acme.orderplacement.jee.item.wsapi.ResponseCode;
import com.acme.orderplacement.service.item.ItemStorageService;
import com.acme.orderplacement.service.item.dto.ItemDto;
import com.acme.orderplacement.service.item.dto.ItemSpecificationDto;

/**
 * <p>
 * TODO: Insert short summary for ItemStorageServicePort
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
@WebService(endpointInterface = "com.acme.orderplacement.jee.item.wsapi.ItemStorageServicePortType", name = "ItemStorageServicePortType", serviceName = "ItemStorageService", targetNamespace = ItemstorageNamespaces.NS_SERVICE_1_0, portName = "ItemStorageServicePort", wsdlLocation = "WEB-INF/wsdl/itemstorageservice-1.0.wsdl")
@HandlerChain(file = "jaxws-handlers.xml")
public class ItemStorageServicePort implements ItemStorageServicePortType {

	// -------------------------------------------------------------------------
	// Fields
	// -------------------------------------------------------------------------

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * <p>
	 * The <tt>Stateless Session Bean</tt> implementing the
	 * {@link ItemStorageService <code>ItemStorageService</code>} we delegate
	 * all calls to.
	 * </p>
	 */
	@EJB
	private ItemStorageService itemStorageService;

	// -------------------------------------------------------------------------
	// API
	// -------------------------------------------------------------------------

	/**
	 * @see com.acme.orderplacement.jee.item.ws.api.ItemStorageServicePortType#registerItem(com.acme.orderplacement.jee.item.ws.api.RegisterItemRequest)
	 */
	public RegisterItemResponse registerItem(
			final RegisterItemRequest registerItemRequest)
			throws ItemAlreadyRegisteredFault_Exception {
		try {
			this.log.info("Processing request [{}] ...", registerItemRequest);

			this.itemStorageService.registerItem(convert(registerItemRequest));

			final RegisterItemResponse response = new RegisterItemResponse();
			response.setResponseCode(ResponseCode.SUCCESS);
			response.setMessage("Item [itemNumber = "
					+ registerItemRequest.getItemToRegister().getItemNumber()
					+ "] successfully registered");

			this.log
					.info(
							"Request [{}] sucessfully processed. Returning response [{}]",
							registerItemRequest, response);

			return response;
		} catch (final EntityAlreadyRegisteredException e) {
			this.log.error("Error processing request [" + registerItemRequest
					+ "]: " + e.getMessage(), e);

			final ItemAlreadyRegisteredFault faultInfo = new ItemAlreadyRegisteredFault();
			faultInfo.setAlreadyRegisteredItem(registerItemRequest
					.getItemToRegister());

			throw new ItemAlreadyRegisteredFault_Exception(
					"The Item [itemNumber = "
							+ registerItemRequest.getItemToRegister()
									.getItemNumber()
							+ "] has already been registered", faultInfo, e);
		}
	}

	// -------------------------------------------------------------------------
	// Internal
	// -------------------------------------------------------------------------

	private final ItemDto convert(
			final RegisterItemRequest registerItemRequestToConvert) {

		final Item itemToRegister = registerItemRequestToConvert
				.getItemToRegister();
		final String itemNumber = itemToRegister.getItemNumber();
		final String itemName = itemToRegister.getName();
		final String itemDescription = itemToRegister.getDescription();
		final List<ItemSpecification> itemSpecifications = itemToRegister
				.getItemSpecifications().getItemSpecification();
		final SortedSet<ItemSpecificationDto> itemSpecDtos = new TreeSet<ItemSpecificationDto>();
		for (final ItemSpecification itemSpec : itemSpecifications) {
			itemSpecDtos.add(convert(itemSpec));
		}

		return new ItemDto(itemNumber, itemName, itemDescription, itemSpecDtos);
	}

	/**
	 * @param itemSpec
	 * @return
	 */
	private ItemSpecificationDto convert(
			final ItemSpecification itemSpecToConvert) {
		final String itemSpecificationNumber = itemSpecToConvert
				.getItemSpecificationNumber();
		final String name = itemSpecToConvert.getName();

		return new ItemSpecificationDto(itemSpecificationNumber, name);
	}
}
