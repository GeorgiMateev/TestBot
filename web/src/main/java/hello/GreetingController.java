package hello;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private void initializeDb() {
        Object runId  = db.createRun(11111111);
        db.saveErrorReport(runId, "<selector/>", "<expected />", "<was />", "type", 111111111);
        db.saveErrorReport(runId, "<selector/>", "<expected />", "<was />", "type", 111111112);
        db.saveErrorReport(runId, "<selector/>", "<expected />", "<was />", "type", 111111113);
        db.saveErrorReport(runId, "<selector/>", "<expected />", "<was />", "type", 111111114);

        Object runId2  = db.createRun(11111112);
        db.saveErrorReport(runId2, "<selector/>", "<expected />", "<was />", "type", 111111111);
        db.saveErrorReport(runId2, "<selector/>", "<expected />", "<was />", "type", 111111112);
        db.saveErrorReport(runId2, "<selector/>", "<expected />", "<was />", "type", 111111113);
        db.saveErrorReport(runId2, "<selector/>", "<expected />", "<was />", "type", 111111114);
    }
    private Database db = new Database();

    @RequestMapping(value = "/data", method = RequestMethod.POST, consumes="application/json")
    public void data(@RequestBody UsageViewModel data, HttpServletResponse  response) {
        db.insertEvents(data.getEvents(), data.getMutations(), data.getTimeStamp());
    }
    
    @RequestMapping("/automate")
    public void automate() {
    	Automation a = new Automation();
    	a.start();
    }

    @RequestMapping(value = "/runs", method = RequestMethod.GET)
    public List<RunResult> runs() {
        return db.getResults();
    }
}
