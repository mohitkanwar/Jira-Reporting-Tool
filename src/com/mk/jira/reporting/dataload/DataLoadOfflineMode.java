package com.mk.jira.reporting.dataload;

import java.io.File;
import java.io.IOException;

import com.mk.jira.reporting.Configurations;
import com.mk.jira.reporting.InstanceCache;
import com.mk.jira.reporting.model.JiraTicket;

public class DataLoadOfflineMode implements DataLoadMode {
	
	@Override
	public String getJiraBoard() {
		System.out.println("In Offline Mode");
		try {
			return InstanceCache.filehandler.getFileContents(Configurations.BOARD_JSON);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getTicketJson(JiraTicket ticket) {
		try {
			String ticketOutput = InstanceCache.filehandler.getFileContents(Configurations.LOCATION
					+ File.separator + "v2" + File.separator + "ticketsdata"
					+ File.separator + ticket.getKey().replaceAll("\"", "")
					+ ".json");
			return ticketOutput;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
