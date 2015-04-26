package hello;

/**
 * Created by kkirilov on 4/25/15.
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PortalController {
    private Database db = new Database();

    @RequestMapping("/portal")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        List<RunResult> runs = db.getResults();
        model.addAttribute("runs", runs);
        return "portal";
    }
}
