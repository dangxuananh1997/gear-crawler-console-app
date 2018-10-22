package dto;

/**
 *
 * @author dangxuananh1997
 */
public class Keyboard extends Product {
    
    private String numberOfKey;
    private String pressForce;
    private String distance;
    private String led;
    private String weight;
    private String size;
    private String switches;

    public Keyboard(String numberOfKey, String pressForce, String distance, String led, String weight, String size, String switches, Product product) {
        super(product.getName(), product.getPrice(), product.getProductLink());
        this.numberOfKey = numberOfKey;
        this.pressForce = pressForce;
        this.distance = distance;
        this.led = led;
        this.weight = weight;
        this.size = size;
        this.switches = switches;
    }
    

    public String getNumberOfKey() {
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

    @Override
    public String toString() {
        return "{" + "numberOfKey=" + numberOfKey + ", pressForce=" + pressForce + ", distance=" + distance + ", led=" + led + ", weight=" + weight + ", size=" + size + ", switches=" + switches + '}';
    }
    
}
