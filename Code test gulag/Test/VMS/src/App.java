import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Main application class for the Vehicle Management System
 * Implements a CLI interface for user interaction with tabulated display and arrow key navigation
 */
public class App {
    private static VehicleManager vehicleManager;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        // Initialize the vehicle manager and scanner
        vehicleManager = new VehicleManager();
        scanner = new Scanner(System.in);
        
        boolean running = true;
        
        ConsoleHelper.clearConsole();
        System.out.println("Welcome to the Vehicle Management System");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
        
        // Main application loop
        while (running) {
            String command = displayMenu();
            command = command.trim().toLowerCase();
            
            try {
                switch (command) {
                    case "add vehicle":
                        addVehicle();
                        break;
                    case "view vehicles":
                        viewVehicles();
                        break;
                    case "update vehicle":
                        updateVehicle();
                        break;
                    case "delete vehicle":
                        deleteVehicle();
                        break;
                    case "exit":
                        running = false;
                        // Save all vehicles before exiting
                        vehicleManager.saveAllVehicles();
                        System.out.println("Thank you for using the Vehicle Management System. Goodbye!");
                        break;
                    default:
                        if (command.startsWith("update vehicle ")) {
                            String id = command.substring("update vehicle ".length());
                            updateVehicleById(id);
                        } else if (command.startsWith("delete vehicle ")) {
                            String id = command.substring("delete vehicle ".length());
                            deleteVehicleById(id);
                        } else {
                            System.out.println("Invalid command. Please try again.");
                        }
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
            
            System.out.println(); // Add a blank line for readability
        }
        
        // Close the scanner
        scanner.close();
    }
    
    /**
     * Displays the main menu options with arrow key navigation
     * @return The selected command
     */
    private static String displayMenu() {
        List<String> options = new ArrayList<>();
        options.add("Add vehicle");
        options.add("View vehicles");
        options.add("Update vehicle");
        options.add("Delete vehicle");
        options.add("Exit");
        
        int selected = ConsoleHelper.showMenu("VEHICLE MANAGEMENT SYSTEM - MENU", options);
        
        if (selected == -1) {
            return "";
        }
        
        switch (selected) {
            case 0: return "add vehicle";
            case 1: return "view vehicles";
            case 2: return "update vehicle";
            case 3: return "delete vehicle";
            case 4: return "exit";
            default: return "";
        }
    }
    
    /**
     * Adds a new vehicle based on user input
     * Displays the new vehicle in tabulated format
     */
    private static void addVehicle() {
        ConsoleHelper.clearConsole();
        System.out.println("\nADD NEW VEHICLE");
        System.out.println("---------------");
        
        // First, let user select vehicle type using arrow keys
        List<String> vehicleTypes = new ArrayList<>();
        vehicleTypes.add("Car");
        vehicleTypes.add("Truck");
        vehicleTypes.add("Motorcycle");
        vehicleTypes.add("Cancel");
        
        int typeSelection = ConsoleHelper.showMenu("Select vehicle type:", vehicleTypes);
        
        if (typeSelection == 3 || typeSelection == -1) { // Cancel or escape
            System.out.println("Vehicle addition cancelled.");
            return;
        }
        
        // Create vehicle based on selection
        Vehicle newVehicle = vehicleManager.createVehicleFromInput(scanner, vehicleTypes.get(typeSelection));
        
        if (newVehicle != null) {
            if (vehicleManager.addVehicle(newVehicle)) {
                System.out.println("Vehicle added successfully!");
                
                // Display the new vehicle in tabulated format
                List<String[]> tableData = new ArrayList<>();
                tableData.add(ConsoleHelper.vehicleToStringArray(newVehicle));
                
                String vehicleType = "";
                if (newVehicle instanceof Car) vehicleType = "car";
                else if (newVehicle instanceof Truck) vehicleType = "truck";
                else if (newVehicle instanceof Motorcycle) vehicleType = "motorcycle";
                
                String[] headers = ConsoleHelper.getVehicleTableHeaders(vehicleType);
                ConsoleHelper.displayTable(headers, tableData);
            } else {
                System.out.println("Failed to add vehicle.");
            }
        } else {
            System.out.println("Failed to create vehicle.");
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Displays all vehicles in the system in a tabulated format
     * Now separated by vehicle type
     */
    private static void viewVehicles() {
        System.out.println("\nALL VEHICLES");
        System.out.println("------------");
        
        List<Vehicle> vehicles = vehicleManager.getAllVehicles();
        
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
        } else {
            // Separate vehicles by type
            List<Vehicle> cars = new ArrayList<>();
            List<Vehicle> trucks = new ArrayList<>();
            List<Vehicle> motorcycles = new ArrayList<>();
            
            for (Vehicle vehicle : vehicles) {
                if (vehicle instanceof Car) {
                    cars.add(vehicle);
                } else if (vehicle instanceof Truck) {
                    trucks.add(vehicle);
                } else if (vehicle instanceof Motorcycle) {
                    motorcycles.add(vehicle);
                }
            }
            
            // Display cars
            if (!cars.isEmpty()) {
                System.out.println("\nCARS");
                System.out.println("----");
                List<String[]> carData = new ArrayList<>();
                for (Vehicle car : cars) {
                    carData.add(ConsoleHelper.vehicleToStringArray(car));
                }
                String[] carHeaders = ConsoleHelper.getVehicleTableHeaders("car");
                ConsoleHelper.displayTable(carHeaders, carData);
                System.out.println("Total cars: " + cars.size());
            }
            
            // Display trucks
            if (!trucks.isEmpty()) {
                System.out.println("\nTRUCKS");
                System.out.println("------");
                List<String[]> truckData = new ArrayList<>();
                for (Vehicle truck : trucks) {
                    truckData.add(ConsoleHelper.vehicleToStringArray(truck));
                }
                String[] truckHeaders = ConsoleHelper.getVehicleTableHeaders("truck");
                ConsoleHelper.displayTable(truckHeaders, truckData);
                System.out.println("Total trucks: " + trucks.size());
            }
            
            // Display motorcycles
            if (!motorcycles.isEmpty()) {
                System.out.println("\nMOTORCYCLES");
                System.out.println("-----------");
                List<String[]> motorcycleData = new ArrayList<>();
                for (Vehicle motorcycle : motorcycles) {
                    motorcycleData.add(ConsoleHelper.vehicleToStringArray(motorcycle));
                }
                String[] motorcycleHeaders = ConsoleHelper.getVehicleTableHeaders("motorcycle");
                ConsoleHelper.displayTable(motorcycleHeaders, motorcycleData);
                System.out.println("Total motorcycles: " + motorcycles.size());
            }
            
            System.out.println("\nTotal vehicles: " + vehicles.size());
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Prompts for a vehicle ID and updates that vehicle
     * Uses arrow key navigation to select from available vehicles
     */
    private static void updateVehicle() {
        List<Vehicle> vehicles = vehicleManager.getAllVehicles();
        
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found to update.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display vehicles in tabulated format
        System.out.println("\nSELECT VEHICLE TO UPDATE");
        System.out.println("------------------------");
        
        // Convert vehicles to table data
        List<String[]> tableData = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            tableData.add(ConsoleHelper.vehicleToStringArray(vehicle));
        }
        
        // Display the table
        String[] headers = ConsoleHelper.getVehicleTableHeaders(null);
        ConsoleHelper.displayTable(headers, tableData);
        
        // Create a list of options for selection
        List<String> options = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            options.add(String.format("%s - %s %s (%d)", 
                vehicle.getId(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear()));
        }
        options.add("Cancel");
        
        int selected = ConsoleHelper.showMenu("Select a vehicle to update:", options);
        
        if (selected == options.size() - 1 || selected == -1) {
            System.out.println("Update cancelled.");
            return;
        }
        
        String id = vehicles.get(selected).getId();
        updateVehicleById(id);
    }
    
    /**
     * Updates a vehicle by its ID
     * @param id The ID of the vehicle to update
     */
    private static void updateVehicleById(String id) {
        Vehicle vehicle = vehicleManager.getVehicleById(id);
        
        if (vehicle == null) {
            System.out.println("Vehicle not found with ID: " + id);
            return;
        }
        
        System.out.println("\nUPDATE VEHICLE: " + id);
        System.out.println("Current details: " + vehicle);
        System.out.println("Enter new details (press Enter to keep current value):");
        
        // Update common vehicle properties
        System.out.print("Make [" + vehicle.getMake() + "]: ");
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            vehicle.setMake(input);
        }
        
        System.out.print("Model [" + vehicle.getModel() + "]: ");
        input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            vehicle.setModel(input);
        }
        
        System.out.print("Year [" + vehicle.getYear() + "]: ");
        input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            try {
                vehicle.setYear(Integer.parseInt(input));
            } catch (NumberFormatException e) {
                System.out.println("Invalid year format. Keeping current value.");
            }
        }
        
        System.out.print("Color [" + vehicle.getColor() + "]: ");
        input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            vehicle.setColor(input);
        }
        
