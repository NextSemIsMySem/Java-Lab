/**
 * Represents a truck vehicle type
 */
public class Truck extends Vehicle {
    private double cargoCapacity;
    private String driveType;
    private boolean hasTowPackage;
    
    public Truck(String id, String make, String model, int year, String color, double price, 
                double cargoCapacity, String driveType, boolean hasTowPackage) {
        super(id, make, model, year, color, price);
        this.cargoCapacity = cargoCapacity;
        this.driveType = driveType;
        this.hasTowPackage = hasTowPackage;
    }
    
    // Getters and setters
    public double getCargoCapacity() { return cargoCapacity; }
    public void setCargoCapacity(double cargoCapacity) { this.cargoCapacity = cargoCapacity; }
    
    public String getDriveType() { return driveType; }
    public void setDriveType(String driveType) { this.driveType = driveType; }
    
    public boolean getHasTowPackage() { return hasTowPackage; }
    public void setHasTowPackage(boolean hasTowPackage) { this.hasTowPackage = hasTowPackage; }
    
    @Override
    public String toString() {
        return super.toString() + String.format(", %.1f cargo capacity, %s drive, Tow package: %s", 
                                               cargoCapacity, driveType, hasTowPackage ? "Yes" : "No");
    }
}