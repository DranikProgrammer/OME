package com.draniksoft.ome.mgmnt_base.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.draniksoft.ome.mgmnt_base.impl.*;
import com.draniksoft.ome.support.load.IntelligentLoader;
import com.draniksoft.ome.support.load.interfaces.IGLRunnable;
import com.draniksoft.ome.utils.Const;
import com.draniksoft.ome.utils.GUtils;

import java.util.HashMap;
import java.util.Iterator;

/*
Class for keeping track of other managers, loading and disposing them
 */
public class AppDO extends AppDataManager {

    public static final String tag = "AppDO";
    public static final AppDO I = new AppDO();

    volatile Preferences prefs;
    HashMap<Class, AppDataManager> m;

    Class<AppDataManager>[] defM = new Class[]{AppDO.class, LangManager.class, ConfigManager.class,
		FileManager.class, LoadHistoryMgr.class, LmlUIMgr.class};

    @Override
    protected void startupLoad(IntelligentLoader l) {

        prefs = Gdx.app.getPreferences(Const.prefsID);

        m = new HashMap<Class, AppDataManager>();

        for (Class<AppDataManager> c : defM) {
            try {
                m.put(c, c.getConstructor().newInstance());
            } catch (Exception e) {
                Gdx.app.error(tag, "", e);
            }

        }

        GL_LOADER gll = new GL_LOADER();
        l.passGLRunnable(gll);

        while (!glR) Thread.yield();

        Gdx.app.debug(tag, "Constructed " + m.size() + " observers");


    }

    @Override
    protected void engineLoad(IntelligentLoader l) {

    }

    @Override
    protected void terminateLoad() {

        prefs.flush();

    }

    public synchronized Preferences getPrefs() {
        return prefs;
    }

    public <T extends AppDataManager> T getMgr(Class<T> t) {
        return (T) m.get(t);
    }

    public Iterator<AppDataManager> getMgrI() {
	  return m.values().iterator();
    }

    public LmlUIMgr LML() {
	  return getMgr(LmlUIMgr.class);
    }

    public LangManager L() {
        return getMgr(LangManager.class);
    }

    public LoadHistoryMgr LH() {
        return getMgr(LoadHistoryMgr.class);
    }

    public FileManager F() {
        return getMgr(FileManager.class);
    }

    public ConfigManager C() {
        return getMgr(ConfigManager.class);
    }

    volatile boolean glR = false;

    private class GL_LOADER implements IGLRunnable {
        @Override
        public byte run() {

		GUtils.initGLData();
		glR = true;

            return IGLRunnable.READY;
        }
    }


}
