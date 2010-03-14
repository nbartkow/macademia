// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ValueComparator.java

package org.macademia.jad;

import java.util.Comparator;

// Referenced classes of package de.linguatools.disco:
//            ReturnDataCol

public class ValueComparator
    implements Comparator
{

    public ValueComparator()
    {
    }

    public int compare(Object o1, Object o2)
    {
        int retval = 0;
        if((o1 instanceof ReturnDataCol) && (o2 instanceof ReturnDataCol))
        {
            ReturnDataCol c1 = (ReturnDataCol)o1;
            ReturnDataCol c2 = (ReturnDataCol)o2;
            if(c1.value < c2.value)
                retval = 1;
            if(c1.value > c2.value)
                retval = -1;
        } else
        {
            throw new ClassCastException("ValueComparator: Illegal arguments!");
        }
        return retval;
    }
}
