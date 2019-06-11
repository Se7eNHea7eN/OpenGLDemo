uniform mat4 vpMatrix;
uniform mat4 modelMatrix;
attribute vec4 iPosition;

void main(void) {
    gl_Position = vpMatrix * modelMatrix * iPosition;
}
