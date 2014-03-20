package com.pearson.lt.mobileplatform.android.core.accessibility;

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;

import com.pearson.lt.mobileplatform.android.core.accessibility.AccessibilityServiceHelper.AccessibilityHelper;

/**
 * Interface implementation for devices with at least v14 APIs.
 */
class AccessibilityServiceHelperICS implements AccessibilityHelper {

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public List<String> getEnabledServices(final Context context) {
		final AccessibilityManager accessibilityManager =
		        (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		final PackageManager manager = context.getPackageManager();
		
		List<String> serviceList = new ArrayList<String>();

        if (accessibilityManager == null) {
            return serviceList;
        }
		
		List<AccessibilityServiceInfo> installedServices = accessibilityManager
				.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
		for (AccessibilityServiceInfo service : installedServices) {
			ResolveInfo resolveInfo = service.getResolveInfo();

			if (resolveInfo == null) {
				serviceList.add("unknown");
				continue;
			}

			String label = resolveInfo.loadLabel(manager).toString();
			if (!serviceList.contains(label)) {
				serviceList.add(label);
			}
		}
		
		return serviceList;
    }
	
	@Override
	public boolean isEnabled(final Context context) {
		final AccessibilityManager accessibilityManager =
		        (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		return (accessibilityManager == null ? false : accessibilityManager.isEnabled());
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
	public boolean isTouchExplorationEnabled(final Context context) {
		final AccessibilityManager accessibilityManager =
		        (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		return (accessibilityManager == null ? false : accessibilityManager.isTouchExplorationEnabled());
	}
}