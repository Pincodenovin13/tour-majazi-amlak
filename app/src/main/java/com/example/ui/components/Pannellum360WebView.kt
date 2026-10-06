package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.model.TourScene
import java.io.ByteArrayOutputStream

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Pannellum360WebView(
    scenes: List<TourScene>,
    currentSceneIndex: Int,
    onSceneChanged: (newIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentScene = scenes.getOrNull(currentSceneIndex) ?: scenes.firstOrNull() ?: return

    // Convert image (from local path or drawable) to Base64 data URL for offline rendering
    val base64Image = remember(currentScene.id, currentScene.drawableResId, currentScene.imagePath) {
        getScenePanoramaBase64(context, currentScene)
    }

    val htmlContent = remember(currentScene.id, base64Image) {
        generatePannellumHtml(currentScene, base64Image)
    }

    class WebAppInterface {
        @JavascriptInterface
        fun onHotspotClick(targetId: String) {
            val targetIdx = scenes.indexOfFirst { it.id == targetId }
            if (targetIdx >= 0) {
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    onSceneChanged(targetIdx)
                }
            }
        }
    }

    Box(modifier = modifier.background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.cacheMode = WebSettings.LOAD_NO_CACHE
                    setBackgroundColor(android.graphics.Color.BLACK)
                    addJavascriptInterface(WebAppInterface(), "AndroidBridge")
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun getScenePanoramaBase64(context: Context, scene: TourScene): String {
    return try {
        val bitmap = if (!scene.imagePath.isNullOrEmpty()) {
            BitmapFactory.decodeFile(scene.imagePath)
        } else if (scene.drawableResId != 0) {
            BitmapFactory.decodeResource(context.resources, scene.drawableResId)
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.img_tour_sample)
        } ?: return ""
        val stream = ByteArrayOutputStream()
        // Compress efficiently
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream)
        val bytes = stream.toByteArray()
        "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        ""
    }
}

