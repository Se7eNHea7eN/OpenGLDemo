uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

attribute vec4 aPosition;

void main(void) {
    gl_Position = projectionMatrix * viewMatrix * aPosition;
}
