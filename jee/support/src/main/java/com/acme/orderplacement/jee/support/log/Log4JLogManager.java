/**
 * 
 */
package com.acme.orderplacement.jee.support.log;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * <p>
 * A <code>JMX</code> enabled controller for dynamically changing
 * <code>Log4J</code> log levels at runtime.
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
@Component(Log4JLogManager.COMPONENT_NAME)
@ManagedResource(objectName = "com.acme.orderplacement:layer=PlatformLayer,type=Log4JComponent,name=Log4JLogManager", description = "An MBean for dynamically changing a category's log level at runtime")
public class Log4JLogManager {

	// -------------------------------------------------------------------------
	// Fields
	// -------------------------------------------------------------------------

	public static final String COMPONENT_NAME = "jee.support.log.Log4JLogManager";

	public static final String DEFAULT_PROPERTIES_LOCATION = "META-INF/conf/log4j-ear.properties";

	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Where our <code>log4j.properties</code> are to be found <strong>on the
	 * classpath</strong>.
	 */
	private String configurationPropertiesLocation = DEFAULT_PROPERTIES_LOCATION;

	// -------------------------------------------------------------------------
	// API
	// -------------------------------------------------------------------------

	/**
	 * @return the configurationPropertiesLocation
	 */
	@ManagedAttribute(defaultValue = Log4JLogManager.DEFAULT_PROPERTIES_LOCATION)
	public String getConfigurationPropertiesLocation() {
		return this.configurationPropertiesLocation;
	}

	/**
	 * @param configurationPropertiesLocation
	 * @throws IllegalArgumentException
	 */
	@ManagedAttribute(description = "Where our log4j.properties are to be found on the classpath", defaultValue = Log4JLogManager.DEFAULT_PROPERTIES_LOCATION)
	public void setConfigurationPropertiesLocation(
			final String configurationPropertiesLocation)
			throws IllegalArgumentException {
		Validate.notEmpty(configurationPropertiesLocation,
				"configurationPropertiesLocation");
		this.configurationPropertiesLocation = configurationPropertiesLocation;
	}

	/**
	 * 
	 */
	@PostConstruct
	@ManagedOperation(description = "(Re)Configure Log4J")
	public void configure() {
		logToStdout("(Re)Configuring Log4J using ["
				+ getConfigurationPropertiesLocation() + "] ...");
		PropertyConfigurator
				.configure(resolveResourceOrFail(getConfigurationPropertiesLocation()));
		logToStdout("Finished (re)configuring Log4J using ["
				+ getConfigurationPropertiesLocation() + "]");
	}

	/**
	 * 
	 */
	@PreDestroy
	@ManagedOperation(description = "Stop Log4J")
	public void close() {
		logToStdout("Stopping Log4J ...");
		LogManager.shutdown();
		logToStdout("Log4J stopped");
	}

	/**
	 * <p>
	 * Change the {@link Level <code>LogLevel</code>} for all {@link Logger
	 * <code>Logger</code>}s sharing the supplied <code>packageName</code> to
	 * <code>newLogLevel</code>. Return the old <code>LogLevel</code>.
	 * </p>
	 * 
	 * @param packageName
	 *            The common package name shared by all <code>Logger</code>s
	 *            whose <code>LogLevel</code> should be changed. Must not be
	 *            <code>null</code>.
	 * @param newLogLevel
	 *            A string representation of the new <code>LogLevel</code>.
	 *            Valid values are <code>OFF</code>, <code>FATAL</code>,
	 *            <code>ERROR</code>, <code>WARN</code>, <code>INFO</code>,
	 *            <code>DEBUG</code> and <code>ALL</code>. If
	 *            <code>newLogLevel</code> is not a valid <code>LogLevel</code>
	 *            this method assumes a new <code>LogLevel</code> of
	 *            <code>DEBUG</code>.
	 * @return The old <code>LogLevel</code>
	 * @throws IllegalArgumentException
	 *             If any of the arguments is <code>null</code> or
	 *             <code>empty</code>
	 */
	@ManagedOperation(description = "Set the log level for the supplied package to the supplied new log level, returning the former log level")
	@ManagedOperationParameters( {
			@ManagedOperationParameter(name = "packageName", description = "Name of the java package whose log level is to be changed"),
			@ManagedOperationParameter(name = "newLogLevel", description = "The log level to change to") })
	public String changeLogLevel(final String packageName,
			final String newLogLevel) throws IllegalArgumentException {
		Validate.notEmpty(packageName, "packageName");
		Validate.notEmpty(newLogLevel, "newLogLevel");
		this.log.info("Changing the LogLevel for category [{}] to [{}] ...",
				packageName, newLogLevel);

		/*
		 * Returns DEBUG if newLogLevel is not a valid Level.
		 */
		final Level newLogLevelObj = Level.toLevel(newLogLevel);
		final Logger logger = LogManager.getLogger(packageName);
		final Level oldEffectiveLevel = logger.getEffectiveLevel();
		logger.setLevel(newLogLevelObj);
		this.log.info(
				"LogLevel for category [{}] changed from [{}] to [{}] ...",
				new Object[] { packageName, oldEffectiveLevel.toString(),
						newLogLevelObj.toString() });

		return oldEffectiveLevel.toString();
	}

	/**
	 * @return
	 */
	@ManagedAttribute(description = "The list of all Loggers currently known to the Log4J hierarchy")
	public List<String> getLoggerNames() {
		final Enumeration<Logger> currentLoggers = LogManager
				.getCurrentLoggers();
		final List<String> loggerNames = new ArrayList<String>();
		while (currentLoggers.hasMoreElements()) {
			final Logger currentLogger = currentLoggers.nextElement();
			loggerNames.add(currentLogger.getName());
		}

		return loggerNames;
	}

	// -------------------------------------------------------------------------
	// Internal
	// -------------------------------------------------------------------------

	private URL resolveResourceOrFail(final String resourcePath)
			throws IllegalArgumentException {
		Validate.notEmpty(resourcePath, "resourcePath");
		final URL resolvedResource = classLoader().getResource(resourcePath);
		if (resolvedResource == null) {

			throw new IllegalArgumentException("Could not find ["
					+ resourcePath + "] anywhere on the current classpath");
		}

		return resolvedResource;
	}

	private ClassLoader classLoader() {
		final ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader() != null ? Thread.currentThread()
				.getContextClassLoader() : getClass().getClassLoader();

		return classLoader;
	}

	private void logToStdout(final String msg) {
		System.out.println(String.format("[%1$s] [%2$s] [%3$s] - %4$s", Thread
				.currentThread().getName(), new Date(), getClass().getName(),
				msg));
	}
}
