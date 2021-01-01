package uninsubria.client.gui;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum class representing the supported aspect ratios and references for GUI components scaling.
 * Should cover 90-95% of common resolutions for monitors.
 * 
 * @author Giulia Pais
 * @version 0.9.8
 */
public enum AspectRatio {
	/*---Enum constants---*/
	RATIO_4_3(4.0 / 3.0, 
				Arrays.asList(800.0, 1024.0, 1400.0, 1600.0),
				Arrays.asList(600.0, 768.0, 1050.0, 1200.0),
				Stream.of(new AbstractMap.SimpleImmutableEntry<>("REF_RESOLUTION", 1024.0),
						new AbstractMap.SimpleImmutableEntry<>("LOGO_DIM", 220.0),
						new AbstractMap.SimpleImmutableEntry<>("RECT_WIDTH", 320.0),
						new AbstractMap.SimpleImmutableEntry<>("RECT_HEIGHT", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("MENU_MARGIN_L", 352.0),
						new AbstractMap.SimpleImmutableEntry<>("MENU_MARGIN_T", 104.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_W", 240.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_H", 280.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_MARGIN_B", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_SPACING", 20.0),
						new AbstractMap.SimpleImmutableEntry<>("FONT_SIZE", 14.0),
						new AbstractMap.SimpleImmutableEntry<>("FONT_SIZE_TITLE", 18.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TOOL_W", 300.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TOOL_H", 603.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TITLE_H", 75.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_BTNBAR_H", 90.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_BTN_H", 90.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_CBOX_W", 170.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_CBOX_H", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TOGGLE_DIM", 50.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_LIST_W", 300.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TXTFIELD_W", 180.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TXTFIELD_H", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_TXT_FIELD_W", 250.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_TXT_FIELD_H", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_SPACING", 15.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_PADDING_TOP", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_ICON_DIM", 75.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_ICON_SIZE", 60.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_BTN_H", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_BTN_SPACING", 14.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_BTN_ARROW_SIZE", 20.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_RECT_H", 550.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_RECT_W", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_ICON_DIM", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_ICON_SIZE", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_SPACING", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_PADDING_TOP", 15.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_TXT_FIELD_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_TXT_FIELD_H", 25.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_BTN_SPACING", 10.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_IMG_DIM", 100.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_H", 110.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_W", 235.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_PAD", 60.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_SPACING", 15.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_TAB_MIN_H", 50.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_ICONS_SIZE", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_ROOM_BTN_H", 45.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_ROOM_BTN_W", 120.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY_W", 250.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY_H", 80.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY2_W", 130.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY2_H", 90.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY3_W", 70.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY3_H", 70.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PSTATSTABLE_H", 80.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PSTATSTABLE_W", 180.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_TURNSCARDTBL_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_TURNSCARDTBL_H", 160.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_WPTABLE_W", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_GRAPH_H", 420.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_GRAPH_W", 620.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_HEADER_H", 95.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_GRID_DIM", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_FOUNDW_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_SIDEPANEL_W", 250.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_BTN_DIM", 100.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_BTN_DIM", 150.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_TILE_H", 500.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_TILE_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_TITLE_SIZE", 30.0))
				  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))), 
	RATIO_16_9(16.0 / 9.0, 
				Arrays.asList(1280.0, 1366.0, 1920.0),
				Arrays.asList(720.0, 768.0, 1080.0),
				Stream.of(new AbstractMap.SimpleImmutableEntry<>("REF_RESOLUTION", 1280.0),
						new AbstractMap.SimpleImmutableEntry<>("LOGO_DIM", 220.0),
						new AbstractMap.SimpleImmutableEntry<>("RECT_WIDTH", 320.0),
						new AbstractMap.SimpleImmutableEntry<>("RECT_HEIGHT", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("MENU_MARGIN_L", 480.0),
						new AbstractMap.SimpleImmutableEntry<>("MENU_MARGIN_T", 80.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_W", 240.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_H", 280.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_MARGIN_B", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_SPACING", 20.0),
						new AbstractMap.SimpleImmutableEntry<>("FONT_SIZE", 14.0),
						new AbstractMap.SimpleImmutableEntry<>("FONT_SIZE_TITLE", 18.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TOOL_W", 300.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TOOL_H", 555.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TITLE_H", 75.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_BTNBAR_H", 90.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_BTN_H", 80.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_CBOX_W", 170.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_CBOX_H", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TOGGLE_DIM", 50.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_LIST_W", 300.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TXTFIELD_W", 200.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TXTFIELD_H", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_TXT_FIELD_W", 250.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_TXT_FIELD_H", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_SPACING", 15.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_PADDING_TOP", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_ICON_DIM", 75.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_ICON_SIZE", 60.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_BTN_H", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_BTN_SPACING", 14.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_BTN_ARROW_SIZE", 20.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_RECT_H", 550.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_RECT_W", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_ICON_DIM", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_ICON_SIZE", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_SPACING", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_PADDING_TOP", 15.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_TXT_FIELD_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_TXT_FIELD_H", 25.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_BTN_SPACING", 10.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_IMG_DIM", 100.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_H", 110.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_W", 235.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_PAD", 60.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_SPACING", 15.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_TAB_MIN_H", 50.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_ICONS_SIZE", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_ROOM_BTN_H", 45.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_ROOM_BTN_W", 120.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY_W", 250.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY_H", 80.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY2_W", 130.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY2_H", 90.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY3_W", 70.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY3_H", 70.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PSTATSTABLE_H", 80.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PSTATSTABLE_W", 180.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_TURNSCARDTBL_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_TURNSCARDTBL_H", 160.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_WPTABLE_W", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_GRAPH_H", 420.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_GRAPH_W", 620.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_HEADER_H", 95.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_GRID_DIM", 550.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_FOUNDW_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_SIDEPANEL_W", 320.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_BTN_DIM", 100.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_BTN_DIM", 150.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_TILE_H", 500.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_TILE_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_TITLE_SIZE", 30.0))
				  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))), 
	RATIO_16_10(16.0 / 10.0, 
				Arrays.asList(1440.0, 1680.0, 1920.0),
				Arrays.asList(900.0, 1050.0, 1200.0),
				Stream.of(new AbstractMap.SimpleImmutableEntry<>("REF_RESOLUTION", 1200.0),
						new AbstractMap.SimpleImmutableEntry<>("LOGO_DIM", 220.0),
						new AbstractMap.SimpleImmutableEntry<>("RECT_WIDTH", 320.0),
						new AbstractMap.SimpleImmutableEntry<>("RECT_HEIGHT", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("MENU_MARGIN_L", 440.0),
						new AbstractMap.SimpleImmutableEntry<>("MENU_MARGIN_T", 95.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_W", 240.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_H", 280.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_MARGIN_B", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("VBOX_SPACING", 20.0),
						new AbstractMap.SimpleImmutableEntry<>("FONT_SIZE", 14.0),
						new AbstractMap.SimpleImmutableEntry<>("FONT_SIZE_TITLE", 18.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TOOL_W", 300.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TOOL_H", 585.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TITLE_H", 75.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_BTNBAR_H", 90.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_BTN_H", 90.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_CBOX_W", 170.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_CBOX_H", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TOGGLE_DIM", 50.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_LIST_W", 300.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TXTFIELD_W", 200.0),
						new AbstractMap.SimpleImmutableEntry<>("OPT_TXTFIELD_H", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_TXT_FIELD_W", 250.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_TXT_FIELD_H", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_SPACING", 15.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_PADDING_TOP", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_ICON_DIM", 75.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_ICON_SIZE", 60.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_BTN_H", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_BTN_SPACING", 14.0),
						new AbstractMap.SimpleImmutableEntry<>("LOG_BTN_ARROW_SIZE", 20.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_RECT_H", 550.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_RECT_W", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_ICON_DIM", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_ICON_SIZE", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_SPACING", 40.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_PADDING_TOP", 15.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_TXT_FIELD_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_TXT_FIELD_H", 25.0),
						new AbstractMap.SimpleImmutableEntry<>("REG_BTN_SPACING", 10.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_IMG_DIM", 100.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_H", 110.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_W", 235.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_PAD", 60.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PROFILE_INFO_SPACING", 15.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_TAB_MIN_H", 50.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_ICONS_SIZE", 30.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_ROOM_BTN_H", 45.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_ROOM_BTN_W", 120.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY_W", 250.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY_H", 80.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY2_W", 130.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY2_H", 90.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY3_W", 70.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_MASONRY3_H", 70.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PSTATSTABLE_H", 80.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_PSTATSTABLE_W", 180.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_TURNSCARDTBL_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_TURNSCARDTBL_H", 160.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_WPTABLE_W", 450.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_GRAPH_H", 420.0),
						new AbstractMap.SimpleImmutableEntry<>("HOME_GRAPH_W", 620.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_HEADER_H", 95.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_GRID_DIM", 500.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_FOUNDW_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_SIDEPANEL_W", 300.0),
						new AbstractMap.SimpleImmutableEntry<>("MATCH_BTN_DIM", 100.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_BTN_DIM", 150.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_TILE_H", 500.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_TILE_W", 350.0),
						new AbstractMap.SimpleImmutableEntry<>("SCORES_TITLE_SIZE", 30.0))
				  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
	
	/*---Fields---*/
	/**
	 * The aspect ratio as a double value.
	 */
	private double ratio;
	/**
	 * The range of supported width for this aspect ratio.
	 */
	private List<Double> widthRange;
	/**
	 * The range of supported height for this aspect ratio.
	 */
	private List<Double> heightRange;
	/**
	 * A map containing references for correct GUI scaling upon resizing.
	 */
	private Map<String, Double> references;
	
	/*---Constructor---*/
	/**
	 * Builds an object of type AspectRatio.
	 * @param ratio The aspect ratio value, defined as width/height
	 * @param widths The range of supported widths for this aspect ratio
	 * @param heights The range of supported heights for this aspect ratio
	 * @param refs A map of reference values for GUI components scaling
	 */
	AspectRatio(double ratio, List<Double> widths, List<Double> heights, Map<String, Double> refs) {
		this.ratio = ratio;
		this.widthRange = widths;
		this.heightRange = heights;
		this.references = refs;
	}
	
	/*---Methods---*/
	/**
	 * Returns an AspectRatio constant by specifying the associated double value.
	 * @param ratio The aspect ratio value
	 * @return An AspectRatio object
	 */
	public static AspectRatio getByRatio(double ratio) {
		for (AspectRatio a : values()) {
			if (Math.abs(a.ratio - ratio) < 0.01) {
				return a;
			}
		}
		return null;
	}

	/**
	 * @return The value of ratio
	 */
	public double getRatio() {
		return ratio;
	}

	/**
	 * 
	 * @return The list of widths supported
	 */
	public List<Double> getWidthRange() {
		return widthRange;
	}

	/**
	 * 
	 * @return The list of heights supported
	 */
	public List<Double> getHeightRange() {
		return heightRange;
	}

	/**
	 * 
	 * @return The reference map
	 */
	public Map<String, Double> getReferences() {
		return references;
	}
	
	/**
	 * Returns a list of resolutions for this aspect ratio as arrays of 2 (width, height).
	 * @return A list of resolutions as arrays.
	 */
	public List<Double[]> getResolutions() {
		Double[] entry;
		List<Double[]> entries = new ArrayList<>(widthRange.size());
		for (int index = 0; index < widthRange.size(); index++) {
			entry = new Double[2];
			entry[0] = widthRange.get(index);
			entry[1] = heightRange.get(index);
			entries.add(entry);
		}
		return entries;
	}
	
	/**
	 * Finds the closest value of width in the range based on the input value.
	 * @param width The value to search for
	 * @return A width value
	 */
	public Double findClosestWidth(Double width) {
		int index = Collections.binarySearch(this.widthRange, width);
		if (index < 0) {
			if (index == -1) {
				index = Math.abs(index + 1);
			} else {
				index = Math.abs(index + 2);
			}
		} 
		return this.widthRange.get(index);
    } 
	
}
