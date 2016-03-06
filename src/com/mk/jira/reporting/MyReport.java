package com.mk.jira.reporting;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.mk.jira.reporting.dataload.DataLoadMode;
import com.mk.jira.reporting.dataload.DataLoadOfflineMode;
import com.mk.jira.reporting.dataload.DataLoadOnlineMode;
import com.mk.jira.reporting.dataload.DataLoaderUtil;
import com.mk.jira.reporting.model.JiraTicket;
import com.mk.jira.reporting.output.HTMLReport;

public class MyReport {

	private static boolean offlineMode = false;

	public static void main(String[] args) throws IOException,
			KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, NoSuchProviderException,
			ParseException {
		System.out.print("Loading Tickets");
		offlineMode = Arrays.asList(args).contains("-o");
		DataLoaderUtil dl = new DataLoaderUtil();
		List<JiraTicket> tickets;
		DataLoadMode mode = offlineMode ? new DataLoadOfflineMode()
				: new DataLoadOnlineMode();
		tickets = dl.getTickets(mode);
		createHTMLReport(tickets);
	}

	private static void createHTMLReport(List<JiraTicket> tickets)
			throws IOException {
		HTMLReport report = new HTMLReport();
		report.paint(tickets);
	}

}
