// File: c:/ddc/Java/ActorX/State.java
// Date: Sat Sep 15 20:59:57 2018
// (C) Dennis de Champeaux/ OntoOO 

package actorX;

import java.util.*;
import java.io.*;


// A state has minimal local storage.
// The attibute consumers contains the runRonitors of their monitors.
// These consumers will be triggered through the wakeUp() operation. 
public class State {
    private float floatValue = 0;
    public void setFloatValue(float x) { floatValue = x; }
    public void increaseFloatValue() { 
	floatValue = (1 + floatValue) * 0.5f; 
    }
    public void decreaseFloatValue() { 
	floatValue = floatValue * 0.75f; 
    }
    public float getFloatValue() { return floatValue; }

    private int intValue = 0;
    public void setIntValue(int x) { intValue = x; }
    public int getIntValue() { return intValue; }

    private String stringValue = "";
    public void setStringValue(String x) { stringValue = x; }
    public String getStringValue() { return stringValue; }

    private boolean bool = false;
    public void setBool(boolean b) { bool = b; }
    public void setTrue() { bool = true; }
    public void setFalse() { bool = false; }
    public boolean bool() { return bool; }

    private Object obj = null;
    public void setObj(Object o) { obj = o;}
    public Object getObj() { return obj; }

    private Vector consumers = new Vector();
    public void addConsumer(RunMonitor mon) { consumers.addElement(mon); }

    public State() {}
    public State(float x) { floatValue = x; }
    public State(int i) { intValue = i; }
    public State(String s) { stringValue = s; }
    public State(Object o) { obj = o; }

    public void wakeUp() { 
	int lng = consumers.size();
	for (int i = 0; i < lng; i++) {
	    RunMonitor rmon = (RunMonitor) consumers.elementAt(i);
	    rmon.wakeUp(); // courtesy wake up call
	}
    } // end of wakeUp

    public String htmlStringInt() { return "<br>" + htmlStringInt0(); }
    public String htmlStringInt(String prefix) { 
	return "<br><b>" + prefix + "</b>" + htmlStringInt0(); 
    }
    public String htmlStringInt0() { return stringValue + " " + intValue; } 

} // end State

