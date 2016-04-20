package networkJavaLab2;

public class WishProduct {
	private String productName;
	private Integer productPrice;
	private String productWisher;
	
	public WishProduct(String productName, Integer productPrice, String productWisher)
	{
		this.productName = productName;
		this.productPrice = productPrice;
		this.productWisher = productWisher;
	}
	
	public String getproductName()
	{
		return productName;
	}
	
	public String getproductWisher()
	{
		return productWisher;
	}
	
	public Integer getproductPrice()
	{
		return productPrice;
	}
}
