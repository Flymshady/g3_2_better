package app;

import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import transforms.*;
import java.io.IOException;
import java.nio.DoubleBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL40.*;

/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

    private int shaderProgram;
    private OGLBuffers buffers;
    private int locView;
    private int locProjection;
    private int locModel;
    private Mat4PerspRH projection;
    private Mat4OrthoRH projectionOH;
    private Camera camera;
    private boolean persp=true;
    private int mode=1;
    private int modeLoc;
    private String modeString="tessel";
    private String projString = "[Perps]";
    private String lineString="[Line]";
    double ox, oy;
    boolean mouseButton1 = false;
    private boolean line = true;
    private int inner_nmb = 1;
    private int outer_nmb = 1;
    private int locInner_nmb, locOuter_nmb;
    private boolean modeChange = false;

    public void init() {
        glClearColor(0.1f, 0.1f, 0.1f, 1);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_DEPTH_TEST);

        shaderProgram = ShaderUtils.loadProgram("/shader.vert", "/shader.frag", null, "/shader.tesc", "/shader.tese", null);
        locView = glGetUniformLocation(shaderProgram, "view");
        locProjection = glGetUniformLocation(shaderProgram, "projection");

        locInner_nmb =  glGetUniformLocation(shaderProgram, "inner_nmb");
        locOuter_nmb =  glGetUniformLocation(shaderProgram, "outer_nmb");
        locModel = glGetUniformLocation(shaderProgram, "model");
        buffers = GridFactory.generateGrid(20,20);
        modeLoc =  glGetUniformLocation(shaderProgram, "mode");
        textRenderer = new OGLTextRenderer(width, height);

        camera = new Camera()
                .withPosition(new Vec3D(0,0,0))
                .withAzimuth(5/4f* Math.PI)
                .withZenith(-1/5f*Math.PI)
                .withFirstPerson(false)
                .withRadius(6);

        projection = new Mat4PerspRH(Math.PI / 3,
                LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH, 1, 50);

        projectionOH= new Mat4OrthoRH(4, 3, 1,50);


    }
    public void display(){

        if(modeChange) {
            if (mode == 1) {
                shaderProgram = ShaderUtils.loadProgram("/shader.vert", "/shader.frag", null, "/shader.tesc", "/shader.tese", null);
            }
            if (mode == 2) {
                shaderProgram = ShaderUtils.loadProgram("/shader.vert", "/shader.frag", "/shader.geom", "/shader.tesc", "/shader.tese", null);
            }
            modeChange=false;
        }


        renderFromViewer();

        textRenderer.clear();
        String text = "Camera - WSAD, L_SHIFT, L_CTRL, Q, E, SPACE, LMB, Scroll";
        String text1 = "Fill/Line - L : "+lineString+"; Inner_nmb "+inner_nmb+"; Outer_nmb "+outer_nmb;
        String text2 =  "Mode - M: "+mode+" = "+modeString;
        String text3 = "Persp/Orto projection - P: "+projString+"; Resizable window";
        textRenderer.addStr2D(3, height-3, text);
        textRenderer.addStr2D(3, height-15, text1);
       // textRenderer.addStr2D(3, height-27, text3);
        textRenderer.addStr2D(3, height-39, text2);
        textRenderer.addStr2D(width-170, height-3, "Štěpán Cellar - PGRF3 - 2019");
        textRenderer.draw();

    }

    private void renderFromViewer() {
        glEnable(GL_DEPTH_TEST);
        glUseProgram(shaderProgram);
        if(line){
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }else{
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0,0, width, height);
        glClearColor(0.5f,0f,0f,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        if(persp) {
            glUniformMatrix4fv(locProjection, false, projection.floatArray());
        }else {
            glUniformMatrix4fv(locProjection, false, projectionOH.floatArray());
        }



        glUniform1f(locInner_nmb, inner_nmb);
        glUniform1f(locOuter_nmb, outer_nmb);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(1).floatArray());

        glPatchParameteri(GL_PATCH_VERTICES, 3);
        buffers.draw(GL_PATCHES, shaderProgram);

    }

    private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
            if (action == GLFW_PRESS || action == GLFW_REPEAT){
                switch (key) {
                    case GLFW_KEY_W:
                        camera = camera.forward(1);
                        break;
                    case GLFW_KEY_D:
                        camera = camera.right(1);
                        break;
                    case GLFW_KEY_S:
                        camera = camera.backward(1);
                        break;
                    case GLFW_KEY_A:
                        camera = camera.left(1);
                        break;
                    case GLFW_KEY_LEFT_CONTROL:
                        camera = camera.down(1);
                        break;
                    case GLFW_KEY_LEFT_SHIFT:
                        camera = camera.up(1);
                        break;
                    case GLFW_KEY_SPACE:
                        camera = camera.withFirstPerson(!camera.getFirstPerson());
                        break;
                    case GLFW_KEY_Q:
                        camera = camera.mulRadius(0.9f);
                        break;
                    case GLFW_KEY_E:
                        camera = camera.mulRadius(1.1f);
                        break;
                    case GLFW_KEY_L:
                        if(line){
                            line=false;
                            lineString="[Fill]";
                        }else{
                            line=true;
                            lineString="[Line]";
                        }
                        break;
                    case GLFW_KEY_O:
                        outer_nmb++;
                        break;
                    case GLFW_KEY_I:
                        inner_nmb++;
                        break;
                    case GLFW_KEY_K:
                        if(outer_nmb>1){
                            outer_nmb--;
                        }
                        break;
                    case GLFW_KEY_J:
                        if(inner_nmb>=1){
                            inner_nmb--;
                        }

                        break;
                    case GLFW_KEY_M:
                        if (mode == 1) {
                            modeString="tessel";
                            mode=2;
                            modeChange=true;
                        }else{
                            modeString="geom & tessel";
                            mode=1;
                            modeChange=true;
                        }
                        break;
                    case GLFW_KEY_P:
                        if(persp){
                            persp=false;
                            projString="[Ortho]";
                        }else{
                            persp=true;
                            projString="[Persp]";
                        }
                        break;

                }
            }
        }
    };

    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 &&
                    (w != width || h != height)) {
                width = w;
                height = h;
                projection = new Mat4PerspRH(Math.PI / 3,
                        LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH, 1, 50);
                if (textRenderer != null)
                    textRenderer.resize(width, height);
            }
        }
    };
    
    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }

        }

    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };



    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            if (dy < 0) {
                camera = camera.mulRadius(1.05f);
                if(outer_nmb>1) {
                    inner_nmb--;
                    outer_nmb--;
                }

            }
            else {
                camera = camera.mulRadius(0.95f);
                inner_nmb++;
                outer_nmb++;
            }
        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    public GLFWWindowSizeCallback getWsCallback() {
        return wsCallback;
    }
}