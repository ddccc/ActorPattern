// File: c:/ddc/Java/Actor8/WaitTask.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Mon Jun 10 17:12:33 2019

package actor8;

import java.util.*;
import java.io.*;

public class WaitTask extends TimerTask {
    static public Timer agentTimer = new Timer(true);
    private State st;
    public WaitTask(State wait1, long delay) {
	st = wait1;
	st.setFalse();
	agentTimer.schedule(this, delay); 
    }
    public void run() {
	st.setTrue();
    }
}
