/*step 2 Develop a class (a.k.a. servant class) that implements the interface.*/
package networkJavaLab2;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("serial")
public class Market extends UnicastRemoteObject implements MarketInterface {
	private String marketName;
    private List<TraderInterface> TradersTable = new ArrayList<>(); //server store references to traders//
    private List<Product> productsToSell = new ArrayList<>();
    private List<WishProduct> wishList = new ArrayList<>();
    
    public Market(String marketName) throws RemoteException{
        super();
        this.marketName = marketName;
    }

    @Override
    public void getTraders(String tradersName) {
    	StringBuilder traders = new StringBuilder();
    	for (TraderInterface traderInterface : TradersTable) {
    		try {
				traders.append(traderInterface.getTraderName()).append("\n");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	try {
			sendMsg(tradersName, traders.toString());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void registerTrader(TraderInterface trader) throws RemoteException {
        if (TradersTable.contains(trader)) {
            throw new RemoteException("trader already registered");
        }
        TradersTable.add(trader);
        sendMsg(trader.getTraderName(), "register successfully");
    }

    @Override
    public void unregisterTrader(TraderInterface trader) throws RemoteException {
        if (!TradersTable.contains(trader)) {
            throw new RemoteException("client not registered");
        }
        TradersTable.remove(trader);
        sendMsg(trader.getTraderName(), "unregister successfully");
    }
    
    @Override
    public void broadcastMsg(String msg) throws RemoteException {
        for (TraderInterface trader : TradersTable) {
            try {
                trader.receiveMsg(msg);
            } catch (RemoteException re) {
                re.printStackTrace();
            }
        }
    }
    
    private void sendMsg(String TraderName, String msg) throws RemoteException {
        for (TraderInterface trader : TradersTable) {
            try {
                if(trader.getTraderName().equals(TraderName))
                {
                	trader.receiveMsg(msg);
                }
            } catch (RemoteException re) {
                re.printStackTrace();
            }
        }
    }
    
    private boolean allowToBuy(Integer amount, String buyersName)
    {
    	boolean permission = false;
    	for (TraderInterface trader : TradersTable) {
            try {
                if(trader.getTraderName().equals(buyersName))
                {
                	permission = trader.charge(amount);
                	break;
                }
            } catch (RemoteException re) {
                re.printStackTrace();
            }
        }
    	return permission;
    }
    
    private void payToSeller(Integer amount, String SellersName)
    {
    	for (TraderInterface trader : TradersTable) 
    	{
            try {
                if(trader.getTraderName().equals(SellersName))
                {
                	trader.getIncome(amount);
                	break;
                }
            } catch (RemoteException re) {
                re.printStackTrace();
            }
        }
    }

	@Override
	public void sell(String productName, Integer productPrice, String sellersName) throws RemoteException {
		Product newProductToSell = new Product(productName, productPrice, sellersName);
		if(productsToSell.size() == 0)
		{
			productsToSell.add(newProductToSell);
			sendMsg(sellersName,"product is ready to sell 1");
			for (WishProduct wishProduct : wishList) 
			{
				check_more(wishProduct);
			}
			return;
		}else
		{
			for(int i = 0; i < productsToSell.size(); i++)
			{
				Product product = productsToSell.get(i);
				if((product.getproductName().equals(productName)) && (product.getproductPrice().equals(productPrice)))
				{
					sendMsg(sellersName, "Product can not put into sell list: The same item exist.");
					break;
				}
				else
				{
					productsToSell.add(newProductToSell);
					sendMsg(sellersName,"product is ready to sell 2222");
					for (WishProduct wishProduct : wishList) 
					{
						check_more(wishProduct);
					}
					break;
				}
			}
		}
	}
	

	@Override
	public void buy(String productName, Integer productPrice, String buyersName) throws Exception
	{
		if(productsToSell.size() == 0)
		{
			sendMsg(buyersName, "no product is selling");
			return;
		}
		
		for(int i = 0; i < productsToSell.size(); i++) // try to find the item
		{
			Product product = productsToSell.get(i);
			if((product.getproductName().equals(productName)) && (product.getproductPrice().equals(productPrice))) // item found
				try 
				{
					if(allowToBuy(product.getproductPrice(),buyersName))
					{
						product.setProductBuyer(buyersName);
						productsToSell.remove(i);
						payToSeller(product.getproductPrice(), product.getproductseller());
						sendMsg(buyersName,productName);
						sendMsg(buyersName," is bought.");
						sendMsg(product.getproductseller(), productName);
						sendMsg(product.getproductseller(), "has been sold.");
					}else
					{
						sendMsg(buyersName, "Sorry, you have no enough money in account");
					}
					return;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		sendMsg(buyersName,"product not exist");// item not found
	}

	@Override
	public void checkSellingProducts(String TraderName) throws RemoteException {
		StringBuilder info_of_selling_product = new StringBuilder();
		info_of_selling_product.append("The products in selling list: ").append("\n").append("Product name: ");
		for (Product product : productsToSell) {
			info_of_selling_product.append(product.getproductName()).append(" Price: ")
			.append(product.getproductPrice()).append("\n");
		}
		sendMsg(TraderName, info_of_selling_product.toString());
	}

	@Override
	public void wish(String productName, Integer productPrice, String tradersName) throws RemoteException {
		if (!check_wishing_product(productName, productPrice, tradersName))
		{
			WishProduct newWish = new WishProduct(productName, productPrice, tradersName);
			wishList.add(newWish);
			sendMsg(tradersName, "The product you wished not exists, wait for coming items");
		}
	}
	
	public boolean check_wishing_product(String productName, Integer productPrice, String tradersName)throws RemoteException
	{
		boolean found = false;
		StringBuilder info_of_wishing_product = new StringBuilder();
		if(productsToSell.size() > 0)
		{
			for (Product product : productsToSell) 
			{
				if(product.getproductName().equals(productName)
						&& (product.getproductPrice() <= productPrice))
				{
					found = true;
					info_of_wishing_product.append("Product name: ").append(product.getproductName()).append("Price: ")
					.append(product.getproductPrice())
					.append("is recommended as you wish").append("\n");
					sendMsg(tradersName, info_of_wishing_product.toString());
				}
			}
		}
		return found;
	}
	
	public void check_more(WishProduct product) throws RemoteException
	{
		String wish_productName = product.getproductName();
		Integer wish_productPrice = product.getproductPrice();
		String wish_productWisher = product.getproductWisher();
		check_wishing_product(wish_productName, wish_productPrice, wish_productWisher);
	}
}
