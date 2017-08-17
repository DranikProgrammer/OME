package com.draniksoft.ome.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.IntBuffer;

/**
 * Graphics/GLWF utils
 *
 *
 * */

public class GUtils {

    public static Lwjgl3Graphics getGraphics(){
        return (Lwjgl3Graphics) Gdx.graphics;
    }

    public static Lwjgl3Window getWindow(){
        return getGraphics().getWindow();
    }

    public static Lwjgl3Application getApp(){
        return (Lwjgl3Application) Gdx.app;
    }

    public static Lwjgl3ApplicationConfiguration getMenuConfig(){
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("OpenMapEditor");

        configuration.setWindowedMode(640, 480);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        configuration.setInitialVisible(true);

        configuration.setDecorated(false);
        configuration.setResizable(false);

        return configuration;
    }

    public static Lwjgl3ApplicationConfiguration getEditorConfig(){

        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("OpenMapEditor");

        configuration.setWindowedMode(640, 480);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        configuration.setInitialVisible(false);

        return configuration;

    }

    public static int getMaxTexSize(){
        IntBuffer intBuffer = BufferUtils.newIntBuffer(16);
        Gdx.gl20.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, intBuffer);
        return  intBuffer.get();
    }


}
