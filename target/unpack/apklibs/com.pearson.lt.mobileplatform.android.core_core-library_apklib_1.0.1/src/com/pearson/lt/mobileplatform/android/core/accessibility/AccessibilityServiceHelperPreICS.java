package com.pearson.lt.mobileplatform.android.core.accessibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;

import com.pearson.lt.mobileplatform.android.core.accessibility.AccessibilityServiceHelper.AccessibilityHelper;

/**
 * Interface implementation that doesn't use APIs available in Ice Cream Sandwich and higher.
 */
class AccessibilityServiceHelperPreICS implements AccessibilityHelper {

    @Override
    public List<String> getEnabledServices(final Context context) {
    	final AccessibilityManager accessibilityManager =
		        (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		final PackageManager manager = context.getPackageManager();
		
		List<String> serviceList = new ArrayList<String>();
        if (accessibilityManager == null) {
            return serviceList;
        }
		
    	ContentResolver resolver = context.getContentResolver();
		boolean enabled = Secure.getInt(resolver, Secure.ACCESSIBILITY_ENABLED, 0) != 0;
		if (enabled) {
			// Running Services
			String services = Secure.getString(resolver, Secure.ENABLED_ACCESSIBILITY_SERVICES);
			System.out.println("services = " + services);
			List<String> enabledServices = new ArrayList<String>();
			if (!TextUtils.isEmpty(services)) {
				enabledServices = Arrays.asList(services.split(":"));
				// need sort to limit set of possible parameter values
				// we don't want TalkBack:KickBack AND KickBack:TalkBack values
				Collections.sort(enabledServices); 
			}

			@SuppressWarnings("deprecation")
			List<ServiceInfo> installedServices = accessibilityManager.getAccessibilityServiceList();
			for (ServiceInfo service : installedServices) {
				String enabledService = service.packageName + "/" + service.name;
				if (enabledServices.contains(enabledService)) {
					String label = service.applicationInfo.loadLabel(manager).toString();
					if (!serviceList.contains(label))
						serviceList.add(label);
				}
			}
		}
		
		return serviceList;
    }

	@Override
	public boolean isEnabled(final Context context) {
		ContentResolver resolver = context.getContentResolver();
		boolean enabled = Secure.getInt(resolver, Secure.ACCESSIBILITY_ENABLED, 0) != 0;
		return enabled;
	}

	@Override
	public boolean isTouchExplorationEnabled(final Context context) {
		// only available in API 14+
		return false;
	}
}