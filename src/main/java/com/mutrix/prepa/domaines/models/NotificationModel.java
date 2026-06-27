package com.mutrix.prepa.domaines.models;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter @Setter
public class NotificationModel {

    private  String title;
    private  String  body;
    private  List<String> receiver;
    private Map<String, Object> metaData;
    private NotificationType notificationType;

}
