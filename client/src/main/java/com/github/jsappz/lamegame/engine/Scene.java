package com.github.jsappz.lamegame.engine;

import com.github.jsappz.lamegame.engine.graph.Mesh;
import com.github.jsappz.lamegame.engine.graph.weather.Fog;
import com.github.jsappz.lamegame.engine.item.GameItem;
import com.github.jsappz.lamegame.engine.item.SkyBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private Map<Mesh, List<GameItem>> meshMap;
    private SkyBox skyBox;
    private SceneLight sceneLight;
    private Fog fog;

    public Scene() {
        meshMap = new HashMap<>();
        fog = Fog.NOFOG;
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public void setGameItems(GameItem[] gameItems) {
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i=0; i<numGameItems; i++) {
            GameItem gameItem = gameItems[i];
            Mesh mesh = gameItem.getMesh();
            List<GameItem> list = meshMap.get(mesh);
            if ( list == null ) {
                list = new ArrayList<>();
                meshMap.put(mesh, list);
            }
            list.add(gameItem);
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }
}
