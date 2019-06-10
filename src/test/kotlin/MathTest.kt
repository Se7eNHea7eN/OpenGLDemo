import com.se7en.opengl.Transform
import org.joml.AxisAngle4f
import org.joml.Matrix4f
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.joml.Quaternionf
import org.joml.Vector3f

class MathTest {
//    @Test
//    fun quaternion() {
//        val rotation = Quaternionf()
//        rotation.rotateZ(Math.toRadians(90.0).toFloat())
//        assertEquals(rotation.normalizedPositiveY(Vector3f()), Vector3f(0f, 1f, 0f))
//    }
//
//    @Test
//    fun matrix() {
//        val position = Vector3f(1f, 2f, 3f)
//        val rotation = Quaternionf(AxisAngle4f(90f, 0f, 0f, 1f))
//        val matrix1 = Matrix4f()
//            .rotate(rotation)
//            .translate(position)
//
//        val matrix2 = Matrix4f()
//            .translate(position)
//            .rotate(rotation)
//
//        assertEquals(matrix1, matrix2)
//    }

    @Test
    fun quaternion() {
        val parent = Transform()
        val transform = Transform()
        transform.localPosition = Vector3f(0f,100f,0f)
        transform.parent = parent;
        parent.localRotation.rotateLocalZ(Math.toRadians(180.0).toFloat())
        assertEquals(transform.position,  Vector3f(0f,-100f,0f))
    }

}