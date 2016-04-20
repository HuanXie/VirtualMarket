/*Develop a client class that gets a reference to a remote object(s) and calls its remote methods.*/
package networkJavaLab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;

@SuppressWarnings("serial")
public class Trader extends UnicastRemoteObject implements TraderInterface
{
	private Client tradersBankClient;  
	private static final String USAGE = "java traderrmi.Client";
    private String tradername;
    MarketInterface marketInt;
    private static final String marketname = "Free_market";
    private static final String DEFAULT_Trader_NAME = "NewTrader";
    
    static enum CommandName {
        register, unregister, sell, buy, wish, getTraders, checkSellingProducts;
    };

    public Trader(String tradername) throws RemoteException // constructor
    {
        super();
        this.tradername = tradername;
        try
        {
        	LocateRegistry.getRegistry(1099).list();
        } catch (RemoteException e)
        {
        	LocateRegistry.createRegistry(1099);
        }
        try {
        	marketInt = (MarketInterface)Naming.lookup(marketname);
        	tradersBankClient = new Client();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public Trader() throws RemoteException {
        this(DEFAULT_Trader_NAME);
    }
    

    public String getTraderName()
    {
        return tradername;
    }
    
    

    public void run() 
    {
        BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
        while (true) 
        {
        	System.out.println(tradername);
        	System.out.println("please enter the command");
            try 
            {
                String userInput = consoleIn.readLine();
                Command parsedCommand = parse(userInput);
                if (parsedCommand == null) {
                	//maybe this is a client command
                	tradersBankClient.execute(userInput);
                } else {
                	execute(parsedCommand);
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private Command parse(String userInput) {
        if (userInput == null) { // no input in console
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(userInput);
        if (tokenizer.countTokens() == 0) { // no input in console
            return null;
        }

        CommandName commandName = null;
        String productName = null;
        Integer productPrice = 0;
        int userInputTokenNo = 1;

        while (tokenizer.hasMoreTokens()) {
            switch (userInputTokenNo) {
                case 1:
                    try {
                        String commandNameString = tokenizer.nextToken(); // get command
                        commandName = CommandName.valueOf(CommandName.class, commandNameString); //get the name of command constant
                    } catch (IllegalArgumentException commandDoesNotExist) {
                        return null;
                    }
                    break;
                case 2:
                    productName = tokenizer.nextToken();
                    break;
                case 3:
                	productPrice = Integer.parseInt(tokenizer.nextToken());
                	break;
                default:
                    System.out.println("Illegal command");
                    return null;
            }
            userInputTokenNo++;
        }
        return new Command(commandName, productName, productPrice);
    }
    
    boolean execute(Command command) throws RemoteException{
    	String tradername = this.getTraderName();
    	String name = null;
    	Integer price = 0;
    	
        if (command == null) {
            return false;
        }
        /*RMI callbacks to clients through the client remote interface*/
        switch (command.getCommandName()) {
            case register:
            	marketInt.registerTrader(this); 
				return true;
            case unregister:
            	marketInt.unregisterTrader(this);
                return true;
            case checkSellingProducts:
            	marketInt.checkSellingProducts(tradername);
                return true;
            case getTraders:
            	marketInt.getTraders(tradername);
            	return true;
            case sell:
            	name = command.getproductName();
            	price = command.getproductPrice();
            	marketInt.sell(name, price, tradername);
            	//System.out.println("product " + name + " is ready to sell");
            	return true;
            case buy:
            	name = command.getproductName();
            	price = command.getproductPrice();
			try {
				marketInt.buy(name, price, tradername);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            	return true;
            case wish:
            	name = command.getproductName();
            	price = command.getproductPrice();
            	marketInt.wish(name, price, tradername);
            	return true;
		default:
			return false;
        }
    }

    
    private class Command {
        private CommandName commandName;
        private String productName;
        private Integer productPrice;

        private CommandName getCommandName() {
            return commandName;
        }
        private String getproductName() {
            return productName;
        }
        
        private Integer getproductPrice() {
            return productPrice;
        }

        private Command(Trader.CommandName commandName) {
            this.commandName = commandName;
        }
        
        private Command(Trader.CommandName commandName, String productName, Integer productPrice) {
            this.commandName = commandName;
            this.productName = productName;
            this.productPrice = productPrice;
        }
    }

    public static void main(String args[])throws RemoteException,NotBoundException,MalformedURLException
    {
        String tradername;
        if (args.length > 0) { //give trader name
        	tradername = args[0];
            new Trader(tradername).run();
        } else { //default trader name
        	new Trader().run();
        }
    }
	
	@Override
    public void receiveMsg(String msg)
    {
        System.out.println(msg);
    }

	@Override
	public boolean charge(Integer amount) throws RemoteException {
		Client.CommandName commandName = Client.CommandName.valueOf(Client.CommandName.class, "withdraw");
		Client.Command chargCommand = new Client.Command(commandName,tradersBankClient.clientname,amount);
		try {
			tradersBankClient.execute(chargCommand);//charged
			return true;
		} catch (RejectedException e) {  //get reject Info
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void getIncome(Integer amount) throws RemoteException {
		Client.CommandName commandName = Client.CommandName.valueOf(Client.CommandName.class, "deposit");
		Client.Command chargCommand = new Client.Command(commandName,tradersBankClient.clientname,amount);
		try {
			tradersBankClient.execute(chargCommand);//charged
		} catch (RejectedException e) {  //get reject Info
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}

