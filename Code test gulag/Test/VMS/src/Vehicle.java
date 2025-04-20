/**
 * Base class for all vehicles in the system
 */
public abstract class Vehicle {
    private String id;
    private String make;
    private String model;
    private int year;
    private String color;
    private double price;
    
    public Vehicle(String id, String make, String model, int year, String color, double price) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.price = price;
    }
    
    // Getters and setters
    public String getId() { return id; }
    
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    @Override
    public String toString() {
        return String.format("%s: %s %s (%d) - $%.2f", id, make, model, year, price);
    }
}