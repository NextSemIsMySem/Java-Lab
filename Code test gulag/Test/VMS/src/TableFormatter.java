/**
 * Utility class for formatting data into well-aligned tables in console output
 */
import java.util.List;

public class TableFormatter {
    
    /**
     * Formats and displays a table with headers and data
     * 
     * @param headers Column headers
     * @param data Table data as a list of string arrays
     * @return Formatted table string
     */
    public static String formatTable(String[] headers, List<String[]> data) {
        if (headers == null || data == null || headers.length == 0) {
            return "No data to display";
        }
        
        // Calculate column widths based on content
        int[] columnWidths = calculateColumnWidths(headers, data);
        
        StringBuilder table = new StringBuilder();
        
        // Create top border
        table.append(formatSeparator(columnWidths));
        
        // Create header row
        table.append(formatRow(headers, columnWidths));
        
        // Create separator row
        table.append(formatSeparator(columnWidths));
        
        // Create data rows
        for (String[] row : data) {
            table.append(formatRow(row, columnWidths));
        }
        
        // Create bottom border
        table.append(formatSeparator(columnWidths));
        
        return table.toString();
    }
    
    /**
     * Calculates appropriate column widths based on content
     */
    private static int[] calculateColumnWidths(String[] headers, List<String[]> data) {
        int[] widths = new int[headers.length];
        
        // Initialize with header lengths
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        
        // Check data lengths
        for (String[] row : data) {
            for (int i = 0; i < row.length && i < widths.length; i++) {
                if (row[i] != null && row[i].length() > widths[i]) {
                    widths[i] = row[i].length();
                }
            }
        }
        
        // Add padding
        for (int i = 0; i < widths.length; i++) {
            widths[i] += 2; // Add some padding
        }
        
        return widths;
    }
    
    /**
     * Formats a single row with proper spacing
     */
    private static String formatRow(String[] row, int[] columnWidths) {
        StringBuilder rowStr = new StringBuilder("| ");
        
        for (int i = 0; i < row.length && i < columnWidths.length; i++) {
            String cell = row[i] != null ? row[i] : "";
            rowStr.append(padRight(cell, columnWidths[i])).append("| ");
        }
        
        return rowStr.append("\n").toString();
    }
    
    /**
     * Creates a separator line for the table
     */
    private static String formatSeparator(int[] columnWidths) {
        StringBuilder separator = new StringBuilder("+-");
        
        for (int width : columnWidths) {
            separator.append(repeat("-", width)).append("+-");
        }
        
        return separator.append("\n").toString();
    }
    
    /**
     * Right-pads a string to the specified length
     */
    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    
    /**
     * Repeats a character a specified number of times
     */
    private static String repeat(String c, int n) {
        return new String(new char[n]).replace("\0", c);
    }
}
