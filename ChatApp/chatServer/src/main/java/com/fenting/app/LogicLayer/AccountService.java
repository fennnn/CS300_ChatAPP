package com.fenting.app.LogicLayer;

import com.fenting.app.DataLayer.AccountDataService;
import com.google.common.hash.Hashing;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class AccountService {
    static private AccountService accountService = null;
    static public synchronized AccountService getAccountService() {
        if(accountService == null) {
            accountService = new AccountService();
        }
        return accountService;
    }

    public void signUp(String account, String password, CompletionHandler handler) {
        password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
        AccountDataService accountDataService = AccountDataService.getAccountDataService();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", "signUp");
            if(accountDataService.addAccount(account, password)) {
                jsonObject.put("error", 0);
            }else {
                jsonObject.put("error","Sorry, account already exist!");
            }
            handler.response(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void login(String account, String password, CompletionHandler handler) {
        password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
        AccountDataService accountDataService = AccountDataService.getAccountDataService();
        int result = accountDataService.varifyAccount(account, password);
        JSONObject jsonObject = new JSONObject();
        try {
            if(result == -1) {
                jsonObject.put("command", "login");
                jsonObject.put("error", "sorry, cannot find your account!");
                handler.response(jsonObject);
                return;
            } else if(result == -2){
                jsonObject.put("command", "login");
                jsonObject.put("error", "sorry, your password is incorrect!");
                handler.response(jsonObject);
                return;
            }else{
                jsonObject.put("command", "login");
                jsonObject.put("error", 0);
                FriendService.getFriendService().addIntoList(account);
            }
            handler.response(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
