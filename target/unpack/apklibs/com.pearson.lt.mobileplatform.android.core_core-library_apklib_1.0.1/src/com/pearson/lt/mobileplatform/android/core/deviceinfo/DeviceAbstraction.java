package com.pearson.lt.mobileplatform.android.core.deviceinfo;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.WindowManager;

import com.pearson.lt.mobileplatform.android.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.pearson.lt.mobileplatform.android.core.accessibility.AccessibilityServiceHelper;
import com.pearson.lt.mobileplatform.android.core.json.JsonUtils;

public final class DeviceAbstraction {

	private static final int QUERY_COMPLETE = 1;

	public interface DeviceAbstractionQueryCallback {
		public void onQueryFinished(final DeviceAbstraction device);
	}

    private static final SparseArray<String> DENSITY_BUCKETS = new SparseArray<String>() {{
        append(DisplayMetrics.DENSITY_LOW, "ldpi");
        append(DisplayMetrics.DENSITY_MEDIUM, "mdpi");
        append(DisplayMetrics.DENSITY_HIGH, "hdpi");
        append(DisplayMetrics.DENSITY_XHIGH, "xhdpi");
        append(DisplayMetrics.DENSITY_XXHIGH, "xxhpi");
        append(DisplayMetrics.DENSITY_XXXHIGH, "xxxhpi");
        append(DisplayMetrics.DENSITY_TV, "tv");
    }};

    // device data
	private String deviceVendor = Build.MANUFACTURER;
	private String deviceModel = Build.MODEL;
	private String deviceOSName = Build.VERSION.RELEASE;
	private int deviceOSVersion = Build.VERSION.SDK_INT;
	private String deviceResolution;
	private String deviceDensity;

	// service Provider
	private String serviceProvider;
	private String serviceProviderCountry;

	// application data
	private String appPackage;
	private String appName;
	private String appVersion;
	private int appBuild;

	// network type
	private String networkType;

	// localization
	private String localeSetting;
	private String languagePreference;

	// accessibility
	private String[] enabledAccessibilityServices = {};

	public DeviceAbstraction() {
	}

	public DeviceAbstraction(final Context context) {
		queryDeviceAbstraction(context);
	}

