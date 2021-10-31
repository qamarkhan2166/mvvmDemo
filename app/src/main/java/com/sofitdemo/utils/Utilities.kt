package com.sofitdemo.utils

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.sofitdemo.R
import com.google.android.material.snackbar.Snackbar
import okhttp3.internal.and
import java.io.File
import java.io.FileFilter
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.NetworkInterface
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Utilities {

    fun hideKeypad(context: Context, view: View?) {
        // Check if no view has focus:
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showAlert(context: Context, message: String?) {
        try {
            val builder = AlertDialog.Builder(context)
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            builder.setMessage(message)
                .setTitle(context.resources.getString(R.string.app_name))
                .setCancelable(true)
                // .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("OK") { dialog, id -> dialog.dismiss() }
            val alert = builder.create()
            alert.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        var showLog = true
        var formatted_address = ""
        fun print(tag: String?, message: String?) {
            if (showLog) {
                Log.v(tag, message!!)
            }
        }

        fun showToast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        @Throws(ParseException::class)
        fun getMonth(date: String?): String {
            val d = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(date)
            val cal = Calendar.getInstance()
            cal.time = d
            return SimpleDateFormat("MMM").format(cal.time)
        }

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun isValidPassword(password: String?): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            val PASSWORD_PATTERN = "(.*.{1,})"
            pattern = Pattern.compile(PASSWORD_PATTERN)
            matcher = pattern.matcher(password)
            return matcher.matches()
        }

        fun isAfterToday(year: Int, month: Int, day: Int): Boolean {
            val today = Calendar.getInstance()
            val myDate = Calendar.getInstance()
            myDate[year, month] = day
            return !myDate.before(today)
        }

        //    public void animateMarker(GoogleMap map, final Location destination, final Marker marker) {
        //        if (marker != null) {
        //            final LatLng startPosition = marker.getPosition();
        //            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());
        //
        //            final float startRotation = marker.getRotation();
        //
        //            float bearing=(float)calculateBearing(startPosition.latitude,startPosition.longitude,destination.getLatitude(),destination.getLongitude());
        //
        ////            CameraPosition cameraPosition =
        ////                    new CameraPosition.Builder()
        ////                            .target(new LatLng(destination.getLatitude(),destination.getLongitude()))
        ////                            .bearing(bearing)
        ////                            .tilt(90)
        ////                            .zoom(16.0f)
        ////                            .build();
        //
        ////            map.animateCamera(
        ////                    CameraUpdateFactory.newCameraPosition(cameraPosition),
        ////                    1000,
        ////                    null
        ////            );
        //
        //            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
        //            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        //            valueAnimator.setDuration(1000); // duration 1 second
        //            valueAnimator.setInterpolator(new LinearInterpolator());
        //            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        //                @Override
        //                public void onAnimationUpdate(ValueAnimator animation) {
        //                    try {
        //                        float v = animation.getAnimatedFraction();
        //                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
        //                        marker.setPosition(newPosition);
        ////                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
        //                    } catch (Exception ex) {
        //                        // I don't care atm..
        //                    }
        //                }
        //            });
        //
        //            valueAnimator.start();
        //        }
        //    }
        private fun calculateBearing(
            lat1: Double,
            lng1: Double,
            lat2: Double,
            lng2: Double
        ): Double {
            val dLon = lng2 - lng1
            val y = Math.sin(dLon) * Math.cos(lat2)
            val x =
                Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)
            var brng = Math.toDegrees(Math.atan2(y, x))
            return (360 - (brng + 360) % 360).also { brng = it }
        }

        //    private interface LatLngInterpolator {
        //        LatLng interpolate(float fraction, LatLng a, LatLng b);
        //
        //        class LinearFixed implements LatLngInterpolator {
        //            @Override
        //            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
        //                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
        //                double lngDelta = b.longitude - a.longitude;
        //                // Take the shortest path across the 180th meridian.
        //                if (Math.abs(lngDelta) > 180) {
        //                    lngDelta -= Math.signum(lngDelta) * 360;
        //                }
        //                double lng = lngDelta * fraction + a.longitude;
        //                return new LatLng(lat, lng);
        //            }
        //        }
        //    }
        private fun computeRotation(fraction: Float, start: Float, end: Float): Float {
            val normalizeEnd = end - start // rotate start to 0
            val normalizedEndAbs = (normalizeEnd + 360) % 360
            val direction: Float =
                if (normalizedEndAbs > 180) (-1).toFloat() else 1.toFloat() // -1 = anticlockwise, 1 = clockwise
            val rotation: Float
            rotation = if (direction > 0) {
                normalizedEndAbs
            } else {
                normalizedEndAbs - 360
            }
            val result = fraction * rotation + start
            return (result + 360) % 360
        }

        //
        fun isValidEmail(email: String?): Boolean {
            val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
            val pattern = Pattern.compile(EMAIL_PATTERN)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        //    public static String getAddressUsingLatLng(final String strType, final TextView txtView, final Context context, String latitude, String longitude){
        //        Geocoder geocoder=new Geocoder(context);
        //        List<Address> addresses;
        //        try {
        ////            addresses = geocoder.fet
        //            if (latitude!=null && longitude!= null) {
        //                addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 5);
        //                if (addresses != null) {
        //                    Address address = addresses.get(0);
        //                    formatted_address = address.getAddressLine(0);  //jsonArray.optJSONObject(0).optString("formatted_address");
        //                    Utilities.print("Formatted Address", "surce: " + formatted_address);
        //                    txtView.setText("" + formatted_address);
        //                    if (SharedHelper.getKey(context, "track_status").equalsIgnoreCase("YES") &&
        //                            (SharedHelper.getKey(context, "req_status").equalsIgnoreCase("STARTED") || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("PICKEDUP")
        //                                    || SharedHelper.getKey(context, "req_status").equalsIgnoreCase("ARRIVED"))) {
        //                        SharedHelper.putKey(context, "extend_address", "" + formatted_address);
        //                    } else {
        //                        if (strType.equalsIgnoreCase("source")) {
        //                            SharedHelper.putKey(context, "source", "" + formatted_address);
        //                        } else {
        //                            SharedHelper.putKey(context, "destination", "" + formatted_address);
        //                        }
        //                    }
        //
        //                } else {
        //                    Toast.makeText(context, "Service not available!", Toast.LENGTH_SHORT).show();
        //                }
        //            }else {
        //                Toast.makeText(context, "latitude and longitude not given!", Toast.LENGTH_SHORT).show();
        //            }
        //        } catch (Exception e){
        //            e.printStackTrace();
        //            formatted_address = "";
        //            SharedHelper.putKey(context, "source", "");
        //            SharedHelper.putKey(context, "destination", "");
        //            txtView.setText("");
        //        }
        //        return ""+formatted_address;
        //
        //    }

        fun formatHoursAndMinutes(totalMinutes: Int): String {
            var minutes = Integer.toString(totalMinutes % 60)
            minutes = if (minutes.length == 1) "0$minutes" else minutes
            return if (totalMinutes / 60 >= 1) {
                (totalMinutes / 60).toString() + " hr " + minutes + " mins"
            } else "$minutes mins"
        }

        fun isServerReachable(context: Context, strUrl: String?): Boolean {
            val connMan =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connMan.activeNetworkInfo
            return if (netInfo != null && netInfo.isConnected) {
                try {
                    val urlServer = URL(strUrl)
                    val urlConn = urlServer.openConnection() as HttpURLConnection
                    urlConn.connectTimeout = 3000 //<- 3Seconds Timeout
                    urlConn.connect()
                    urlConn.responseCode == 200
                } catch (e1: MalformedURLException) {
                    false
                } catch (e: IOException) {
                    false
                }
            } else false
        }

        fun BulidNumber(): String {
            val buildNumber = ""
            buildNumber + "VERSION.RELEASE {" + Build.VERSION.RELEASE + "}"
            """
     $buildNumber
     VERSION.INCREMENTAL {${Build.VERSION.INCREMENTAL}}
     """.trimIndent()
            """
     $buildNumber
     VERSION.SDK {${Build.VERSION.SDK}}
     """.trimIndent()
            """
     $buildNumber
     BOARD {${Build.BOARD}}
     """.trimIndent()
            """
     $buildNumber
     BRAND {${Build.BRAND}}
     """.trimIndent()
            """
     $buildNumber
     DEVICE {${Build.DEVICE}}
     """.trimIndent()
            """
     $buildNumber
     FINGERPRINT {${Build.FINGERPRINT}}
     """.trimIndent()
            """
     $buildNumber
     HOST {${Build.HOST}}
     """.trimIndent()
            """
     $buildNumber
     ID {${Build.ID}}
     """.trimIndent()
            return buildNumber
        }

        fun HWArch(): String? {
            return System.getProperty("os.arch")
        }

        fun getNumberOfCores(): Int {
            return if (Build.VERSION.SDK_INT >= 17) {
                Runtime.getRuntime().availableProcessors()
            } else {
                // Use saurabh64's answer
                getNumCoresOldPhones()
            }
        }

        fun KernelArch(): String? {
            val arch = Build.CPU_ABI
            return arch ?: "00:00"
        }

        fun KernelRelease(): String? {
            return System.getProperty("os.version")
        }

        fun FINGERPRINT(): String? {
            val finger = Build.FINGERPRINT
            return finger ?: "00:00"
        }

        fun getIPV4(): String? {
            return UtilsRegister.getIPAddress(true)
        }

        fun getIPV6(): String? {
            return UtilsRegister.getIPAddress(false)
        }

        fun getNumCoresOldPhones(): Int {
            //Private Class to display only CPU devices in the directory listing
            class CpuFilter : FileFilter {
                override fun accept(pathname: File): Boolean {
                    //Check if filename is "cpu", followed by a single digit number
                    return Pattern.matches("cpu[0-9]+", pathname.name)
                }
            }
            return try {
                //Get directory containing CPU info
                val dir = File("/sys/devices/system/cpu/")
                //Filter to only list the devices we care about
                val files = dir.listFiles(CpuFilter())
                //Return the number of cores (virtual CPU devices)
                files.size
            } catch (e: java.lang.Exception) {
                //Default to return 1 core
                1
            }
        }

        fun SecurityPatch(): String {
            return SystemProperties.get("ro.build.version.security_patch").toString()
        }

        fun AndroidVersion(): String {
            val version = Build.VERSION.SDK_INT
            val versionRelease = Build.VERSION.RELEASE
            return "$version/$versionRelease"
        }

        fun MANUFACTURER(): String {
            return Build.MANUFACTURER
        }

        fun ModelNumber(): String? {
            return Build.MODEL
        }

        fun ModelName(): String {
            return Build.BRAND + " " + Build.DEVICE
        }

        fun MakeDeviceId(context: Context): String? {
            val id: String
            id = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) + "_" + getSecureRandomNumber() + "-" + getMACaddress() + "-" + random_int() + "-" + GetTime()
            var digest: MessageDigest? = null
            try {
                digest = MessageDigest.getInstance("SHA-256")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            val hash = digest!!.digest(id.toByteArray(StandardCharsets.UTF_8))
            val SHA_256: String =
                bytesToHex(hash)
            //pref.saveDeviceId(SHA_256)
            return SHA_256
        }

        fun bytesToHex(bytes: ByteArray): String {
            val sb = StringBuilder()
            for (b in bytes) {
                sb.append(String.format("%02x", b))
            }
            return sb.toString()
        }

        fun getSecureRandomNumber(): String {
            //secureRandNum
            val random = SecureRandom()
            var num = random.nextInt(999999999) + 99999
            if (num < 0) {
                num = -num
            }
            return num.toString() + ""
        } //getSecureRandomNumber()-ends

        fun getMACaddress(): String {
            try {
                val all: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                    val macBytes = nif.hardwareAddress ?: return ""
                    val res1 = java.lang.StringBuilder()
                    for (b in macBytes) {
                        res1.append(Integer.toHexString(b and 0xFF) + ":")
                    }
                    if (res1.length > 0) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (ex: Exception) {
            }
            return "00:00:00:00:00:00"
        }

        fun random_int(): Int {
            //RandInt
            val Min = 100000
            val Max = 1000000000
            return (Math.random() * (Max - Min)).toInt() + Min
        } //random_int()-ends

        private fun GetTime(): String? {
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
            return sdf.format(Date())
        } //GetTime()-ends
    }

    fun getCompleteAddressString(context: Context?, LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
//                Log.e("Utilis", "My Current: $addresses")
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                if (returnedAddress.maxAddressLineIndex > 0) {
                    for (i in 0 until returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ")
                    }
                } else {
                    strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("Utilis My Current", "" + strReturnedAddress.toString())
            } else {
                Log.w("Utilis My Current", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("Utilis My Current", "Canont get Address!")
        }
        return strAdd
    }

    fun displayMessage(view: View?, context: Context?, toastString: String) {
        try {
            Snackbar.make(view!!, toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        } catch (e: Exception) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show()
            } catch (ee: Exception) {
                ee.printStackTrace()
            }
        }
    }

    //    public void rotateMarker(final Marker marker, final float toRotation) {
    //            final Handler handler = new Handler();
    //            final long start = SystemClock.uptimeMillis();
    //            final float startRotation = marker.getRotation();
    //            final long duration = 1000;
    //
    //            final Interpolator interpolator = new LinearInterpolator();
    //
    //            handler.post(new Runnable() {
    //                @Override
    //                public void run() {
    //
    //                    long elapsed = SystemClock.uptimeMillis() - start;
    //                    float t = interpolator.getInterpolation((float) elapsed / duration);
    //
    //                    float rot = t * toRotation + (1 - t) * startRotation;
    //
    //                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
    //                    if (t < 1.0) {
    //                        // Post again 16ms later.
    //                        handler.postDelayed(this, 16);
    //                    } else {
    //                    }
    //
    //                }
    //            });
    //    }
    fun checktimings(time: String?): Boolean {
        val pattern = "HH:mm"
        val sdf = SimpleDateFormat(pattern)
        try {
            val currentTime = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
            val date1 = sdf.parse(time)
            val date2 = sdf.parse(currentTime)
            return date1.after(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }


}