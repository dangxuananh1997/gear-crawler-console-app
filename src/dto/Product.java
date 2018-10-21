package dto;

/**
 *
 * @author dangxuananh1997
 */
public class Product {
    
    private int id;
    private String hashCode;
    private String name;
    private int price;
    private String productLink;

    public Product(String hashCode, String name, int price, String productLink) {
        this.hashCode = hashCode;
        this.name = name;
        this.price = price;
        this.productLink = productLink;
    }

    public int getId() {
        return id;
    }

    public String getHashCode() {
        return hashCode;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getProductLink() {
        return productLink;
    }
    
}
