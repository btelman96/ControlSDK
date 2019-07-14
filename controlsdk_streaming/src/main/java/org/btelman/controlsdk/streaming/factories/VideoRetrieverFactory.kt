package org.btelman.controlsdk.streaming.factories

import android.os.Bundle
import org.btelman.controlsdk.streaming.models.CameraDeviceInfo
import org.btelman.controlsdk.streaming.video.processors.BaseVideoProcessor
import org.btelman.controlsdk.streaming.video.retrievers.BaseVideoRetriever
import org.btelman.controlsdk.streaming.video.retrievers.api16.Camera1SurfaceTextureComponent
import org.btelman.controlsdk.streaming.video.retrievers.api21.Camera2SurfaceTextureComponent

object VideoRetrieverFactory {

    fun findRetriever(bundle: Bundle): BaseVideoRetriever? {
        bundle.getSerializable("videoProcessorClass")?.let {
            (it as? Class<*>)?.let {clazz ->
                if(clazz.isAssignableFrom(BaseVideoRetriever::class.java)){
                    return clazz.newInstance() as BaseVideoRetriever
                }
            }
        }
        CameraDeviceInfo.fromBundle(bundle)?.also {
            when {
                it.camera.contains("/dev/video") -> TODO("USB Camera retriever class")
                it.camera.contains("/dev/camera") -> return if(Camera2SurfaceTextureComponent.isSupported()){
                    Camera2SurfaceTextureComponent()
                } else{
                    Camera1SurfaceTextureComponent()
                }
                it.camera.contains("http") -> TODO("Camera stream from other device")
            }
        }
        return DEFAULT.newInstance()
    }

    fun <T : BaseVideoProcessor> putClassInBundle(clazz: Class<T>, bundle: Bundle){
        bundle.putSerializable("videoRetrieverClass", clazz)
    }

    //TODO replace with non-abstract class when we get FFMpeg in here
    val DEFAULT = BaseVideoRetriever::class.java
}
