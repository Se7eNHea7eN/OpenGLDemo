uniform mat4 uMVPMatrix;
attribute vec4 iPosition;

attribute vec4 aColor;

varying vec4 vColor;
void main()
{
    vColor = aColor;
	gl_Position = uMVPMatrix * iPosition;
    gl_PointSize = 5.0;
}