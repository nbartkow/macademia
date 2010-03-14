// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ReturnDataCol.java

package org.macademia.jad;


public class ReturnDataCol
{

    ReturnDataCol()
    {
        word = "";
        value = 0.0F;
        relation = 0;
    }

    ReturnDataCol(String w, float floatValue)
    {
        word = w;
        value = floatValue;
    }

    public String word;
    public float value;
    public int relation;
}
