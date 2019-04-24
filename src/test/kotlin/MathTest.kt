import org.joml.AxisAngle4f
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.joml.Quaternionf
import org.joml.Vector3f
class MathTest {
    @Test fun quaternion() {
        val rotation = Quaternionf()
        assertEquals(rotation.positiveY(Vector3f(1f,0f,0f)), Vector3f(0f,1f,0f))
    }
}