//**************************************************************************************************************
//Class: Image.java
//Description: An image class that contains the height, width, centroid, and image data to be used later

package shapefinder;

/**
 *
 * @author Brandon
 */
public class Image {

    int[][] image;
    int width;
    int height;
    int[][] centroid;
    public Image() {

    }

    public Image(int[][] image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }
}
