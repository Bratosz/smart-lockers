package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothActualStatus;
import pl.bratosz.smartlockers.model.clothes.ClothDestination;
import pl.bratosz.smartlockers.model.clothes.ClothStatus;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.ClothesStatusRepository;

import java.util.Date;
import java.util.List;

import static pl.bratosz.smartlockers.model.clothes.ClothActualStatus.*;
import static pl.bratosz.smartlockers.model.clothes.ClothDestination.*;

@Service
public class ClothStatusService {
    private ClothesStatusRepository clothesStatusRepository;
    private User user;

    public ClothStatusService(ClothesStatusRepository clothesStatusRepository) {
        this.clothesStatusRepository = clothesStatusRepository;
    }

    public ClothStatus create(ClothDestination destination,
                              User user) {
        ClothActualStatus actualStatus =
                resolveStatus(destination);
        ClothStatus clothStatus =
                new ClothStatus(actualStatus, destination, user, new Date());
        return clothesStatusRepository.save(clothStatus);
    }

    public ClothStatus create(ClothActualStatus actualStatus,
                              Cloth cloth,
                              User user) {
        ClothDestination destination =
                resolveDestination(actualStatus);
        ClothStatus clothStatus =
                new ClothStatus(actualStatus, destination, cloth, user, new Date());
        return clothesStatusRepository.save(clothStatus);
    }

    public ClothStatus create(ClothDestination destiny,
                              Cloth cloth,
                              User user) {
        ClothActualStatus status =
                resolveStatus(destiny);
        ClothStatus clothStatus =
                new ClothStatus(status, destiny, cloth, user, new Date());
        return clothesStatusRepository.save(clothStatus);
    }

    private ClothDestination resolveDestination(ClothActualStatus actualStatus) {
        ClothDestination destination = null;
        switch(actualStatus) {
            case ORDERED:
                destination = FOR_ASSIGN;
                break;
            case ASSIGNED:
            case IN_PREPARATION:
                destination = FOR_RELEASE;
                break;
            case RELEASED:
                destination = FOR_WASH;
                break;
            case ACCEPTED_FOR_EXCHANGE:
                destination = FOR_WITHDRAW_AND_EXCHANGE;
                break;
            case EXCHANGED:
            case ACCEPTED_AND_WITHDRAWN:
                destination = FOR_DISPOSAL;
                break;
        }
        return destination;
    }

    private ClothActualStatus resolveStatus(ClothDestination destiny) {
        ClothActualStatus status = null;
        switch(destiny) {
            case FOR_ASSIGN:
                status = ORDERED;
                break;
            case FOR_RELEASE:
                status = ASSIGNED;
                break;
            case FOR_WASH:
                status = RELEASED;
                break;
            case FOR_WITHDRAW_AND_DELETE:
                status = RELEASED;
                break;
            case FOR_WITHDRAW_AND_EXCHANGE:
                status = RELEASED;
                break;
            case FOR_DISPOSAL:
                status = ACCEPTED_AND_WITHDRAWN;
                break;
        }
        return status;
    }

    public void hardDelete(List<ClothStatus> statusHistory) {
        for(ClothStatus status : statusHistory) {
            clothesStatusRepository.deleteById(status.getId());
        }
    }
}
