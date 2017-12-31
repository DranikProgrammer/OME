package com.draniksoft.ome.editor.support.compositionObserver.abstr;

import com.artemis.Aspect;
import com.artemis.World;
import com.artemis.utils.IntBag;

public abstract class SimpleCompositionObserver extends CompositionObserver {

    public static final String tag = "SimpleCompositionObserver";

    private boolean active;
    protected int _selE = -1;
    protected World _w;

    private Aspect.Builder aB;
    Aspect a;


    @Override
    public void init(World w) {
        this._w = w;

        _init();

        aB = getAspectB();

        if (aB != null) {
            a = aB.build(w);
        }

    }

    @Override
    public void setSelection(int e) {
        if (e == _selE) return;
        boolean bac = active;
        int prevE = _selE;
        this._selE = e;
        active = matches(e);
        on_selchanged(bac, prevE);
    }

    protected abstract void on_selchanged(boolean previousActivity, int previd);

    @Override
    public void selectionCompChanged() {
        boolean bac = active;
        active = _selE > -1 && matches(_selE);
        on_selCompChanhed(bac);
    }

    protected abstract void on_selCompChanhed(boolean previousActivity);

    @Override
    public boolean isSelActive() {
        return active;
    }

    @Override
    public boolean matches(int e) {
        return e > -1 && a.isInterested(_w.getEntity(e));
    }


    protected abstract void _init();


    protected abstract Aspect.Builder getAspectB();

    protected IntBag getMEs() {
	  return _w.getAspectSubscriptionManager().get(getAspectB()).getEntities();
    }

}
