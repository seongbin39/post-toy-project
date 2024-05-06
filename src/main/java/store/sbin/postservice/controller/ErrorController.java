package store.sbin.postservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @RequestMapping("/403")
    public String accessDenied() {
        return "error/403";
    }

    @RequestMapping("/404")
    public String notFound() {
        return "error/404";
    }

    @RequestMapping("/500")
    public String internalServerError() {
        return "error/500";
    }

    @RequestMapping("/503")
    public String serviceUnavailable() {
        return "error/503";
    }

}
