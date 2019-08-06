// File: c:/ddc/Java/Actor8/PersonalityAspect.java
// Date: Mon Oct 16 17:37:49 2017
// (C) OntoOO/ Dennis de Champeaux

package actor8;

import java.util.*;
import java.io.*;

public class PersonalityAspect {
    private int index;
    private float weight;
    private float value;
    int getIndex() { return index; }
    public float getWeight() { return weight; }
    public void setWeight(float f) { weight = f; }
    public float getValue() { return value; }
    public void setValue(float f) { value = f; }
    public PersonalityAspect(int i, float w, float v) {
	index = i; weight = w; value = v;
    }
    public String ascii() {
	return "weight " + weight + " value " + value + " " +
	    PersonalityFeature.allFeatures[index].ascii();
    }
}
