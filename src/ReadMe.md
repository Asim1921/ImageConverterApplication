# Image Converter Application

This application is designed to convert and manipulate images using different quantization matrices and compression qualities. It provides a graphical user interface (GUI) to view and modify images, apply quantization matrices, and save the images with custom compression settings.

## Features

- **Image Loading:** Load and display original and converted images.
- **Zoom Functionality:** Zoom in, zoom out, and reset the image to its actual size.
- **Quantization Matrix Application:** Apply default, constant, or DC-only quantization matrices.
- **Save with Compression:** Save images with different JPEG compression qualities.

## Requirements

- **Java Development Kit (JDK) 8 or higher**
- **IntelliJ IDEA or any other Java IDE**

## Setup Instructions

### 1. Clone or Download the Repository

Download the repository and open it in your preferred Java IDE.

### 2. Set Up the Project

1. **Ensure JDK is installed:**
    - The project requires JDK 8 or higher. Ensure that your IDE is configured to use the correct JDK.

2. **Add Required Libraries:**
    - This project relies on Java's built-in `ImageIO` library, so no additional libraries are required.

### 3. Running the Application

1. **Open the `Main.java` file** in your IDE.
2. **Run the `Main` class** to start the application.

### 4. Using the Application

1. **Loading Images:**
    - The application loads the original image from `src/flower.bmp`.
    - The converted image is loaded from `src/flower_compressed.jpg`.

2. **Viewing Images:**
    - The GUI displays the original and converted images side by side.
    - Use the zoom controls below each image to zoom in, zoom out, or reset to the actual size.

3. **Applying Quantization Matrices:**
    - Select a quantization matrix from the options: Default, Constant, or DC Only.
    - The matrix values will be applied to the image, though the visual effect may not be directly observable in this basic implementation.

4. **Saving the Image:**
    - Click on the "File" menu and choose "Save As..." to save the converted image.
    - Choose from the available compression quality options: Default, Low Constant, High Constant, or DC Only.
    - The image will be saved as a JPEG file with the selected compression quality.

### 5. Saving with Custom JPEG Compression

The application allows you to save the image with a specific compression quality. The compression quality affects the image file size and quality:

- **Default:** Balanced quality and file size.
- **Low Constant:** Higher quality, larger file size.
- **High Constant:** Lower quality, smaller file size.
- **DC Only:** Minimal quality, smallest file size.

### 6. Discussion on Image Compression

JPEG compression reduces the file size by discarding some of the image's data, which may result in quality loss. The different quality settings demonstrate how varying the compression factor impacts the resulting image. The `DC Only` option shows an extreme case with very high compression and significant quality loss.

## Conclusion

This project demonstrates how to load, manipulate, and save images with different compression qualities using Java. The GUI provides a straightforward way to interact with the image processing features.

