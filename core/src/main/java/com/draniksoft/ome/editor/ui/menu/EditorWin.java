package com.draniksoft.ome.editor.ui.menu;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.draniksoft.ome.editor.systems.gui.UiSystem;
import com.draniksoft.ome.mgmnt_base.base.AppDO;
import com.draniksoft.ome.mgmnt_base.impl.LmlUIMgr;
import com.draniksoft.ome.support.ui.util.WindowAgent;
import com.draniksoft.ome.support.ui.viewsys.BaseWinView;
import com.draniksoft.ome.utils.respone.ResponseCode;
import com.draniksoft.ome.utils.struct.ResponseListener;
import com.draniksoft.ome.utils.ui.editorMenu.WinControllerOverlay;
import com.kotcrab.vis.ui.widget.VisWindow;

public class EditorWin extends VisWindow implements WinControllerOverlay {

    private static final String tag = "EditorWin";

    BaseWinView vw;
    boolean parsing = false;

    World w;
    UiSystem uiSys;

    Table root;
    ScrollPane pane;

    float nW = 0;


    WindowAgent agent;

    String curID = null;


    public EditorWin(World w) {
	  super("");
	  this.w = w;
	  this.uiSys = w.getSystem(UiSystem.class);
	  root = new Table();
	  pane = new ScrollPane(root);

	  pane.setScrollingDisabled(true, false);

	  add(pane).expand().fill().padTop(40);

	  setKeepWithinStage(false);
	  setMovable(false);
	  setResizable(false);

	  setDebug(true, false);
    }


    public void initFor(BaseWinView v) {
	  this.vw = v;
	  v.WINMODE = true;

	  Gdx.app.debug(tag, "initing on new win");

	  nW = getWidth();

	  v.init(this);
	  v.calc(this);

	  if (agent != null)
		agent.opened(vw);

	  root.clearChildren();
	  root.add(v.getActor()).expand().fill();


	  vw.opened();

	  apply();

    }

    public String getViewID() {
	  return curID;
    }

    public void clearWin() {
	  Gdx.app.debug(tag, "Clearin win");
	  root.clear();
	  if (vw != null) vw.closed();
	  if (agent != null) agent.closed();
	  agent = null;
	  curID = null;
	  vw = null;
    }


    public void open(final String id, final WindowAgent ag) {

	  Gdx.app.debug(tag, "opening on " + id);

	  final LmlUIMgr m = AppDO.I.LML();

	  if (m.hasViewAvailable(id)) {
		clearWin();
		agent = ag;
		initFor((BaseWinView) m.getView(id));

		curID = id;

	  } else {

		parsing = true;
		m.parseView(new ResponseListener() {
		    @Override
		    public void onResponse(short code) {
			  if (code == ResponseCode.SUCCESSFUL) {
				clearWin();
				agent = ag;
				initFor((BaseWinView) m.obtainParsed());
				curID = id;
			  } else {
				clearWin();
			  }
			  parsing = false;
		    }
		}, id);
	  }

    }


    public boolean canOpen(String vID) {
	  Class c = AppDO.I.LML().getViewStC(vID);

	  return c != null && BaseWinView.class.isAssignableFrom(c);

    }

    public void recalc() {
	  if (vw != null)
		vw.calc(this);
    }

    public float getCalcWidth() {
	  return nW;
    }


    public boolean isOpen() {
	  return vw != null;
    }

    public boolean isBusy() {
	  return isOpen() || parsing;
    }

    public BaseWinView getVw() {
	  return vw;
    }

    //


    //


    @Override
    public float getCW() {
	  return getWidth();
    }

    @Override
    public float getWW() {
	  return uiSys.getStageW();
    }

    @Override
    public float getWH() {
	  return uiSys.getStageH();
    }

    @Override
    public void setCalcW(float val) {
	  nW = val;
    }


    public boolean cfg_menuR;
    @Override
    public void setMenuReplace(boolean r) {
	  cfg_menuR = r;
    }

    public boolean cfg_menuH;

    @Override
    public void setMenuHide(boolean h) {
	  cfg_menuH = h;
    }

    @Override
    public void apply() {

	  uiSys.validateLayout();
    }

    @Override
    public void setScroll(boolean enabledX, boolean enabledY) {
	  pane.setScrollingDisabled(!enabledX, !enabledY);
    }

    public void resized() {

    }

}
