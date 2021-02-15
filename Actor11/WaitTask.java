// File: c:/ddc/Java/Actor11/WaitTask.java
// (C) OntoOO/ Dennis de Champeaux
// Date: Mon Jun 17 16:18:55 2019

package actor11;

import java.util.*;
import java.io.*;

public class WaitTask extends TimerTask {
    static public Timer agentTimer = new Timer(true);
    private State st;
    public WaitTask(State wait1, long delay) {
	st = wait1;
	agentTimer.schedule(this, delay); 
    }
    public void run() {
	st.wakeUp();
    }
}
