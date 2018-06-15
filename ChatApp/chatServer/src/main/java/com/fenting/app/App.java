package com.fenting.app;

import com.fenting.app.NetworkLayer.ServerManager;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ServerManager.getServerManager(8888);
    }
}
