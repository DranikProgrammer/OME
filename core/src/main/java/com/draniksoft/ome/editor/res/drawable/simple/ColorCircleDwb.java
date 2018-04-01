package com.draniksoft.ome.editor.res.drawable.simple;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.cyphercove.gdx.flexbatch.FlexBatch;
import com.draniksoft.ome.editor.base_gfx.batchables.STB;
import com.draniksoft.ome.editor.res.drawable.utils.Drawable;
import com.draniksoft.ome.utils.GU;

public class ColorCircleDwb extends Drawable {

    private int r = 20;
    private float mns = 1, mxs = 1;
    private Color c;

    public ColorCircleDwb(int r, float mns, float mxs, Color c) {
	  this.r = r;
	  this.mns = mns;
	  this.mxs = mxs;
	  this.c = c;
    }

    @Override
    public void draw(FlexBatch b, int x, int y) {

	  STB.circle(x, y, r(), c);

    }

    @Override
    public void draw(FlexBatch b, int x, int y, int w, int h) {

	  int nr = Math.min(Math.min(w, h) / 2, r);

	  STB.circle(x, y, nr, c);

    }

    @Override
    public boolean contains(Vector2 p) {
	  return p.dst(0, 0) < r();
    }

    @Override
    public void size(Vector2 v) {
	  v.x = Math.max(v.x, r());
	  v.y = Math.max(v.y, r());
    }

    public int r() {
	  return (int) (r * GU.CAM_SCALE(mns, mxs));
    }
}
