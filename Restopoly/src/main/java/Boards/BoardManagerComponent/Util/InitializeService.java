package Boards.BoardManagerComponent.Util;

import Boards.BoardManagerComponent.Board;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by dima on 18.05.16.
 */
public class InitializeService {

    private final Queue<Board> queue;

    public InitializeService() {
        this.queue = new LinkedList();
        startListen();
    }

    public synchronized void addBoard(Board board){
        queue.offer(board);
        notifyAll();
    }


    public void startListen() {

        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                try{
                    while (!Thread.currentThread().isInterrupted()){

                        Board b = getNextBoard();
                        Thread.sleep(1000);
                        b.initialize();
                    }

                }catch (InterruptedException ex){

                }catch (UnirestException|URISyntaxException ex){
                    System.out.println("Board initialize FAILED");
                    ex.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }

    private synchronized Board getNextBoard() throws InterruptedException {
        if (isQueueEmpty()) wait();
        return queue.poll();
    }

    private synchronized boolean isQueueEmpty(){
        return queue.isEmpty();
    }

}