	@JsonIgnore
	public static void startDeviceAbstractionQuery(final Context context, final DeviceAbstractionQueryCallback callback) {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message message) {
				if (message.what == DeviceAbstraction.QUERY_COMPLETE) {
					DeviceAbstraction device = (DeviceAbstraction) message.obj;
					if (callback != null)
						callback.onQueryFinished(device);
				}

			}
		};
		final Thread thread = new Thread() {

			@Override
			public void run() {
				DeviceAbstraction device = new DeviceAbstraction(context);

				Message message = Message.obtain();
				message.what = DeviceAbstraction.QUERY_COMPLETE;
				message.obj = device;

				handler.sendMessage(message);
			}

		};
		thread.start();
	}

	@JsonIgnore
	public void queryDeviceAbstraction(final Context context) {
		// get data associated with the device's display
		setDisplayDataProperties(context);
		setServiceProviderDataProperties(context);
		setApplicationDataProperties(context);
		setNetworkDataProperties(context);
		setLocalizationDataProperties(context);
		setAccessibilityDataProperties(context);
	}

	@JsonIgnore
	private void setAccessibilityDataProperties(final Context context) {
		List<String> enabledServices = AccessibilityServiceHelper.getEnabledServices(context);
		enabledAccessibilityServices = enabledServices.toArray(new String[enabledServices.size()]);
	}

	@JsonIgnore
	private void setLocalizationDataProperties(final Context context) {
	    localeSetting = context.getResources().getConfiguration().locale.getDisplayName();
	    languagePreference = Locale.getDefault().getDisplayLanguage();
	}

	@JsonIgnore
	private void setApplicationDataProperties(final Context context) {
		final PackageManager manager = context.getPackageManager();
		try {
			PackageInfo packageInfo = null;
			packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);

			if (packageInfo != null) {
				appVersion = packageInfo.versionName;
				appBuild = packageInfo.versionCode;
				appPackage = packageInfo.packageName;
				appName = packageInfo.applicationInfo.loadLabel(manager).toString();
			}
		} catch (Exception e) {
		}
	}

	@JsonIgnore
	private void setServiceProviderDataProperties(final Context context) {
		final TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		serviceProvider = telephonyManager.getNetworkOperatorName();
		serviceProviderCountry =  telephonyManager.getNetworkCountryIso();
	}

	@JsonIgnore
	private void setDisplayDataProperties(final Context context) {
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);

	    float density  = metrics.density;
	    int dpHeight = (int)(metrics.heightPixels / density);
	    int dpWidth  = (int)(metrics.widthPixels / density);

	    deviceResolution = dpWidth + "x" + dpHeight + " dp (" + metrics.widthPixels + "x" + metrics.heightPixels + " px)";
	    deviceDensity = parseDensityValueToName(metrics.densityDpi);
	}

	@JsonIgnore
	private void setNetworkDataProperties(final Context context) {
		final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		try {
			final NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			final NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (wifi != null && wifi.isAvailable()) {
				networkType = "Wifi";
			} else if (mobile != null && mobile.isAvailable()) {
				NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
				String subtype = networkInfo.getSubtypeName();
				if (TextUtils.isEmpty(subtype)) {
					networkType = networkInfo.getTypeName();
				} else {
					networkType = networkInfo.getTypeName() + "/" + subtype;
				}
                if (networkType == null) {
                    networkType = "Unable to retrieve mobile network information.";
                }
			} else {
				networkType = "No Network";
			}
		} catch (Exception e) {
			networkType = "Permission not set";
        }
	}

	@JsonIgnore
	private String parseDensityValueToName(final int densityDpi) {
		String dpi = DENSITY_BUCKETS.get(densityDpi);
		if (dpi == null) {
			dpi = "unknown";
		}
		return dpi;
	}

	/**
	 * Converts the content of this class into a JSON string.
	 */
	@Override
	@JsonIgnore
	public String toString() {
		return JsonUtils.toJsonString(this);
	}

	/**
	 * @return the deviceVendor
	 */
	public String getDeviceVendor() {
		return deviceVendor;
	}

	/**
	 * @return the deviceModel
	 */
	public String getDeviceModel() {
		return deviceModel;
	}

	/**
	 * @return the deviceOSName
	 */
	public String getDeviceOSName() {
		return deviceOSName;
	}

	/**
	 * @return the deviceOSVersion
	 */
	public int getDeviceOSVersion() {
		return deviceOSVersion;
	}

	/**
	 * @return the deviceResolution
	 */
	public String getDeviceResolution() {
		return deviceResolution;
	}

	/**
	 * @return the deviceDensity
	 */
	public String getDeviceDensity() {
		return deviceDensity;
	}

	/**
	 * @return the serviceProvider
	 */
	public String getServiceProvider() {
		return serviceProvider;
	}

	/**
	 * @return the serviceProviderCountry
	 */
	public String getServiceProviderCountry() {
		return serviceProviderCountry;
	}

	/**
	 * @return the appPackage
	 */
	public String getAppPackage() {
		return appPackage;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @return the appVersion
	 */
	public String getAppVersion() {
		return appVersion;
	}

	/**
	 * @return the appBuild
	 */
	public int getAppBuild() {
		return appBuild;
	}

	/**
	 * @return the networkType
	 */
	public String getNetworkType() {
		return networkType;
	}

	/**
	 * @return the localeSetting
	 */
	public String getLocaleSetting() {
		return localeSetting;
	}

	/**
	 * @return the languagePreference
	 */
	public String getLanguagePreference() {
		return languagePreference;
	}

	/**
	 * @return the enabledAccessibilityServices
	 */
	public String[] getEnabledAccessibilityServices() {
		return enabledAccessibilityServices;
	}

}
