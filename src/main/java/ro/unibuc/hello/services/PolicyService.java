package ro.unibuc.hello.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.unibuc.hello.dtos.PolicyDTO;
import ro.unibuc.hello.entities.Policy;
import ro.unibuc.hello.repositories.PolicyRepository;
import ro.unibuc.hello.entities.Statement;
import ro.unibuc.hello.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    public PolicyDTO getPolicyById(String id) throws EntityNotFoundException {
        var policy = policyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Policy")
        );
        return new PolicyDTO(policy.id, policy.name, policy.statements);
    }

    public PolicyDTO addPolicy(String name, List<Statement> statements) {
        var id = UUID.randomUUID().toString();
        var policy = new Policy(id, name, statements);
        policyRepository.save(policy);
        return new PolicyDTO(policy.id, policy.name, policy.statements);
    }

    public PolicyDTO updatePolicy(String id, String name, List<Statement> statements) throws EntityNotFoundException {
        var policy = policyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Policy")
        );
        policy.name = name;
        policy.statements = statements;
        policyRepository.save(policy);
        return policy.toDTO();
    }

    public void deletePolicy(String id) throws EntityNotFoundException {
        var policy = policyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Policy")
        );
        policyRepository.delete(policy);
    }

    public List<PolicyDTO> getPolicies() {
        return policyRepository.findAll().stream()
                .map(Policy::toDTO)
                .collect(Collectors.toList());
    }

}
