package com.draniksoft.ome.editor.systems.gui;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.draniksoft.ome.editor.support.event.workflow.ModeChangeE;
import com.draniksoft.ome.editor.systems.render.ui.UIRenderSystem;
import com.draniksoft.ome.editor.ui.BaseWindow;
import com.draniksoft.ome.editor.ui.wins.MOList;
import com.draniksoft.ome.editor.ui.wins.actions.ActionList;
import com.draniksoft.ome.editor.ui.wins.asset.AssetMgmntWin;
import com.draniksoft.ome.editor.ui.wins.asset.DrawableSelectionWin;
import com.draniksoft.ome.editor.ui.wins.em.AddCompWin;
import com.draniksoft.ome.editor.ui.wins.em.EmList;
import com.draniksoft.ome.editor.ui.wins.em.EmSuppWin;
import com.draniksoft.ome.editor.ui.wins.inspector.InspectorMainWin;
import com.draniksoft.ome.editor.ui.wins.uimgmnt.WindowMgrWin;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import net.mostlyoriginal.api.event.common.Subscribe;

import java.util.HashMap;

public class UiSystem extends BaseSystem {

    private static String tag = "UiSystem";

    public static class WinCodes {

        public static final int win1 = 1;
        public static final int winMgr = 2;
        public static final int em_win = 3;
        public static final int assetMgr = 4;
        public static final int actionList = 5;
        public static final int inspector = 7;

        public static final int drawableSelWin = 21;
        public static final int addCWin = 22;


        public static final int emSuppWin = 31;

        public static HashMap<Integer, Class> map = new HashMap<Integer, Class>();

        static {
            System.out.println("Initializing wincodes::map");
            map.put(win1, MOList.class);
            map.put(winMgr, WindowMgrWin.class);
            map.put(em_win, EmList.class);
            map.put(assetMgr, AssetMgmntWin.class);
            map.put(actionList, ActionList.class);
            map.put(inspector, InspectorMainWin.class);
            map.put(addCWin, AddCompWin.class);
            map.put(drawableSelWin, DrawableSelectionWin.class);
            map.put(emSuppWin, EmSuppWin.class);


        }
    }

    UIRenderSystem topSys;

    @Wire(name = "top_stage")
    Stage uiStage;

    @Wire
    InputMultiplexer multiplexer;


    ObjectMap<Integer, BaseWindow> wins;
    IntArray backupIDs;


    MenuBar menuBar;

    Table rootT;

    boolean openBlock = false;


    @Override
    protected void processSystem() {

    }

    @Override
    protected void initialize() {

        wins = new ObjectMap<Integer, BaseWindow>();
        backupIDs = new IntArray();

        rootT = new Table();
        rootT.setFillParent(true);
        uiStage.addActor(rootT);

        menuBar = new MenuBar();

        asembleMenuBar();


        rootT.add(menuBar.getTable()).expand().top();


        //uiStage.setDebugAll(true);

        multiplexer.addProcessor(0, uiStage);

        initBaseWins();


    }

    private void initBaseWins() {

        int ar[] = {WinCodes.inspector};

        for (int id : ar) {
            buildWin(id, true);
        }


    }


    public IntArray crateBackup() {

        backupIDs.clear();

        for (ObjectMap.Entry<Integer, BaseWindow> entry : wins) {

            if (entry.value.isOpen()) {
                backupIDs.add(entry.key);
                entry.value.close();
            }
        }

        Gdx.app.debug(tag, "Created window backUP :: sz " + backupIDs.size);

        return backupIDs;

    }

    public void loadBackup() {

        Gdx.app.debug(tag, "loading backup");

        for (int i = 0; i < backupIDs.size; i++) {

            Gdx.app.debug(tag, "opening " + backupIDs.get(i));

            open(backupIDs.get(i), "#backup_r");
        }
    }

    /**
     * We can open something "Twice" because the uri can point to something else
     */
    public void open(int code, String uri) {

        if (openBlock) return;

        if (!wins.containsKey(code)) {
            if (!buildWin(code, false)) {
                return;
            }
        }

        if ((wins.get(code).getStage() == null)) uiStage.addActor(wins.get(code));

        wins.get(code).open(uri);


    }

    public void close(int code) {

        /**
         * We can't close something that is not created,
         * or something that is closed
         */
        if (!wins.containsKey(code) || !wins.get(code).isOpen()) {
            return;
        }

        wins.get(code).close();

    }

    public BaseWindow getWin(int c) {

        if (!wins.containsKey(c)) return null;

        return wins.get(c);
    }

    private boolean buildWin(int code, boolean silent) {

        BaseWindow w = null;

        try {
            if (WinCodes.map.containsKey(code))
                w = (BaseWindow) WinCodes.map.get(code).getConstructor().newInstance();
        } catch (Exception e) {
            Gdx.app.debug(tag, "", e);
        }
        if (w == null) return false;

        w.code = code;

        w.init(world);

        wins.put(code, w);

        if (!silent)
            uiStage.addActor(w);

        return true;

    }

    public ObjectMap.Values<BaseWindow> getWins() {
        return wins.values();
    }

    private void asembleMenuBar() {

        Menu fileM = new Menu("File");

        Menu editM = new Menu("Edit");

        Menu stx = new Menu("Management");

        Menu hz = new Menu("Windows");

        /*fileM.addItem(new WinOpenMenuItem("Open", WinCodes.open_proj, "lol", this));
        fileM.addItem(new MenuItem("Save", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.getSystem(ActionSystem.class).exec(new SaveProjectAction());
            }
        }));


        Menu editM = new Menu("Edit");
        editM.addItem(new MenuItem("Undo", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.getSystem(ActionSystem.class).undo();
            }
        }));
*/
        menuBar.addMenu(fileM);
        menuBar.addMenu(editM);
        menuBar.addMenu(stx);
        menuBar.addMenu(hz);

    }

    public void centerWin(int code) {

        BaseWindow w = wins.get(code);
        if (w == null) return;

        w.centerWindow();

    }


    @Subscribe
    public void modeChanged(ModeChangeE e) {

        Gdx.app.debug(tag, "Mode changed");


        openBlock = e.SHOW_MODE;

        if (e.SHOW_MODE) {
            crateBackup();
        } else {
            loadBackup();
        }


    }

}