private fun generatePannellumHtml(scene: TourScene, imageBase64: String): String {
    val hotspotsJson = scene.hotspots.joinToString(separator = ",") { hs ->
        """
        {
            "pitch": ${(hs.yPercent - 0.5) * -60},
            "yaw": ${(hs.xPercent - 0.5) * 180},
            "type": "scene",
            "text": "${hs.title}",
            "sceneId": "${hs.targetSceneId ?: ""}"
        }
        """.trimIndent()
    }

    return """
    <!DOCTYPE html>
    <html lang="fa" dir="rtl">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
        <title>Pannellum 360 Tour</title>
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            html, body { width: 100%; height: 100%; overflow: hidden; background: #000; font-family: sans-serif; }
            #panorama { width: 100%; height: 100%; position: relative; }
            .p-hud {
                position: absolute; top: 12px; right: 12px;
                background: rgba(0,0,0,0.65); color: #fff;
                padding: 6px 12px; border-radius: 20px; font-size: 13px; font-weight: bold;
                border: 1px solid rgba(255,193,7,0.8);
                display: flex; align-items: center; gap: 6px; z-index: 10;
            }
            .p-badge {
                color: #FFC107; font-size: 11px; text-transform: uppercase;
            }
            .canvas-container { width: 100%; height: 100%; cursor: grab; }
            .canvas-container:active { cursor: grabbing; }
            .hotspot-marker {
                position: absolute; width: 48px; height: 48px;
                background: #FF5722;
                border: 2px solid #FFFFFF; border-radius: 50%;
                display: flex; align-items: center; justify-content: center;
                cursor: pointer; transform: translate(-50%, -50%);
                animation: pulse 1.6s infinite ease-in-out;
                box-shadow: 0 0 16px rgba(255,87,34,0.85);
                z-index: 5;
            }
            .hotspot-arrow {
                width: 0; height: 0;
                border-left: 8px solid transparent;
                border-right: 8px solid transparent;
                border-bottom: 14px solid #FFFFFF;
            }
            .hotspot-label {
                position: absolute; top: 50px; background: rgba(0,0,0,0.85);
                color: #fff; font-size: 11px; padding: 4px 10px; border-radius: 8px;
                white-space: nowrap; pointer-events: none; border: 1px solid rgba(255,87,34,0.6);
            }
            @keyframes pulse {
                0% { transform: translate(-50%, -50%) scale(0.95); }
                50% { transform: translate(-50%, -50%) scale(1.15); }
                100% { transform: translate(-50%, -50%) scale(0.95); }
            }
            .p-controls {
                position: absolute; bottom: 20px; right: 20px;
                display: flex; flex-direction: column; gap: 8px; z-index: 10;
            }
            .p-btn {
                width: 40px; height: 40px; border-radius: 50%;
                background: rgba(0,0,0,0.7); color: #fff; border: 1px solid rgba(255,255,255,0.4);
                display: flex; align-items: center; justify-content: center;
                font-size: 18px; font-weight: bold; cursor: pointer;
            }
        </style>
    </head>
    <body>
        <div id="panorama">
            <div class="p-hud">
                <span class="p-badge">PANNELLUM 360</span>
                <span>${scene.name}</span>
            </div>

            <canvas id="viewCanvas" class="canvas-container"></canvas>

            <div id="hotspotsContainer"></div>

            <div class="p-controls">
                <div class="p-btn" onclick="zoomIn()">+</div>
                <div class="p-btn" onclick="zoomOut()">-</div>
                <div class="p-btn" onclick="toggleAuto()">⟳</div>
            </div>
        </div>

        <script>
            var canvas = document.getElementById('viewCanvas');
            var ctx = canvas.getContext('2d');
            var img = new Image();
            img.src = "$imageBase64";

            var yaw = 0;
            var pitch = 0;
            var fov = 1.0;
            var isDragging = false;
            var startX = 0, startY = 0;
            var autoRotate = false;

            var hotspots = [$hotspotsJson];

            function resize() {
                canvas.width = window.innerWidth;
                canvas.height = window.innerHeight;
                render();
            }
            window.addEventListener('resize', resize);

            img.onload = function() {
                resize();
                createHotspots();
            };

            function render() {
                if (!img.width) return;
                var cw = canvas.width;
                var ch = canvas.height;
                ctx.clearRect(0, 0, cw, ch);

                var imgRatio = img.width / img.height;
                var targetHeight = ch * fov;
                var targetWidth = targetHeight * imgRatio;

                var offsetX = (yaw * 2.5) % targetWidth;
                var offsetY = (ch - targetHeight) / 2 + pitch * 1.5;

                ctx.drawImage(img, offsetX, offsetY, targetWidth, targetHeight);
                if (offsetX > 0) {
                    ctx.drawImage(img, offsetX - targetWidth, offsetY, targetWidth, targetHeight);
                } else if (offsetX + targetWidth < cw) {
                    ctx.drawImage(img, offsetX + targetWidth, offsetY, targetWidth, targetHeight);
                }

                updateHotspots(offsetX, offsetY, targetWidth, targetHeight);
            }

            function createHotspots() {
                var container = document.getElementById('hotspotsContainer');
                container.innerHTML = '';
                hotspots.forEach(function(hs, index) {
                    var el = document.createElement('div');
                    el.className = 'hotspot-marker';
                    el.id = 'hs_' + index;
                    el.innerHTML = '<div class="hotspot-arrow"></div><div class="hotspot-label">' + hs.text + '</div>';
                    el.onclick = function() {
                        if (hs.sceneId && window.AndroidBridge) {
                            window.AndroidBridge.onHotspotClick(hs.sceneId);
                        }
                    };
                    container.appendChild(el);
                });
            }

            function updateHotspots(offsetX, offsetY, targetWidth, targetHeight) {
                hotspots.forEach(function(hs, index) {
                    var el = document.getElementById('hs_' + index);
                    if (!el) return;
                    var normX = (hs.yaw / 180 + 0.5) * targetWidth + offsetX;
                    var normY = (-hs.pitch / 60 + 0.5) * targetHeight + offsetY;

                    normX = ((normX % targetWidth) + targetWidth) % targetWidth;
                    if (normX >= 0 && normX <= canvas.width) {
                        el.style.display = 'flex';
                        el.style.left = normX + 'px';
                        el.style.top = normY + 'px';
                    } else {
                        el.style.display = 'none';
                    }
                });
            }

            canvas.addEventListener('mousedown', function(e) {
                isDragging = true; startX = e.clientX; startY = e.clientY;
            });
            window.addEventListener('mouseup', function() { isDragging = false; });
            canvas.addEventListener('mousemove', function(e) {
                if (!isDragging) return;
                var dx = e.clientX - startX;
                var dy = e.clientY - startY;
                yaw += dx;
                pitch = Math.max(-60, Math.min(60, pitch + dy));
                startX = e.clientX; startY = e.clientY;
                render();
            });

            canvas.addEventListener('touchstart', function(e) {
                if (e.touches.length === 1) {
                    isDragging = true;
                    startX = e.touches[0].clientX;
                    startY = e.touches[0].clientY;
                }
            });
            window.addEventListener('touchend', function() { isDragging = false; });
            canvas.addEventListener('touchmove', function(e) {
                if (!isDragging || e.touches.length !== 1) return;
                var dx = e.touches[0].clientX - startX;
                var dy = e.touches[0].clientY - startY;
                yaw += dx;
                pitch = Math.max(-60, Math.min(60, pitch + dy));
                startX = e.touches[0].clientX;
                startY = e.touches[0].clientY;
                render();
            });

            function zoomIn() { fov = Math.min(2.5, fov + 0.2); render(); }
            function zoomOut() { fov = Math.max(1.0, fov - 0.2); render(); }
            function toggleAuto() {
                autoRotate = !autoRotate;
                if (autoRotate) runAuto();
            }
            function runAuto() {
                if (!autoRotate) return;
                yaw -= 1;
                render();
                requestAnimationFrame(runAuto);
            }
        </script>
    </body>
    </html>
    """.trimIndent()
}
