package com.fenting.app.LogicLayer;

import com.fenting.app.NetworkLayer.CompletionHandler;
import com.fenting.app.NetworkLayer.NetworkService;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountService {
    static private AccountService accountService = null;
    static public String myAccount;
    static public AccountService getAccountService() {
        if(accountService == null)
            accountService = new AccountService();
        return accountService;
    }

    public void login(String account, String password, CompletionHandler handler) {
        NetworkService networkService = NetworkService.getNetworkService();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", "login");
            jsonObject.put("password", password);
            jsonObject.put("account", account);
            networkService.CGIRequest(jsonObject, handler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void signUp(String account, String password, CompletionHandler handler) {
        NetworkService networkService = NetworkService.getNetworkService();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", "signUp");
            jsonObject.put("password", password);
            jsonObject.put("account", account);
            networkService.CGIRequest(jsonObject, handler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
