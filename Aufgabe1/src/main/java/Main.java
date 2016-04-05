/**
 * Created by dima on 05.04.16.
 */
import static spark.Spark.*;
public class Main {


    public static void main(String[] args) {
        get("/hello", (req,res) -> "<b>Hello du AFFE</b>");
    }
}
