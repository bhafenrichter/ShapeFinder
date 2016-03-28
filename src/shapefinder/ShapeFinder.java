/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefinder;

import java.io.RandomAccessFile;

public class ShapeFinder {

    public static void main(String[] args) {
        ShapeFinderModel model = generateModel();

        for (int i = 0; i < model.filteredImage.image.length; i++) {
            for (int j = 0; j < model.filteredImage.image[0].length; j++) {
                if (model.filteredImage.image[i][j] > 0) {
                    checkForSquare(model.filteredImage.image, i, j, model.tolerance);
                    checkForRectangle(model.filteredImage.image, i, j, model.tolerance);
                    checkForCircle(model.filteredImage.image, i, j, model.tolerance);
                }

            }
        }
        printImage(model.filteredImage.image);
    }

    public static ShapeFinderModel generateModel() {
        KeyboardInputClass input = new KeyboardInputClass();
        ShapeFinderModel model = new ShapeFinderModel();

        //model.tolerance = input.getInteger(false, 0, 0, 0, "Specify Tolerance for Shape Finder: (Ex. 4)");
        model.tolerance = 0;
        //String imagePath = input.getKeyboardInput("Specify the name of the file containing the image data: ");
        String imagePath = "lpg2";
        model.rawImage = getImage(imagePath);

        Regions r = new Regions(2, model.rawImage.width, model.rawImage.height, model.rawImage.image, 0, true, model.tolerance);
        model.filteredImage = new Image(r.getSingleRegion(0), model.rawImage.width, model.rawImage.height);

        return model;
    }

    private static Image getImage(String imagePath) {
        KeyboardInputClass input = new KeyboardInputClass();
        String userInput = "";
        int row, column;
        Image image = new Image();
        if (imagePath.length() > 0) {
            try {
                RandomAccessFile imageFile = new RandomAccessFile(imagePath, "r");
                int start = 0;
                imageFile.seek(start);					//move pointer to beginning of file
                //Read the file type, rows, and columns (stored as integers). This requires reading
                //four bytes per value. These bytes represent an integer stored by C++ or Basic
                //(i.e., in low byte to high byte order (not reversed bit order!)). The routine
                //converts to a Java integer representation (i.e., high byte to low byte order).
                char c1 = (char) imageFile.read();
                char c2 = (char) imageFile.read();
                char c3 = (char) imageFile.read();
                char c4 = (char) imageFile.read();
                int imageType = (c4 << 24) | (c3 << 16) | (c2 << 8) | c1;
                if ((imageType != 1) && (imageType != 2) && (imageType != 3)) {
                    userInput = input.getKeyboardInput("Bad file type. Press ENTER to continue...");
                    System.exit(0);
                }
                c1 = (char) imageFile.read();
                c2 = (char) imageFile.read();
                c3 = (char) imageFile.read();
                c4 = (char) imageFile.read();
                int width = (c4 << 24) | (c3 << 16) | (c2 << 8) | c1;
                c1 = (char) imageFile.read();
                c2 = (char) imageFile.read();
                c3 = (char) imageFile.read();
                c4 = (char) imageFile.read();
                int height = (c4 << 24) | (c3 << 16) | (c2 << 8) | c1;
                //set up color or grayscale array(s)
                int[][] gray = new int[height][width];

                for (row = 0; row < height; row++) {
                    for (column = 0; column < width; column++) {
                        if (imageType == 1) {			//color
                            input.getKeyboardInput("Not greyscale image. Press ENTER to continue...");
                        } else if (imageType == 2) //grayscale
                        {
                            gray[row][column] = (char) imageFile.read();
                        }
                    }
                }
                imageFile.close();
                image.image = gray;
                image.width = width;
                image.height = height;
            } catch (Exception e) {
                userInput = input.getKeyboardInput("Error trying to read file. Press ENTER to continue...");
                System.exit(0);
            }
        }
        return image;
    }

    public static void printImage(int[][] imageData) {
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[0].length; j++) {
                System.out.print(imageData[i][j] + " ");
            }
            System.out.println("");
        }
    }

    private static boolean checkForSquare(int[][] image, int i, int j, int tolerance) {
        int squareWidth = 0;
        int squareHeight = 0;

        //compute width of square
        while (image[i][j] > 0) {
            squareWidth++;
            i++;
        }
        
        while(image[i][j] > 0){
            squareHeight++;
            j++;
        }

        if(squareWidth == squareHeight){
            return true;
        }else{
            return false;
        }
    }

    private static boolean checkForRectangle(int[][] image, int i, int j, int tolerance) {
        return false;
    }

    private static boolean checkForCircle(int[][] image, int i, int j, int tolerance) {
        return false;
    }
}
