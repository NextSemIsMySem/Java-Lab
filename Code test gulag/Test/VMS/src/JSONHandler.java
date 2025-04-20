import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * JSONHandler class - Handles loading and saving vehicle data to a JSON file
 * Implements abstraction by hiding the details of file I/O and JSON handling
 */
public class JSONHandler {
    private static final String DATA_FILE = "src/vehicles.json";
    
    /**
     * Saves a list of vehicles to a JSON file
     * @param vehicles List of vehicles to save
     * @return true if successful, false otherwise
     */
    @SuppressWarnings("unchecked")
    public boolean saveVehicles(List<Vehicle> vehicles) {
        JSONArray vehicleArray = new JSONArray();
        
        try {
            // Convert each vehicle to a JSON object
            for (Vehicle vehicle : vehicles) {
                JSONObject vehicleObj = new JSONObject();
                
                // Common vehicle properties
                vehicleObj.put("id", vehicle.getId());
                vehicleObj.put("make", vehicle.getMake());
                vehicleObj.put("model", vehicle.getModel());
                vehicleObj.put("year", vehicle.getYear());
                vehicleObj.put("color", vehicle.getColor());
                vehicleObj.put("price", vehicle.getPrice());
                
                // Determine vehicle type and add specific properties
                if (vehicle instanceof Car) {
                    Car car = (Car) vehicle;
                    vehicleObj.put("type", "Car");
                    vehicleObj.put("numDoors", car.getNumDoors());
                    vehicleObj.put("transmissionType", car.getTransmissionType());
                    vehicleObj.put("engineSize", car.getEngineSize());
                } else if (vehicle instanceof Truck) {
                    Truck truck = (Truck) vehicle;
                    vehicleObj.put("type", "Truck");
                    vehicleObj.put("cargoCapacity", truck.getCargoCapacity());
                    vehicleObj.put("driveType", truck.getDriveType());
                    vehicleObj.put("hasTowPackage", truck.getHasTowPackage());
                } else if (vehicle instanceof Motorcycle) {
                    Motorcycle motorcycle = (Motorcycle) vehicle;
                    vehicleObj.put("type", "Motorcycle");
                    vehicleObj.put("bikeType", motorcycle.getBikeType());
                    vehicleObj.put("engineCC", motorcycle.getEngineCC());
                    vehicleObj.put("hasFairing", motorcycle.getHasFairing());
                }
                
                vehicleArray.add(vehicleObj);
            }
            
            // Write JSON array to file
            try (FileWriter file = new FileWriter(DATA_FILE)) {
                file.write(vehicleArray.toJSONString());
                file.flush();
                return true;
            }
            
        } catch (IOException e) {
            System.err.println("Error saving vehicles: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads vehicles from a JSON file
     * @return List of vehicles loaded from file
     */
    public List<Vehicle> loadVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        File dataFile = new File(DATA_FILE);
        
        // Create empty file if it doesn't exist
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                try (FileWriter file = new FileWriter(DATA_FILE)) {
                    file.write("[]");
                    file.flush();
                }
            } catch (IOException e) {
                System.err.println("Error creating data file: " + e.getMessage());
                return vehicles; // Return empty list
            }
        }
        
        JSONParser parser = new JSONParser();
        
        try (FileReader reader = new FileReader(DATA_FILE)) {
            Object obj = parser.parse(reader);
            
            // Check if the file contains valid JSON array
            if (!(obj instanceof JSONArray)) {
                System.err.println("Error: Data file does not contain a valid JSON array");
                return vehicles;
            }
            
            JSONArray vehicleArray = (JSONArray) obj;
            
            // Process each vehicle in the JSON array
            for (Object vehicleObj : vehicleArray) {
                try {
                    JSONObject jsonVehicle = (JSONObject) vehicleObj;
                    String type = (String) jsonVehicle.get("type");
                    
                    // Extract common properties with null checks
                    String id = jsonVehicle.get("id") != null ? (String) jsonVehicle.get("id") : generateUniqueId();
                    String make = jsonVehicle.get("make") != null ? (String) jsonVehicle.get("make") : "";
                    String model = jsonVehicle.get("model") != null ? (String) jsonVehicle.get("model") : "";
                    
                    // Safe conversion from JSON numbers to Java primitives
                    int year = 0;
                    if (jsonVehicle.get("year") != null) {
                        if (jsonVehicle.get("year") instanceof Long) {
                            year = ((Long) jsonVehicle.get("year")).intValue();
                        } else if (jsonVehicle.get("year") instanceof Integer) {
                            year = (Integer) jsonVehicle.get("year");
                        } else {
                            try {
                                year = Integer.parseInt(jsonVehicle.get("year").toString());
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing year: " + e.getMessage());
                            }
                        }
                    }
                    
                    String color = jsonVehicle.get("color") != null ? (String) jsonVehicle.get("color") : "";
                    
                    double price = 0.0;
                    if (jsonVehicle.get("price") != null) {
                        if (jsonVehicle.get("price") instanceof Number) {
                            price = ((Number) jsonVehicle.get("price")).doubleValue();
                        } else {
                            try {
                                price = Double.parseDouble(jsonVehicle.get("price").toString());
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing price: " + e.getMessage());
                            }
                        }
                    }
                    
                    // Create appropriate vehicle type based on "type" field
                    if ("Car".equals(type)) {
                        int numDoors = 0;
                        if (jsonVehicle.get("numDoors") != null) {
                            if (jsonVehicle.get("numDoors") instanceof Number) {
                                numDoors = ((Number) jsonVehicle.get("numDoors")).intValue();
                            } else {
                                try {
                                    numDoors = Integer.parseInt(jsonVehicle.get("numDoors").toString());
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing numDoors: " + e.getMessage());
                                }
                            }
                        }
                        
                        String transmissionType = jsonVehicle.get("transmissionType") != null ? 
                                                (String) jsonVehicle.get("transmissionType") : "";
                        
                        double engineSize = 0.0;
                        if (jsonVehicle.get("engineSize") != null) {
                            if (jsonVehicle.get("engineSize") instanceof Number) {
                                engineSize = ((Number) jsonVehicle.get("engineSize")).doubleValue();
                            } else {
                                try {
                                    engineSize = Double.parseDouble(jsonVehicle.get("engineSize").toString());
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing engineSize: " + e.getMessage());
                                }
                            }
                        }
                        
                        vehicles.add(new Car(id, make, model, year, color, price, 
                                            numDoors, transmissionType, engineSize));
                        
                    } else if ("Truck".equals(type)) {
                        double cargoCapacity = 0.0;
                        if (jsonVehicle.get("cargoCapacity") != null) {
                            if (jsonVehicle.get("cargoCapacity") instanceof Number) {
                                cargoCapacity = ((Number) jsonVehicle.get("cargoCapacity")).doubleValue();
                            } else {
                                try {
                                    cargoCapacity = Double.parseDouble(jsonVehicle.get("cargoCapacity").toString());
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing cargoCapacity: " + e.getMessage());
                                }
                            }
                        }
                        
                        String driveType = jsonVehicle.get("driveType") != null ? 
                                         (String) jsonVehicle.get("driveType") : "";
                        
                        boolean hasTowPackage = false;
                        if (jsonVehicle.get("hasTowPackage") != null) {
                            if (jsonVehicle.get("hasTowPackage") instanceof Boolean) {
                                hasTowPackage = (Boolean) jsonVehicle.get("hasTowPackage");
                            } else {
                                hasTowPackage = Boolean.parseBoolean(jsonVehicle.get("hasTowPackage").toString());
                            }
                        }
                        
                        vehicles.add(new Truck(id, make, model, year, color, price,
                                              cargoCapacity, driveType, hasTowPackage));
                        
                    } else if ("Motorcycle".equals(type)) {
                        String bikeType = jsonVehicle.get("bikeType") != null ? 
                                        (String) jsonVehicle.get("bikeType") : "";
                        
                        int engineCC = 0;
                        if (jsonVehicle.get("engineCC") != null) {
                            if (jsonVehicle.get("engineCC") instanceof Number) {
                                engineCC = ((Number) jsonVehicle.get("engineCC")).intValue();
                            } else {
                                try {
                                    engineCC = Integer.parseInt(jsonVehicle.get("engineCC").toString());
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing engineCC: " + e.getMessage());
                                }
                            }
                        }
                        
                        boolean hasFairing = false;
                        if (jsonVehicle.get("hasFairing") != null) {
                            if (jsonVehicle.get("hasFairing") instanceof Boolean) {
                                hasFairing = (Boolean) jsonVehicle.get("hasFairing");
                            } else {
                                hasFairing = Boolean.parseBoolean(jsonVehicle.get("hasFairing").toString());
                            }
                        }
                        
                        vehicles.add(new Motorcycle(id, make, model, year, color, price,
                                                   bikeType, engineCC, hasFairing));
                    }
                } catch (Exception e) {
                    System.err.println("Error processing vehicle: " + e.getMessage());
                    // Continue to next vehicle instead of aborting the entire loading process
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading data file: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Error parsing JSON data: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while loading vehicles: " + e.getMessage());
        }
        
        return vehicles;
    }
    
    /**
     * Generates a unique ID for a new vehicle
     * @return A unique ID string
     */
    public String generateUniqueId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}