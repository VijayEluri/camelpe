/**
 * 
 */
package com.acme.orderplacement.jee.framework.camelpe.camel.spi.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.model.ExecutorServiceAwareDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.ExecutorServiceStrategy;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.util.ObjectHelper;

/**
 * <p>
 * TODO: Insert short summary for CdiExecutorServiceStrategyUtil
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
final class CdiExecutorServiceStrategyUtil {

	/**
	 * <p>
	 * TODO: Insert short summary for CdiThreadFactory
	 * </p>
	 * 
	 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
	 * 
	 */
	private static final class CdiThreadFactory implements ThreadFactory {

		private final boolean daemon;

		private final String name;

		private final String pattern;

		CdiThreadFactory(final boolean daemon, final String name,
				final String pattern) {
			this.daemon = daemon;
			this.name = name;
			this.pattern = pattern;
		}

		public Thread newThread(final Runnable r) {
			final Thread answer = new WeldRequestContextInitiatingThread(r,
					getThreadName(this.pattern, this.name));
			answer.setDaemon(this.daemon);
			return answer;
		}
	}

	public static final String DEFAULT_PATTERN = "CDI Camel Thread ${counter} - ${name}";

	private static AtomicInteger threadCounter = new AtomicInteger();

	private CdiExecutorServiceStrategyUtil() {
	}

	private static synchronized int nextThreadCounter() {
		return threadCounter.getAndIncrement();
	}

	/**
	 * Creates a new thread name with the given prefix
	 * 
	 * @param pattern
	 *            the pattern
	 * @param name
	 *            the name
	 * @return the thread name, which is unique
	 */
	static String getThreadName(final String pattern, final String name) {
		final String normalizedPattern = pattern != null ? pattern
				: DEFAULT_PATTERN;

		// we support ${longName} and ${name} as name placeholders
		final String longName = name;
		final String shortName = name.contains("?") ? ObjectHelper.before(name,
				"?") : name;

		String answer = normalizedPattern.replaceFirst("\\$\\{counter\\}", ""
				+ nextThreadCounter());
		answer = answer.replaceFirst("\\$\\{longName\\}", longName);
		answer = answer.replaceFirst("\\$\\{name\\}", shortName);
		if ((answer.indexOf("$") > -1) || (answer.indexOf("${") > -1)
				|| (answer.indexOf("}") > -1)) {
			throw new IllegalArgumentException("Pattern is invalid: "
					+ normalizedPattern);
		}

		return answer;
	}

	/**
	 * Creates a new scheduled thread pool which can schedule threads.
	 * 
	 * @param poolSize
	 *            the core pool size
	 * @param pattern
	 *            pattern of the thread name
	 * @param name
	 *            ${name} in the pattern name
	 * @param daemon
	 *            whether the threads is daemon or not
	 * @return the created pool
	 */
	static ScheduledExecutorService newScheduledThreadPool(final int poolSize,
			final String pattern, final String name, final boolean daemon) {
		return Executors.newScheduledThreadPool(poolSize, new CdiThreadFactory(
				daemon, name, pattern));
	}

	/**
	 * Creates a new fixed thread pool
	 * 
	 * @param poolSize
	 *            the fixed pool size
	 * @param pattern
	 *            pattern of the thread name
	 * @param name
	 *            ${name} in the pattern name
	 * @param daemon
	 *            whether the threads is daemon or not
	 * @return the created pool
	 */
	static ExecutorService newFixedThreadPool(final int poolSize,
			final String pattern, final String name, final boolean daemon) {
		return Executors.newFixedThreadPool(poolSize, new CdiThreadFactory(
				daemon, name, pattern));
	}

	/**
	 * Creates a new single thread pool (usually for background tasks)
	 * 
	 * @param pattern
	 *            pattern of the thread name
	 * @param name
	 *            ${name} in the pattern name
	 * @param daemon
	 *            whether the threads is daemon or not
	 * @return the created pool
	 */
	static ExecutorService newSingleThreadExecutor(final String pattern,
			final String name, final boolean daemon) {
		return Executors.newSingleThreadExecutor(new CdiThreadFactory(daemon,
				name, pattern));
	}

	/**
	 * Creates a new cached thread pool
	 * 
	 * @param pattern
	 *            pattern of the thread name
	 * @param name
	 *            ${name} in the pattern name
	 * @param daemon
	 *            whether the threads is daemon or not
	 * @return the created pool
	 */
	static ExecutorService newCachedThreadPool(final String pattern,
			final String name, final boolean daemon) {
		return Executors.newCachedThreadPool(new CdiThreadFactory(daemon, name,
				pattern));
	}

	/**
	 * Creates a new custom thread pool using 60 seconds as keep alive and with
	 * an unbounded queue.
	 * 
	 * @param pattern
	 *            pattern of the thread name
	 * @param name
	 *            ${name} in the pattern name
	 * @param corePoolSize
	 *            the core size
	 * @param maxPoolSize
	 *            the maximum pool size
	 * @return the created pool
	 */
	static ExecutorService newThreadPool(final String pattern,
			final String name, final int corePoolSize, final int maxPoolSize) {
		return newThreadPool(pattern, name, corePoolSize, maxPoolSize, 60,
				TimeUnit.SECONDS, -1,
				new ThreadPoolExecutor.CallerRunsPolicy(), true);
	}

	/**
	 * Creates a new custom thread pool
	 * 
	 * @param pattern
	 *            pattern of the thread name
	 * @param name
	 *            ${name} in the pattern name
	 * @param corePoolSize
	 *            the core size
	 * @param maxPoolSize
	 *            the maximum pool size
	 * @param keepAliveTime
	 *            keep alive time
	 * @param timeUnit
	 *            keep alive time unit
	 * @param maxQueueSize
	 *            the maximum number of tasks in the queue, use
	 *            <tt>Integer.MAX_VALUE</tt> or <tt>-1</tt> to indicate
	 *            unbounded
	 * @param rejectedExecutionHandler
	 *            the handler for tasks which cannot be executed by the thread
	 *            pool. If <tt>null</tt> is provided then
	 *            {@link java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
	 *            CallerRunsPolicy} is used.
	 * @param daemon
	 *            whether the threads is daemon or not
	 * @return the created pool
	 * @throws IllegalArgumentException
	 *             if parameters is not valid
	 */
	static ExecutorService newThreadPool(final String pattern,
			final String name, final int corePoolSize, final int maxPoolSize,
			final long keepAliveTime, final TimeUnit timeUnit,
			final int maxQueueSize,
			final RejectedExecutionHandler rejectedExecutionHandler,
			final boolean daemon) {

		// validate max >= core
		if (maxPoolSize < corePoolSize) {
			throw new IllegalArgumentException(
					"MaxPoolSize must be >= corePoolSize, was " + maxPoolSize
							+ " >= " + corePoolSize);
		}

		final BlockingQueue<Runnable> queue;
		if ((corePoolSize == 0) && (maxQueueSize <= 0)) {
			// use a synchronous so we can act like the cached thread pool
			queue = new SynchronousQueue<Runnable>();
		} else if (maxQueueSize <= 0) {
			// unbounded task queue
			queue = new LinkedBlockingQueue<Runnable>();
		} else {
			// bounded task queue
			queue = new LinkedBlockingQueue<Runnable>(maxQueueSize);
		}
		final ThreadPoolExecutor answer = new ThreadPoolExecutor(corePoolSize,
				maxPoolSize, keepAliveTime, timeUnit, queue);
		answer.setThreadFactory(new CdiThreadFactory(daemon, name, pattern));
		answer
				.setRejectedExecutionHandler(rejectedExecutionHandler != null ? rejectedExecutionHandler
						: new ThreadPoolExecutor.CallerRunsPolicy());

		return answer;
	}

	/**
	 * Will lookup and get the configured
	 * {@link java.util.concurrent.ExecutorService} from the given definition.
	 * <p/>
	 * This method will lookup for configured thread pool in the following order
	 * <ul>
	 * <li>from the definition if any explicit configured executor service.</li>
	 * <li>from the {@link org.apache.camel.spi.Registry} if found</li>
	 * <li>from the known list of {@link org.apache.camel.spi.ThreadPoolProfile
	 * ThreadPoolProfile(s)}.</li>
	 * <li>if none found, then <tt>null</tt> is returned.</li>
	 * </ul>
	 * The various {@link ExecutorServiceAwareDefinition} should use this helper
	 * method to ensure they support configured executor services in the same
	 * coherent way.
	 * 
	 * @param routeContext
	 *            the rout context
	 * @param name
	 *            name which is appended to the thread name, when the
	 *            {@link java.util.concurrent.ExecutorService} is created based
	 *            on a {@link org.apache.camel.spi.ThreadPoolProfile}.
	 * @param definition
	 *            the node definition which may leverage executor service.
	 * @return the configured executor service, or <tt>null</tt> if none was
	 *         configured.
	 * @throws IllegalArgumentException
	 *             is thrown if lookup of executor service in
	 *             {@link org.apache.camel.spi.Registry} was not found
	 */
	static ExecutorService getConfiguredExecutorService(
			final RouteContext routeContext,
			final String name,
			final ExecutorServiceAwareDefinition<? extends ProcessorDefinition<?>> definition)
			throws IllegalArgumentException {
		final ExecutorServiceStrategy strategy = routeContext.getCamelContext()
				.getExecutorServiceStrategy();
		ObjectHelper.notNull(strategy, "ExecutorServiceStrategy", routeContext
				.getCamelContext());

		// prefer to use explicit configured executor on the definition
		if (definition.getExecutorService() != null) {
			return definition.getExecutorService();
		} else if (definition.getExecutorServiceRef() != null) {
			final ExecutorService answer = strategy.lookup(definition, name,
					definition.getExecutorServiceRef());
			if (answer == null) {
				throw new IllegalArgumentException("ExecutorServiceRef "
						+ definition.getExecutorServiceRef()
						+ " not found in registry.");
			}
			return answer;
		}

		return null;
	}

	/**
	 * Will lookup and get the configured
	 * {@link java.util.concurrent.ScheduledExecutorService} from the given
	 * definition.
	 * <p/>
	 * This method will lookup for configured thread pool in the following order
	 * <ul>
	 * <li>from the definition if any explicit configured executor service.</li>
	 * <li>from the {@link org.apache.camel.spi.Registry} if found</li>
	 * <li>from the known list of {@link org.apache.camel.spi.ThreadPoolProfile
	 * ThreadPoolProfile(s)}.</li>
	 * <li>if none found, then <tt>null</tt> is returned.</li>
	 * </ul>
	 * The various {@link ExecutorServiceAwareDefinition} should use this helper
	 * method to ensure they support configured executor services in the same
	 * coherent way.
	 * 
	 * @param routeContext
	 *            the rout context
	 * @param name
	 *            name which is appended to the thread name, when the
	 *            {@link java.util.concurrent.ExecutorService} is created based
	 *            on a {@link org.apache.camel.spi.ThreadPoolProfile}.
	 * @param definition
	 *            the node definition which may leverage executor service.
	 * @return the configured executor service, or <tt>null</tt> if none was
	 *         configured.
	 * @throws IllegalArgumentException
	 *             is thrown if lookup of executor service in
	 *             {@link org.apache.camel.spi.Registry} was not found or the
	 *             found instance is not a ScheduledExecutorService type.
	 */
	static ScheduledExecutorService getConfiguredScheduledExecutorService(
			final RouteContext routeContext,
			final String name,
			final ExecutorServiceAwareDefinition<? extends ProcessorDefinition<?>> definition)
			throws IllegalArgumentException {
		final ExecutorServiceStrategy strategy = routeContext.getCamelContext()
				.getExecutorServiceStrategy();
		ObjectHelper.notNull(strategy, "ExecutorServiceStrategy", routeContext
				.getCamelContext());

		// prefer to use explicit configured executor on the definition
		if (definition.getExecutorService() != null) {
			final ExecutorService executorService = definition
					.getExecutorService();
			if (executorService instanceof ScheduledExecutorService) {
				return (ScheduledExecutorService) executorService;
			}
			throw new IllegalArgumentException("ExecutorServiceRef "
					+ definition.getExecutorServiceRef()
					+ " is not an ScheduledExecutorService instance");
		} else if (definition.getExecutorServiceRef() != null) {
			final ScheduledExecutorService answer = strategy.lookupScheduled(
					definition, name, definition.getExecutorServiceRef());
			if (answer == null) {
				throw new IllegalArgumentException("ExecutorServiceRef "
						+ definition.getExecutorServiceRef()
						+ " not found in registry.");
			}
			return answer;
		}

		return null;
	}
}
