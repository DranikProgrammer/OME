package com.draniksoft.ome.support.ui.viewsys;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.draniksoft.ome.mgmnt_base.base.AppDO;
import com.draniksoft.ome.utils.struct.Pair;
import com.github.czyzby.lml.parser.LmlParser;

public abstract class BaseView {

    private static final String tag = "BaseView";

    public String ID;
    public String lang;
    public boolean active = false;

    // name :: tag
    Array<Pair<String, String>> inclds;
    Array<BaseView> rtIncs;

    BaseView parent = null;

    public BaseView() {
	  inclds = new Array<Pair<String, String>>();
	  rtIncs = new Array<BaseView>();
    }


    /*
    Kept for compatibility reasons here, is null in a non world env
    */

    protected World _w;

    public void injectW(World w) {
	  this._w = w;
    }

    public abstract Actor get();

    public abstract void preinit();

    public abstract void postinit();

    //

    public void opened() {
	  active = true;
	  AppDO.I.LML().injectIncludes(this);
    }

    public void closed() {
	  clearInjectedIncludes();
	  active = false;
	  parent = null;
    }

    public void clearInjectedIncludes() {
	  for (BaseView v : rtIncs) {
		freeincld(v);
	  }
	  rtIncs.clear();
    }

    /*
    	Should be called when a widget wants to notify the parent that its preferred site changed and it requests a new layout
     */
    public boolean invalidateParent() {
	  if (parent != null) return false;
	  return parent.invalidateReq(this);
    }

    // true - request accepted
    private boolean invalidateReq(BaseView vw) {
	  return false;
    }

    //

    public void setParent(BaseView vw) {
	  parent = vw;
    }

    public BaseView getParent() {
	  return parent;
    }

    public void clearParent() {
	  parent = null;
    }

    //

    public void clearInclds() {
	  inclds.clear();
    }

    public void dyleteIncldByName(String name) {
	  for (int i = 0; i < inclds.size; i++) {
		if (inclds.get(i).K().equals(name)) {
		    inclds.removeIndex(i);
		}
	  }
    }

    public void dyleteIncldByVID(String id) {
	  for (int i = 0; i < inclds.size; i++) {
		if (inclds.get(i).V().equals(id)) {
		    inclds.removeIndex(i);
		}
	  }
    }

    public void addIncld(String name, String id) {
	  inclds.add(Pair.P(name, id));
    }

    public void pushIncld(String name, String id) {
	  addIncld(name, id);
	  AppDO.I.LML().injectInclude(this, name, id);
    }

    // deletes from runtime includes
    public void removeIncldbVID(String id) {
	  dyleteIncldByVID(id);
	  int res = -1;
	  for (int i = 0; i < rtIncs.size; i++) {
		if (rtIncs.get(i).ID.equals(id)) {
		    res = i;
		}
	  }
	  if (res > 0) {
		freeincld(rtIncs.get(res));
		rtIncs.removeIndex(res);

		Gdx.app.debug(tag, "Removed include at runtime");
	  }
    }

    public void removeIncldByName(String name) {
	  for (Pair<String, String> p : inclds) {
		if (p.K().equals(name)) {
		    removeIncldbVID(p.V());
		}
	  }
    }

    public Array<Pair<String, String>> getInclds() {
	  return inclds;
    }


    protected void handleInclude(String nm, BaseView vw) {
	  if (!active) return;
	  vw.addedAsInclude(this);
	  vw.opened();
	  rtIncs.add(vw);
    }

    private void freeincld(BaseView v) {
	  v.closed();
    }

    public void obtainIncld(String name, BaseView vw) {
	  handleInclude(name, vw);
    }


    public void addedAsInclude(BaseView p) {
	  parent = p;
    }

    //

    public void clearData() {
	  if (active) return;
	  parent = null;
	  active = false;
	  inclds.clear();
	  rtIncs.clear();
    }

    public void prepareParser(LmlParser p) {
    }

    public void clearParser(LmlParser p) {
    }

}
