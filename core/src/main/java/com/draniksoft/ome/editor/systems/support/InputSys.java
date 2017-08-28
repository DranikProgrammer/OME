package com.draniksoft.ome.editor.systems.support;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.draniksoft.ome.editor.components.supp.SelectionC;
import com.draniksoft.ome.editor.support.event.SelectionChangeE;
import com.draniksoft.ome.utils.PUtils;
import net.mostlyoriginal.api.event.common.EventSystem;

public class InputSys extends BaseSystem implements InputProcessor {

    static final String tag = "InputSys";

    @Wire
    World phys;

    @Wire(name = "game_vp")
    Viewport gameVP;

    @Wire
    InputMultiplexer mx;

    float tdt = -1;

    float tl = 10;

    Vector2 tV;
    IntArray es;

    float pressT = .3f;

    @Override
    protected void initialize() {
        tV = new Vector2();
        es = new IntArray();

        mx.addProcessor(this);
    }

    @Override
    protected void processSystem() {

        if (tdt >= 0) {
            tdt += Gdx.graphics.getDeltaTime();
        }

        if (tdt > pressT) {
            Gdx.app.debug(tag, "Touch down");
            processI();
            tdt = -1;
        }
    }

    private void processI() {

        int x = Gdx.input.getX();
        int y = Gdx.input.getY();

        tV.set(x, y);

        Gdx.app.debug(tag, "TD AT " + tV.toString());

        tV = gameVP.unproject(tV);

        es.clear();
        phys.QueryAABB(new QueryCallback() {
                           @Override
                           public boolean reportFixture(Fixture fixture) {
                               es.add((Integer) fixture.getBody().getUserData());
                               return false;
                           }
                       }, (tV.x - tl) / PUtils.PPM, (tV.y - tl) / PUtils.PPM,
                (tV.x + tl) / PUtils.PPM, (tV.y + tl) / PUtils.PPM);


        IntBag sels = world.getAspectSubscriptionManager().get(Aspect.all(SelectionC.class)).getEntities();

        for (int i = 0; i < sels.size(); i++) {

            if (es.size > 0 && sels.get(i) == es.get(0)) return;

            world.getMapper(SelectionC.class).remove(sels.get(i));

        }

        int e = -1;
        if (es.size > 0) {

            e = es.get(0);

            world.getMapper(SelectionC.class).create(e);

        }

        SelectionChangeE ev = new SelectionChangeE();
        ev.old = sels.size() > 0 ? sels.get(0) : -1;
        ev.n = e;
        world.getSystem(EventSystem.class).dispatch(ev);

        Gdx.app.debug(tag, "Selection changed ");


    }

    public int getSel() {
        if (es.size > 0)
            return es.get(0);
        else
            return 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tdt = 0;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        tdt = -1;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        tdt = -1;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
