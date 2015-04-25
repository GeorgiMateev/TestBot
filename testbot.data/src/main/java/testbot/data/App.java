package testbot.data;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	System.out.print("Db start");
        //Database db = new Database();
        //db.addDummyData();
    	
    	Automation a = new Automation();
    	a.start();
    }
}
