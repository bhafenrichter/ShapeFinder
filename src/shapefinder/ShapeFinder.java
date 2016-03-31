//Program:      Hafenrichter3.java
//Course:       COSC 430
//Description:  Implementation of the brilliant Shape Finder assignment that identifies filled squares, non-squares
//              and circles in a grayscale image.
//Author:       Brandon Hafenrichter
//Revised       3/30/16
//Language:     Java
//IDE:          Netbeans
//**************************************************************************************************************
//**************************************************************************************************************
//Class: ShapeFinder.java
//Description: Hold the core methods used to verify if shape is square, rectangle, circle, or none of the above
package shapefinder;

import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ShapeFinder {

//**************************************************************************************************************
//Method:           Main
//Description:      This method asks the user for the input needed, calculates which regionIds need to be traversed
//                  because they contain a shape and finally verifying what shape the object in that region is
//Parameters:       String[] args   standard procedure for main function of java program
//Returns:          None
    public static void main(String[] args) {
        while(true){
            ShapeFinderModel model = generateModel();
        //set up the regions
        Regions r = new Regions(2, model.rawImage.width, model.rawImage.height, model.rawImage.image, 0, true, 0);
        r.findRegions();
        r.filterRegions(200, 10000, false, 0);
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
            
            //threshold for smaller shapes
            if(pixelCount < 500){continue;}
            
            if(regionId == 74130){
                System.out.println("");
            }
            
            int centroidX = r.centroids[regionId][0];
            int centroidY = r.centroids[regionId][1];
            //printImage(cur);
            
            if(checkForSquare(cur, model, model.tolerance)){
                model.squareCount++;
            }else if(checkForRectangle(cur, model, pixelCount)){
                model.rectangleCount++;
            }else if(checkForCircle(cur, model, centroidX, centroidY, pixelCount, model.circleTolerance)){
                model.circleCount++;
            }
        }
        System.out.println("Circles: " + model.circleCount + ", Squares: " + model.squareCount + ", Rectangles: " + model.rectangleCount);
        
        //draw the shapes
        int[][] gray = model.rawImage.image;
        
        EasyImageDisplay sampleDisplayObject;
        sampleDisplayObject = new EasyImageDisplay(1, model.rawImage.width, model.rawImage.height, model.red, model.green, model.blue, gray);
			sampleDisplayObject.showImage("Image Display Routine",true);
        
            KeyboardInputClass input = new KeyboardInputClass();
            String decision = input.getKeyboardInput("Want to try another image? (Y/N)");
            if(decision.toLowerCase().equals("n") || decision.toLowerCase().equals("no")){
                break;
            }
        }
        System.exit(0);
    }

//**************************************************************************************************************
//Method:       generateModel
//Description:  gets all of the required information from the user in order to run the program
//Parameters:   None
//Returns:      ShapeFinderModel model  this is the model that contains all of the data for the program
    public static ShapeFinderModel generateModel() {
        KeyboardInputClass input = new KeyboardInputClass();
        ShapeFinderModel model = new ShapeFinderModel();

        model.tolerance = 5;
        model.tolerance = input.getInteger(false, 0, 0, 0, "Specify Tolerance for Shape Finder: (Default: 5)");
        if(model.tolerance == 0){model.tolerance = 5;}
        model.circleTolerance = 10;
        String imagePath = input.getKeyboardInput("Specify the name of the file containing the image data: ");
        model.setImage(getImage(imagePath));
        return model;
    }
//**************************************************************************************************************
//Method:       getImage
//Description:  This takes the image path gathered from the generateModel() and finds/parses the image for later use
//Parameters:   String imagePath    path to the image
//Returns:      Image               returns the image in an int[][] along with its width and height
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
//**************************************************************************************************************
//Method:       printImage
//Description:  a debugging method that will print any int[][] onto the screen
//Parameters:   int[][] imageData   the image that needs to be printed
//Returns:      None
    public static void printImage(int[][] imageData) {
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[0].length; j++) {
                System.out.print(imageData[i][j]);
            }
            System.out.println("");
        }
    }
