package networkJavaLab2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TraderInterface extends Remote
{
    void receiveMsg(String msg) throws RemoteException;

    String getTraderName() throws RemoteException;
    
    boolean charge(Integer amount) throws RemoteException;
    
    void getIncome(Integer amount) throws RemoteException;
    
}