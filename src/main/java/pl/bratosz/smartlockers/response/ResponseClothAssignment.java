package pl.bratosz.smartlockers.response;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;

public class ResponseClothAssignment {
    @JsonView(Views.Public.class)
    private boolean assigned;

    public ResponseClothAssignment(boolean assigned) {
        this.assigned = assigned;
    }

    public static ResponseClothAssignment createForSucceed() {
        return new ResponseClothAssignment(true);
    }

    public static ResponseClothAssignment createForFailure() {
        return new ResponseClothAssignment(false);
    }
}
