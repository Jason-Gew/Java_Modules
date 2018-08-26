package gew.logging.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {

    private Integer messageId;
    private Integer level;
    private String timestamp;
    private String message;

}
