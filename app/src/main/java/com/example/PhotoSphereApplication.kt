package com.example

import android.app.Application
import android.util.Log
import com.example.storage.SphereImageStore
import org.opencv.android.OpenCVLoader
import java.io.File

/**
 * Loads OpenCV's native library once, at process start.
 */
class PhotoSphereApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        isOpenCvAvailable = OpenCVLoader.initLocal()
        if (isOpenCvAvailable) {
            Log.i(TAG, "OpenCV initialized: ${OpenCVLoader.OPENCV_VERSION}")
        } else {
            Log.e(TAG, "OpenCV failed to initialize; sphere stitching is disabled")
        }
        pruneOrphanedSpheres()
    }

    private fun pruneOrphanedSpheres() {
        val directory = File(cacheDir, SphereImageStore.SPHERES_DIRECTORY_NAME)
        val spheres = directory.listFiles { file -> file.name.endsWith(".jpg") } ?: return
        if (spheres.size <= 1) return
        val newest = spheres.maxByOrNull { it.lastModified() } ?: return
        spheres.forEach { stale ->
            if (stale != newest && !stale.delete()) {
                Log.w(TAG, "Could not clear orphaned sphere ${stale.name}")
            }
        }
    }

    companion object {
        private const val TAG = "PhotoSphereApp"
        var isOpenCvAvailable: Boolean = false
            private set
    }
}