        System.out.print("Price [" + vehicle.getPrice() + "]: ");
        input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            try {
                vehicle.setPrice(Double.parseDouble(input));
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format. Keeping current value.");
            }
        }
        
        // Update specific properties based on vehicle type
        if (vehicle instanceof Car) {
            Car car = (Car) vehicle;
            
            System.out.print("Number of doors [" + car.getNumDoors() + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                try {
                    car.setNumDoors(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid format. Keeping current value.");
                }
            }
            
            System.out.print("Transmission type [" + car.getTransmissionType() + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                car.setTransmissionType(input);
            }
            
            System.out.print("Engine size [" + car.getEngineSize() + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                try {
                    car.setEngineSize(Double.parseDouble(input));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid format. Keeping current value.");
                }
            }
            
        } else if (vehicle instanceof Truck) {
            Truck truck = (Truck) vehicle;
            
            System.out.print("Cargo capacity [" + truck.getCargoCapacity() + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                try {
                    truck.setCargoCapacity(Double.parseDouble(input));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid format. Keeping current value.");
                }
            }
            
            System.out.print("Drive type [" + truck.getDriveType() + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                truck.setDriveType(input);
            }
            
            System.out.print("Has tow package [" + (truck.getHasTowPackage() ? "Yes" : "No") + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                truck.setHasTowPackage(input.equalsIgnoreCase("yes"));
            }
            
        } else if (vehicle instanceof Motorcycle) {
            Motorcycle motorcycle = (Motorcycle) vehicle;
            
            System.out.print("Bike type [" + motorcycle.getBikeType() + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                motorcycle.setBikeType(input);
            }
            
            System.out.print("Engine CC [" + motorcycle.getEngineCC() + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                try {
                    motorcycle.setEngineCC(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid format. Keeping current value.");
                }
            }
            
            System.out.print("Has fairing [" + (motorcycle.getHasFairing() ? "Yes" : "No") + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                motorcycle.setHasFairing(input.equalsIgnoreCase("yes"));
            }
        }
        
        // Save the updated vehicle
        if (vehicleManager.updateVehicle(vehicle)) {
            System.out.println("Vehicle updated successfully!");
            System.out.println(vehicle);
        } else {
            System.out.println("Failed to update vehicle.");
        }
    }
    
    /**
     * Prompts for a vehicle ID and deletes that vehicle
     * Uses arrow key navigation to select from available vehicles
     */
    private static void deleteVehicle() {
        List<Vehicle> vehicles = vehicleManager.getAllVehicles();
        
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found to delete.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display vehicles in tabulated format
        System.out.println("\nSELECT VEHICLE TO DELETE");
        System.out.println("------------------------");
        
        // Convert vehicles to table data
        List<String[]> tableData = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            tableData.add(ConsoleHelper.vehicleToStringArray(vehicle));
        }
        
        // Display the table
        String[] headers = ConsoleHelper.getVehicleTableHeaders(null);
        ConsoleHelper.displayTable(headers, tableData);
        
        // Create a list of options for selection
        List<String> options = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            options.add(String.format("%s - %s %s (%d)", 
                vehicle.getId(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear()));
        }
        options.add("Cancel");
        
        int selected = ConsoleHelper.showMenu("Select a vehicle to delete:", options);
        
        if (selected == options.size() - 1 || selected == -1) {
            System.out.println("Delete cancelled.");
            return;
        }
        
        String id = vehicles.get(selected).getId();
        deleteVehicleById(id);
    }
    
    /**
     * Deletes a vehicle by its ID
     * Uses arrow key navigation for confirmation
     * @param id The ID of the vehicle to delete
     */
    private static void deleteVehicleById(String id) {
        Vehicle vehicle = vehicleManager.getVehicleById(id);
        
        if (vehicle == null) {
            System.out.println("Vehicle not found with ID: " + id);
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\nDELETE VEHICLE: " + id);
        
        // Display vehicle details in tabulated format
        List<String[]> tableData = new ArrayList<>();
        tableData.add(ConsoleHelper.vehicleToStringArray(vehicle));
        
        String vehicleType = "";
        if (vehicle instanceof Car) vehicleType = "car";
        else if (vehicle instanceof Truck) vehicleType = "truck";
        else if (vehicle instanceof Motorcycle) vehicleType = "motorcycle";
        
        String[] headers = ConsoleHelper.getVehicleTableHeaders(vehicleType);
        ConsoleHelper.displayTable(headers, tableData);
        
        // Confirmation with arrow key navigation
        List<String> options = new ArrayList<>();
        options.add("Yes, delete this vehicle");
        options.add("No, cancel deletion");
        
        int selected = ConsoleHelper.showMenu("Are you sure you want to delete this vehicle?", options);
        
        if (selected == 0) { // Yes option
            if (vehicleManager.deleteVehicle(id)) {
                System.out.println("Vehicle deleted successfully!");
            } else {
                System.out.println("Failed to delete vehicle.");
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
}
