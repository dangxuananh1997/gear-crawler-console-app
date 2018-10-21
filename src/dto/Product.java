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

    public Product(String hashCode, String name, int price) {
        this.hashCode = hashCode;
        this.name = name;
        this.price = price;
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
    
}
