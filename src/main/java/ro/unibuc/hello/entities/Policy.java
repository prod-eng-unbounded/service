package ro.unibuc.hello.entities;
import lombok.*;
import org.springframework.data.annotation.Id;
import ro.unibuc.hello.common.EffectTypes;
import ro.unibuc.hello.dtos.PolicyDTO;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Policy {
    @Id
    private String id;
    private String name;
    private List<Statement> statements;

    @Override
    public String toString() {
        return String.format(
            "Policy[id=%s, name='%s', statements='%s']",
                id, name,
                statements.stream()
                        .map(Statement::toString)
                        .reduce("", (a, b) -> a + ", " + b));
    }

    public PolicyDTO toDTO() {
        return new PolicyDTO(id, name, statements);
    }

    public int hasRightsToAction(String actionCode){
        boolean hasRights;
        for(var statement : statements) {
            for(var statementAction : statement.getActions()) {
                if(statementAction.equals(actionCode)) {
                    hasRights = statement.getEffect().equals(EffectTypes.ALLOW);
                    return hasRights ? 1 : -1;
                }
            }
        }
        return 0;
    }
}
