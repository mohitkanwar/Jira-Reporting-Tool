package com.mk.jira.sort;

import java.io.IOException;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mk.jira.reporting.Configurations;
import com.mk.jira.reporting.InstanceCache;

public class JiraTicketStatusSortUtil {
	private static int statusCount;
	private static JsonArray statusArray;
	
	static {
		try {
			String statusjson=InstanceCache.filehandler.getConfigFileContents(Configurations.STATUS_FILE_PATH);
			JsonParser jsonParser = new JsonParser();
			JsonObject statusJSON = (JsonObject) jsonParser.parse(statusjson);
			statusArray=statusJSON.get("statuses").getAsJsonArray();
			
		} catch (IOException e) {
			throw new RuntimeException(Configurations.STATUS_FILE_PATH
					+ "Not found or unreachable");
		}
	}

	public Integer getSortOrder(String status) {
		Iterator<JsonElement> it =statusArray.iterator();
		while(it.hasNext()){
			JsonObject jo = it.next().getAsJsonObject();
			if(jo.get("name").getAsString().equals(status)){
				return jo.get("sortOrder").getAsInt();
			}
		}
		return -1;
	}
	public Integer getLifeSortOrder(String status) {
		Iterator<JsonElement> it =statusArray.iterator();
		while(it.hasNext()){
			JsonObject jo = it.next().getAsJsonObject();
			if(jo.get("name").getAsString().equals(status)){
				return jo.get("lifeOrder").getAsInt();
			}
		}
		return -1;
	}
	public String getStatusColour(String status) {
		Iterator<JsonElement> it =statusArray.iterator();
		while(it.hasNext()){
			JsonObject jo = it.next().getAsJsonObject();
			if(jo.get("name").getAsString().equals(status)){
				return jo.get("colour").getAsString();
			}
		}
		return "White";
	}

	public static int getStatusCount() {
		return statusCount;
	}

}
