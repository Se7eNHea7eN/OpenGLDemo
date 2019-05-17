uniform mat4 vpMatrix;
uniform mat4 modelMatrix;
attribute vec4 aPosition;

void main(void) {
    gl_Position = vpMatrix * modelMatrix * aPosition;
}
