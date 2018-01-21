package com.draniksoft.ome.editor.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.draniksoft.ome.editor.ui.menu.base_addons.BaseBarButtons;
import com.draniksoft.ome.editor.ui.menu.base_addons.BaseShowButtons;
import com.draniksoft.ome.mgmnt_base.base.AppDO;
import com.draniksoft.ome.mgmnt_base.impl.LmlUIMgr;
import com.draniksoft.ome.support.ui.viewsys.BaseView;
import com.draniksoft.ome.utils.struct.Pair;
import com.github.czyzby.lml.annotation.LmlActor;
import com.kotcrab.vis.ui.widget.VisTable;


/*
Not managed by any parent or container. Self validating panel
 */
public class BottomMenu extends BaseView {

    private static final String tag = "BottomMenu";

    @LmlActor("root")
    VisTable root;

    @LmlActor("btn_C")
    Container btnC;

    @LmlActor("cnt")
    VisTable cnt;

    BaseShowButtons showB;
    BaseBarButtons editB;

    boolean showM = false;


    private void clearViews() {
	  btnC.clearChildren();
	  cnt.clearChildren();


	  clearInjectedIncludes();

	  if (showB != null) {
		showB = null;
	  }
	  if (editB != null) {
		editB = null;
	  }
    }

    public void modeChanged(boolean showM) {
	  this.showM = showM;

	  clearViews();

	  final LmlUIMgr mgr = AppDO.I.LML();
	  clearInclds();
	  getInclds().add(Pair.P("btns", showM ? "bar_show_buttons" : "bar_base_buttons"));
	  if (!showM) getInclds().add(Pair.P("ext", AppDO.I.C().getConfVal_S("base_bar_editor_addon")));
	  mgr.injectIncludes(this);
    }

    public void resized(float w) {

    }

    @Override
    public void obtainIncld(String name, BaseView vw) {
	  Gdx.app.debug(tag, "GOT " + name + " " + (vw == null ? "NULL" : vw.toString()));

	  if (vw == null) {
		Gdx.app.error(tag, name + " ->> NULL INCLUDE");
		return;
	  }
	  super.obtainIncld(name, vw);

	  if (vw instanceof BaseShowButtons) {
		showB = (BaseShowButtons) vw;
	  } else if (vw instanceof BaseBarButtons) {
		editB = (BaseBarButtons) vw;
	  }

	  if (name.equals("btns")) {
		btnC.setActor(vw.get());
	  } else {
		cnt.add(vw.get()).expand().fill();
	  }

    }

    @Override
    public void opened() {
	  super.opened();
	  Gdx.app.debug(tag, "Opening");
	  modeChanged(false);
    }

    @Override
    public Actor get() {
	  return root;
    }

    @Override
    public void preinit() {
    }

    @Override
    public void postinit() {

    }

    /**
     * LAYOUT PART
     */

    boolean hidden;

    public void setHidden(boolean s) {
	  if (!hidden && s) {
		clearViews();
	  }
	  hidden = s;
    }
}


