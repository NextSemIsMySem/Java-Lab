import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JSONHandler class - Handles loading and saving vehicle data to a JSON file
 * Implements abstraction by hiding the details of file I/O and JSON handling
 * 
 * This version doesn't depend on external JSON libraries to avoid classpath issues
 */
public class JSONHandler {
    // Fixed path to better handle file location
    private final String dataFilePath;
    
    /**
     * Default constructor - always tries to use the src folder for the data file
     */
    public JSONHandler() {
        // Set filename and get the current directory
        String filename = "vehicles.json";
        File currentDir = new File("").getAbsoluteFile();
        
        // Always prefer the src folder
        File srcFolder = new File(currentDir, "src");
        File dataFile = new File(srcFolder, filename);
        
        // If src folder doesn't exist, try to create it
        if (!srcFolder.exists()) {
            if (srcFolder.mkdir()) {
                System.out.println("Created src folder at: " + srcFolder.getAbsolutePath());
            } else {
                System.err.println("Failed to create src folder. Using current directory instead.");
                dataFile = new File(currentDir, filename);
            }
        }
        
        // Check if the file exists, if not create it
        if (!dataFile.exists()) {
            try {
                if (createEmptyJsonFile(dataFile)) {
                    System.out.println("Created new data file at: " + dataFile.getAbsolutePath());
                }
            } catch (IOException e) {
                System.err.println("Failed to create data file at " + dataFile.getAbsolutePath() + 
                                  ". Reason: " + e.getMessage());
                
                // Fallback to current directory if src folder access fails
                dataFile = new File(currentDir, filename);
                try {
                    if (createEmptyJsonFile(dataFile)) {
                        System.out.println("Created fallback data file at: " + dataFile.getAbsolutePath());
                    }
                } catch (IOException e2) {
                    System.err.println("Failed to create fallback data file. Reason: " + e2.getMessage());
                }
            }
        }
        
        this.dataFilePath = dataFile.getAbsolutePath();
        System.out.println("Using data file path: " + this.dataFilePath);
    }
    
