/**
 * 
 */
package com.acme.orderplacement.persistence.support.jpa;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.acme.orderplacement.persistence.support.GenericJpaDao;
import com.acme.orderplacement.persistence.support.exception.DataAccessRuntimeException;
import com.acme.orderplacement.persistence.support.exception.NoSuchPersistentObjectException;
import com.acme.orderplacement.persistence.support.exception.ObjectNotPersistentException;
import com.acme.orderplacement.persistence.support.exception.ObjectNotTransientException;
import com.acme.orderplacement.persistence.support.exception.ObjectTransientException;
import com.acme.orderplacement.persistence.support.exception.PersistentStateConcurrentlyModifiedException;
import com.acme.orderplacement.persistence.support.exception.PersistentStateDeletedException;
import com.acme.orderplacement.persistence.support.exception.PersistentStateLockedException;
import com.acme.orderplacement.persistence.support.meta.annotation.ReadOnlyPersistenceOperation;
import com.acme.orderplacement.persistence.support.meta.annotation.StateModifyingPersistenceOperation;

/**
 * <p>
 * TODO: Insert short summary for AbstractJpaDao
 * </p>
 * <p>
 * TODO: Insert comprehensive explanation for AbstractJpaDao
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
public abstract class AbstractJpaDao<T, ID extends Serializable> implements
		GenericJpaDao<T, ID> {

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------

	/**
	 * Our faithful logger.
	 */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * The {@link Class <code>Class</code>} of the entity we are handling.
	 */
	private final Class<T> persistentClass = (Class<T>) ((ParameterizedType) this
			.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	// ------------------------------------------------------------------------
	// Implementation of GenericJpaDao<T, ID>
	// ------------------------------------------------------------------------

	/**
	 * @see com.acme.orderplacement.persistence.support.GenericJpaDao#evict(java.lang.Object)
	 */
	public void evict(final T persistentObject)
			throws DataAccessRuntimeException, ObjectNotPersistentException {
		entityManager().detach(persistentObject);
	}

	/**
	 * @see com.acme.orderplacement.persistence.support.GenericJpaDao#findAll()
	 */
	@ReadOnlyPersistenceOperation
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<T> findAll() throws DataAccessRuntimeException {
		final List<T> allEntities = entityManager().createQuery(
				"From " + getPersistentClass().getName()).getResultList();
		getLog().debug("Returned all ({}) entities of type = [{}].",
				allEntities.size(), getPersistentClass().getName());

		return Collections.<T> unmodifiableList(allEntities);
	}

	/**
	 * @see com.acme.orderplacement.persistence.support.GenericJpaDao#findById(java.io.Serializable,
	 *      boolean)
	 */
	@ReadOnlyPersistenceOperation
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public T findById(final ID id, final boolean lock)
			throws NoSuchPersistentObjectException, DataAccessRuntimeException {
		final T matchingEntity = entityManager().find(getPersistentClass(), id);
		if (matchingEntity == null) {
			getLog().error("Could not find entity of type [{}] with ID [{}]",
					getPersistentClass(), id);

			throw new NoSuchPersistentObjectException(getPersistentClass(), id);
		}
		if (lock) {
			entityManager().lock(matchingEntity, LockModeType.WRITE);
		}
		getLog().debug("Found entity = [{}] with ID = [{}].", matchingEntity,
				id);

		return matchingEntity;
	}

	/**
	 * @see com.acme.orderplacement.persistence.support.GenericJpaDao#flush()
	 * 
	 *      TODO: Does this method need to be marked @Transactional?
	 */
	public void flush() throws DataAccessRuntimeException,
			PersistentStateLockedException,
			PersistentStateConcurrentlyModifiedException,
			PersistentStateDeletedException {
		entityManager().flush();
		getLog()
				.debug(
						"Flushed current Session. "
								+ "Changes have been written to the DB's transaction log, but not yet committed.");
	}

	/**
	 * @see com.acme.orderplacement.persistence.support.GenericJpaDao#makePersistent(java.lang.Object)
	 */
	@StateModifyingPersistenceOperation(idempotent = false)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public T makePersistent(final T transientObject)
			throws DataAccessRuntimeException, ObjectNotTransientException {
		entityManager().persist(transientObject);
		getLog().debug("Saved entity = [{}].", transientObject);

		return transientObject;
	}

	/**
	 * @see com.acme.orderplacement.persistence.support.GenericJpaDao#makePersistentOrUpdatePersistentState(java.lang.Object)
	 */
	@StateModifyingPersistenceOperation(idempotent = false)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public T makePersistentOrUpdatePersistentState(
			final T persistentOrDetachedObject)
			throws DataAccessRuntimeException {
		final T persistentObject = entityManager().merge(
				persistentOrDetachedObject);
		getLog().debug("Updated entity = [{}]", persistentOrDetachedObject);

		return persistentObject;
	}

	/**
	 * @see com.acme.orderplacement.persistence.support.GenericJpaDao#makeTransient(java.lang.Object)
	 */
	@StateModifyingPersistenceOperation(idempotent = true)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void makeTransient(final T persistentOrDetachedObject)
			throws DataAccessRuntimeException, ObjectTransientException {
		entityManager().remove(persistentOrDetachedObject);
		getLog().debug("Removed entity = [{}] from persistent storage.",
				persistentOrDetachedObject);
	}

	// ------------------------------------------------------------------------
	// Protected
	// ------------------------------------------------------------------------

	/**
	 * @param queryName
	 * @param parameters
	 * @return
	 */
	protected List<T> findByNamedQuery(final String queryName,
			final Map<String, ?> parameters) throws DataAccessRuntimeException {
		Validate.notNull(queryName, "queryName");
		final Query namedQuery = entityManager().createNamedQuery(queryName);
		for (final Map.Entry<String, ?> param : parameters.entrySet()) {
			namedQuery.setParameter(param.getKey(), param.getValue());
		}

		return Collections.<T> unmodifiableList(namedQuery.getResultList());
	}

	/**
	 * @param queryName
	 * @param parameters
	 * @return
	 */
	protected List<T> findByNamedQuery(final String queryName,
			final Object... parameters) throws DataAccessRuntimeException {
		Validate.notNull(queryName, "queryName");
		final Query namedQuery = entityManager().createNamedQuery(queryName);
		int idx = 1;
		for (final Object param : parameters) {
			namedQuery.setParameter(idx++, param);
		}

		return Collections.<T> unmodifiableList(namedQuery.getResultList());
	}

	/**
	 * @param queryName
	 * @param parameters
	 * @return
	 */
	protected T findUniqueByNamedQuery(final String queryName,
			final Map<String, ?> parameters) throws DataAccessRuntimeException {
		Validate.notNull(queryName, "queryName");
		final List<T> resultList = findByNamedQuery(queryName, parameters);
		final T uniqueResult;
		if (resultList.isEmpty()) {
			uniqueResult = null;
		} else if (resultList.size() == 1) {
			uniqueResult = resultList.get(0);
		} else {
			throw new IncorrectResultSizeDataAccessException("Query ["
					+ queryName + "] did not return unique result", 1,
					resultList.size());
		}

		return uniqueResult;
	}

	/**
	 * @param queryName
	 * @param parameters
	 * @return
	 */
	protected T findUniqueByNamedQuery(final String queryName,
			final Object... parameters) throws DataAccessRuntimeException {
		Validate.notNull(queryName, "queryName");
		final List<T> resultList = findByNamedQuery(queryName, parameters);
		final T uniqueResult;
		if (resultList.isEmpty()) {
			uniqueResult = null;
		} else if (resultList.size() == 1) {
			uniqueResult = resultList.get(0);
		} else {
			throw new IncorrectResultSizeDataAccessException("Query ["
					+ queryName + "] did not return unique result", 1,
					resultList.size());
		}

		return uniqueResult;
	}

	protected Class<T> getPersistentClass() {
		return this.persistentClass;
	}

	protected final Logger getLog() {

		return this.log;
	}

	protected abstract EntityManager entityManager();
}
