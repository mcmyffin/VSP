package Common.Util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;

/**
 * Created by dima on 19.05.16.
 */
public class DebugService extends PrintStream{

    private static OutputStream errStream = new ByteArrayOutputStream();
    private static OutputStream outStream = new ByteArrayOutputStream();

    private static DebugService errService = new DebugService(errStream);
    private static DebugService outService = new DebugService(outStream);


    private static List<String> list;

    public DebugService(OutputStream outputStream) {
        super(outputStream);
        if(list == null){
            this.list = new ArrayList();
        }
    }

    public static void setErrStream(){
        System.setErr(errService);
    }

    public static void setOutStream(){
        System.setOut(outService);
    }

    public static synchronized String getLog(){
        String txt = "";
        for(String t : list){
            txt += t+"\n";
        }
        return txt;
    }

    public static synchronized String logInHtml(){
        String txt = "<html>";
        txt+="<head><title>LOG</title></head>";
        txt+="<body>";
        int i = 0;
        for(String t : list){
            txt += "<p>["+i+"] "+t+"</p>";
            i++;
        }
        txt+="</body>";
        txt+="</html>";
        return txt;
    }

    public static String getErrList(){
        String txt = new String();
        List<String> list = errService.getList();
        for(String x : list){
            txt+="<p><font color=\"red\">"+x+"</font></p>";
        }
        return txt;
    }

    public static String getOutList(){
        String txt = new String();
        for(String x : list){
            txt+=x+"\n";
        }
        return txt;
    }

    private void add(String s){
//        String txt = "";
//        for(char c : s.toCharArray()) {
//            if (c == '\t') {
//                txt += "<tab indent=15>";
//            }else if (c == '\n'){
//                txt+="<br>";
//            }else{
//                txt +=c;
//            }
//        }
        list.add(s);
    }

    @Override
    public void print(String s) {
        super.print(s);
        add(s);
    }

    @Override
    public void print(boolean b) {
        super.print(b);
        add(b+"");
    }

    @Override
    public void print(char c) {
        super.print(c);
        add(c+"");
    }

    @Override
    public void print(int i) {
        super.print(i);
        add(i+"");
    }

    @Override
    public void print(long l) {
        super.print(l);
        add(l+"");
    }

    @Override
    public void print(float f) {
        super.print(f);
        add(f+"");
    }

    @Override
    public void print(double d) {
        super.print(d);
        add(d+"");
    }

    @Override
    public void print(char[] s) {
        super.print(s);
        add(s.toString());
    }

    @Override
    public void print(Object obj) {
        super.print(obj);
        add(obj.toString());
    }

    public List<String> getList(){
        return this.list;
    }

    public static void start(){
        setOutStream();
        setErrStream();

        get("/debug", (req,res) -> {
            if(req.queryMap().toMap().containsKey("html")){
                return DebugService.logInHtml();
            }else return getOutList();
        });
    }
}
