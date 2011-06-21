package functional.httpservice.shopping

public interface ShoppingContract {

    void add(String productName, int price)
    
    String productDetails(String productName)
    
    String cart()
    
}
