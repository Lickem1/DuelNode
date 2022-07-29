package tk.duelnode.lobby.data.queue;

import lombok.Getter;

@Getter
public class Queue {

    private int queueTimer;

    public void increment() {
        queueTimer++;
    }

    public String getTimeInQueue() {
        int i = queueTimer;
        int ms = i / 60;
        int ss = i % 60;
        String m = ((ms < 10) ? "0" : "") + ms;
        String s = ((ss < 10) ? "0" : "") + ss;
        return m + ":" + s;
    }
}
