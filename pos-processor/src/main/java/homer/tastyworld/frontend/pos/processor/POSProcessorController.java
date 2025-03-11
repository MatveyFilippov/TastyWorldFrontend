package homer.tastyworld.frontend.pos.processor;

import homer.tastyworld.frontend.pos.processor.core.OrderInfoPane;
import homer.tastyworld.frontend.pos.processor.core.OrderUpdatesListener;
import homer.tastyworld.frontend.starterpack.api.requests.MyParams;
import homer.tastyworld.frontend.starterpack.base.exceptions.SubscriptionDaysAreOverError;
import homer.tastyworld.frontend.starterpack.base.utils.misc.TypeChanger;
import homer.tastyworld.frontend.starterpack.base.utils.ui.AlertWindow;
import homer.tastyworld.frontend.starterpack.base.utils.ui.helpers.PaneHelper;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class POSProcessorController {

    @FXML
    private ScrollPane scrollItems, scrollOrders;
    @FXML
    private AnchorPane printOrderImgBtn, doneOrderImgBtn;
    @FXML
    private AnchorPane orderCreatedTimeTopic, orderNameTopic;

    @FXML
    private void initialize() {
        checkDaysLeft();
        initImgBtnsInMainPane();
        OrderInfoPane.init(scrollItems, printOrderImgBtn, doneOrderImgBtn, orderCreatedTimeTopic, orderNameTopic);
        OrderUpdatesListener.init(scrollOrders);
    }

    private void checkDaysLeft() {
        long subscriptionAvailableDays = MyParams.getTokenSubscriptionAvailableDays();
        if (subscriptionAvailableDays < 0) {
            throw new SubscriptionDaysAreOverError();
        }
        if (subscriptionAvailableDays <= 7) {
            String text = String.format(
                    "Если не оплатить подписку, то через %s программа перестанет работать",
                    TypeChanger.toDaysFormat(subscriptionAvailableDays)
            );
            AlertWindow.showInfo("Близится окончание подписки", text, true);
        }
    }

    private void initImgBtnsInMainPane() {
        PaneHelper.setImageBackgroundCentre(
                printOrderImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/printOrderImgBtn.png")
        );
        PaneHelper.setImageBackgroundCentre(
                doneOrderImgBtn,
                POSProcessorApplication.class.getResourceAsStream("images/buttons/doneOrderImgBtn.png")
        );
    }

}
