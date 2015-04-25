package hello;

/**
 * Created by kkirilov on 4/25/15.
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class PortalController {
    private Database db = new Database();

    @RequestMapping("/portal")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        ArrayList<IssueDiff> list = new ArrayList<>();

        IssueDiff m = new IssueDiff();
        m.setActualParentHtml("<ul><li></li></ul>");
        m.setExpectedParentHtml("<ul><li></li><li></li></ul>");
        IssueDiff m1 = new IssueDiff();
        m1.setActualParentHtml("<ul><li></li></ul>");
        m1.setExpectedParentHtml("<ul><li></li><li></li></ul>");
        IssueDiff m2 = new IssueDiff();
        m2.setExpectedParentHtml("<ul><li></li><li></li></ul>");
        m2.setActualParentHtml("<ul><li></li></ul>");
        IssueDiff m3 = new IssueDiff();
        m3.setExpectedParentHtml("<ul><li></li><li></li></ul>");
        m3.setActualParentHtml("<ul><li></li></ul>");
        list.add(m);
        list.add(m1);
        list.add(m2);
        list.add(m3);

        RunResult run = new RunResult();
        run.setIssues(list);

        RunResult run2 = new RunResult();
        run2.setIssues(list);
        ArrayList<RunResult> runs = new ArrayList<>();
        runs.add(run);
        runs.add(run2);
        model.addAttribute("runs", runs);
        return "portal";
    }
}
