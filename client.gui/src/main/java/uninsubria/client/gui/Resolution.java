/**
 * 
 */
package uninsubria.client.gui;

/**
 * Class representing a screen resolution with aspect ratio, width and height.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public class Resolution {
	/*---Fields---*/
	/**
	 * The aspect ratio as double value (defined as width/height)
	 */
	private Double aspectRatio;
	/**
	 * Array representing the dimensions, position 0 is associated with width and position 1 is associated with height.
	 */
	private Double[] widthHeight;
	
	/*---Constructors---*/
	/**
	 * Builds an object of type Resolution.
	 * @param ar The aspect ratio value
	 * @param width The width value
	 */
	public Resolution(Double ar, Double width) {
		this.widthHeight = new Double[2];
		this.aspectRatio = ar;
		this.widthHeight[0] = width;
		this.widthHeight[1] = width/ar;
	}
	
	/**
	 * Builds an object of type Resolution.
	 * @param width_height An array with screen dimensions
	 */
	public Resolution(Double[] width_height) {
		this.widthHeight = new Double[2];
		this.widthHeight = width_height;
		this.aspectRatio = widthHeight[0] / widthHeight[1];
	}
	
	/*---Methods---*/
	/**
	 * 
	 * @return The value of aspectRatio
	 */
	public Double getAspectRatio() {
		return aspectRatio;
	}
	
	/**
	 * 
	 * @return The value of widthHeight
	 */
	public Double[] getWidthHeight() {
		return widthHeight;
	}

	@Override
	public String toString() {
		String s = this.widthHeight[0] + " x " + this.widthHeight[1];
		return s;
	}
	
	

}
