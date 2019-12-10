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
    private int shaderProgramLight;
    private OGLBuffers buffers;
    private int locView;
    private int locViewLight;
    private int locProjection;
    private int locProjectionLight;
    private int locLightVP;
    private int locModel;
    private int locModelLight;
    private Mat4PerspRH projection;
    private Mat4OrthoRH projectionOH;
    private Camera camera;
    private int locTime;
    private int locType, locTypeLight;
    private int locTimeLight;
    private float time;
    private float rot1 = 0;
    private boolean rot1B = true;
    private String rot1String = "[On]";
    private float rotLight = 0;
    private boolean rotLightB = true;
    private String rotLString = "[On]";
    private boolean persp=true;
    private int mode=1;
    private int modeLoc;
    private String modeString="geom & tessel";
    private String projString = "[Perps]";
    private String lineString="[Fill]";
    private int attenuation=1;
    private String attenuationString = "[On]";
    private int locAttenuation;
    private OGLRenderTarget renderTarget;
    private OGLTexture2D.Viewer viewer;
    private OGLTexture2D texture1;
    private int spotlight=0;
    private String spotlightString="[Off]";
    private int locSpotlight;
    private float rotLSpeedBonus=0.01f;
    protected OGLTextRenderer textRenderer;
    double ox, oy;
    boolean mouseButton1 = false;
    private int lightPos;
    private int locBlinn_Phong;
    private int blinn_phong=1;
    private String blinn_phongS="[On]";
    private boolean line = false;
    private int inner_nmb = 1;
    private int outer_nmb = 1;
    private int locInner_nmb, locOuter_nmb;
    private boolean modeChange = false;

    public void init() {
        glClearColor(0.1f, 0.1f, 0.1f, 1);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_DEPTH_TEST);

        shaderProgram = ShaderUtils.loadProgram("/tessel");
        shaderProgramLight = ShaderUtils.loadProgram("/light");
        locView = glGetUniformLocation(shaderProgram, "view");
        locProjection = glGetUniformLocation(shaderProgram, "projection");
        locType=  glGetUniformLocation(shaderProgram, "type");
        locTime =  glGetUniformLocation(shaderProgram, "time");

        locInner_nmb =  glGetUniformLocation(shaderProgram, "inner_nmb");
        locOuter_nmb =  glGetUniformLocation(shaderProgram, "outer_nmb");

        locModel = glGetUniformLocation(shaderProgram, "model");
        locViewLight = glGetUniformLocation(shaderProgramLight, "view");
        locProjectionLight = glGetUniformLocation(shaderProgramLight, "projection");
        locTypeLight =  glGetUniformLocation(shaderProgramLight, "type");
        locTimeLight =  glGetUniformLocation(shaderProgramLight, "time");
        locModelLight =  glGetUniformLocation(shaderProgramLight, "model");
        lightPos =  glGetUniformLocation(shaderProgram, "lightPos");
        locBlinn_Phong =  glGetUniformLocation(shaderProgram, "blinn_phong");
        locSpotlight =  glGetUniformLocation(shaderProgram, "spotlight");
        locLightVP = glGetUniformLocation(shaderProgram, "lightViewProjection");
        locAttenuation=glGetUniformLocation(shaderProgram, "attenuation");
        buffers = GridFactory.generateGrid(20,20);
        renderTarget = new OGLRenderTarget(1024,1024);
        modeLoc =  glGetUniformLocation(shaderProgram, "mode");
        viewer = new OGLTexture2D.Viewer();
        textRenderer = new OGLTextRenderer(width, height);

        try {
            texture1 = new OGLTexture2D("res/texture/mosaic.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera = new Camera()
                .withPosition(new Vec3D(0,0,0))
                .withAzimuth(5/4f* Math.PI)
                .withZenith(-1/5f*Math.PI)
                .withFirstPerson(false)
                .withRadius(6);

        projection = new Mat4PerspRH(Math.PI / 3,
                LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH, 1, 50);

        projectionOH= new Mat4OrthoRH(15, 15, 1,50);


    }
    public void display(){

        if(modeChange) {
            if (mode == 1) {
                shaderProgram = ShaderUtils.loadProgram("/tessel.vert", "/tessel.frag", "/tessel.geom", "/tessel.tesc", "/tessel.tese", null);
            }
            if (mode == 2) {
                shaderProgram = ShaderUtils.loadProgram("/tessel.vert", "/tessel.frag", null, "/tessel.tesc", "/tessel.tese", null);
            }
            modeChange=false;
        }

        if(time>1000f) time=0.1f;
        time += 0.1;

        renderFromLight();
        renderFromViewer();

        textRenderer.clear();
        String text = "Camera - WSAD, L_SHIFT, L_CTRL, Q, E, SPACE, LMB, Scroll";
        String text1 = "Fill/Line - L : "+lineString+";Inner_nmb "+inner_nmb+"; Outer_nmb "+outer_nmb;
        String text2 =  "Mode - 1 - 2: "+mode+"="+modeString;
        String text3 = "Persp/Orto projection - P: "+projString+"; Resizable window";
        textRenderer.addStr2D(3, height-3, text);
        textRenderer.addStr2D(3, height-15, text1);
       // textRenderer.addStr2D(3, height-27, text3);
        textRenderer.addStr2D(3, height-39, text2);
        textRenderer.addStr2D(width-170, height-3, "Štěpán Cellar - PGRF3 - 2019");
        textRenderer.draw();

        viewer.view(renderTarget.getColorTexture(), -1, 0, 0.5);
        viewer.view(renderTarget.getDepthTexture(), -1, -0.5, 0.5);
    }

    private void renderFromLight() {
        glEnable(GL_DEPTH_TEST);
        glUseProgram(shaderProgram);
        Vec3D light = new Vec3D(0, 0, 15).mul(new Mat3RotY(rotLight));
        glUniform3f(lightPos,(float)light.getX(), (float)light.getY(), (float)light.getZ());
        glUniform1i(modeLoc, mode);
        glUseProgram(shaderProgramLight);

        renderTarget.bind();
        glClearColor(0f,0.5f,0f,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(locViewLight, false, new Mat4ViewRH(light, light.mul(-1), new Vec3D(0,1,0)).floatArray());
        glUniformMatrix4fv(locProjectionLight, false, new Mat4OrthoRH(10,10,1,20).floatArray());

        glUniform1f(locTimeLight, time);
        /*
        if(mode==7 || mode==8) {    //per vertex || per pixel mode
            glUniform1f(locTypeLight, 8);
            glUniformMatrix4fv (locModelLight, false,
                    new Mat4Scale(2).mul(new Mat4Transl(3,0,2)).floatArray());
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
        }else{
            glUniform1f(locTypeLight, 1);
            glUniformMatrix4fv(locModelLight, false,
                    new Mat4Scale(1).mul(new Mat4RotY(rot1)).mul(new Mat4Transl(-3, -3, 1)).floatArray());
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
            glUniform1f(locTypeLight, 2);
            glUniformMatrix4fv(locModelLight, false,
                    new Mat4Scale(1).mul(new Mat4Transl(-1, 3, 0)).floatArray());
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
            glUniform1f(locTypeLight, 3);
            glUniformMatrix4fv(locModelLight, false,
                    new Mat4Scale(5).mul(new Mat4Transl(0, 0, -6)).floatArray());
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
            glUniform1f(locTypeLight, 4);
            glUniformMatrix4fv(locModelLight, false,
                    new Mat4Scale(0.2).mul(new Mat4RotX(rot1)).mul(new Mat4Transl(1, -3, 0)).floatArray());
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
            glUniform1f(locTypeLight, 5);
            glUniformMatrix4fv(locModelLight, false,
                    new Mat4Scale(0.5).mul(new Mat4RotY(-rot1)).mul(new Mat4Transl(2, 0, 2)).floatArray());
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
            glUniform1f(locTypeLight, 6);
            glUniformMatrix4fv(locModelLight, false,
                    new Mat4Scale(0.5).mul(new Mat4RotZ(rot1)).mul(new Mat4Transl(-3, 0, 0)).floatArray());
            buffers.draw(GL_TRIANGLES, shaderProgramLight);
        }
        glUniform1f(locTypeLight, 0);
        glUniformMatrix4fv(locModelLight, false,
                new Mat4Scale(1).mul(new Mat4RotY(rot1)).mul(new Mat4Transl(2, 3, 1)).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgramLight);

         */
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

        Vec3D light = new Vec3D(0, 0, 15).mul(new Mat3RotY(rotLight));
        glUniform3f(lightPos,(float)light.getX(), (float)light.getY(), (float)light.getZ());

        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());


        Mat4 matMVPlight =  new Mat4ViewRH(light, light.mul(-1), new Vec3D(0,1,0))
                .mul(new Mat4OrthoRH(10,10,1,20));

        glUniformMatrix4fv(locLightVP,false, matMVPlight.floatArray());
        renderTarget.getDepthTexture().bind(shaderProgram, "depthTexture",1);
        texture1.bind(shaderProgram, "res/texture/mosaic.jpg",0);

        glUniform1f(locTime, time);
        glUniform1f(locInner_nmb, inner_nmb);
        glUniform1f(locOuter_nmb, outer_nmb);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(1).floatArray());
        glPatchParameteri(GL_PATCH_VERTICES, 3);
        buffers.draw(GL_PATCHES, shaderProgram);
/*
        glUniform1f(locTime, time);
        glUniform1f(locType, 0);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(1).mul(new Mat4RotY(rot1)).mul(new Mat4Transl(2,3,1)).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 1);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(1).mul(new Mat4RotY(rot1)).mul(new Mat4Transl(-3,-3,1)).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 2);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(1).mul(new Mat4Transl(-1,3,0)).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 3);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(5).mul(new Mat4Transl(0,0,-6)).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 4);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(0.2).mul(new Mat4RotX(rot1)).mul(new Mat4Transl(1,-3,0)).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 5);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(0.5).mul(new Mat4RotY(-rot1)).mul(new Mat4Transl(2,0,2)).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 6);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(0.5).mul(new Mat4RotZ(rot1)).mul(new Mat4Transl(-3,0,0)).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 7);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(0.4).mul(new Mat4Transl(0,0,15)).mul(new Mat4RotY(rotLight)).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);
        glUniform1f(locType, 8);
        glUniformMatrix4fv (locModel, false,
                new Mat4Scale(1).floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgram);

 */
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
                camera = camera.mulRadius(1.1f);
                if(outer_nmb>1) {
                    inner_nmb--;
                    outer_nmb--;
                }

            }
            else {
                camera = camera.mulRadius(0.9f);
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