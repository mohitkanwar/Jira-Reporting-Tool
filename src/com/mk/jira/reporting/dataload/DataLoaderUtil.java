package com.mk.jira.reporting.dataload;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mk.jira.reporting.Configurations;
import com.mk.jira.reporting.JiraConstants;
import com.mk.jira.reporting.model.Comment;
import com.mk.jira.reporting.model.Contact;
import com.mk.jira.reporting.model.History;
import com.mk.jira.reporting.model.HistoryItem;
import com.mk.jira.reporting.model.JiraTicket;
import com.mk.jira.reporting.model.Status;

public class DataLoaderUtil {

	private final static DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss");

	public List<JiraTicket> getTickets(DataLoadMode mode)
			throws KeyManagementException, NoSuchAlgorithmException,
			CertificateException, KeyStoreException, NoSuchProviderException,
			IOException, ParseException {
		return parseBoardJson(mode);
	}

	private List<JiraTicket> parseBoardJson(DataLoadMode mode)
			throws ParseException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			NoSuchProviderException {
		List<JiraTicket> tickets = new ArrayList<JiraTicket>();
		JsonParser jsonParser = new JsonParser();
		JsonObject jiraBoardJsonObject = (JsonObject) jsonParser.parse(mode
				.getJiraBoard());
		JsonArray issuesArray = jiraBoardJsonObject.getAsJsonArray(JiraConstants.ISSUES_KEY);
		Iterator<JsonElement> it = issuesArray.iterator();

		while (it.hasNext()) {
			loadTicket(mode, tickets, jsonParser, it.next());
		}
		return tickets;
	}

	private void loadTicket(DataLoadMode mode, List<JiraTicket> tickets,
			JsonParser jsonParser, JsonElement t )
			throws ParseException {
		JiraTicket ticket = new JiraTicket();
		
		JsonObject ticketJo = (JsonObject) jsonParser.parse(t.toString());
		JsonObject fields = getFields(jsonParser, ticketJo);
		loadSummary(ticket, fields);
		loadCreateDate(ticket, fields);
		loadAssignee(ticket, fields);
		loadTicketId(ticket, ticketJo);
		String key = loadKey(ticket, ticketJo);
		loadJiraKey(ticket, key);
		loadLastUpdateDate(ticket, fields);
		loadDescription(ticket, fields);
		loadStatus(ticket, fields);
		loadDetailsLink(ticket, ticketJo);
		String ticketOutput = mode.getTicketJson(ticket);
		JsonObject issueJsonObject = (JsonObject) jsonParser
				.parse(ticketOutput);
		JsonObject ticketFields = issueJsonObject.get(JiraConstants.FIELDS_KEY)
				.getAsJsonObject();
		loadIssueType(ticket, ticketFields);
		loadComments(ticket, ticketFields);
		loadChangeLog(ticket, issueJsonObject);
		tickets.add(ticket);
		System.out.println("Loaded Ticket : " + ticket.getKey());
	}

	private void loadDetailsLink(JiraTicket ticket, JsonObject ticketJo) {
		String link = ticketJo.get(JiraConstants.SELF_KEY).getAsString();
		ticket.setDetailsLink(link);
	}

	private void loadStatus(JiraTicket ticket, JsonObject fields) {
		String statusName = fields.get(JiraConstants.STATUS_KEY).getAsJsonObject()
				.get(JiraConstants.NAME_KEY).getAsString();
		String statusIcon = fields.get(JiraConstants.STATUS_KEY).getAsJsonObject()
				.get(JiraConstants.ICON_URL_KEY).getAsString();
		Status statusObj = new Status();
		statusObj.setName(statusName);
		statusObj.setIconUrl(statusIcon);
		ticket.setStatus(statusObj);
	}

	private void loadDescription(JiraTicket ticket, JsonObject fields) {
		String description = fields.get(JiraConstants.DESCRIPTION_KEY).getAsString();
		ticket.setDescription(description);
	}

	private void loadLastUpdateDate(JiraTicket ticket, JsonObject fields) {
		String updated = fields.get(JiraConstants.UPDATED_KEY).getAsString();
		ticket.setLastupdated(updated);
	}

	private void loadJiraKey(JiraTicket ticket, String key) {
		ticket.setJiraLink(Configurations.JIRA_SERVER_PATH+JiraConstants.BROWSE_KEY + key);
	}

	private String loadKey(JiraTicket ticket, JsonObject ticketJo) {
		String key = ticketJo.get(JiraConstants.KEY_KEY).getAsString();
		ticket.setKey(key);
		return key;
	}

	private void loadTicketId(JiraTicket ticket, JsonObject ticketJo) {
		String id = ticketJo.get(JiraConstants.ID_KEY).getAsString();
		ticket.setJiraIssueId(id);
	}

	private void loadAssignee(JiraTicket ticket, JsonObject fields) {
		JsonObject assignee = fields.get(JiraConstants.ASSIGNEE_KEY).getAsJsonObject();
		Contact contact = new Contact();
		contact.setName(assignee.get(JiraConstants.DISPLAY_NAME_KEY).getAsString());
		contact.setEmailAddress(assignee.get(JiraConstants.EMAIL_ADDRESS_KEY).getAsString());
		ticket.setAssignee(contact);
	}

