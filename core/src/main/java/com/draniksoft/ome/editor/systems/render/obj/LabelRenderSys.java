package com.draniksoft.ome.editor.systems.render.obj;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.draniksoft.ome.editor.components.label.LabelC;
import com.draniksoft.ome.editor.components.label.LabelRTC;
import com.draniksoft.ome.editor.components.pos.PosBoundsC;
import com.draniksoft.ome.editor.components.pos.PosC;

public class LabelRenderSys extends IteratingSystem {

    private static final String tag = "LabelRenderSys";

    public LabelRenderSys() {
	  super(Aspect.all(PosBoundsC.class, PosC.class, LabelC.class, LabelRTC.class));
    }

    @Wire(name = "batch")
    Batch b;

    @Wire(name = "game_cam")
    OrthographicCamera cam;


    @Override
    protected void begin() {
	  b.setProjectionMatrix(cam.combined);
	  b.begin();
    }

    ComponentMapper<LabelRTC> rM;
    ComponentMapper<PosBoundsC> pM;


    @Override
    protected void process(int _e) {

	  PosBoundsC sc = pM.get(_e);
	  LabelRTC lc = rM.get(_e);


	  //lc.c.setPosition(sc.x + sc.w / 2 - lc.w / 2, sc.y - lc.h);
	  lc.c.draw(b);
	  lc.c.getFont().getData().setScale(1);

    }

    @Override
    protected void end() {
	  b.end();
    }
}
