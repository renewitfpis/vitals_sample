package sg.lifecare.data.remote.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import sg.lifecare.vitals2.R;

public class AlertRuleResponse extends Response {

    @SerializedName("Data")
    private List<AlertRuleResponse.Data> data;

    @Override
    public List<AlertRuleResponse.Data> getData() {
        return data;
    }

    public class Data {

        public static final int ARM_STATE_ARM = 1;
        public static final int ARM_STATE_DISARM = 2;
        public static final int ARM_STATE_AWAY = 3;

        public static final int ARM_TYPE_SYSTEM = 1;
        public static final int ARM_TYPE_CUSTOM = 2;

        public static final String ARM = "arm";
        public static final String DISARM = "disarm";
        public static final String DISARM_AUTO = "disarm-auto";

        private static final String SYSTEM_RULE = "r";
        private static final String CUSTOM_RULE = "y";


        private String _id;
        private String activity_name;
        private String activity_type;
        private String arm_state;
        // create_date
        private String name;
        // related_entities
        private String rule_message;
        // start_time
        private String type;
        // type2
        // zone

        public String getId() {
            return _id;
        }

        public String getName() {
            return name;
        }

        public String getMessage() {
            return rule_message;
        }

        public int getArmState() {
            if (ARM.equals(arm_state)) {
                return ARM_STATE_ARM;
            } else if (DISARM.equals(arm_state)){
                return ARM_STATE_DISARM;
            } else if (DISARM_AUTO.equals(arm_state)) {
                return ARM_STATE_AWAY;
            }
            return 0;
        }

        public int getArmType() {
            if (SYSTEM_RULE.equals(type)) {
                return ARM_TYPE_SYSTEM;
            } else if (CUSTOM_RULE.equals(type)) {
                return ARM_TYPE_CUSTOM;
            }

            return 0;
        }

        public int getArmStateString() {
            if (ARM.equals(arm_state)) {
                return R.string.rule_armed;
            } else if (DISARM.equals(arm_state)){
                return R.string.rule_disarmed;
            } else if (DISARM_AUTO.equals(arm_state)) {
                return R.string.rule_away;
            }
            return 0;
        }

        public int getArmStateColor() {
            if (ARM.equals(arm_state)) {
                return R.color.rule_arm;
            } else if (DISARM.equals(arm_state)){
                return R.color.rule_disarm;
            } else if (DISARM_AUTO.equals(arm_state)) {
                return R.color.rule_away;
            }
            return 0;
        }
    }

    class Entity {
        private String _id;
        private String authentication_string_lower;
        private String first_name;
        private String last_name;
        // medias
        private String name;
    }
}
