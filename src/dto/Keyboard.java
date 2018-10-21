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

    public Keyboard(int numberOfKey, String pressForce, String distance, String led, String weight, String size, String switches, String hashCode, String name, int price) {
        super(hashCode, name, price);
        this.numberOfKey = numberOfKey;
        this.pressForce = pressForce;
        this.distance = distance;
        this.led = led;
        this.weight = weight;
        this.size = size;
        this.switches = switches;
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
