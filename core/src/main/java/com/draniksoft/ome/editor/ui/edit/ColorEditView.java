package com.draniksoft.ome.editor.ui.edit;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.draniksoft.ome.support.ui.viewsys.BaseWinView;
import com.github.czyzby.lml.annotation.LmlActor;
import com.kotcrab.vis.ui.widget.VisTable;

public class ColorEditView extends BaseWinView {

    private static final String tag = "ColorEditView";



    public void initfor(int id) {

    }

    @LmlActor("root")
    VisTable t;

    @Override
    public Actor getActor() {
	  return t;
    }

    @Override
    public void preinit() {


    }

    @Override
    public void postinit() {

    }
}