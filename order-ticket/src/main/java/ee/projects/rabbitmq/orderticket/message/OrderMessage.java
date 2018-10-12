package ee.projects.rabbitmq.orderticket.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor @NoArgsConstructor
public class OrderMessage {

    @Getter @Setter
    private String uuid;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String desc;

    @Getter @Setter
    private String status;

    @Getter @Setter
    private Date date;
}
