/*step 1 Define a remote interface(s) which extends java.rmi.Remote.*/
package networkJavaLab2;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MarketInterface extends Remote {
    void registerTrader(TraderInterface obj) throws RemoteException;

    void unregisterTrader(TraderInterface obj) throws RemoteException;
    
    void broadcastMsg(String msg) throws RemoteException;
    
    void sell(String productName, Integer productPrice, String sellersName) throws RemoteException;
    
    void buy(String productName, Integer productPrice, String buyersName) throws Exception;
    
    void checkSellingProducts(String TraderName) throws RemoteException;
    
    void wish(String productName, Integer productPrice, String tradersName)throws RemoteException;
 
    void getTraders(String tradersName) throws RemoteException;
}

