package ro.unibuc.hello.entities;
import org.springframework.data.annotation.Id;
import ro.unibuc.hello.dtos.PolicyDTO;

import java.util.List;

public class Policy {
    @Id
    public String id;
    public String name;
    public List<Statement> statements;

    public Policy() {}

    public Policy(String id, String name, List<Statement> statements){
        this.id = id;
        this.name = name;
        this.statements = statements;
    }

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
}