//**************************************************************************************************************
//Method:       getPixelCount
//Description:  This method gathers how many pixels are in the current region.  This is used to compare areas
//              later in verification of certain shapes
//Parameters:   int[][] image   the image that needs to be analyzed
//Returns:      int             the number of pixels in the image
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
//**************************************************************************************************************
//Method:       checkForSquare
//Description:  This method checks the int[][] image to determine whether or not it contains a square
//Parameters:   int[][] image           the image to be checked
//              ShapeFinderModel model  if it is a square, the proper pixels need to be highlighted
//              int tolerance           determines how much of square the image has to be
//Returns:      boolean                 true if it is a square, false otherwise
    private static boolean checkForSquare(int[][] image, ShapeFinderModel model, int tolerance) {
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
//**************************************************************************************************************
//Method:       checkForRectangle
//Description:  This method checks the int[][] image to determine whether or not it contains a rectangle
//Parameters:   int[][] image           the image to be checked
//              ShapeFinderModel model  if it is a square, the proper pixels need to be highlighted
//              int pixelCount          compares the area to the pixel count to verify shape
//Returns:      boolean                 true if it is a square, false otherwise
    private static boolean checkForRectangle(int[][] image, ShapeFinderModel model, int pixelCount) {
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
    
//**************************************************************************************************************
//Method:       checkForCircle
//Description:  This method checks the int[][] image to determine whether or not it contains a circle
//Parameters:   int[][] image           the image to be checked
//              ShapeFinderModel model  if it is a square, the proper pixels need to be highlighted
//              int pixelCount          number of pixels the circle takes up
//              int centroidX,centroidY the centroid coordinates of the potential circle
//              int tolerance           determines how much of circle the image has to be
//Returns:      boolean                 true if it is a square, false otherwise
    private static boolean checkForCircle(int[][] image, ShapeFinderModel model, int centroidX, int centroidY, int pixelCount, int tolerance) {
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
        //check other side
        int otherSideRadius = 0;
        for (int i = 0; i < image.length; i++) {
            if(i + centroidY < image.length && image[centroidX][centroidY - i] != 0){
                otherSideRadius++;
            }else{
                //we've reached the end
                break;
            }
        }
        //check top
        int topRadius = 0;
        for (int i = 0; i < image.length; i++) {
            if(i + centroidY < image.length && image[i + centroidX][centroidY] != 0){
                topRadius++;
            }else{
                //we've reached the end
                break;
            }
        }
        
        //check bottom
        int bottomRadius = 0;
        for (int i = 0; i < image.length; i++) {
            if(i + centroidY < image.length && image[centroidX - i][centroidY] != 0){
                bottomRadius++;
            }else{
                //we've reached the end
                break;
            }
        }
        
        //check diagonals
        int topRightDiagonal = 0;
        for (int i = 0; i < image.length; i++) {
            if(i + centroidY < image.length && image[centroidX + i][centroidY + i] != 0){
                topRightDiagonal++;
            }else{
                //we've reached the end
                break;
            }
        }
        int topLeftDiagonal = 0;
        for (int i = 0; i < image.length; i++) {
            if(i + centroidY < image.length && image[centroidX - i][centroidY + i] != 0){
                topLeftDiagonal++;
            }else{
                //we've reached the end
                break;
            }
        }
        int bottomRightDiagonal = 0;
        for (int i = 0; i < image.length; i++) {
            if(i + centroidY < image.length && image[centroidX + i][centroidY - i] != 0){
                bottomRightDiagonal++;
            }else{
                //we've reached the end
                break;
            }
        }
        int bottomLeftDiagonal = 0;
        for (int i = 0; i < image.length; i++) {
            if(i + centroidY < image.length && image[centroidX - i][centroidY - i] != 0){
                bottomLeftDiagonal++;
            }else{
                //we've reached the end
                break;
            }
        }
        
        int radiusAvg = (radius + topRadius + bottomRadius + otherSideRadius) / 4;
        int diagonalAvg = (topRightDiagonal + topLeftDiagonal + bottomRightDiagonal + bottomLeftDiagonal) / 4;
        int squareArea = (radius * 2) * (radius * 2);
        int lowerRadiusBounds = radiusAvg - tolerance;
        int upperRadiusBounds = radiusAvg + tolerance;
        int lowerDiagonalBounds = diagonalAvg - tolerance;
        int upperDiagonalBounds = diagonalAvg + tolerance;
        
        if(radius >= lowerRadiusBounds && radius <= upperRadiusBounds 
                && topRadius >= lowerRadiusBounds && topRadius <= upperRadiusBounds 
                && bottomRadius >= lowerRadiusBounds && bottomRadius <= upperRadiusBounds 
                && otherSideRadius >= lowerRadiusBounds && otherSideRadius <= upperRadiusBounds 
                && topRightDiagonal >= lowerDiagonalBounds && topRightDiagonal <= upperDiagonalBounds 
                && topLeftDiagonal >= lowerDiagonalBounds && topLeftDiagonal <= upperDiagonalBounds 
                && bottomRightDiagonal >= lowerDiagonalBounds && bottomRightDiagonal <= upperDiagonalBounds 
                && bottomLeftDiagonal >= lowerDiagonalBounds && bottomLeftDiagonal <= upperDiagonalBounds 
                && radiusAvg > 5
                && pixelCount < squareArea
                && diagonalAvg != radiusAvg){
            //fill the circle in
            for (int i = 0; i < image.length; i++) {
                for (int j = 0; j < image[0].length; j++) {
                    if(image[i][j] > 0){
                        model.blue[i][j] = 255;
                    }
                }
            }
            return true;
        }else{
            return false;
        }
    }
}
