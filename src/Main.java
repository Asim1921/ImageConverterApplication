import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class Main {
    private static BufferedImage originalImage;
    private static BufferedImage jpegImage;
    private static JLabel originalImageLabel;
    private static JLabel jpegImageLabel;
    private static double originalZoomFactor = 1.0;
    private static double jpegZoomFactor = 1.0;
    private static int originalImageWidth, originalImageHeight;
    private static int jpegImageWidth, jpegImageHeight;

    private static JTable luminanceTable;
    private static JTable chrominanceTable;

    public static void main(String[] args) {
        try {
            // Load the original image
            originalImage = ImageIO.read(new File("src/flower.bmp"));
            jpegImage = ImageIO.read(new File("src/flower_compressed.jpg"));

            originalImageWidth = originalImage.getWidth();
            originalImageHeight = originalImage.getHeight();
            jpegImageWidth = jpegImage.getWidth();
            jpegImageHeight = jpegImage.getHeight();

            // Setup main frame
            JFrame frame = new JFrame("Comp 435 Multimedia Technologies TME 2 Sample Screen");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Create a menu bar with a "Save As" option
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            fileMenu.setForeground(Color.WHITE);
            JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
            saveAsMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveImageAs(jpegImage);
                }
            });
            fileMenu.add(saveAsMenuItem);
            menuBar.add(fileMenu);
            menuBar.setBackground(new Color(70, 130, 180));
            frame.setJMenuBar(menuBar);

            // Create image panels
            JPanel imagesPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Add margin between images
            imagesPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding around images
            originalImageLabel = new JLabel(new ImageIcon(originalImage));
            jpegImageLabel = new JLabel(new ImageIcon(jpegImage));
            imagesPanel.add(createImagePanel("Original Image", originalImageLabel, true));
            imagesPanel.add(createImagePanel("Converted Image", jpegImageLabel, false));

            // Create quantization matrix panel
            JPanel matrixPanel = createMatrixPanel();
            matrixPanel.setBackground(new Color(240, 255, 255));

            frame.add(imagesPanel, BorderLayout.CENTER);
            frame.add(matrixPanel, BorderLayout.EAST);

            frame.pack();
            frame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveImageAs(BufferedImage image) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image As");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG files", "jpg", "jpeg"));
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".jpg") && !fileToSave.getName().toLowerCase().endsWith(".jpeg")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".jpg");
            }
            try {
                // Save with different quality levels based on user input
                String[] options = {"Default", "Low Constant", "High Constant", "DC Only"};
                int response = JOptionPane.showOptionDialog(null, "Choose the compression quality:", "Select Quality",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);

                float quality = 0.75f; // Default
                switch (response) {
                    case 1: quality = 0.95f; break; // Low constant
                    case 2: quality = 0.1f; break; // High constant
                    case 3: quality = 0.05f; break; // DC Only
                }

                saveJpegWithQuality(image, fileToSave, quality);
                JOptionPane.showMessageDialog(null, "Image saved successfully at " + fileToSave.getAbsolutePath(), "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to save the image: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void saveJpegWithQuality(BufferedImage image, File outputFile, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (writers.hasNext()) {
            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
                writer.setOutput(ios);
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
            } finally {
                writer.dispose();
            }
        } else {
            throw new IOException("No writers found for JPG format");
        }
    }

    private static JPanel createImagePanel(String title, JLabel imageLabel, boolean isOriginal) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLUE, 2, true), title,
                2, 2, null, Color.BLUE));
        panel.setBackground(new Color(245, 245, 245));

        JPanel controlsPanel = new JPanel();
        controlsPanel.setBackground(new Color(230, 230, 250));
        JButton zoomInButton = new JButton("Zoom in");
        JButton zoomOutButton = new JButton("Zoom out");
        JButton actualSizeButton = new JButton("Actual Size");

        // Style buttons
        styleButton(zoomInButton);
        styleButton(zoomOutButton);
        styleButton(actualSizeButton);

        // Add action listeners for zoom controls
        if (isOriginal) {
            zoomInButton.addActionListener(e -> zoomImage(imageLabel, true, 1.25));
            zoomOutButton.addActionListener(e -> zoomImage(imageLabel, true, 0.8));
            actualSizeButton.addActionListener(e -> resetImageSize(imageLabel, true));
        } else {
            zoomInButton.addActionListener(e -> zoomImage(imageLabel, false, 1.25));
            zoomOutButton.addActionListener(e -> zoomImage(imageLabel, false, 0.8));
            actualSizeButton.addActionListener(e -> resetImageSize(imageLabel, false));
        }

        controlsPanel.add(zoomInButton);
        controlsPanel.add(zoomOutButton);
        controlsPanel.add(actualSizeButton);

        panel.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        panel.add(controlsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(Color.BLUE, 1));
    }

    private static JPanel createMatrixPanel() {
        JPanel matrixPanel = new JPanel();
        matrixPanel.setLayout(new BoxLayout(matrixPanel, BoxLayout.Y_AXIS));
        matrixPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLUE, 2, true),
                "Quantization Matrix", 2, 2, null, Color.BLUE));

        luminanceTable = createMatrixTable();
        chrominanceTable = createMatrixTable();

        matrixPanel.add(new JLabel("Luminance:"));
        matrixPanel.add(new JScrollPane(luminanceTable));

        matrixPanel.add(new JLabel("Chrominance:"));
        matrixPanel.add(new JScrollPane(chrominanceTable));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 230, 250));
        JButton defaultButton = new JButton("Default");
        JButton constantButton = new JButton("Constant");
        JButton dcButton = new JButton("DC only");

        // Style buttons
        styleButton(defaultButton);
        styleButton(constantButton);
        styleButton(dcButton);

        // Add action listeners for quantization buttons
        defaultButton.addActionListener(e -> applyQuantizationMatrix("default"));
        constantButton.addActionListener(e -> applyQuantizationMatrix("constant"));
        dcButton.addActionListener(e -> applyQuantizationMatrix("dc"));

        buttonPanel.add(defaultButton);
        buttonPanel.add(constantButton);
        buttonPanel.add(dcButton);

        matrixPanel.add(buttonPanel);
        return matrixPanel;
    }

    private static JTable createMatrixTable() {
        String[] columnNames = {"", "", "", "", "", "", "", ""};
        Object[][] data = new Object[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                data[i][j] = 0;
            }
        }
        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(176, 224, 230));
        table.setRowHeight(25);
        return table;
    }

    private static void applyQuantizationMatrix(String type) {
        int[][] quantizationMatrix = new int[8][8];

        switch (type) {
            case "default":
                quantizationMatrix = getDefaultMatrix();
                System.out.println("Applied Default Quantization Matrix");
                break;
            case "constant":
                String value = JOptionPane.showInputDialog("Enter a constant value:");
                if (value != null && !value.trim().isEmpty()) {
                    try {
                        int constantValue = Integer.parseInt(value.trim());
                        quantizationMatrix = getConstantMatrix(constantValue);
                        System.out.println("Applied Constant Quantization Matrix with value " + constantValue);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Input was canceled or empty. No changes were made.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case "dc":
                quantizationMatrix = getDCOnlyMatrix();
                System.out.println("Applied DC-only Quantization Matrix");
                break;
        }

        updateMatrixTable(luminanceTable, quantizationMatrix);
        updateMatrixTable(chrominanceTable, quantizationMatrix);
    }

    private static void updateMatrixTable(JTable table, int[][] matrix) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                model.setValueAt(matrix[i][j], i, j);
            }
        }
    }

    private static int[][] getDefaultMatrix() {
        return new int[][]{
                {16, 11, 10, 16, 24, 40, 51, 61},
                {12, 12, 14, 19, 26, 58, 60, 55},
                {14, 13, 16, 24, 40, 57, 69, 56},
                {14, 17, 22, 29, 51, 87, 80, 62},
                {18, 22, 37, 56, 68, 109, 103, 77},
                {24, 35, 55, 64, 81, 104, 113, 92},
                {49, 64, 78, 87, 103, 121, 120, 101},
                {72, 92, 95, 98, 112, 100, 103, 99}
        };
    }

    private static int[][] getConstantMatrix(int value) {
        int[][] matrix = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                matrix[i][j] = value;
            }
        }
        return matrix;
    }

    private static int[][] getDCOnlyMatrix() {
        int[][] matrix = new int[8][8];
        matrix[0][0] = 1; // Set the DC component
        return matrix;
    }

    private static void zoomImage(JLabel imageLabel, boolean isOriginal, double scale) {
        if (isOriginal) {
            originalZoomFactor *= scale;
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            Image image = icon.getImage();
            int newWidth = (int) (originalImageWidth * originalZoomFactor);
            int newHeight = (int) (originalImageHeight * originalZoomFactor);
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            jpegZoomFactor *= scale;
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            Image image = icon.getImage();
            int newWidth = (int) (jpegImageWidth * jpegZoomFactor);
            int newHeight = (int) (jpegImageHeight * jpegZoomFactor);
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        }
        imageLabel.revalidate();
    }

    private static void resetImageSize(JLabel imageLabel, boolean isOriginal) {
        if (isOriginal) {
            originalZoomFactor = 1.0;
            ImageIcon icon = new ImageIcon(originalImage);
            Image scaledImage = icon.getImage().getScaledInstance(originalImageWidth, originalImageHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            jpegZoomFactor = 1.0;
            ImageIcon icon = new ImageIcon(jpegImage);
            Image scaledImage = icon.getImage().getScaledInstance(jpegImageWidth, jpegImageHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        }
        imageLabel.revalidate();
    }
}
