/*step 3 Develop a server class that provide a container for servants,
 * creates the servants and registers them at the Naming
Service.*/
package networkJavaLab2;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;

@SuppressWarnings("serial")
public class MarketServer extends UnicastRemoteObject{
//    private static final String USAGE = "java Marketrmi.Server";
    private static final String Market = "Free_market";
    static Market marketobj;

    public MarketServer(String marketName) throws RemoteException{
        try {
            marketobj = new Market(marketName);
            // Register the newly created object at rmiregistry.
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }
            Naming.rebind(marketName, marketobj);
            System.out.println(marketName + marketobj + " is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException {
        new MarketServer(Market); //create a new Market with default name
    }
}
