package com.draniksoft.ome.editor.ui.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.draniksoft.ome.editor.systems.support.UiSystem;
import com.kotcrab.vis.ui.widget.MenuItem;


public class UriMenuItem extends MenuItem {

    public UriMenuItem(String s, final int code, final String uri, final UiSystem uiSys) {

        super(s, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                uiSys.open(code, uri);
            }
        });

    }

}
