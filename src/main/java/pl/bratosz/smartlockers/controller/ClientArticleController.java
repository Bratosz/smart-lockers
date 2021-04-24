package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.service.ClientArticleService;

import java.util.List;

@RestController
@RequestMapping("/client-articles")
public class ClientArticleController {

    private ClientArticleService clientArticleService;

    public ClientArticleController(ClientArticleService clientArticleService) {
        this.clientArticleService = clientArticleService;
    }

    @GetMapping("/{clientId}")
    @JsonView(Views.Public.class)
    public List<ClientArticle> get(
            @PathVariable long clientId) {
        return clientArticleService.get(clientId);
    }
}