    /**
     * Constructor with specific file path
     * @param filePath The path to the JSON file
     */
    public JSONHandler(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                createEmptyJsonFile(file);
                System.out.println("Created new data file at specified path: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to create data file at specified path: " + 
                                  filePath + ". Reason: " + e.getMessage());
            }
        }
        
        this.dataFilePath = file.getAbsolutePath();
        System.out.println("Using custom data file: " + this.dataFilePath);
    }
    
    /**
     * Creates an empty JSON array file
     * @param file The file to create
     * @return true if file created or already exists, false if creation failed
     * @throws IOException If file creation fails
     */
    private boolean createEmptyJsonFile(File file) throws IOException {
        // Create parent directories if needed
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Failed to create parent directories");
            }
        }
        
        boolean fileExists = file.exists();
        if (!fileExists) {
            fileExists = file.createNewFile();
            if (!fileExists) {
                throw new IOException("Failed to create new file");
            }
        }
        
        // Write an empty array if the file was just created
        if (fileExists && file.length() == 0) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("[]");
                writer.flush();
            }
        }
        
        return fileExists;
    }
    
    /**
     * Saves a list of vehicles to a JSON file
     * @param vehicles List of vehicles to save
     * @return true if successful, false otherwise
     */
    public boolean saveVehicles(List<Vehicle> vehicles) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        
        try {
            boolean first = true;
            // Convert each vehicle to a JSON object
            for (Vehicle vehicle : vehicles) {
                if (!first) {
                    jsonBuilder.append(",");
                }
                first = false;
                
                jsonBuilder.append("{");
                
                // Common vehicle properties
                appendJsonProperty(jsonBuilder, "id", vehicle.getId(), true);
                appendJsonProperty(jsonBuilder, "make", vehicle.getMake(), false);
                appendJsonProperty(jsonBuilder, "model", vehicle.getModel(), false);
                appendJsonProperty(jsonBuilder, "year", vehicle.getYear(), false);
                appendJsonProperty(jsonBuilder, "color", vehicle.getColor(), false);
                appendJsonProperty(jsonBuilder, "price", vehicle.getPrice(), false);
                
                // Determine vehicle type and add specific properties
                if (vehicle instanceof Car) {
                    Car car = (Car) vehicle;
                    appendJsonProperty(jsonBuilder, "type", "Car", false);
                    appendJsonProperty(jsonBuilder, "numDoors", car.getNumDoors(), false);
                    appendJsonProperty(jsonBuilder, "transmissionType", car.getTransmissionType(), false);
                    appendJsonProperty(jsonBuilder, "engineSize", car.getEngineSize(), false);
                } else if (vehicle instanceof Truck) {
                    Truck truck = (Truck) vehicle;
                    appendJsonProperty(jsonBuilder, "type", "Truck", false);
                    appendJsonProperty(jsonBuilder, "cargoCapacity", truck.getCargoCapacity(), false);
                    appendJsonProperty(jsonBuilder, "driveType", truck.getDriveType(), false);
                    appendJsonProperty(jsonBuilder, "hasTowPackage", truck.getHasTowPackage(), false);
                } else if (vehicle instanceof Motorcycle) {
                    Motorcycle motorcycle = (Motorcycle) vehicle;
                    appendJsonProperty(jsonBuilder, "type", "Motorcycle", false);
                    appendJsonProperty(jsonBuilder, "bikeType", motorcycle.getBikeType(), false);
                    appendJsonProperty(jsonBuilder, "engineCC", motorcycle.getEngineCC(), false);
                    appendJsonProperty(jsonBuilder, "hasFairing", motorcycle.getHasFairing(), false);
                }
                
                // Close the JSON object
                jsonBuilder.append("}");
            }
            
            // Close the JSON array
            jsonBuilder.append("]");
            
            // Write JSON string to file
            try (FileWriter file = new FileWriter(dataFilePath)) {
                file.write(jsonBuilder.toString());
                file.flush();
                return true;
            } catch (IOException e) {
                System.err.println("Error writing to file " + dataFilePath + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error saving vehicles: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Adds a property to a JSON object being built
     * @param builder The StringBuilder to append to
     * @param key The property key
     * @param value The property value
     * @param isFirst Whether this is the first property in the object
     */
    private void appendJsonProperty(StringBuilder builder, String key, Object value, boolean isFirst) {
        if (!isFirst) {
            builder.append(",");
        }
        
        builder.append("\"").append(key).append("\":");
        
        if (value == null) {
            builder.append("null");
        } else if (value instanceof String) {
            builder.append("\"").append(escapeJsonString((String)value)).append("\"");
        } else if (value instanceof Boolean) {
            builder.append(value);
        } else if (value instanceof Number) {
            builder.append(value);
        } else {
            builder.append("\"").append(escapeJsonString(value.toString())).append("\"");
        }
    }
    
    /**
     * Escapes special characters in a string for JSON
     * @param input The input string
     * @return The escaped string
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\\': result.append("\\\\"); break;
                case '\"': result.append("\\\""); break;
                case '\n': result.append("\\n"); break;
                case '\r': result.append("\\r"); break;
                case '\t': result.append("\\t"); break;
                case '\b': result.append("\\b"); break;
                case '\f': result.append("\\f"); break;
                default: result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * Loads vehicles from a JSON file
     * @return List of vehicles loaded from file
     */
    public List<Vehicle> loadVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        File dataFile = new File(dataFilePath);
        
        // Create empty file if it doesn't exist
        if (!dataFile.exists()) {
            try {
                createEmptyJsonFile(dataFile);
                System.out.println("Created missing data file at: " + dataFilePath);
            } catch (IOException e) {
                System.err.println("Error creating data file: " + e.getMessage());
                e.printStackTrace();
                return vehicles; // Return empty list
            }
        }
        
        // Check if file is readable
        if (!dataFile.canRead()) {
            System.err.println("Error: Cannot read data file at " + dataFilePath);
            return vehicles;
        }
        
        // Check if file is empty
        if (dataFile.length() == 0) {
            System.out.println("Data file is empty, initializing with empty array");
            try {
                try (FileWriter writer = new FileWriter(dataFile)) {
                    writer.write("[]");
                    writer.flush();
                }
            } catch (IOException e) {
                System.err.println("Error initializing empty data file: " + e.getMessage());
                return vehicles;
            }
        }
        
        try {
            // Read the file content
            StringBuilder contentBuilder = new StringBuilder();
            try (FileReader reader = new FileReader(dataFilePath)) {
                char[] buffer = new char[1024];
                int charsRead;
                while ((charsRead = reader.read(buffer)) != -1) {
                    contentBuilder.append(buffer, 0, charsRead);
                }
            }
            
            String jsonContent = contentBuilder.toString().trim();
            System.out.println("Reading vehicles from: " + dataFilePath);
            
            // Basic validation - must be a JSON array
            if (!jsonContent.startsWith("[") || !jsonContent.endsWith("]")) {
                System.err.println("Error: Data file does not contain a valid JSON array");
                return vehicles;
            }
            
            // Empty array case
            if (jsonContent.equals("[]")) {
                return vehicles;
            }
            
            // Parse the JSON array content
            List<Map<String, Object>> vehicleList = parseJsonArray(jsonContent);
            System.out.println("Found " + vehicleList.size() + " vehicles in data file");
            
            // Process each vehicle
            for (Map<String, Object> jsonVehicle : vehicleList) {
                try {
                    String type = (String) jsonVehicle.get("type");
                    
                    if (type == null) {
                        System.err.println("Warning: Vehicle missing type information, skipping");
                        continue;
                    }
                    
                    // Extract common properties with null checks
                    String id = jsonVehicle.get("id") != null ? (String) jsonVehicle.get("id") : generateUniqueId();
                    String make = jsonVehicle.get("make") != null ? (String) jsonVehicle.get("make") : "";
                    String model = jsonVehicle.get("model") != null ? (String) jsonVehicle.get("model") : "";
                    
                    // Safe conversion to Java primitives
                    int year = safeParseInt(jsonVehicle.get("year"), 0);
                    String color = jsonVehicle.get("color") != null ? (String) jsonVehicle.get("color") : "";
                    double price = safeParseDouble(jsonVehicle.get("price"), 0.0);
                    
                    // Create appropriate vehicle type based on "type" field
                    if ("Car".equals(type)) {
                        int numDoors = safeParseInt(jsonVehicle.get("numDoors"), 0);
                        String transmissionType = jsonVehicle.get("transmissionType") != null ? 
                                                (String) jsonVehicle.get("transmissionType") : "";
                        double engineSize = safeParseDouble(jsonVehicle.get("engineSize"), 0.0);
                        
                        vehicles.add(new Car(id, make, model, year, color, price, 
                                            numDoors, transmissionType, engineSize));
                        
                    } else if ("Truck".equals(type)) {
                        double cargoCapacity = safeParseDouble(jsonVehicle.get("cargoCapacity"), 0.0);
                        String driveType = jsonVehicle.get("driveType") != null ? 
                                         (String) jsonVehicle.get("driveType") : "";
                        boolean hasTowPackage = safeParseBool(jsonVehicle.get("hasTowPackage"), false);
                        
                        vehicles.add(new Truck(id, make, model, year, color, price,
                                              cargoCapacity, driveType, hasTowPackage));
                        
                    } else if ("Motorcycle".equals(type)) {
                        String bikeType = jsonVehicle.get("bikeType") != null ? 
                                        (String) jsonVehicle.get("bikeType") : "";
                        int engineCC = safeParseInt(jsonVehicle.get("engineCC"), 0);
                        boolean hasFairing = safeParseBool(jsonVehicle.get("hasFairing"), false);
                        
                        vehicles.add(new Motorcycle(id, make, model, year, color, price,
                                                   bikeType, engineCC, hasFairing));
                    } else {
                        System.err.println("Unknown vehicle type: " + type);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing vehicle: " + e.getMessage());
                    e.printStackTrace();
                    // Continue to next vehicle instead of aborting the entire loading process
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading data file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error while loading vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }
    
    /**
     * Parse a JSON array string into a list of maps
     * @param jsonArrayString The JSON array string
     * @return List of maps representing JSON objects
     */
    private List<Map<String, Object>> parseJsonArray(String jsonArrayString) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // Remove the outer brackets and split by objects
        String content = jsonArrayString.substring(1, jsonArrayString.length() - 1).trim();
        
        // Empty array case
        if (content.isEmpty()) {
            return result;
        }
        
        // Split the content into individual JSON objects
        List<String> objectStrings = splitJsonObjects(content);
        
        // Parse each JSON object
        for (String objString : objectStrings) {
            if (!objString.isEmpty()) {
                Map<String, Object> jsonObject = parseJsonObject(objString);
                result.add(jsonObject);
            }
        }
        
        return result;
    }
    
    /**
     * Split a string containing multiple JSON objects into individual object strings
     * @param content String containing multiple JSON objects separated by commas
     * @return List of individual JSON object strings
     */
    private List<String> splitJsonObjects(String content) {
        List<String> result = new ArrayList<>();
        
        int depth = 0;
        int startIndex = 0;
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (inString) {
                if (c == '\\' && !escaped) {
                    escaped = true;
                } else if (c == '"' && !escaped) {
                    inString = false;
                } else {
                    escaped = false;
                }
            } else {
                if (c == '"') {
                    inString = true;
                } else if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                } else if (c == ',' && depth == 0) {
                    result.add(content.substring(startIndex, i).trim());
                    startIndex = i + 1;
                }
            }
        }
        
        // Add the last object
        if (startIndex < content.length()) {
            result.add(content.substring(startIndex).trim());
        }
        
        return result;
    }
    
    /**
     * Parse a JSON object string into a map
     * @param jsonObjectString The JSON object string (without the outer {})
     * @return Map representing the JSON object
     */
    private Map<String, Object> parseJsonObject(String jsonObjectString) {
        Map<String, Object> result = new HashMap<>();
        
        // Ensure the string has the proper format
        if (!jsonObjectString.startsWith("{") || !jsonObjectString.endsWith("}")) {
            System.err.println("Invalid JSON object: " + jsonObjectString);
            return result;
        }
        
        // Remove the outer braces
        String content = jsonObjectString.substring(1, jsonObjectString.length() - 1).trim();
        
        // Empty object case
        if (content.isEmpty()) {
            return result;
        }
        
        // Split the content into key-value pairs
        List<String> pairs = splitJsonPairs(content);
        
        // Parse each key-value pair
        for (String pair : pairs) {
            if (!pair.isEmpty()) {
                int colonIndex = pair.indexOf(":");
                if (colonIndex > 0) {
                    String key = pair.substring(0, colonIndex).trim();
                    String value = pair.substring(colonIndex + 1).trim();
                    
                    // Remove quotes from the key
                    if (key.startsWith("\"") && key.endsWith("\"")) {
                        key = key.substring(1, key.length() - 1);
                    }
                    
                    // Parse the value
                    result.put(key, parseJsonValue(value));
                }
            }
        }
        
        return result;
    }
    
    /**
     * Split a string containing multiple key-value pairs into individual pair strings
     * @param content String containing multiple key-value pairs separated by commas
     * @return List of individual key-value pair strings
     */
    private List<String> splitJsonPairs(String content) {
        List<String> result = new ArrayList<>();
        
        int depth = 0;
        int arrayDepth = 0;
        int startIndex = 0;
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (inString) {
                if (c == '\\' && !escaped) {
                    escaped = true;
                } else if (c == '"' && !escaped) {
                    inString = false;
                } else {
                    escaped = false;
                }
            } else {
                if (c == '"') {
                    inString = true;
                } else if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                } else if (c == '[') {
                    arrayDepth++;
                } else if (c == ']') {
                    arrayDepth--;
                } else if (c == ',' && depth == 0 && arrayDepth == 0) {
                    result.add(content.substring(startIndex, i).trim());
                    startIndex = i + 1;
                }
            }
        }
        
        // Add the last pair
        if (startIndex < content.length()) {
            result.add(content.substring(startIndex).trim());
        }
        
        return result;
    }
    
    /**
     * Parse a JSON value string into the appropriate Java object
     * @param valueString The JSON value string
     * @return The parsed Java object
     */
    private Object parseJsonValue(String valueString) {
        valueString = valueString.trim();
        
        // String value
        if (valueString.startsWith("\"") && valueString.endsWith("\"")) {
            return valueString.substring(1, valueString.length() - 1);
        }
        
        // Boolean value
        if (valueString.equals("true")) {
            return Boolean.TRUE;
        }
        if (valueString.equals("false")) {
            return Boolean.FALSE;
        }
        
        // Null value
        if (valueString.equals("null")) {
            return null;
        }
        
        // Number value
        try {
            if (valueString.contains(".")) {
                return Double.parseDouble(valueString);
            } else {
                return Integer.parseInt(valueString);
            }
        } catch (NumberFormatException e) {
            // Not a number, just return as string
            return valueString;
        }
    }
    
    /**
     * Safely parses an integer from a JSON value
     * @param value The JSON value to parse
     * @param defaultValue The default value to return if parsing fails
     * @return The parsed integer or the default value
     */
    private int safeParseInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing integer: " + e.getMessage());
            return defaultValue;
        }
    }
    
    /**
     * Safely parses a double from a JSON value
     * @param value The JSON value to parse
     * @param defaultValue The default value to return if parsing fails
     * @return The parsed double or the default value
     */
    private double safeParseDouble(Object value, double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing double: " + e.getMessage());
            return defaultValue;
        }
    }
    
    /**
     * Safely parses a boolean from a JSON value
     * @param value The JSON value to parse
     * @param defaultValue The default value to return if parsing fails
     * @return The parsed boolean or the default value
     */
    private boolean safeParseBool(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        
        return Boolean.parseBoolean(value.toString());
    }
    
    /**
     * Generates a unique ID for a new vehicle
     * @return A unique ID string
     */
    public String generateUniqueId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}