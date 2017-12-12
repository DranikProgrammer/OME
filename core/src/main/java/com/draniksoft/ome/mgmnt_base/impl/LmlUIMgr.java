package com.draniksoft.ome.mgmnt_base.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.draniksoft.ome.main_menu.MainBase;
import com.draniksoft.ome.mgmnt_base.base.AppDO;
import com.draniksoft.ome.mgmnt_base.base.AppDataManager;
import com.draniksoft.ome.support.load.IntelligentLoader;
import com.draniksoft.ome.support.load.interfaces.IRunnable;
import com.draniksoft.ome.support.ui.helper.ViewConfSt;
import com.draniksoft.ome.support.ui.lml.BaseActionProvider;
import com.draniksoft.ome.support.ui.lml.ExtViewMacro;
import com.draniksoft.ome.support.ui.lml.WinMacro;
import com.draniksoft.ome.support.ui.viewsys.BaseView;
import com.draniksoft.ome.utils.FUtills;
import com.draniksoft.ome.utils.respone.ResponseCode;
import com.draniksoft.ome.utils.struct.Pair;
import com.draniksoft.ome.utils.struct.ResponseListener;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;
import com.github.czyzby.lml.vis.util.VisLmlParserBuilder;
import com.kotcrab.vis.ui.VisUI;

import java.util.LinkedList;
import java.util.Queue;

public class LmlUIMgr extends AppDataManager {

    private static final String tag = "LmlUIMgr";

    ObjectMap<String, ViewConfSt> viewM;

    ObjectMap<String, BaseView> vws;

    volatile LmlParser p;
    BaseActionProvider pv;

    /*
    Parse process
     */

    Queue<Pair<String, ResponseListener>> queue;

    public volatile BaseView tvw;

    @Override
    protected void startupLoad(IntelligentLoader l) {

	  viewM = new ObjectMap<String, ViewConfSt>();
	  vws = new ObjectMap<String, BaseView>();
	  queue = new LinkedList<Pair<String, ResponseListener>>();

	  l.passRunnable(new Loader());

    }

    private class Loader implements IRunnable {

	  @Override
	  public void run(IntelligentLoader l) {

		parseViewData();

		buildBase();

		parseBase();

	  }

	  @Override
	  public byte getState() {
		return IRunnable.RUNNING;
	  }
    }

    private void parseBase() {

	  p.parseTemplate(Gdx.files.internal("lml/base.lml"));
	  p.parseTemplate(Gdx.files.internal("lml/stylebase.lml"));

    }

    private void buildBase() {
	  pv = new BaseActionProvider();

	  VisLmlParserBuilder bd = VisLml.parser();

	  bd.i18nBundle(AppDO.I.L().getBundle());
	  bd.skin(VisUI.getSkin());

	  bd.macro(ExtViewMacro.getP(), "include");
	  bd.macro(WinMacro.getP(), "win");

	  bd.actions("", pv);
	  p = bd.build();
    }


    public void parseView(ResponseListener l, String id) {
	  if (!viewM.containsKey(id)) {
		l.onResponse(ResponseCode.FAILED);
	  }

	  if (tvw != null) {
		queue.add(Pair.createPair(id, l));
		return;
	  }

	  buildView(l, id);

    }


    private void loadView(BaseView vw, ViewConfSt st) {
	  vw.preinit();

	  if (st.lmlPath != null) {
		vw.prepareParser(p);
		p.createView(vw, Gdx.files.internal("lml/" + st.lmlPath));
		vw.clearParser(p);
	  }
	  vw.postinit();
    }

    private void buildView(final ResponseListener l, final String id) {
	  new Thread() {
		@Override
		public void run() {
		    try {
			  setName("LML PARSE");
			  Gdx.app.debug(tag, "Building " + id);

			  ViewConfSt st = viewM.get(id);
			  Class c = st.viewClass;
			  BaseView vw = (BaseView) c.getConstructor().newInstance();
			  vw.ID = id;
			  vw.lang = AppDO.I.L().getLangS();
			  tvw = vw;

			  if (MainBase.engine != null) {
				vw.injectW(MainBase.engine);
			  }

			  loadView(vw, st);

			  vws.put(id, vw);
			  l.onResponse(ResponseCode.SUCCESSFUL);

		    } catch (Exception e) {
			  Gdx.app.error(tag, "", e);
		    }

		    tvw = null;

		    if (!queue.isEmpty()) {
			  Pair<String, ResponseListener> e = queue.poll();
			  buildView(e.getElement1(), e.getElement0());
		    }

		}
	  }.start();
    }

    public void injectInclude(final BaseView vw, final String name, String tag) {
	  if (hasViewAvailable(tag)) {
		vw.obtainIncld(name, getView(tag));
	  } else {
		parseView(new ResponseListener() {
		    @Override
		    public void onResponse(short code) {
			  vw.obtainIncld(name, obtainParsed());
		    }
		}, tag);
	  }
    }

    public void injectIncludes(final BaseView vw) {
	  if (vw.getInclds().size > 0) Gdx.app.debug(tag, "Injecting includes for " + vw.ID);
	  for (final Pair<String, String> p : vw.getInclds()) {
		injectInclude(vw, p.getElement0(), p.getElement1());
	  }
    }


    public BaseView obtainParsed() {
	  BaseView v2 = tvw;
	  tvw = null;
	  return v2;
    }

    public Class getViewStC(String id) {
	  if (viewM.containsKey(id)) {
		return viewM.get(id).viewClass;
	  }
	  return null;
    }

    public boolean hasViewAvailable(String id) {
	  if (!vws.containsKey(id)) return false;
	  return !vws.get(id).active;
    }


    public BaseView getView(String id) {
	  if (hasViewAvailable(id)) {
		return vws.get(id);
	  }
	  return null;
    }

    private void parseViewData() {
	  viewM.clear();
	  JsonValue r = FUtills.r.parse(Gdx.files.internal("_data/ui_views.json"));
	  for (JsonValue v : r) {
		ViewConfSt st = new ViewConfSt();
		st.lmlPath = v.has("lml") ? v.getString("lml") : "";
		try {
		    st.viewClass = Class.forName(v.getString("class"));
		} catch (ClassNotFoundException e) {
		    continue;
		}
		viewM.put(v.name, st);
	  }

	  Gdx.app.debug(tag, "Parsed " + viewM.size + " views");
    }

    @Override
    protected void engineLoad(IntelligentLoader l) {

    }

    @Override
    protected void terminateLoad() {

    }


}