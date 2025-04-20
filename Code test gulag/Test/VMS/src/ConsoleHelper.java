import java.util.List;
import java.io.IOException;
import java.util.Scanner;

/**
 * Helper class for console operations including menu navigation and table display
 */
public class ConsoleHelper {
    /**
     * Clears the console (works in some terminals but not all)
     */
    public static void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clearing fails, just add some newlines
            System.out.println("\n\n\n\n\n");
        }
    }
    
    /**
     * Displays a menu with numeric selection
     * @param title The title of the menu
     * @param options List of options to display
     * @return The index of the selected option, or -1 if cancelled
     */
    public static int showMenu(String title, List<String> options) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n" + title);
            System.out.println("-".repeat(title.length()));
            
            // Display menu options with numbers
            for (int i = 0; i < options.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, options.get(i));
            }
            
            System.out.print("\nEnter your choice (1-" + options.size() + ") or 0 to cancel: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    return -1;
                } else if (choice >= 1 && choice <= options.size()) {
                    return choice - 1;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
            
            System.out.println(); // Add a blank line for readability
        }
    }
    
    /**
     * Converts a vehicle to a string array for table display
     * @param vehicle The vehicle to convert
     * @return String array of vehicle properties
     */
    public static String[] vehicleToStringArray(Vehicle vehicle) {
        if (vehicle instanceof Car) {
            Car car = (Car) vehicle;
            return new String[] {
                car.getId(),
                "Car",
                car.getMake(),
                car.getModel(),
                String.valueOf(car.getYear()),
                car.getColor(),
                String.format("$%.2f", car.getPrice()),
                String.valueOf(car.getNumDoors()),
                car.getTransmissionType(),
                String.format("%.1fL", car.getEngineSize())
            };
        } else if (vehicle instanceof Truck) {
            Truck truck = (Truck) vehicle;
            return new String[] {
                truck.getId(),
                "Truck",
                truck.getMake(),
                truck.getModel(),
                String.valueOf(truck.getYear()),
                truck.getColor(),
                String.format("$%.2f", truck.getPrice()),
                String.format("%.1f", truck.getCargoCapacity()),
                truck.getDriveType(),
                truck.getHasTowPackage() ? "Yes" : "No"
            };
        } else if (vehicle instanceof Motorcycle) {
            Motorcycle motorcycle = (Motorcycle) vehicle;
            return new String[] {
                motorcycle.getId(),
                "Motorcycle",
                motorcycle.getMake(),
                motorcycle.getModel(),
                String.valueOf(motorcycle.getYear()),
                motorcycle.getColor(),
                String.format("$%.2f", motorcycle.getPrice()),
                motorcycle.getBikeType(),
                String.valueOf(motorcycle.getEngineCC()) + "cc",
                motorcycle.getHasFairing() ? "Yes" : "No"
            };
        }
        
        return new String[] {
            vehicle.getId(),
            "Unknown",
            vehicle.getMake(),
            vehicle.getModel(),
            String.valueOf(vehicle.getYear()),
            vehicle.getColor(),
            String.format("$%.2f", vehicle.getPrice())
        };
    }
    
    /**
     * Gets table headers based on vehicle type
     * @param type The type of vehicle (car, truck, motorcycle) or null for general headers
     * @return Array of header strings
     */
    public static String[] getVehicleTableHeaders(String type) {
        if ("car".equalsIgnoreCase(type)) {
            return new String[] {"ID", "Type", "Make", "Model", "Year", "Color", "Price", "Doors", "Transmission", "Engine"};
        } else if ("truck".equalsIgnoreCase(type)) {
            return new String[] {"ID", "Type", "Make", "Model", "Year", "Color", "Price", "Cargo Cap.", "Drive Type", "Tow Pkg"};
        } else if ("motorcycle".equalsIgnoreCase(type)) {
            return new String[] {"ID", "Type", "Make", "Model", "Year", "Color", "Price", "Bike Type", "Engine", "Fairing"};
        } else {
            return new String[] {"ID", "Type", "Make", "Model", "Year", "Color", "Price", "Spec 1", "Spec 2", "Spec 3"};
        }
    }
    
    /**
     * Displays data in a tabulated format
     * @param headers Table headers
     * @param data Table data rows
     */
    public static void displayTable(String[] headers, List<String[]> data) {
        if (data.isEmpty()) {
            System.out.println("No data to display");
            return;
        }
        
        // Find the max width needed for each column
        int[] colWidths = new int[headers.length];
        
        // Check headers first
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
        }
        
        // Check data
        for (String[] row : data) {
            for (int i = 0; i < row.length && i < colWidths.length; i++) {
                if (row[i] != null && row[i].length() > colWidths[i]) {
                    colWidths[i] = row[i].length();
                }
            }
        }
        
        // Create top border
        StringBuilder topBorder = new StringBuilder("+");
        for (int width : colWidths) {
            topBorder.append("-".repeat(width + 2)).append("+");
        }
        System.out.println(topBorder);
        
        // Print header row
        StringBuilder headerRow = new StringBuilder("|");
        for (int i = 0; i < headers.length; i++) {
            headerRow.append(" ").append(padRight(headers[i], colWidths[i])).append(" |");
        }
        System.out.println(headerRow);
        
        // Print separator row
        StringBuilder separatorRow = new StringBuilder("|");
        for (int width : colWidths) {
            separatorRow.append("-").append("-".repeat(width)).append("-|");
        }
        System.out.println(separatorRow);
        
        // Print data rows
        for (String[] row : data) {
            StringBuilder dataRow = new StringBuilder("|");
            for (int i = 0; i < headers.length; i++) {
                String value = i < row.length ? row[i] : "";
                dataRow.append(" ").append(padRight(value, colWidths[i])).append(" |");
            }
            System.out.println(dataRow);
        }
        
        // Create bottom border (same as top border)
        System.out.println(topBorder);
    }
    
    /**
     * Pads a string with spaces to the right to achieve the specified length
     * @param str String to pad
     * @param length Desired length
     * @return Padded string
     */
    private static String padRight(String str, int length) {
        if (str == null) {
            str = "";
        }
        return str + " ".repeat(Math.max(0, length - str.length()));
    }
}