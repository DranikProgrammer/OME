package com.draniksoft.ome.editor.support.actions.proj;

import com.artemis.World;
import com.draniksoft.ome.editor.load.MapLoadBundle;
import com.draniksoft.ome.editor.support.actions.Action;
import com.draniksoft.ome.editor.systems.file_mgmnt.ProjectLoadSystem;

public class LoadProjectAction implements Action {

    String path;

    public LoadProjectAction(String path) {
        this.path = path;
    }

    @Override
    public void _do(World w) {

	  w.getSystem(ProjectLoadSystem.class).setBundle(new MapLoadBundle(path));
	  w.getSystem(ProjectLoadSystem.class).load();

    }

    @Override
    public void _undo(World _w) {

    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public boolean isCleaner() {
        return true;
    }

    @Override
    public String getSimpleConcl() {
        return "Loaded project";
    }

    @Override
    public void destruct() {

    }
}