	private void loadCreateDate(JiraTicket ticket, JsonObject fields)
			throws ParseException {
		String created = fields.get(JiraConstants.CREATED_KEY).getAsString();
		ticket.setCreated(DATE_FORMAT.parse(created));
	}

	private void loadSummary(JiraTicket ticket, JsonObject fields) {
		String summary = fields.get(JiraConstants.SUMMARY_KEY).getAsString();
		ticket.setSummary(summary);
	}

	private JsonObject getFields(JsonParser jsonParser, JsonObject ticketJo) {
		JsonObject fields = (JsonObject) jsonParser.parse(ticketJo.get(
				JiraConstants.FIELDS_KEY).toString());
		return fields;
	}

	private void loadIssueType(JiraTicket ticket, JsonObject ticketFields) {
		String issueType = ticketFields.get(JiraConstants.ISSUETYPE_KEY).getAsJsonObject()
				.get(JiraConstants.NAME_KEY).getAsString();
		ticket.setIssuetype(issueType);
	}

	private void loadComments(JiraTicket ticket, JsonObject ticketFields)
			throws ParseException {
		JsonArray commentFieldArray = ticketFields.get(JiraConstants.COMMENT_KEY)
				.getAsJsonObject().get(JiraConstants.COMMENTS_KEY).getAsJsonArray();

		Iterator<JsonElement> cIt = commentFieldArray.iterator();
		List<Comment> comments = new ArrayList<Comment>();
		ticket.setComments(comments);
		while (cIt.hasNext()) {
			JsonObject comment = cIt.next().getAsJsonObject();

			Comment c = new Comment();
			Contact author = new Contact();

			author.setName(comment.get(JiraConstants.AUTHOR_KEY).getAsJsonObject()
					.get(JiraConstants.DISPLAY_NAME_KEY).getAsString());
			author.setEmailAddress(comment.get(JiraConstants.AUTHOR_KEY).getAsJsonObject()
					.get(JiraConstants.EMAIL_ADDRESS_KEY).getAsString());
			c.setAuthor(author);
			c.setBody(comment.get(JiraConstants.BODY_KEY).getAsString());
			c.setCreateDate(comment.get(JiraConstants.CREATED_KEY).getAsString());
			comments.add(c);
		}
	}

	private void loadChangeLog(JiraTicket ticket, JsonObject issueJsonObject)
			throws ParseException {
		JsonArray changeLogArray = issueJsonObject.get(JiraConstants.CHANGELOG_KEY)
				.getAsJsonObject().get(JiraConstants.HISTORIES_KEY).getAsJsonArray();
		Iterator<JsonElement> changeLogArrayIterator = changeLogArray
				.iterator();
		List<History> changeLog = new ArrayList<History>();

		while (changeLogArrayIterator.hasNext()) {
			History history = new History();
			JsonObject historyJson = changeLogArrayIterator.next()
					.getAsJsonObject();
			Contact author = new Contact();
			author.setName(historyJson.get(JiraConstants.AUTHOR_KEY).getAsJsonObject()
					.get(JiraConstants.DISPLAY_NAME_KEY).getAsString());
			author.setEmailAddress(historyJson.get(JiraConstants.AUTHOR_KEY)
					.getAsJsonObject().get(JiraConstants.EMAIL_ADDRESS_KEY).getAsString());
			history.setAuthor(author);

			history.setCreated(DATE_FORMAT.parse(historyJson.get(JiraConstants.CREATED_KEY)
					.getAsString()));
			loadHistoryItems(history, historyJson);
			changeLog.add(history);
		}
		Collections.sort(changeLog);
		ticket.setChangelog(changeLog);
	}

	private void loadHistoryItems(History history, JsonObject historyJson) {
		JsonArray historyItems = historyJson.get(JiraConstants.ITEMS_KEY)
				.getAsJsonArray();

		Iterator<JsonElement> historyItemsIterator = historyItems
				.iterator();
		List<HistoryItem> items = new ArrayList<HistoryItem>();
		while (historyItemsIterator.hasNext()) {
			JsonObject historyItemJson = historyItemsIterator.next()
					.getAsJsonObject();
			HistoryItem historyItem = new HistoryItem();
			historyItem.setField(historyItemJson.get(JiraConstants.FIELD_KEY)
					.getAsString());
			historyItem.setFieldtype(historyItemJson.get(JiraConstants.FIELDTYPE_KEY)
					.getAsString());

			JsonElement historyItem_from = historyItemJson.get(JiraConstants.FROM_KEY);
			if (!historyItem_from.isJsonNull()) {
				historyItem.setFrom((historyItem_from.getAsString()));
			}
			if (!historyItemJson.get(JiraConstants.FROM_STRING_KEY).isJsonNull()) {
				historyItem.setFromString(historyItemJson.get(
						JiraConstants.FROM_STRING_KEY).getAsString());
			}

			JsonElement historyItem_to = historyItemJson.get(JiraConstants.TO_KEY);
			if (!historyItem_to.isJsonNull()) {
				historyItem.setTo((historyItem_to.getAsString()));
			}
			if (!historyItemJson.get(JiraConstants.TO_STRING_KEY).isJsonNull()) {
				historyItem.setToString(historyItemJson.get(JiraConstants.TO_STRING_KEY)
						.getAsString());
			}

			items.add(historyItem);
		}

		history.setItems(items);
	}

}
