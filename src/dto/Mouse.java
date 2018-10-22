package dto;

/**
 *
 * @author dangxuananh1997
 */
public class Mouse extends Product {
    
    private String weight;
    private String maxDPI;
    private String led;
    private int numberOfButton;

    public Mouse(String name, int price, String productLink) {
        super(name, price, productLink);
    }

    public String getWeight() {
        return weight;
    }

    public String getMaxDPI() {
        return maxDPI;
    }

    public String getLed() {
        return led;
    }

    public int getNumberOfButton() {
        return numberOfButton;
    }
    
}
