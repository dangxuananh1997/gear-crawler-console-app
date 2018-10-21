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

    public Mouse(String weight, String maxDPI, String led, int numberOfButton, String hashCode, String name, int price) {
        super(hashCode, name, price);
        this.weight = weight;
        this.maxDPI = maxDPI;
        this.led = led;
        this.numberOfButton = numberOfButton;
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
