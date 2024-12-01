package CC13;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

// Abstract Data Type: Represents a parking spot with its attributes and behaviors.
class ParkingSpot {
    int spotId;                
    String vehicleNumber;      
    boolean isOccupied;        

    public ParkingSpot(int spotId) {
        this.spotId = spotId;
        this.vehicleNumber = "";
        this.isOccupied = false;
    }

    public void occupy(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
        this.isOccupied = true;
    }

    public void vacate() {
        this.vehicleNumber = "";
        this.isOccupied = false;
    }

    @Override
    public String toString() {
        // Provides a string representation of the parking spot for display.
        return "<html>Spot ID: " + spotId + "<br>Vehicle: " + (isOccupied ? vehicleNumber : "None") + "</html>";
    }
}

public class ParkingManagementSystem extends JFrame {
    // Linear Data Structure: Stores all parking spots in sequence.
    private ArrayList<ParkingSpot> carParkingSpots;
    private ArrayList<ParkingSpot> motorcycleParkingSpots;

    // Non-linear Data Structure: Maps spot IDs to their corresponding parking spots for fast lookup.
    private HashMap<Integer, ParkingSpot> carSpotMap;
    private HashMap<Integer, ParkingSpot> motorcycleSpotMap;

    private JPanel gridPanel;      // Panel for displaying the grid of parking spots.
    private File plateNumbersFile; // File to persist vehicle and spot data.

