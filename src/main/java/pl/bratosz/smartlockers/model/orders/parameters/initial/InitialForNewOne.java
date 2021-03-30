package pl.bratosz.smartlockers.model.orders.parameters.initial;

import pl.bratosz.smartlockers.model.clothes.Article;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.model.orders.OrderType;

public interface InitialForNewOne {
    Article getArticle();
    ClothSize getSize();
}
