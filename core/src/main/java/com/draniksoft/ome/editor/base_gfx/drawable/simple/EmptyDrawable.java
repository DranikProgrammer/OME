package com.draniksoft.ome.editor.base_gfx.drawable.simple;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.draniksoft.ome.editor.base_gfx.drawable.utils.Drawable;

public class EmptyDrawable implements Drawable {

    @Override
    public void draw(Batch b, float x, float y, float w, float h) {

    }

    @Override
    public void destruct() {

    }

    public Drawable copy() {
	  return null;
    }

}
