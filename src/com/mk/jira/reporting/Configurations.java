package com.mk.jira.reporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class contains all the configurations of the application. This class
 * would construct all the configurations on load and would be immutable
 * throughout application
 * 
 * @author Mohit Kanwar
 *
 */

public final class Configurations {

	public static final String USER_DIRECTORY = System.getProperty("user.dir")
			+ File.separator;
	public static final String CONFIG_PROPERTIES_FILE_PATH = USER_DIRECTORY
			+ "config.properties";
	public static final String STATUS_FILE_PATH = USER_DIRECTORY
			+ "status.json";

	private static Properties prop = new Properties();
	static {

		InputStream input = null;
		try {
			File configFile = new File(CONFIG_PROPERTIES_FILE_PATH);
			if (configFile.exists()) {
				input = new FileInputStream(configFile);
				prop.load(input);
			}
			else{
				System.out.println("Looking forconfig file at ["+CONFIG_PROPERTIES_FILE_PATH+"] but it is not found.");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static final String JIRA_SERVER_PATH = populateJiraServerPath();
	public static final String JQL_JIRA_BOARD = populateJiraBoardJQL();
	public static final String LOCATION = new File(Configurations.class.getProtectionDomain().getCodeSource()
			.getLocation().getPath()).getParent()+ File.separator;
	public static final String PATH_TO_CACERTS = populatePathsToCacerts();
	public static final String AUTH_KEY = populateSecureKey();
	public static final String BOARD_JSON = LOCATION
			+ File.separator + "v2" + File.separator + "ticketsdata"
			+ File.separator + "board.json";
	private Configurations() {

	}

	private static String populateSecureKey() {
		return prop.getProperty("auth.key");
	}

	private static String populatePathsToCacerts() {
		return prop.getProperty("cacerts.path");
	}

	private static String populateJiraBoardJQL() {
		return prop.getProperty("jira.board.jql");
	}

	private static String populateJiraServerPath() {
		return prop.getProperty("jira.server.path");
	}

	

}
