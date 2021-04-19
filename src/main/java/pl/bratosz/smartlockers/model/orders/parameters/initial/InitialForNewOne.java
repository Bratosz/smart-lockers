package pl.bratosz.smartlockers.model.orders.parameters.initial;

import pl.bratosz.smartlockers.model.clothes.ArticleType;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

public interface InitialForNewOne {
    ArticleType getArticleType();
    ClothSize getSize();
}
