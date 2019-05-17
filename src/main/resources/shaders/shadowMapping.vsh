uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
attribute vec4 aPosition;

void main(void) {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * aPosition;
}
