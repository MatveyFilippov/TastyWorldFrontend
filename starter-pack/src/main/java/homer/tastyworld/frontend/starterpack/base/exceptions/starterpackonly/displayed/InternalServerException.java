package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.displayed;

import homer.tastyworld.frontend.starterpack.base.exceptions.DisplayedException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.ErrorAlert;

public class InternalServerException extends DisplayedException {

    public InternalServerException() {
        super();
    }

    @Override
    protected void action() {
        ErrorAlert.showAlert(
                "Внешняя ошибка",
                "Произошла ошибка на сервере, разработчики уже получили уведомление",
                true
        );
    }

}
