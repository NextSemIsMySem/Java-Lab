import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * Manages vehicle operations including CRUD functionality
 */
public class VehicleManager {
    private List<Vehicle> vehicles;
    private JSONHandler jsonHandler;
    
    public VehicleManager() {
        this.jsonHandler = new JSONHandler();
        this.vehicles = jsonHandler.loadVehicles();
    }
    
    /**
     * Adds a vehicle to the system
     * @param vehicle The vehicle to add
     * @return true if successful
     */
    public boolean addVehicle(Vehicle vehicle) {
        boolean result = vehicles.add(vehicle);
        if (result) {
            jsonHandler.saveVehicles(vehicles);
        }
        return result;
    }
    
    /**
     * Creates a vehicle object based on user input
     * @param scanner Scanner for reading input
     * @param type The type of vehicle to create
     * @return The created vehicle
     */
    public Vehicle createVehicleFromInput(Scanner scanner, String type) {
        System.out.print("Make: ");
        String make = scanner.nextLine().trim();
        
        System.out.print("Model: ");
        String model = scanner.nextLine().trim();
        
        System.out.print("Year: ");
        int year = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Color: ");
        String color = scanner.nextLine().trim();
        
        System.out.print("Price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        
        // Generate a unique ID
        String id = UUID.randomUUID().toString().substring(0, 8);
        
        if (type.equalsIgnoreCase("Car")) {
            System.out.print("Number of doors: ");
            int numDoors = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Transmission type: ");
            String transmissionType = scanner.nextLine().trim();
            
            System.out.print("Engine size (L): ");
            double engineSize = Double.parseDouble(scanner.nextLine().trim());
            
            return new Car(id, make, model, year, color, price, numDoors, transmissionType, engineSize);
            
        } else if (type.equalsIgnoreCase("Truck")) {
            System.out.print("Cargo capacity: ");
            double cargoCapacity = Double.parseDouble(scanner.nextLine().trim());
            
            System.out.print("Drive type: ");
            String driveType = scanner.nextLine().trim();
            
            System.out.print("Has tow package (yes/no): ");
            boolean hasTowPackage = scanner.nextLine().trim().equalsIgnoreCase("yes");
            
            return new Truck(id, make, model, year, color, price, cargoCapacity, driveType, hasTowPackage);
            
        } else if (type.equalsIgnoreCase("Motorcycle")) {
            System.out.print("Bike type: ");
            String bikeType = scanner.nextLine().trim();
            
            System.out.print("Engine CC: ");
            int engineCC = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Has fairing (yes/no): ");
            boolean hasFairing = scanner.nextLine().trim().equalsIgnoreCase("yes");
            
            return new Motorcycle(id, make, model, year, color, price, bikeType, engineCC, hasFairing);
        }
        
        return null;
    }
    
    /**
     * Returns all vehicles in the system
     * @return List of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }
    
    /**
     * Finds a vehicle by ID
     * @param id The ID to search for
     * @return The found vehicle or null
     */
    public Vehicle getVehicleById(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(id)) {
                return vehicle;
            }
        }
        return null;
    }
    
    /**
     * Updates a vehicle
     * @param vehicle The updated vehicle
     * @return true if successful
     */
    public boolean updateVehicle(Vehicle vehicle) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(vehicle.getId())) {
                vehicles.set(i, vehicle);
                jsonHandler.saveVehicles(vehicles);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Deletes a vehicle by ID
     * @param id The ID of the vehicle to delete
     * @return true if successful
     */
    public boolean deleteVehicle(String id) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(id)) {
                vehicles.remove(i);
                jsonHandler.saveVehicles(vehicles);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Saves all vehicles to persistent storage
     * @return true if successful
     */
    public boolean saveAllVehicles() {
        return jsonHandler.saveVehicles(vehicles);
    }
}