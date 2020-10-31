/**
 * 
 */
package uninsubria.client.guicontrollers;

/**
 * Enum class for application graphic themes. Each constant holds a reference to the appropriate css file.
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public enum Theme {
	/*---Enum constants---*/
	NIGHT_SKY("/css/night_sky.css");
	
    
	/*---Constructor---*/
	/**
	 * Builds an object of type Theme
	 * @param sheetPath The path to the css file
	 */
	Theme(String sheetPath) {
		this.styleSheet = sheetPath;
	}

	/*---Fields---*/
	/**
	 * The path to the css file
	 */
	private String styleSheet;	

	/*---Methods---*/
	/**
	 * 
	 * @return The value of styleSheet
	 */
	public String getFilePath() {
		return this.styleSheet;
	}
}
