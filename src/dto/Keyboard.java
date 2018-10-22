package dto;

/**
 *
 * @author dangxuananh1997
 */
public class Keyboard extends Product {
    
    private int numberOfKey;
    private String pressForce;
    private String distance;
    private String led;
    private String weight;
    private String size;
    private String switches;

    public Keyboard(String name, int price, String productLink) {
        super(name, price, productLink);
    }

    public int getNumberOfKey() {
        return numberOfKey;
    }

    public String getPressForce() {
        return pressForce;
    }

    public String getDistance() {
        return distance;
    }

    public String getLed() {
        return led;
    }

    public String getWeight() {
        return weight;
    }

    public String getSize() {
        return size;
    }

    public String getSwitches() {
        return switches;
    }
    
}
