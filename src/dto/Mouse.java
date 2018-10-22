package dto;

/**
 *
 * @author dangxuananh1997
 */
public class Mouse extends Product {
    
    private String weight;
    private String maxDPI;
    private String led;
    private String numberOfButton;

    public Mouse(String weight, String maxDPI, String led, String numberOfButton, Product product) {
        super(product.getName(), product.getPrice(), product.getProductLink());
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

    public String getNumberOfButton() {
        return numberOfButton;
    }

    @Override
    public String toString() {
        return "{" + "weight=" + weight + ", maxDPI=" + maxDPI + ", led=" + led + ", numberOfButton=" + numberOfButton + '}';
    }
    
}
