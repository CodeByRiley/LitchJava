package com.codebyriley.Util.Math;

public class Transform {
    public Vector3f mPosition;
    public float mRotation;
    public Vector3f mScale;
    
    public Transform() {
        mPosition = new Vector3f(0, 0, 0);
        mScale = new Vector3f(1, 1, 1);
        mRotation = 0;
    }

    public Transform(Vector3f position, Vector3f scale, float rotation) {
        mPosition = position;
        mRotation = rotation;
        mScale = scale;
    }
    
    public void SetPosition(Vector3f position) {
        mPosition = position;
    }

    public void SetPosition(float x, float y, float z) {
        mPosition.x = x;
        mPosition.y = y;
        mPosition.z = z;
    }
    
    public void SetRotation(float rotation) {
        mRotation = rotation;
    }

    public void SetScale(Vector3f scale) {
        mScale = scale;
    }
    
    public void SetScale(float x, float y, float z) {
        mScale.x = x;
        mScale.y = y;
        mScale.z = z;
    }

    public Vector3f GetPosition() {
        return mPosition;
    }

    public float GetRotation() {
        return mRotation;
    }

    public Vector3f GetScale() {
        return mScale;
    }


}