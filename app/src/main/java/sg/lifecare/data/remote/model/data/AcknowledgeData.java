package sg.lifecare.data.remote.model.data;

public class AcknowledgeData {

    private String EntityId;
    private String RuleEditEntityId;
    private String RuleId;

    public AcknowledgeData () {
    }

    public void setEntityId(String id) {
        EntityId = id;
    }

    public void setRuleEditEntityId(String id) {
        RuleEditEntityId = id;
    }

    public void setRuleId(String id) {
        RuleId = id;
    }
}
