package homer.tastyworld.frontend.starterpack.base.exceptions.starterpackonly.api.rest;

import homer.tastyworld.frontend.starterpack.base.exceptions.DisplayedException;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;

public class InternalServerException extends DisplayedException {

    public InternalServerException() {
        super();
    }

    @Override
    protected void action() {
        AlertWindow.showError(
                "Внешняя ошибка",
                "Произошла ошибка на сервере, разработчики уже получили уведомление",
                true
        );
    }

}
