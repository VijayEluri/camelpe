/**
 * 
 */
package com.acme.orderplacement.jee.framework.ws.internal.wslog;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.lang.Validate;

import com.acme.orderplacement.framework.wslog.WebserviceRequestDto;

/**
 * <p>
 * TODO: Insert short summary for
 * SOAPMessageContextToWebserviceRequestDtoConverter
 * </p>
 * 
 * @author <a href="mailto:olaf.bergner@saxsys.de">Olaf Bergner</a>
 * 
 */
public class SOAPMessageContextToWebserviceRequestDtoConverter {

	private static final String DEFAULT_SOAP_MESSAGE_ENCODING = "utf-8";

	private static final String UNKNOWN_WSDL_OPERATION = "<UNKNOWN>";

	/**
	 * @param soapMessageContext
	 * @param messageReceivedOn
	 * @return
	 * @throws SOAPException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws IllegalArgumentException
	 */
	public WebserviceRequestDto convert(
			final SOAPMessageContext soapMessageContext,
			final Date messageReceivedOn) throws SOAPException, IOException,
			UnsupportedEncodingException, IllegalArgumentException {
		Validate.notNull(soapMessageContext, "soapMessageContext");
		Validate.notNull(messageReceivedOn, "messageReceivedOn");

		final Map<String, String> convertedHttpRequestHeaders = extractHttpRequestHeadersFrom(soapMessageContext);

		final String contextRoot = extractContextRootFrom(soapMessageContext);

		final String wsdlServiceName = extractWsdlServiceNameFrom(soapMessageContext);

		final String wsdlOperation = extractWsdlOperationNameFrom(soapMessageContext);

		final String sourceIp = extractSourceIpFrom(soapMessageContext);

		final String soapMessageAsString = extractSoapMessageStringRepresentationFrom(soapMessageContext);

		return new WebserviceRequestDto(contextRoot, wsdlServiceName,
				wsdlOperation, sourceIp, messageReceivedOn,
				soapMessageAsString, convertedHttpRequestHeaders);
	}

	private Map<String, String> extractHttpRequestHeadersFrom(
			final SOAPMessageContext soapMessageContext) {
		final Map<String, List<String>> httpRequestHeaders = (Map<String, List<String>>) soapMessageContext
				.get(MessageContext.HTTP_REQUEST_HEADERS);
		final Map<String, String> convertedHttpRequestHeaders = new HashMap<String, String>();
		for (final Map.Entry<String, List<String>> header : httpRequestHeaders
				.entrySet()) {
			convertedHttpRequestHeaders.put(header.getKey(), header.getValue()
					.toString());
		}

		return convertedHttpRequestHeaders;
	}

	private String extractContextRootFrom(
			final SOAPMessageContext soapMessageContext) {
		final ServletContext servletContext = (ServletContext) soapMessageContext
				.get(MessageContext.SERVLET_CONTEXT);

		return servletContext.getContextPath();
	}

	private String extractWsdlServiceNameFrom(
			final SOAPMessageContext soapMessageContext) {
		final QName wsdlServiceQName = (QName) soapMessageContext
				.get(MessageContext.WSDL_SERVICE);

		return wsdlServiceQName.getLocalPart();
	}

	private String extractWsdlOperationNameFrom(
			final SOAPMessageContext soapMessageContext) {
		final QName wsdlOperationQName = (QName) soapMessageContext
				.get(MessageContext.WSDL_OPERATION);
		final String wsdlOperation = wsdlOperationQName != null ? wsdlOperationQName
				.getLocalPart()
				: UNKNOWN_WSDL_OPERATION;

		return wsdlOperation;
	}

	private String extractSourceIpFrom(
			final SOAPMessageContext soapMessageContext) {
		final String sourceIp = ((HttpServletRequest) soapMessageContext
				.get(MessageContext.SERVLET_REQUEST)).getRemoteAddr();

		return sourceIp;
	}

	private String extractSoapMessageStringRepresentationFrom(
			final SOAPMessageContext soapMessageContext) throws SOAPException,
			IOException, UnsupportedEncodingException {
		BufferedOutputStream soapMessageAsBytesBuffer = null;
		try {
			final SOAPMessage soapMessage = soapMessageContext.getMessage();

			final String soapMessageEncoding = (String) soapMessage
					.getProperty(SOAPMessage.CHARACTER_SET_ENCODING);

			final ByteArrayOutputStream soapMessageAsBytes = new ByteArrayOutputStream();
			soapMessageAsBytesBuffer = new BufferedOutputStream(
					soapMessageAsBytes);
			soapMessage.writeTo(soapMessageAsBytesBuffer);
			soapMessageAsBytesBuffer.flush();

			final String soapMessageAsString = new String(soapMessageAsBytes
					.toByteArray(),
					soapMessageEncoding != null ? soapMessageEncoding
							: DEFAULT_SOAP_MESSAGE_ENCODING);

			return soapMessageAsString;
		} finally {
			if (soapMessageAsBytesBuffer != null) {
				soapMessageAsBytesBuffer.close();
			}
		}
	}
}