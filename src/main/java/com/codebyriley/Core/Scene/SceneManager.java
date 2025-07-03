package com.codebyriley.Core.Scene;

import com.codebyriley.Core.Engine;
import com.codebyriley.Core.Rendering.Primatives.Renderer;
import com.codebyriley.Core.Rendering.Text.TextRenderer;

public class SceneManager {
    public static SceneTransitions mTransitionType;
    public static SceneBase mCurrentScene;
    public static SceneBase mNextScene;
    public static boolean mIsChangingScene = false;
    public static float mTransitionDuration = 0.25f; // Scene Transition Time (seconds)
    private static float mTransitionElapsed = 0f;
    private static float mCurrentFade = 0f;
    private static boolean mFadingOut = true;
    private static Renderer renderer;

    public static void ChangeScene(SceneBase scene, SceneTransitions transitionType, float duration) {
        mNextScene = scene;
        mTransitionType = transitionType;
        mTransitionDuration = duration; // Store the duration
        mIsChangingScene = true;
        mTransitionElapsed = 0f;
        mFadingOut = true;
    }

    public static void Update(float dT) {
        if (mIsChangingScene) {
            mTransitionElapsed += Engine.deltaTime / 1000.0f; // ms to seconds
            float t = Math.min(mTransitionElapsed / mTransitionDuration, 1.0f);
            float eased = applyEasing(mTransitionType, t);
            mCurrentFade = mFadingOut ? eased : 1.0f - eased;

            // Update both scenes during transition
            if (mCurrentScene != null) mCurrentScene.Update(dT);
            if (mNextScene != null) mNextScene.Update(dT);

            if (t >= 1.0f && mFadingOut) {
                // Switch scenes and start fade-in
                mCurrentScene = mNextScene;
                mNextScene = null;
                mTransitionElapsed = 0f;
                mFadingOut = false;
            } else if (t >= 1.0f && !mFadingOut) {
                // End transition
                mIsChangingScene = false;
                mCurrentFade = 0f;
            }
        } else {
            if (mCurrentScene != null) mCurrentScene.Update(dT);
            mCurrentFade = 0f;
        }
    }

    public static float getCurrentFade() {
        return mIsChangingScene ? mCurrentFade : 0.0f;
    }


    public static float applyEasing(SceneTransitions transition, float t) {
        switch (transition) {
            case EASE_IN: return SceneEasings.easeInSine(t);
            case EASE_OUT: return SceneEasings.easeOutSine(t);
            case EASE_IN_OUT: return SceneEasings.easeInOutSine(t);
            case EASE_IN_EXPO: return SceneEasings.easeInExpo(t);
            case EASE_OUT_EXPO: return SceneEasings.easeOutExpo(t);
            case EASE_IN_OUT_EXPO: return SceneEasings.easeInOutExpo(t);
            default: return t;
        }
    }

    public static void Init(Renderer renderer) {
        SceneManager.renderer = renderer;
    }

    public static void SetScene(SceneBase scene) {
        mCurrentScene = scene;
    }

    public static SceneBase GetCurrentScene() {
        return mCurrentScene;
    }
    public static void FixedUpdate(float fixedDeltaTime) {
        if (mCurrentScene != null) mCurrentScene.FixedUpdate(fixedDeltaTime);
    }

    public static void Draw(Renderer renderer, TextRenderer textRenderer) {
        if (mCurrentScene != null) mCurrentScene.Draw(renderer, textRenderer);
    }
}
