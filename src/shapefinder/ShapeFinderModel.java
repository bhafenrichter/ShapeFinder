/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefinder;

/**
 *
 * @author Brandon
 */
public class ShapeFinderModel {
    Image rawImage;
    Image filteredImage;
    int width; 
    int height;
    int tolerance;
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
    
    public void setImage(Image image){
        rawImage = image;
        red = new int[rawImage.height][rawImage.width];
        blue = new int[rawImage.height][rawImage.width];
        green = new int[rawImage.height][rawImage.width];
    }
}
