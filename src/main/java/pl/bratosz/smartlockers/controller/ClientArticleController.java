package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/update-price/{newPrice}/{id}")
    @JsonView(Views.Public.class)
    public ClientArticle updatePrice(
            @PathVariable float newPrice,
            @PathVariable long id) {
        return clientArticleService.updatePrice(newPrice, id);
    }

    @PostMapping("/set-depreciation-period/for-all/{periodInMonths}/{clientId}")
    @JsonView(Views.Public.class)
    public List<ClientArticle> setDepreciationPeriod(
            @PathVariable int periodInMonths,
            @PathVariable long clientId) {
        return clientArticleService.setDepreciationPeriod(
                periodInMonths,
                clientId);
    }

    @PostMapping("/set-percentage-cap/for-all/{percentageCap}/{clientId}")
    @JsonView(Views.Public.class)
    public List<ClientArticle> setDepreciationPercentageCap(
            @PathVariable int percentageCap,
            @PathVariable long clientId) {
        return clientArticleService.setDepreciationPercentageCap(
                percentageCap,
                clientId);

    }
}
