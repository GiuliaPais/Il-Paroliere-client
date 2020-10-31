/**
 * 
 */
package uninsubria.client.gui;

import java.util.ResourceBundle;

/**
 * Common interface for GUI controllers.
 * 
 * @author Giulia Pais
 * @version 0.9.0
 *
 */
public interface Controller {
	/**
	 * Sets the correct values for localized text resources in the controller.
	 * @param resBundle The ResourceBundle for the chosen language
	 */
	public void setTextResources(ResourceBundle resBundle);
}
