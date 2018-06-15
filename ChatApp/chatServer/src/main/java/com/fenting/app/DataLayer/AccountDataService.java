package com.fenting.app.DataLayer;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class AccountDataService {

    static private AccountDataService accountDataService = null;
    static synchronized public AccountDataService getAccountDataService() {
        if(accountDataService == null) {
            accountDataService = new AccountDataService();
            accountDataService.init();
        }
        return accountDataService;
    }

    private HashMap<String, String> accountMap;
    private Lock lock = new ReentrantLock();

    private void init() {
        File rootFolder = new File("Data");
        if(!rootFolder.exists())
            rootFolder.mkdir();
        File accountFile = new File("Data/accounts.xml");
        if(!accountFile.exists()) {
            try {
                accountFile.createNewFile();
                FileWriter fileWriter = new FileWriter(accountFile);
                fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                fileWriter.write("<accounts>\n");
                fileWriter.write("</accounts>\n");
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lock.lock();
        accountMap = new HashMap<String, String>();
        lock.unlock();
        try {
            lock.lock();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(accountFile);
            Element rootElement = document.getRootElement();
            List<Element> accountList = rootElement.getChildren();
            for( int i=0; i<accountList.size(); ++i) {
                Element account = accountList.get(i);
                Attribute attribute = account.getAttribute("accountID");
                accountMap.put(attribute.getValue(), account.getChild("password").getText());
            }
            lock.unlock();
        }catch(JDOMException e){
            e.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public int varifyAccount(String account, String password){
        lock.lock();
        if(!accountMap.containsKey(account)) {
            lock.unlock();
            return -1;
        }
        if(!accountMap.get(account).equals(password)) {
            lock.unlock();
            return -2;
        }
        lock.unlock();
        return 0;
    }

    public boolean addAccount(String account, String password) {
        lock.lock();
        if(accountMap.containsKey(account)){
            lock.unlock();
            return false;
        }
        accountMap.put(account, password);
        Element rootElement = new Element("accounts");
        Document document = new Document(rootElement);
        Iterator it = accountMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Element accountElement = new Element("account");
            accountElement.setAttribute(new Attribute("accountID", (String)entry.getKey()));
            Element passwordElement = new Element("password");
            passwordElement.setText((String) entry.getValue());
            accountElement.addContent(passwordElement);
            document.getRootElement().addContent(accountElement);
        }
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try {
            FileOutputStream out;
            File file = new File("Data/accounts.xml");
            out = new FileOutputStream(file);
            xmlOutput.output(document, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lock.unlock();
        return true;
    }
}
