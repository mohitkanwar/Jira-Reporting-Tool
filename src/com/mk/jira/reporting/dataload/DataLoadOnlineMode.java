package com.mk.jira.reporting.dataload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.mk.jira.reporting.Configurations;
import com.mk.jira.reporting.InstanceCache;
import com.mk.jira.reporting.model.JiraTicket;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

public class DataLoadOnlineMode implements DataLoadMode {
	private static final String JQL_QUERY_STRING = "/rest/api/2/search?jql=";
	private static final String RESPONSE_TYPE = "application/json";
	private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
	private static final String AUTH_TYPE = "Basic ";
	private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

	@Override
	public String getJiraBoard() {
		String url = Configurations.JIRA_SERVER_PATH + JQL_QUERY_STRING
				+ Configurations.JQL_JIRA_BOARD;
		try {
			String output = "";
			ClientResponse response = getResponse(url);

			output = response.getEntity(String.class);
			InstanceCache.filehandler.createFile(output, Configurations.BOARD_JSON);
			return output;
		} catch (IOException | KeyManagementException
				| NoSuchAlgorithmException | KeyStoreException
				| CertificateException | NoSuchProviderException e) {
			throw new RuntimeException(e);
		}

	}

	private static ClientResponse getResponse(String url)
			throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, FileNotFoundException,
			NoSuchProviderException, IOException {
		Client client = getClient();
		WebResource webResource = client.resource(url);

		ClientResponse response = webResource
				.header(AUTHORIZATION_HEADER_NAME,
						AUTH_TYPE + Configurations.AUTH_KEY)
				.header(CONTENT_TYPE_HEADER_NAME, RESPONSE_TYPE)
				.accept(RESPONSE_TYPE).get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}
		return response;
	}

	private static Client getClient() throws NoSuchAlgorithmException,
			KeyStoreException, IOException, CertificateException,
			FileNotFoundException, NoSuchProviderException,
			KeyManagementException {
		ClientConfig config = new DefaultClientConfig();
		SSLContext ctx = SSLContext.getInstance("SSL");
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(Configurations.PATH_TO_CACERTS),
				"changeit".toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509",
				"SunJSSE");
		tmf.init(ks);

		TrustManager myTrustManager[] = tmf.getTrustManagers();

		ctx.init(null, myTrustManager, null);
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};
		config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
				new HTTPSProperties(hostnameVerifier, ctx));
		Client client = Client.create(config);
		return client;
	}

	private static String getTicketsFromJira(String key, String linkUrl)
			throws KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, FileNotFoundException,
			NoSuchProviderException, IOException {
		String ticketOutput;
		ClientResponse response = getResponse(linkUrl);
		ticketOutput = response.getEntity(String.class);
		try {
			InstanceCache.filehandler.createFile(ticketOutput, Configurations.LOCATION
					+ File.separator + "v2" + File.separator + "ticketsdata"
					+ File.separator + key.replaceAll("\"", "") + ".json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ticketOutput;
	}

	@Override
	public String getTicketJson(JiraTicket ticket) {

		try {
			return getTicketsFromJira(ticket.getKey(), ticket.getDetailsLink()+ "?expand=changelog");
		} catch (KeyManagementException | NoSuchAlgorithmException
				| KeyStoreException | CertificateException
				| NoSuchProviderException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

}