    public ParkingManagementSystem() {
        carParkingSpots = new ArrayList<>();  // Initialize linear data structure for cars.
        motorcycleParkingSpots = new ArrayList<>();  // Initialize linear data structure for motorcycles.
        carSpotMap = new HashMap<>();        // Initialize non-linear data structure for cars.
        motorcycleSpotMap = new HashMap<>(); // Initialize non-linear data structure for motorcycles.

        // Create 50 parking spots for cars, stored in both the ArrayList and HashMap.
        for (int i = 1; i <= 50; i++) {
            ParkingSpot spot = new ParkingSpot(i);
            carParkingSpots.add(spot);    // Add to ArrayList.
            carSpotMap.put(i, spot);     // Add to HashMap for efficient access.
        }

        // Create 50 parking spots for motorcycles, stored in both the ArrayList and HashMap.
        for (int i = 1; i <= 50; i++) {
            ParkingSpot spot = new ParkingSpot(i); // Unique spotId for motorcycles starting from 1.
            motorcycleParkingSpots.add(spot);    // Add to ArrayList.
            motorcycleSpotMap.put(i, spot); // Add to HashMap for efficient access.
        }

        // Initialize file system for storing vehicle data.
        setupFileSystem();

        setTitle("Parking Management System"); // Set window title.
        setSize(800, 800);                     // Set window dimensions.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application on close.
        setLayout(new BorderLayout());         // Use BorderLayout for the UI.

        // Create buttons for managing the parking system.
        JButton addButton = new JButton("Add Vehicle");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVehicle(); // Prompt the user to add a vehicle.
            }
        });

        JButton removeButton = new JButton("Remove Vehicle");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeVehicle(); // Prompt the user to remove a vehicle.
            }
        });

        JButton displayButton = new JButton("Refresh Spots");
        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshGrid(); // Refresh the parking spot display.
            }
        });

        // Panel for the buttons.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Horizontal layout for buttons.
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(displayButton);

        // Panel for the parking grid.
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(12, 10, 10, 10)); // Adjusted for labels and spots.
        refreshGrid(); // Initialize grid with the current state of spots.

        // Add components to the JFrame.
        add(buttonPanel, BorderLayout.NORTH);  // Buttons at the top.
        add(gridPanel, BorderLayout.CENTER);  // Grid in the center.

        setVisible(true); // Make the UI visible.
    }

    private void setupFileSystem() {
        // File System: Locate the user's Downloads directory and create the necessary file.
        String userHome = System.getProperty("user.home");
        File downloadDir = new File(userHome, "Downloads"); // Locate Downloads folder.
        if (!downloadDir.exists()) {
            JOptionPane.showMessageDialog(this, "Error: Downloads folder not found.");
            return;
        }

        plateNumbersFile = new File(downloadDir, "Plate_Numbers.txt");
        if (!plateNumbersFile.exists()) {
            try {
                plateNumbersFile.createNewFile(); // Create file if it doesn't exist.
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error creating Plate_Numbers.txt file: " + e.getMessage());
            }
        }
    }

    private void refreshGrid() {
        // Updates the parking grid UI with the current state of all spots.
        gridPanel.removeAll(); // Clear existing components.

        // Add label for car area.
        JLabel carLabel = new JLabel("Car Area", SwingConstants.CENTER);
        carLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gridPanel.add(carLabel);

        for (int i = 0; i < 9; i++) {
            gridPanel.add(new JLabel()); // Fill the rest of the row.
        }

        for (ParkingSpot spot : carParkingSpots) { // Iterate through all car spots.
            JButton spotButton = new JButton(spot.toString()); // Display spot details.
            spotButton.setBackground(spot.isOccupied ? Color.RED : Color.GREEN); // Red = occupied, Green = vacant.
            spotButton.setOpaque(true); // Required for color changes on some systems.
            spotButton.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border.
            spotButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Display detailed information about the spot.
                    JOptionPane.showMessageDialog(
                            ParkingManagementSystem.this,
                            spot.toString(),
                            "Spot Details",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            });
            gridPanel.add(spotButton); // Add button to grid.
        }

        // Add label for motorcycle area.
        JLabel motorcycleLabel = new JLabel("Motorcycle Area", SwingConstants.CENTER);
        motorcycleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gridPanel.add(motorcycleLabel);

        for (int i = 0; i < 9; i++) {
            gridPanel.add(new JLabel()); // Fill the rest of the row.
        }

        for (ParkingSpot spot : motorcycleParkingSpots) { // Iterate through all motorcycle spots.
            JButton spotButton = new JButton(spot.toString()); // Display spot details.
            spotButton.setBackground(spot.isOccupied ? Color.BLUE : Color.YELLOW); // Blue = occupied, Yellow = vacant.
            spotButton.setOpaque(true); // Required for color changes on some systems.
            spotButton.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border.
            spotButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Display detailed information about the spot.
                    JOptionPane.showMessageDialog(
                            ParkingManagementSystem.this,
                            spot.toString(),
                            "Spot Details",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            });
            gridPanel.add(spotButton); // Add button to grid.
        }

        gridPanel.revalidate(); // Refresh UI.
        gridPanel.repaint();    // Update display.
    }

    private void addVehicle() {
        // Prompt user to choose vehicle type.
        String[] vehicleOptions = {"Car", "Motorcycle"};
        int vehicleChoice = JOptionPane.showOptionDialog(
                this,
                "What type of vehicle do you have?",
                "Vehicle Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                vehicleOptions,
                vehicleOptions[0]
        );
    
        if (vehicleChoice == JOptionPane.CLOSED_OPTION) {
            return; // Exit if cancelled
        }
    
        // Prompt user to choose between sequential or manual spot selection.
        String[] options = {"Sequential Parking", "Choose a Spot"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "How would you like to park?",
                "Parking Option",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
    
        ArrayList<ParkingSpot> selectedParkingSpots = vehicleChoice == 0 ? carParkingSpots : motorcycleParkingSpots;
        HashMap<Integer, ParkingSpot> selectedSpotMap = vehicleChoice == 0 ? carSpotMap : motorcycleSpotMap;
    
        if (choice == 0) { // Sequential Parking: First available spot.
            for (ParkingSpot spot : selectedParkingSpots) { // Linear search for the first free spot.
                if (!spot.isOccupied) {
                    String vehicleNumber = JOptionPane.showInputDialog(this, "Enter Vehicle Plate Number:");
                    if (vehicleNumber == null) {
                        return; // Exit if cancelled
                    }
                    if (vehicleNumber.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Invalid Vehicle Plate Number");
                        return;
                    }
                    spot.occupy(vehicleNumber);       // Mark spot as occupied.
                    selectedSpotMap.put(spot.spotId, spot);  // Update HashMap.
                    refreshGrid();                   // Refresh UI.
                    addVehicleToFile(vehicleChoice == 0 ? "Car" : "Motorcycle", spot.spotId, vehicleNumber); // Update file system.
                    JOptionPane.showMessageDialog(
                            this,
                            "Receipt\n---------------\nSpot ID: " + spot.spotId + "\nPlate Number: " + vehicleNumber + "\n---------------\nDon't throw, to be returned",
                            "Parking Confirmation",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return; // Exit after parking.
                }
            }
            JOptionPane.showMessageDialog(this, "No available spots"); // All spots occupied.
        } else if (choice == 1) { // Choose a Spot manually.
            String vehicleNumber = JOptionPane.showInputDialog(this, "Enter Vehicle Plate Number:");
            if (vehicleNumber == null) {
                return; // Exit if cancelled
            }
            if (vehicleNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid Vehicle Number");
                return;
            }
    
            ArrayList<Integer> availableSpots = new ArrayList<>(); // List for available spot IDs.
            for (ParkingSpot spot : selectedParkingSpots) {
                if (!spot.isOccupied) {
                    availableSpots.add(spot.spotId); // Collect all vacant spots.
                }
            }
    
            if (availableSpots.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available spots");
                return;
            }
    
            Integer[] spotArray = availableSpots.toArray(new Integer[0]); // Convert to array.
            Integer chosenSpotId = (Integer) JOptionPane.showInputDialog(
                    this,
                    "Choose a parking spot:",
                    "Available Spots",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    spotArray,
                    spotArray[0]
            );
    
            if (chosenSpotId != null) {
                ParkingSpot chosenSpot = selectedSpotMap.get(chosenSpotId); // Retrieve spot via HashMap.
                if (chosenSpot != null) {
                    chosenSpot.occupy(vehicleNumber); 
                    selectedSpotMap.put(chosenSpot.spotId, chosenSpot); // Update HashMap.
                    refreshGrid(); // Refresh UI.
                    addVehicleToFile(vehicleChoice == 0 ? "Car" : "Motorcycle", chosenSpot.spotId, vehicleNumber); // Update file system.
                    JOptionPane.showMessageDialog(
                            this,
                            "Receipt\n---------------\nSpot ID: " + chosenSpot.spotId + "\nPlate Number: " + vehicleNumber + "\n---------------\nDon't throw, to be returned",
                            "Parking Confirmation",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Spot not found");
                }
            }
        }
    }
    private void removeVehicle() {
        // Prompt user to choose vehicle type.
        String[] vehicleOptions = {"Car", "Motorcycle"};
        int vehicleChoice = JOptionPane.showOptionDialog(
                this,
                "What type of vehicle do you want to remove?",
                "Vehicle Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                vehicleOptions,
                vehicleOptions[0]
        );

        if (vehicleChoice == JOptionPane.CLOSED_OPTION) {
            return; // Exit if cancelled
        }

        // Prompt user for the spot ID to vacate.
        String spotIdStr = JOptionPane.showInputDialog(this, "Enter Spot ID to vacate:");
        if (spotIdStr == null) {
            return; // Exit if cancelled
        }
        try {
            int spotId = Integer.parseInt(spotIdStr);
            HashMap<Integer, ParkingSpot> selectedSpotMap = vehicleChoice == 0 ? carSpotMap : motorcycleSpotMap;
            ParkingSpot spot = selectedSpotMap.get(spotId); // Retrieve spot via HashMap.
            if (spot != null && spot.isOccupied) {
                updateFileOnRemove(spot); // Update file system before vacating.
                spot.vacate();           // Mark spot as vacant.
                selectedSpotMap.put(spot.spotId, spot); // Update HashMap.
                refreshGrid();           // Refresh UI.
                JOptionPane.showMessageDialog(this, "Spot " + spot.spotId + " is now vacated");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Spot ID or Spot is already vacant");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Spot ID");
        }
    }
     private void updateFileOnAdd(ParkingSpot spot) {
        // Append new vehicle entry to the file.
        try (FileWriter fileWriter = new FileWriter(plateNumbersFile, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write("Car - Spot: " + spot.spotId + " - Plate: " + spot.vehicleNumber);
            bufferedWriter.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating Plate_Numbers.txt file: " + e.getMessage());
        }
    }

    private void updateFileOnRemove(ParkingSpot spot) {
        try {
            // Read all lines from the file into memory.
            ArrayList<String> lines = new ArrayList<>();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(plateNumbersFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    // Only keep lines that don't match the vacated spot's details.
                    if (!line.contains("Spot: " + spot.spotId + " - Plate: " + spot.vehicleNumber)) {
                        lines.add(line);
                    }
                }
            }
    
            // Overwrite the file with the updated lines.
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(plateNumbersFile))) {
                for (String line : lines) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating Plate_Numbers.txt file: " + e.getMessage());
        }
    }

    private void addVehicleToFile(String vehicleType, int spotId, String plateNumber) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(plateNumbersFile, true))) {
            // Format: "VehicleType - Spot: X - Plate: ABC1234"
            String entry = vehicleType + " - Spot: " + spotId + " - Plate: " + plateNumber;
            bufferedWriter.write(entry);
            bufferedWriter.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to Plate_Numbers.txt file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ParkingManagementSystem(); // Launch the application.
    }
}   