/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefinder;

import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ShapeFinder {

    public static void main(String[] args) {
        ShapeFinderModel model = generateModel();
        
        //set up the regions
        Regions r = new Regions(2, model.rawImage.width, model.rawImage.height, model.rawImage.image, 0, true, model.tolerance);
        r.findRegions();
        r.filterRegions(100, 10000, false, 0);
        r.computeRegionProperties();
        
        ArrayList<Integer> regionIds = new ArrayList<Integer>();
        
        //find out what regionIDs have shapes
        for (int i = 0; i < r.labeledImage.length; i++) {
            for (int j = 0; j < r.labeledImage[0].length; j++) {
                int cur = r.labeledImage[i][j];
                if(cur > 0 && !regionIds.contains(cur)){
                    regionIds.add(cur);
                    
                }
            }
        }
        
        //iterate through each image and determine what they are
        for (int i = 0; i < regionIds.size(); i++) {
            int regionId = regionIds.get(i);
            int[][] cur = r.getSingleRegion(regionId);
            int pixelCount = getPixelCount(cur);
            
            //printImage(r.centroids);

            int centroidX = r.centroids[regionId][0];
            int centroidY = r.centroids[regionId][1];
            printImage(cur);
            boolean isSquare = checkForSquare(cur, model, pixelCount, model.tolerance);
            boolean isRect = false;
            if(!isSquare){
                isRect = checkForRectangle(cur, model, pixelCount, model.tolerance);
            }
            boolean isCircle = checkForCircle(cur, model, centroidX, centroidY, model.tolerance);
            
            if(isSquare){model.squareCount++;}
            if(isRect){model.rectangleCount++;}
        }
        System.out.println("Circles: " + model.circleCount + ", Squares: " + model.squareCount + ", Rectangles: " + model.rectangleCount);
        
        //draw the shapes
        int[][] gray = model.rawImage.image;
        
        EasyImageDisplay sampleDisplayObject;
        sampleDisplayObject = new EasyImageDisplay(1, model.rawImage.width, model.rawImage.height, model.red, model.green, model.blue, gray);
			sampleDisplayObject.showImage("Image Display Routine",true);
    }

    public static ShapeFinderModel generateModel() {
        KeyboardInputClass input = new KeyboardInputClass();
        ShapeFinderModel model = new ShapeFinderModel();

        //model.tolerance = input.getInteger(false, 0, 0, 0, "Specify Tolerance for Shape Finder: (Ex. 4)");
        model.tolerance = 0;
        //String imagePath = input.getKeyboardInput("Specify the name of the file containing the image data: ");
        String imagePath = "lpg2";
        model.setImage(getImage(imagePath));
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
                System.out.print(imageData[i][j]);
            }
            System.out.println("");
        }
    }

    public static int getPixelCount(int[][] image){
        int count = 0;
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                if(image[i][j] > 0){
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private static boolean checkForSquare(int[][] image, ShapeFinderModel model, int pixelCount, int tolerance) {
        int squareWidth = 0;
        int squareHeight = 0;
        int i = 0; 
        int j = 0;
        
        
        //find where the shape is
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[0].length; y++) {
                if(image[x][y] > 0){
                    i = x;
                    j = y;
                    break;
                }
            }
            if(i != 0 && j != 0){ break; }
        }
        
        int tempI = i;
        int tempJ = j;
        //compute width of square
        while (image[i][j] > 0) {
            squareWidth++;
            i++;
        }
        i = tempI;
        while(image[i][j] > 0){
            squareHeight++;
            j++;
        }
        j = tempJ;
        if(squareWidth + tolerance >= squareHeight && squareWidth - tolerance <= squareHeight){
            //highlight the values on the array
            for (int k = 0; k < squareHeight; k++) {
                for (int l = 0; l < squareWidth; l++) {
                    model.red[k + i][l + j] = 255;
                }
            }
            return true;
        }else{
            return false;
        }
    }

    private static boolean checkForRectangle(int[][] image, ShapeFinderModel model, int pixelCount, int tolerance) {
        int width = 0;
        int height = 0;
        int i = 0; 
        int j = 0;
        
        //find where the shape is
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[0].length; y++) {
                if(image[x][y] > 0){
                    i = x;
                    j = y;
                    break;
                }
            }
            if(i != 0 && j != 0){ break; }
        }
        
        int tempI = i;
        int tempJ = j;
        
        //compute dimensions of rectangle
        while (image[i][j] > 0) {
            height++;
            i++;
        }
        
        i = tempI;
        
        while(image[i][j] > 0){
            width++;
            j++;
        }
        
        j = tempJ;
        
        //iterate through the dimensions of the rectangle and see if there are any points at which it isn't filled
        int searchCount = 0;
        for (int k = 0; k < height; k++) {
            for (int l = 0; l < width; l++) {
                if(image[k + i][l + j] == 0){
                    return false;
                }
                searchCount++;
            }
        }
        if (searchCount == pixelCount) {
            //highlight the effected pixels
            for (int k = 0; k < height; k++) {
                for (int l = 0; l < width; l++) {
                    model.green[k + i][l + j] = 255;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private static boolean checkForCircle(int[][] image, ShapeFinderModel model, int centroidX, int centroidY, int tolerance) {
        //coordinate of first top of circle
//        int x1 = 0; 
//        int y1 = 0;
//        
//        //find where the shapes top is
//        for (int x = 0; x < image.length; x++) {
//            for (int y = 0; y < image[0].length; y++) {
//                if(image[x][y] > 0){
//                    x1 = x;
//                    y1 = y;
//                    break;
//                }
//            }
//            if(x1 != 0 && y1 != 0){ break; }
//        }
//        
//        //coordinate of the bottom of the circle
//        int x2 = 0;
//        int y2 = 0;
//        //find out where the shapes bottom is to compute diameter
//        for (int x = 0; x < image.length; x++) {
//            for (int y = 0; y < image[0].length; y++) {
//                if(x > 0 && image[x][y] == 0 && image[x-1][y] > 0){
//                    x2 = x;
//                    y2 = y;
//                    break;
//                }
//            }
//            if(x2 != 0 && y2 != 0){ break; }
//        }
//        
//        int radius = (x2 - x1) / 2;
//        double area = Math.PI * Math.pow(radius, 2);

        //calculate the radius
        int radius = 0;
        for (int i = 0; i < image.length; i++) {
            if(i + centroidY < image.length && image[centroidX][i + centroidY] != 0){
                radius++;
            }else{
                //we've reached the end
                break;
            }
        }
        
        return false;
    }
}
