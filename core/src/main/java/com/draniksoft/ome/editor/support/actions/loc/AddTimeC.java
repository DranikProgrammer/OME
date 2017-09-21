package com.draniksoft.ome.editor.support.actions.loc;

import com.artemis.World;
import com.draniksoft.ome.editor.components.time.TimedC;
import com.draniksoft.ome.editor.support.actions.Action;

public class AddTimeC implements Action {

    public AddTimeC(int _e) {
        this._e = _e;
    }

    public AddTimeC(int _e, int se, int ee) {
        this._e = _e;
        this.se = se;
        this.ee = ee;
    }

    int _e;
    int se;
    int ee;

    @Override
    public void _do(World w) {

        TimedC tc = w.getMapper(TimedC.class).create(_e);
        tc.s = se;
        tc.e = ee;

    }

    @Override
    public void _undo(World w) {

        w.getMapper(TimedC.class).remove(_e);

    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public boolean isCleaner() {
        return false;
    }
}
