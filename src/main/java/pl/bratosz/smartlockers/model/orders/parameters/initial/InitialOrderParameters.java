package pl.bratosz.smartlockers.model.orders.parameters.initial;

import pl.bratosz.smartlockers.model.clothes.ArticleType;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

public class InitialOrderParameters implements InitialForNewOne {
    private ArticleType articleType;
    private ClothSize size;
    private Cloth clothToExchange;

    public InitialOrderParameters(ArticleType articleType, ClothSize size, Cloth clothToExchange) {
        this.articleType = articleType;
        this.size = size;
        this.clothToExchange = clothToExchange;
    }

    @Override
    public ArticleType getArticleType() {
        return articleType;
    }

    @Override
    public ClothSize getSize() {
        return size;
    }

    public Cloth getClothToExchange() {
        return clothToExchange;
    }
}
