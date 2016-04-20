package networkJavaLab2;

public class Product {
	private String productName;
	private Integer productPrice;
	private String productseller;
	private String productbuyer;
	
	public Product(String productName, Integer productPrice, String productseller)
	{
		this.productName = productName;
		this.productPrice = productPrice;
		this.productseller = productseller;
		
	}
	
	public String getproductName()
	{
		return productName;
	}
	
	public String getproductseller()
	{
		return productseller;
	}
	
	public Integer getproductPrice()
	{
		return productPrice;
	}
	
	public void setProductBuyer(String buyer)
	{
		this.productbuyer = buyer;
	}
}
