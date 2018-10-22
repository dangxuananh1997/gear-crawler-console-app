package dto;

/**
 *
 * @author dangxuananh1997
 */
public class Product {
    
    private int id;
    private int hashCode;
    private String name;
    private String image;
    private int price;
    private String productLink;

    public Product(String name, String image, int price, String productLink) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.productLink = productLink;
        this.hashCode = (name + image + price + productLink).hashCode();
    }

    public int getId() {
        return id;
    }

    public int getHashCode() {
        return hashCode;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getPrice() {
        return price;
    }

    public String getProductLink() {
        return productLink;
    }

    public void setId(int id) {
        this.id = id;
    }

}
