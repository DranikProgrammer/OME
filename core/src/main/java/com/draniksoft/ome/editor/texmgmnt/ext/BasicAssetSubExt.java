package com.draniksoft.ome.editor.texmgmnt.ext;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.draniksoft.ome.editor.texmgmnt.acess.TextureRAccesor;
import com.draniksoft.ome.support.execution_base.ExecutionProvider;
import com.draniksoft.ome.utils.FUtills;
import com.draniksoft.ome.utils.struct.EmptyIterator;
import com.draniksoft.ome.utils.struct.ResponseListener;

import java.util.Iterator;
import java.util.concurrent.Callable;

public class BasicAssetSubExt extends AssetSubExtension {

    private static final String tag = "BasicAssetSubExt";

    TextureAtlas atlas;
    ObjectMap<String, TextureRAccesor> map;

    /*
	  URI -- EXTENSION NAME SHOULD BE OMITTED !!
     */


    @Override
    public TextureRAccesor get(String uri) {
	  return null;
    }

    @Override
    public Iterator<TextureRAccesor> getAll() {
	  if (map == null) return new EmptyIterator<TextureRAccesor>();
	  return map.values().iterator();
    }

    /*
        Load
     */

    String atlasUri = "";

    @Override
    public void load(final ExecutionProvider p, World w) {
	  final String atlasPath = FUtills.uriToPath(atlasUri);
	  p.getAssets().load(atlasPath, TextureAtlas.class);
	  p.awaitAsset(atlasPath, new ResponseListener() {
		@Override
		public void onResponse(short code) {
		    atlas = p.getAssets().get(atlasPath, TextureAtlas.class);
		    p.exec(new LoadCallable());
		}
	  });
    }

    private class LoadCallable implements Callable<Void> {

	  @Override
	  public Void call() throws Exception {
		Gdx.app.debug(tag, "Loading async stuff");
		if (map == null) map = new ObjectMap<String, TextureRAccesor>();

		for (TextureAtlas.AtlasRegion r : atlas.getRegions()) {
		    String id = "";

		    if (r.index == -1) {
			  id = r.name;
		    } else {
			  id = r.name + "@" + r.index;

		    }
		    if (map.containsKey(id)) {
			  map.get(id).updateOn(new TextureRAccesor(r));
		    } else {
			  TextureRAccesor a = new TextureRAccesor(r);
			  map.put(id, a);
		    }

		}

		return null;
	  }
    }
}
