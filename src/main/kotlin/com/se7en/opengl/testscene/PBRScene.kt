package com.se7en.opengl.testscene

import com.se7en.opengl.GlScene
import com.se7en.opengl.GlSkyBox
import com.se7en.opengl.WHITE
import com.se7en.opengl.geometry.Sphere
import com.se7en.opengl.lighting.GlPointLight
import com.se7en.opengl.material.Illumination
import com.se7en.opengl.material.Material
import com.se7en.opengl.material.PBRMaterial
import com.se7en.opengl.times
import com.se7en.opengl.utils.Debug
import org.joml.Vector3f

class PBRScene : GlScene() {

    private val pointLight1 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(-4f, -4f, -8f)
            lightColor = WHITE * 10f
        }
    }

    private val pointLight2 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(-4f, 4f, -8f)
            lightColor = WHITE * 10f
        }
    }

    private val pointLight3 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(4f, -4f, -8f)
            lightColor = WHITE * 10f
        }
    }
    private val pointLight4 = object : GlPointLight() {
        init {
            transform.localPosition = Vector3f(4f, 4f, -8f)
            lightColor = WHITE * 10f
        }
    }



    init {
        for( i in 0..7)
            for(j in 0 ..7){
                object : Sphere(){
                    override fun createMaterial(): Material = PBRMaterial().apply {
                        metallic = 0.97f
                        roughness = 0.3f
                    }

                    override fun update(deltaTime: Long) {
                        super.update(deltaTime)
                        (material as PBRMaterial).apply {
//                            metallic = Math.sin(0.001 *timeSinceStart.toDouble()).toFloat()
//                            roughness = Math.cos(0.001 *timeSinceStart.toDouble()).toFloat()
                        }
                    }
                }.apply {
                    transform.localScale = Vector3f(0.7f)
                    transform.localPosition = Vector3f(7f - 2*i,7f - 2*j,-5f)
                }

            }
        mainCamera.transform.localPosition = Vector3f(7f, 0f, -15f)
        mainCamera.transform.localRotation.rotateY(Math.toRadians(30.0).toFloat())
//        mainCamera.transform.localRotation.rotateX(Math.toRadians(20.0).toFloat())
    }

}