package com.ips.gs.IPSOperartion;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;
import org.apache.log4j.PropertyConfigurator;

import com.ips.gs.connector.connector.CECUtil;
import com.ips.gs.connector.connector.PEConnector;
import com.ips.gs.connector.util.PropertyReader;

import org.apache.log4j.Logger;

public class IPSOperations {

	public static Properties prop;
	public static Logger log = Logger.getLogger(IPSOperations.class);


	public void groupCasesBySupplierId(String wobnumber, String supplierId){
		
		Map<String, Object> caseData=null;

		try {
			PropertyReader pr = new PropertyReader();

			String pathSep = System.getProperty("file.separator");
			prop=pr.loadPropertyFile();
			String logpath = prop.getProperty("LOG4J_FILE_PATH");
			String activityRoot= prop.getProperty("LOG_PATH");
			String logPropertyFile =logpath+pathSep+"log4j.properties"; 


			PropertyConfigurator.configure(logPropertyFile);
			log = Logger.getLogger(IPSOperations.class);

			PropertyReader.loadLogConfiguration(logPropertyFile, activityRoot+"/GroupingCases", "GroupingCases.log");
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> Entered into Grouping Cases Method >>>>>>>>>>>>>>>>>>>>>>>>>>>>");

			String rosterName = prop.getProperty("ROSTER");
			log.info("WobNumber is "+wobnumber);
			log.info("Roster Name "+rosterName);
			PEConnector pecon = new PEConnector();
			caseData = pecon.getWorkObject(wobnumber, rosterName);

			//ceutil = new CECUtil();
			//docid=ceutil.addDocumentWithStream(fip,"CreditProposal_"+Wobnumber, docClass, Wobnumber);
			//pecon.addAttachment(docid, Wobnumber, rosterName);


			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> Grouping Cases Completed Sucessfully >>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error Occured while grouping : "+e.fillInStackTrace());
		}

	}

	public static void main(String[] args) {
		
		IPSOperations cc = new IPSOperations();

	}

}
