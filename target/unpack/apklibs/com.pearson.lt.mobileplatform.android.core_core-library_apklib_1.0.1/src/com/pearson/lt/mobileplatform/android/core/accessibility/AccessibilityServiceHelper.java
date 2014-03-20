package com.pearson.lt.mobileplatform.android.core.accessibility;

import java.util.List;

import android.content.Context;
import android.os.Build;

public final class AccessibilityServiceHelper {

    /**
     * Interface for the full API.
     */
	static interface AccessibilityHelper {
		public List<String> getEnabledServices(final Context context);
		public boolean isEnabled(final Context context);
		public boolean isTouchExplorationEnabled(final Context context);
	}
	
    static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			HELPER = new AccessibilityServiceHelperICS();
		} else {
			HELPER = new AccessibilityServiceHelperPreICS();
		}
	}

    /**
     * Select the correct implementation to use for the current platform.
     */
	private static final AccessibilityHelper HELPER;

	/**
	 * Gets a list of enabled accessibility services. 
	 * @param context A Context of the application package.
	 * @return Returns a list of accessibility services that are currently enabled. 
	 */
	public static List<String> getEnabledServices(final Context context) {
    	return HELPER.getEnabledServices(context);
    }
	
	/**
	 * Returns if the accessibility in the system is enabled.
	 * @param context A Context of the application package.
	 * @return True if accessibility is enabled, false otherwise.
	 */
	public static boolean isEnabled(final Context context) {
		return HELPER.isEnabled(context);
	}
	
	/**
	 * Returns if the touch exploration in the system is enabled.
	 * @param context A Context of the application package.
	 * @return True if touch exploration is enabled, false otherwise.
	 */
	public static boolean isTouchExplorationEnabled(final Context context) {
		return HELPER.isTouchExplorationEnabled(context);
	}
	
}
