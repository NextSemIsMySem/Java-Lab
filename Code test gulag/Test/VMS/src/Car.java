/**
 * Represents a car vehicle type
 */
public class Car extends Vehicle {
    private int numDoors;
    private String transmissionType;
    private double engineSize;
    
    public Car(String id, String make, String model, int year, String color, double price, 
               int numDoors, String transmissionType, double engineSize) {
        super(id, make, model, year, color, price);
        this.numDoors = numDoors;
        this.transmissionType = transmissionType;
        this.engineSize = engineSize;
    }
    
    // Getters and setters
    public int getNumDoors() { return numDoors; }
    public void setNumDoors(int numDoors) { this.numDoors = numDoors; }
    
    public String getTransmissionType() { return transmissionType; }
    public void setTransmissionType(String transmissionType) { this.transmissionType = transmissionType; }
    
    public double getEngineSize() { return engineSize; }
    public void setEngineSize(double engineSize) { this.engineSize = engineSize; }
    
    @Override
    public String toString() {
        return super.toString() + String.format(", %d doors, %s transmission, %.1fL engine", 
                                                numDoors, transmissionType, engineSize);
    }
}