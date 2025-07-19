package bg.sopra.steria.order.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/version")
public class VersionController {

    @Value("${spring.application.version}")
    private String applicationVersion;

    @GetMapping
    public Map<String, String> getVersion() {
        Map<String, String> version = new HashMap<>();
        version.put("version", applicationVersion);
        return version;
    }
}