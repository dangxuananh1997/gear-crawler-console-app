package dto;

/**
 *
 * @author dangxuananh1997
 */
public class Laptop extends Product {
    
    private String cpu;
    private String gpu;
    private String ram;
    private String hardDrive;
    private String monitor;
    private String ports;
    private String lan;
    private String wireless;

    public Laptop(String cpu, String gpu, String ram, String hardDrive, String monitor, String ports, String lan, String wireless, Product product) {
        super(product.getName(), product.getImage(), product.getPrice(), product.getProductLink());
        this.cpu = cpu;
        this.gpu = gpu;
        this.ram = ram;
        this.hardDrive = hardDrive;
        this.monitor = monitor;
        this.ports = ports;
        this.lan = lan;
        this.wireless = wireless;
    }

    public String getCpu() {
        return cpu;
    }

    public String getGpu() {
        return gpu;
    }

    public String getRam() {
        return ram;
    }

    public String getHardDrive() {
        return hardDrive;
    }

    public String getMonitor() {
        return monitor;
    }

    public String getPorts() {
        return ports;
    }

    public String getLan() {
        return lan;
    }

    public String getWireless() {
        return wireless;
    }

    @Override
    public String toString() {
        return getHashCode() + "\n" + getName() + "\n" + getPrice() + "\n" + getImage() + "\n" + cpu + "\n" + gpu + "\n" + ram + "\n" + hardDrive + "\n" + monitor + "\n" + ports + "\n" + lan + "\n" + wireless + "\n";
    }
    
}
