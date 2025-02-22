package homer.tastyworld.frontend.starterpack.exceptions.starterpackonly.displayed;

import homer.tastyworld.frontend.starterpack.exceptions.DisplayedException;
import homer.tastyworld.frontend.starterpack.ui.ErrorAlert;

public class InternalServerException extends DisplayedException {

    public InternalServerException() {
        ErrorAlert.showAlert(
                "Внешняя ошибка",
                "Произошла ошибка на сервере, разработчики уже получили уведомление",
                true
        );
    }

}
