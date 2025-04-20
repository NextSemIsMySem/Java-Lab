/**
 * Represents a motorcycle vehicle type
 */
public class Motorcycle extends Vehicle {
    private String bikeType;
    private int engineCC;
    private boolean hasFairing;
    
    public Motorcycle(String id, String make, String model, int year, String color, double price, 
                     String bikeType, int engineCC, boolean hasFairing) {
        super(id, make, model, year, color, price);
        this.bikeType = bikeType;
        this.engineCC = engineCC;
        this.hasFairing = hasFairing;
    }
    
    // Getters and setters
    public String getBikeType() { return bikeType; }
    public void setBikeType(String bikeType) { this.bikeType = bikeType; }
    
    public int getEngineCC() { return engineCC; }
    public void setEngineCC(int engineCC) { this.engineCC = engineCC; }
    
    public boolean getHasFairing() { return hasFairing; }
    public void setHasFairing(boolean hasFairing) { this.hasFairing = hasFairing; }
    
    @Override
    public String toString() {
        return super.toString() + String.format(", %s, %dcc engine, Fairing: %s", 
                                               bikeType, engineCC, hasFairing ? "Yes" : "No");
    }
}