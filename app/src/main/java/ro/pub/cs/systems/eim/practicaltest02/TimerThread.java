package ro.pub.cs.systems.eim.practicaltest02;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerThread extends Thread
{

    ServerThread serverThread;

    TimerTask task;

    public TimerThread(ServerThread serverThread) {
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        Timer timer = new Timer();
        task = new MyClass(serverThread);
        timer.schedule(task, 5000);
    }
}

class MyClass extends TimerTask {
    ServerThread serverThread;

    public MyClass(ServerThread serverThread) {
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        serverThread.setStale(true);
    }
}
