package org.rairlab.planner.utils;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by naveensundarg on 9/29/17.
 */
public class Visualizer {


    private Visualizer(){
        throw new AssertionError("Cannot instantiate the Visualizer");


    }
    static ColoredPrinter cp = new ColoredPrinter.Builder(1, false).build();


    static Queue<String> spool = new ArrayDeque<>();
    private static final AtomicBoolean shouldVisualize;
    private static int depth = 0;
    static {
        shouldVisualize = new AtomicBoolean(true);
    }

    public static boolean getShouldVisualize() {
        return shouldVisualize.get();
    }

     public static void setShouldVisualize(boolean visualize) {
         shouldVisualize.set(visualize);

    }


    public  static void nested(String name){

        StringBuffer stringBuffer = new StringBuffer("");
        if(shouldVisualize.get()){
            for(int i = 0; i<depth+1; i++){

                stringBuffer.append(" ▶ ");
            }

            stringBuffer.append(name);

        }


        spool.add(stringBuffer.toString());
    }

    public static void unspool(long delay){

        spool.forEach(x->{
           // try {
             //   Thread.sleep(delay);
                cp.println(x);
         /*   } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

        });
    }

    public static void push(){

        depth = depth + 1;
    }

    public  static void pop(){

        depth = depth - 1;
    }

    public  static void reset(){

        depth = 0;
    }


   public static void print(String message) {
        cp.print(message);
        cp.clear();
     }


    public static void printRed(String message) {

        cp.setForegroundColor(Ansi.FColor.RED);
         cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.clear();
     }

   public  static void printInfo(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.BLUE);   //setting format
        cp.print(header);
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }

    public static void printSuccess(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.BLACK);
        cp.setBackgroundColor(Ansi.BColor.GREEN);   //setting format
        cp.print(header);
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }


   public static void printDebug1(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.BLACK);
        cp.setBackgroundColor(Ansi.BColor.YELLOW);   //setting format
        cp.print(header);
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }

   public static void printDebug2(String header, String message) {

        cp.setForegroundColor(Ansi.FColor.BLACK);
        cp.setBackgroundColor(Ansi.BColor.MAGENTA);   //setting format
        cp.print(header);
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }

   public static void printFailure(String message) {

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.RED);   //setting format
        cp.print(message);
        cp.clear();
        cp.println("");
        cp.clear();
    }

  public  static void printDropped(String message) {

        cp.setForegroundColor(Ansi.FColor.WHITE);
        cp.setBackgroundColor(Ansi.BColor.RED);   //setting format
        cp.print("Dropped Goals:");
        cp.clear();
        cp.print(" ");
        cp.setAttribute(Ansi.Attribute.BOLD);
        cp.print(message);
        cp.println("");
        cp.clear();
    }

}
