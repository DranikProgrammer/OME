package com.draniksoft.ome.editor.manager;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.draniksoft.ome.editor.load.LoadSaveManager;
import com.draniksoft.ome.editor.load.ProjectSaver;
import com.draniksoft.ome.editor.support.event.asset.AssetListChange;
import com.draniksoft.ome.support.load.IntelligentLoader;
import com.draniksoft.ome.utils.FUtills;
import com.draniksoft.ome.utils.dao.AssetDDao;
import net.mostlyoriginal.api.event.common.EventSystem;

import java.util.HashMap;
import java.util.Map;

public class DrawableMgr extends BaseSystem implements LoadSaveManager {

    static final String tag = "DrawableMgr";

    public Texture t;


    @Wire
    AssetManager assM;

    ObjectMap<String, TextureAtlas> dA; // Atlases
    ObjectMap<String, AssetDDao> dD;// descriptors ;

    Array<AssetDDao> mapD;
    Array<AssetDDao> localD;
    Array<AssetDDao> intD;
    JsonReader jR;

    Map<String, String> redirects;

    Array<AssetDDao> loadDs;

    @Override
    protected void processSystem() {

        int i = 0;

        for (AssetDDao dao : loadDs) {

            if (assM.isLoaded(dao.p)) {

                loadDs.removeIndex(i);

                addAsset(dao);

            }

            i++;

        }


    }


    // Adds asset, supposes it to be loaded
    private void addAsset(AssetDDao dao) {

        Gdx.app.debug(tag, "Successfully loaded " + dao.uri);

        dA.put(dao.id, assM.get(dao.p, TextureAtlas.class));
        dD.put(dao.id, dao);

        dao.loaded = true;

        world.getSystem(EventSystem.class).dispatch(new AssetListChange(dao.id));

    }

    @Override
    protected void initialize() {

        dA = new ObjectMap<String, TextureAtlas>();
        dD = new ObjectMap<String, AssetDDao>();

        jR = new JsonReader();

        loadDs = new Array<AssetDDao>();
        localD = new Array<AssetDDao>();
        // localD = AppDataObserver.getI().getAssetDM().getDaos();
        mapD = new Array<AssetDDao>();
        intD = new Array<AssetDDao>();

        redirects = new HashMap<String, String>();

        initInternalAss();

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                assM.finishLoading();
            }
        });

        processSystem();

    }

    private void initInternalAss() {

        Gdx.app.debug(tag, "Initing internal assets");

        String rJsonStr = Gdx.files.internal("_data/iassets.json").readString();

        JsonValue rootV = jR.parse(rJsonStr);

        for (int i = 0; i < rootV.size; i++) {

            JsonValue v = rootV.get(i);

            AssetDDao d = new AssetDDao();

            d.name = v.getString("name");
            d.id = v.getString("id");

            d.sysmz = v.getBoolean("sysmz", false);

            if (d.name == null || d.id == null) continue;

            d.uri = FUtills.toUIRPath("i_assets/" + d.id.substring(2) + "/f.atlas", 1);
            d.p = "i_assets/" + d.id.substring(2) + "/f.atlas";

            if (v.getBoolean("fl"))
                loadDwbl(d);

            if (v.has("redirect")) {
                d.redirect = v.getString("redirect");
                redirects.put(d.redirect, d.id);
            }
            intD.add(d);

        }

        Gdx.app.debug(tag, "Collected " + intD.size + " internals");


    }


    public void loadDwbl(AssetDDao d) {

        if (d == null)
            return;

        loadDs.add(d);

        assM.load(d.p, TextureAtlas.class);

        Gdx.app.debug(tag, "Added " + d.id + " to load queue");

    }



    public TextureAtlas getAtlas(String id) {

        if (!dA.containsKey(id)) return null;

        return dA.get(id);

    }

    public Array<AssetDDao> getAllAviabDao() {

        Array<AssetDDao> ds = new Array<AssetDDao>();

        ds.addAll(intD);
        ds.addAll(localD);
        ds.addAll(mapD);


        return ds;
    }

    public Array<AssetDDao> getLoadDs() {
        return loadDs;
    }

    public ObjectMap<String, TextureAtlas> getAllAtlasI() {

        return dA;

    }


    public void putRedirect(String r, String id) {

        if (!dA.containsKey(id)) return;

        redirects.put(r, id);

    }

    public void resetRedirects() {

        redirects.clear();

        for (AssetDDao d : intD) {

            if (d.sysmz && d.redirect != null) {

                redirects.put(d.redirect, d.id);

            }

        }

    }

    public Map<String, String> getRedirects() {
        return redirects;
    }

    public void resetToDefaulRedirect(String r) {

        redirects.clear();

        for (AssetDDao d : intD) {

            if (d.sysmz && d.redirect != null & d.redirect.equals(r)) {

                redirects.put(r, d.id);

            }

        }


    }

    public Array<AssetDDao> getLoaded() {

        return dD.values().toArray();

    }

    public ObjectMap<String, AssetDDao> getAllDescI() {
        return dD;
    }


    public boolean containsAtlas(String n) {
        return dA.containsKey(passRedirect(n));
    }

    private String passRedirect(String id) {
        if (redirects.containsKey(id)) return redirects.get(id);
        return id;
    }

    public TextureRegion getRegion(String aid, String name, int id) {

        if (redirects.containsKey(aid)) aid = redirects.get(aid);

        if (!dA.containsKey(aid)) return null;

        return dA.get(aid).findRegion(name, id);

    }

    public TextureRegion getRegionF(String aid, String name, int id) {

        if (!dA.containsKey(aid)) return null;

        return dA.get(aid).findRegion(name, id);

    }

    public TextureRegion getRegion(String uri) {

        String splitS = "@";

        String[] parts = uri.split(splitS);


        if (parts.length > 2) {
            return getRegion(parts[0], parts[1], Integer.valueOf(parts[2]));
        }

        return getRegion(parts[0], parts[1], -1);


    }

    @Override
    public void save(IntelligentLoader l, ProjectSaver s) {

    }
}
