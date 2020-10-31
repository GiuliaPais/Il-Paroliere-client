/**
 * 
 */
package uninsubria.client.gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author Giulia Pais
 *
 */
class AspectRatioTest {

	@Test
	void testfindClosestWidthLeftLimit() {
		Double closest = AspectRatio.RATIO_4_3.findClosestWidth(600.0);
		assertEquals(closest, 800.0);
		closest = AspectRatio.RATIO_16_9.findClosestWidth(600.0);
		assertEquals(closest, 1280.0);
		closest = AspectRatio.RATIO_16_10.findClosestWidth(600.0);
		assertEquals(closest, 1440.0);
	}
	
	@Test
	void testfindClosestWidthRightLimit() {
		Double closest = AspectRatio.RATIO_4_3.findClosestWidth(2000.0);
		assertEquals(closest, 1600.0);
		closest = AspectRatio.RATIO_16_9.findClosestWidth(2000.0);
		assertEquals(closest, 1920.0);
		closest = AspectRatio.RATIO_16_10.findClosestWidth(2000.0);
		assertEquals(closest, 1920.0);
	}
	
	@Test
	void testfindClosestWidthInRange() {
		Double closest = AspectRatio.RATIO_4_3.findClosestWidth(1200.0);
		assertEquals(closest, 1024.0);
		closest = AspectRatio.RATIO_4_3.findClosestWidth(1400.0);
		assertEquals(closest, 1400.0);
		closest = AspectRatio.RATIO_16_9.findClosestWidth(1300.0);
		assertEquals(closest, 1280.0);
		closest = AspectRatio.RATIO_16_9.findClosestWidth(1920.0);
		assertEquals(closest, 1920.0);
		closest = AspectRatio.RATIO_16_10.findClosestWidth(1700.0);
		assertEquals(closest, 1680.0);
		closest = AspectRatio.RATIO_16_10.findClosestWidth(1920.0);
		assertEquals(closest, 1920.0);
	}

}
