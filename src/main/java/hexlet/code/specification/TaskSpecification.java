package hexlet.code.specification;

import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component()
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDTO params) {

        return withTitleCont(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withTitleCont(String substring) {

        return (root, query, cb) -> substring == null ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + substring.toLowerCase() + "%");
    }

    private Specification<Task> withStatus(String statusSlug) {

        return (root, query, cb) -> statusSlug == null ? cb.conjunction()
                : cb.equal(root.get("taskStatus").get("slug"), statusSlug);
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {

        return (root, query, cb) -> assigneeId == null ? cb.conjunction()
                : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withLabelId(Long labelId) {

        return (root, query, cb) -> labelId == null ? cb.conjunction()
                : cb.equal(root.get("labels").get("id"), labelId);
    }
}
