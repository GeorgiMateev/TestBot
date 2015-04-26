package hello;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private Database db = new Database();

    @RequestMapping(value = "/data", method = RequestMethod.POST, consumes="application/json")
    public void data(@RequestBody UsageViewModel data, HttpServletResponse  response) {
        db.insertEvents(data.getEvents(), data.getMutations(), data.getTimeStamp());
    }
    
    @RequestMapping("/automate")
    public RunResult automate() {
    	Automation a = new Automation();
        Object runId = a.start();
        return db.getResultById(runId);
    }

    @RequestMapping(value = "/runs", method = RequestMethod.GET)
    public List<RunResult> runs() {
        return db.getResults();
    }
}
