package com.github.jsappz.lamegame.game;

import com.github.jsappz.lamegame.engine.*;
import com.github.jsappz.lamegame.engine.graph.*;
import com.github.jsappz.lamegame.engine.graph.lights.DirectionalLight;
import com.github.jsappz.lamegame.engine.graph.weather.Fog;
import com.github.jsappz.lamegame.engine.item.GameItem;
import com.github.jsappz.lamegame.engine.item.SkyBox;
import com.github.jsappz.lamegame.engine.item.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements GameLogic {
    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final Vector3f WHITE_LIGHT = new Vector3f(1, 1, 1);

    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;
    private Scene scene;
    private Hud hud;
    private float lightAngle;
    private Terrain terrain;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init();
        scene = new Scene();

        float skyBoxScale = 50.0f;
        float terrainScale = 10;
        int terrainSize = 3;
        float minY = -0.1f;
        float maxY = 0.1f;
        int textInc = 40;
        terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "textures/heightmap.png", "textures/terrain.png", textInc);
        scene.setGameItems(terrain.getGameItems());

        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.15f));

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("models/skybox.obj", "textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

        // Create HUD
        hud = new HudImpl("DEMO");

        camera.getPosition().x = 0.65f;
        camera.getPosition().y = 1.15f;
        camera.getPosition().y = 4.34f;
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(WHITE_LIGHT);

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(-1, 0, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(.5f, .5f, .5f ), lightPosition, lightIntensity));
    }


    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

            // Update HUD compass
            hud.rotateCompass(camera.getRotation().y);
        }

        // Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        // Check if there has been a collision. If true, set the y position to
        // the maximum height
        float height = terrain.getHeight(camera.getPosition());
        if ( camera.getPosition().y <= height )  {
            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
        }

        SceneLight sceneLight = scene.getSceneLight();

        // Update directional light direction, intensity and color
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
        lightAngle += 1.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
            sceneLight.getAmbientLight().set(0.3f, 0.3f, 0.4f);
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            sceneLight.getAmbientLight().set(factor, factor, factor);
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            sceneLight.getAmbientLight().set(1, 1, 1);
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            mesh.cleanUp();
        }
        hud.cleanup();
    }

}
