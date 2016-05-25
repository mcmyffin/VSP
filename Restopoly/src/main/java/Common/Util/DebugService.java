package Common.Util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 19.05.16.
 */
public class DebugService extends PrintStream{

    private static OutputStream errStream = new ByteArrayOutputStream();
    private static OutputStream outStream = new ByteArrayOutputStream();

    private static DebugService errService = new DebugService(errStream);
    private static DebugService outService = new DebugService(outStream);


    private List<String> list;

    public DebugService(OutputStream outputStream) {
        super(outputStream);
        this.list = new ArrayList();
    }

    public static void setErrStream(){
        System.setErr(errService);
    }

    public static void setOutStream(){
        System.setOut(outService);
    }

    public static String getErrList(){
        String txt = new String();
        List<String> list = errService.getList();
        for(String x : list){
            txt+="\n"+x;
        }
        return txt;
    }

    public static String getOutList(){
        String txt = new String();
        List<String> list = outService.getList();
        for(String x : list){
            txt+="\n"+x;
        }
        return txt;
    }

    @Override
    public void print(String s) {
        super.print(s);
        this.list.add(s);
    }

    @Override
    public void print(boolean b) {
        super.print(b);
        this.list.add(b+"");
    }

    @Override
    public void print(char c) {
        super.print(c);
        this.list.add(c+"");
    }

    @Override
    public void print(int i) {
        super.print(i);
        this.list.add(i+"");
    }

    @Override
    public void print(long l) {
        super.print(l);
        this.list.add(l+"");
    }

    @Override
    public void print(float f) {
        super.print(f);
        this.list.add(f+"");
    }

    @Override
    public void print(double d) {
        super.print(d);
        this.list.add(d+"");
    }

    @Override
    public void print(char[] s) {
        super.print(s);
        this.list.add(s.toString());
    }

    @Override
    public void print(Object obj) {
        super.print(obj);
        this.list.add(obj.toString());
    }

    public List<String> getList(){
        return this.list;
    }
}
