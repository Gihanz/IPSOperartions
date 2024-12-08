package com.ips.gs.connector.connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.DynamicReferentialContainmentRelationship;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.UserContext;
import com.ips.gs.connector.util.PropertyReader;
import com.filenet.api.collection.DocumentSet;

public class CECUtil {

	private String uname = null;
	private String password = null;
	private String cp = null;
	private String ceuri = null;
	private String objName=null;
	public static Logger log = Logger.getLogger(CECUtil.class);

	public static ObjectStore objectStore = null;

	public static Domain domain = null;
	public static Connection connection = null;
	private String jaaspath = null;


	public CECUtil()
	{
		try
		{
			PropertyReader pr = new PropertyReader();
			Properties prop = pr.loadPropertyFile();
			this.uname = prop.getProperty("USERNAME");
			this.password = prop.getProperty("PASSWORD");
			this.ceuri = prop.getProperty("CEURI");
			this.cp = prop.getProperty("CONNECTION");
			this.objName=prop.getProperty("OBJECTSTORENAME");
			this.jaaspath = prop.getProperty("JAAS_PATH");
			System.out.println("Properties from Connection file is Uname is " + this.uname + " Password is " + this.password + " CE URI " + this.ceuri + " Connection point " + this.cp);
		}
		catch (Exception e)
		{
			log.info("Error Occured while initiating connector class");
		}
	}


	public void groupFoldersWithRCR() throws EngineRuntimeException{

		System.setProperty("java.security.auth.login.config", jaaspath);
		connection = Factory.Connection.getConnection(ceuri);
		Subject sub = UserContext.createSubject(connection, uname,password,null);
		UserContext.get().pushSubject(sub);
		domain = Factory.Domain.getInstance(connection, null);
		objectStore = Factory.ObjectStore.fetchInstance(domain, objName, null);
		System.out.println(objectStore.get_DisplayName());
		
		SearchScope search = new SearchScope(objectStore);
		
		SearchSQL sql= new SearchSQL("SELECT * FROM [IPS_InvoiceProcessing] WHERE [IPS_SupplierNumber] = '0772' AND [CmAcmCaseIdentifier] <> 'IPS_InvoiceProcessing_000000100003'");
		IndependentObjectSet independentObjectSet = search.fetchObjects(sql,Integer.getInteger("50"),null, Boolean.valueOf(true));
		   
		Folder fldr;
		Document doc;
		Iterator it = independentObjectSet.iterator();
		while (it.hasNext()){
			   fldr = (Folder)it.next();
			   System.out.println("Folder name = "+fldr.get_FolderName());
			   
			   Folder caseFolder = Factory.Folder.fetchInstance(objectStore,"0031F96A-0000-C017-B883-B6F0B758FA3F", null);
			   
			// create a subfolder under case folder
               Folder newSubFolder = Factory.Folder.createInstance(objectStore, "CmAcmCaseSubfolder");
               newSubFolder.set_Parent(caseFolder);
               newSubFolder.set_FolderName(fldr.get_FolderName());
               newSubFolder.getProperties().putValue("ORG_ID", "0772");
               newSubFolder.getProperties().putValue("CmAcmParentCase", caseFolder);
               newSubFolder.save(RefreshMode.REFRESH);
               
			   DocumentSet documents = fldr.get_ContainedDocuments();
			   
			   Iterator itd=documents.iterator();
               while(itd.hasNext())
               {
                   doc=(Document)itd.next();
                   System.out.println("Document name = "+doc.get_Name());
                   

                   //ReferentialContainmentRelationship rcr = caseFolder.file((IndependentlyPersistableObject) doc,AutoUniqueName.AUTO_UNIQUE, "Gihan", DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
   				   
                   DynamicReferentialContainmentRelationship rcr = Factory.DynamicReferentialContainmentRelationship.createInstance(objectStore, null);
                   System.out.println("1");
                   rcr.set_Head(doc); 
                   System.out.println("2");
                   rcr.set_Tail(newSubFolder); 
                   System.out.println("3");
                   
                   rcr.save(RefreshMode.REFRESH);
                   System.out.println("4");
               } 
               System.out.println("5");
		   }
		   
	}
	
	public void groupFoldersWithMove() throws EngineRuntimeException{

		System.setProperty("java.security.auth.login.config", jaaspath);
		connection = Factory.Connection.getConnection(ceuri);
		Subject sub = UserContext.createSubject(connection, uname,password,null);
		UserContext.get().pushSubject(sub);
		domain = Factory.Domain.getInstance(connection, null);
		objectStore = Factory.ObjectStore.fetchInstance(domain, objName, null);
		System.out.println(objectStore.get_DisplayName());
		
		SearchScope search = new SearchScope(objectStore);
		
		SearchSQL sql= new SearchSQL("SELECT * FROM [IPS_InvoiceProcessing] WHERE [IPS_SupplierNumber] = '0772' AND [CmAcmCaseIdentifier] <> 'IPS_InvoiceProcessing_000000100003'");
		IndependentObjectSet independentObjectSet = search.fetchObjects(sql,Integer.getInteger("50"),null, Boolean.valueOf(true));
		   
		Folder fldr;
		Document doc;
		Iterator it = independentObjectSet.iterator();
		while (it.hasNext()){
			   fldr = (Folder)it.next();
			   System.out.println("Folder name = "+fldr.get_FolderName());
			   
			   Folder caseFolder = Factory.Folder.fetchInstance(objectStore,"0031F96A-0000-C017-B883-B6F0B758FA3F", null);
			   
			// create a subfolder under case folder
               Folder newSubFolder = Factory.Folder.createInstance(objectStore, "CmAcmCaseSubfolder");
               newSubFolder.set_Parent(caseFolder);
               newSubFolder.set_FolderName(fldr.get_FolderName());
               newSubFolder.getProperties().putValue("ORG_ID", "0772");
               newSubFolder.getProperties().putValue("CmAcmParentCase", caseFolder);
               newSubFolder.save(RefreshMode.REFRESH);
               
               fldr.move(newSubFolder);
               fldr.save(RefreshMode.REFRESH);
               
			   /*DocumentSet documents = fldr.get_ContainedDocuments();
			   
			   Iterator itd=documents.iterator();
               while(itd.hasNext())
               {
                   doc=(Document)itd.next();
                   System.out.println("Document name = "+doc.get_Name());
                   

                   //ReferentialContainmentRelationship rcr = caseFolder.file((IndependentlyPersistableObject) doc,AutoUniqueName.AUTO_UNIQUE, "Gihan", DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
   				   
                   DynamicReferentialContainmentRelationship rcr = Factory.DynamicReferentialContainmentRelationship.createInstance(objectStore, null);
                   System.out.println("1");
                   rcr.set_Head(doc); 
                   System.out.println("2");
                   rcr.set_Tail(newSubFolder); 
                   System.out.println("3");
                   
                   rcr.save(RefreshMode.REFRESH);
                   System.out.println("4");
               }*/ 
               System.out.println("5");
		   }
		   
	}
	
	public static void main(String[] args) {
		CECUtil ss = new CECUtil();
		//ss.groupFoldersWithRCR();
		ss.groupFoldersWithMove();
	}

}
