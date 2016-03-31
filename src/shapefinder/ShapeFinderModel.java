//**************************************************************************************************************
//Class: ShapeFinderModel.java
//Description: contains all of the vital information to run the Shape Finder program correctly

package shapefinder;

public class ShapeFinderModel {
    Image rawImage;
    Image filteredImage;
    int width; 
    int height;
    int tolerance;
    int circleTolerance;
    int squareCount;
    int rectangleCount;
    int circleCount;
    int[][] red;
    int[][] blue;
    int[][] green;
    
    public ShapeFinderModel(){
        int[][] image = new int[0][0];
        tolerance = 0;
        squareCount = 0;
        rectangleCount = 0;
        circleCount = 0;
        width = 0;
        height = 0;
    }
//**************************************************************************************************************
//Method:           setImage
//Description:      updates the red, blue, and green int[][] to the new image height and width
//Parameters:       Image image     the image with the new dimensions   
//Returns:          None
    
    public void setImage(Image image){
        rawImage = image;
        red = new int[rawImage.height][rawImage.width];
        blue = new int[rawImage.height][rawImage.width];
        green = new int[rawImage.height][rawImage.width];
    }
}
