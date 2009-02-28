package com.acme.orderplacement.jee.ejb.item;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;

import com.acme.orderplacement.common.support.role.ApplicationUserRole;
import com.acme.orderplacement.jee.ejb.internal.spring.CustomSpringBeanAutowiringInterceptor;
import com.acme.orderplacement.service.item.ItemStorageService;
import com.acme.orderplacement.service.item.dto.ItemDto;
import com.acme.orderplacement.service.support.exception.entity.EntityAlreadyRegisteredException;

/**
 * <p>
 * <tt>EJB 3.0</tt> facade for the {@link ItemStorageService
 * <code>ItemStorageService</code>}
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
@RolesAllowed( { ApplicationUserRole.ROLE_GUEST,
		ApplicationUserRole.ROLE_EMPLOYEE, ApplicationUserRole.ROLE_ACCOUNTANT,
		ApplicationUserRole.ROLE_ADMIN })
@Stateless(name = ItemStorageServiceEjb.BEAN_NAME, mappedName = "com/acme/orderplacement/ejb/ItemStorageService")
@Local( { ItemStorageService.class })
@Interceptors( { CustomSpringBeanAutowiringInterceptor.class })
public class ItemStorageServiceEjb implements ItemStorageService {

	// -------------------------------------------------------------------------
	// Fields
	// -------------------------------------------------------------------------

	public static final String BEAN_NAME = "ItemStorageServiceEJB";

	/**
	 * The <tt>Spring</tt> managed <code>ItemStorageService</code>
	 * implementation we delegate all service requests to.
	 */
	@Autowired(required = true)
	private ItemStorageService delegate;

	// -------------------------------------------------------------------------
	// API
	// -------------------------------------------------------------------------

	/**
	 * @see ItemStorageService#registerItem(ItemDto)
	 */
	@RolesAllowed( { ApplicationUserRole.ROLE_GUEST,
			ApplicationUserRole.ROLE_EMPLOYEE,
			ApplicationUserRole.ROLE_ACCOUNTANT, ApplicationUserRole.ROLE_ADMIN })
	public void registerItem(final ItemDto newItemToRegister)
			throws EntityAlreadyRegisteredException, IllegalArgumentException {
		this.delegate.registerItem(newItemToRegister);
	}
}
